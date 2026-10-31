package simuladorkernel;

import java.util.LinkedList;
import java.util.Queue;

public class Semaforo {

    private int valor = 1;                              
    private PCB propietario = null;                    
    private final Queue<PCB> colaEspera = new LinkedList<>();


    public boolean wait(PCB pcb) {
        if (valor == 1) {
            valor = 0;
            propietario = pcb;
            System.out.println("  [SEMAFORO] P" + pcb.getPid() + " hizo wait(): ENTRA a la sección crítica");
            return true;
        }
        pcb.setEstado(PCB.Estado.BLOQUEADO);
        colaEspera.add(pcb);
        System.out.println("  [SEMAFORO] P" + pcb.getPid() + " hizo wait(): OCUPADO (lo tiene P"
                + propietario.getPid() + "), P" + pcb.getPid() + " pasa a la cola de espera");
        return false;
    }

    public void signal() {
        if (valor == 1) {
            throw new IllegalStateException("signal() sin wait(): el semáforo ya estaba libre");
        }
        System.out.println("  [SEMAFORO] P" + propietario.getPid() + " hizo signal(): sale de la sección crítica");
        if (colaEspera.isEmpty()) {
            valor = 1;
            propietario = null;
        } else {
            propietario = colaEspera.poll();            // el primero de la fila recibe la llave
            propietario.setEstado(PCB.Estado.LISTO);
            System.out.println("  [SEMAFORO] P" + propietario.getPid() + " despierta y ENTRA a la sección crítica");
        }
    }

    public int getValor() { return valor; }
    public int getTamanoCola() { return colaEspera.size(); }
}