package simuladorkernel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GestorRecursos {

    public static final int NUM_RECURSOS = 2;

    private final PCB[] duenos = new PCB[NUM_RECURSOS + 1];

    private final Map<PCB, Integer> esperando = new LinkedHashMap<>();

    public boolean solicitar(PCB pcb, int recurso) {
        if (recurso < 1 || recurso > NUM_RECURSOS) {
            throw new IllegalArgumentException("Recurso inválido: solo existen R1 y R2");
        }
        if (esperando.containsKey(pcb)) {
            throw new IllegalStateException("P" + pcb.getPid() + " ya está bloqueado esperando un recurso");
        }
        if (duenos[recurso] == pcb) {
            System.out.println("  [RECURSO] P" + pcb.getPid() + " ya tiene R" + recurso);
            return true;
        }
        if (duenos[recurso] == null) {
            duenos[recurso] = pcb;
            System.out.println("  [RECURSO] P" + pcb.getPid() + " solicita R" + recurso + ": CONCEDIDO");
            return true;
        }
        
        pcb.setEstado(PCB.Estado.BLOQUEADO);
        esperando.put(pcb, recurso);
        System.out.println("  [RECURSO] P" + pcb.getPid() + " solicita R" + recurso
                + ": OCUPADO (lo tiene P" + duenos[recurso].getPid() + "), P" + pcb.getPid() + " queda BLOQUEADO");
        return false;
    }

    public List<PCB> detectarDeadlock() {
        for (PCB inicio : esperando.keySet()) {
            List<PCB> camino = new ArrayList<>();
            PCB actual = inicio;
            while (true) {
                camino.add(actual);
                Integer recurso = esperando.get(actual);
                if (recurso == null) break;                
                PCB dueno = duenos[recurso];
                if (dueno == null) break;
                if (dueno == inicio) return camino;        
                if (camino.contains(dueno)) break;         
                actual = dueno;
            }
        }
        return new ArrayList<>();
    }


    public boolean reportarDeadlock() {
        List<PCB> ciclo = detectarDeadlock();
        if (ciclo.isEmpty()) {
            System.out.println("  [DEADLOCK] No se detectó ningún interbloqueo.");
            return false;
        }
        System.out.println("  [DEADLOCK] ¡ALERTA! Espera circular detectada:");
        for (PCB p : ciclo) {
            int quiere = esperando.get(p);
            System.out.println("      P" + p.getPid() + " espera R" + quiere
                    + ", que tiene P" + duenos[quiere].getPid());
        }
        return true;
    }


    public void forzarDeadlock(PCB a, PCB b) {
        System.out.println("=== FORZANDO DEADLOCK ===");
        solicitar(a, 1);  
        solicitar(b, 2);   
        solicitar(a, 2);   
        solicitar(b, 1);  
    }

    public void abortar(PCB pcb) {
        boolean tieneAlgo = esperando.containsKey(pcb);
        for (int r = 1; r <= NUM_RECURSOS; r++) {
            if (duenos[r] == pcb) tieneAlgo = true;
        }
        if (!tieneAlgo) {
            throw new IllegalStateException("P" + pcb.getPid() + " no tiene ni espera recursos");
        }
        System.out.println("  [ABORTAR] Se aborta a la fuerza el proceso P" + pcb.getPid());
        esperando.remove(pcb);
        pcb.setEstado(PCB.Estado.TERMINADO);
        liberarRecursos(pcb);
    }

    public void liberarRecursos(PCB pcb) {
        for (int r = 1; r <= NUM_RECURSOS; r++) {
            if (duenos[r] == pcb) {
                duenos[r] = null;
                System.out.println("  [RECURSO] P" + pcb.getPid() + " libera R" + r);
                PCB siguiente = null;
                for (Map.Entry<PCB, Integer> e : esperando.entrySet()) {
                    if (e.getValue() == r) { siguiente = e.getKey(); break; }
                }
                if (siguiente != null) {
                    esperando.remove(siguiente);
                    duenos[r] = siguiente;
                    siguiente.setEstado(PCB.Estado.LISTO);
                    System.out.println("  [RECURSO] P" + siguiente.getPid() + " despierta y recibe R" + r);
                }
            }
        }
    }
    public void mostrarEstado() {
        System.out.println("=== ESTADO DE RECURSOS ===");
        for (int r = 1; r <= NUM_RECURSOS; r++) {
            System.out.println("  R" + r + ": " + (duenos[r] == null ? "LIBRE" : "P" + duenos[r].getPid()));
        }
        for (Map.Entry<PCB, Integer> e : esperando.entrySet()) {
            System.out.println("  P" + e.getKey().getPid() + " BLOQUEADO esperando R" + e.getValue());
        }
    }
}