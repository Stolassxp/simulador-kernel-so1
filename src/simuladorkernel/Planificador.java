package simuladorkernel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Planificador {

    public static final int QUANTUM = 3;

    private final Memoria memoria;
    private final Queue<PCB> colaListos = new LinkedList<>();
    private final List<PCB> terminados = new ArrayList<>();
    private int reloj = 0;               
    private int cambiosContexto = 0;

    public Planificador(Memoria memoria) {
        this.memoria = memoria;
    }

    public void admitir(PCB pcb) {
        memoria.asignar(pcb); 
        pcb.setEstado(PCB.Estado.LISTO);
        colaListos.add(pcb);
        System.out.println("Proceso " + pcb.getPid() + " admitido -> cola de listos");
    }

    public void ejecutar() {
        if (colaListos.isEmpty()) {
            System.out.println("No hay procesos en la cola de listos.");
            return;
        }

        System.out.println("=== INICIO ROUND ROBIN (quantum = " + QUANTUM + ") ===");

        while (!colaListos.isEmpty()) {
            PCB actual = colaListos.poll();  
            if (actual.getEstado() == PCB.Estado.TERMINADO
                    || actual.getEstado() == PCB.Estado.BLOQUEADO) {
                System.out.println("P" + actual.getPid() + " está " + actual.getEstado() + ": se omite");
                continue;
            }
            actual.setEstado(PCB.Estado.EJECUCION);

            int inicio = reloj;
            int usado = actual.ejecutar(QUANTUM); 
            reloj += usado;

            System.out.print("[t=" + inicio + "-" + reloj + "] P" + actual.getPid()
                    + " ejecuta " + usado + " (restante: " + actual.getRafagaRestante() + ")");

            if (actual.estaTerminado()) {
                actual.setEstado(PCB.Estado.TERMINADO);
                memoria.liberar(actual.getPid());
                terminados.add(actual);
                System.out.println(" -> TERMINADO, memoria liberada");
            } else {
              
                actual.setEstado(PCB.Estado.LISTO);
                colaListos.add(actual);
                cambiosContexto++;
                System.out.println(" -> quantum agotado, cambio de contexto, al final de la cola");
            }
        }

        System.out.println("=== FIN ROUND ROBIN (tiempo total: " + reloj
                + ", cambios de contexto: " + cambiosContexto + ") ===");
    }
}