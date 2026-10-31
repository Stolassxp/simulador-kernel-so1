package simuladorkernel;

import java.util.ArrayList;
import java.util.List;

public class Memoria {

    public static final int TAMANO_TOTAL = 100; // MB

    private static class Bloque {
        int pid;
        int inicio;
        int tamano;

        Bloque(int pid, int inicio, int tamano) {
            this.pid = pid;
            this.inicio = inicio;
            this.tamano = tamano;
        }
    }

    private final List<Bloque> bloques = new ArrayList<>();

    public void asignar(PCB pcb) {
        int tamano = pcb.getTamanoMemoria();

        if (tamano > TAMANO_TOTAL) {
            throw new IllegalStateException("Out of Memory: el proceso " + pcb.getPid()
                    + " pide " + tamano + " MB y la RAM total es de " + TAMANO_TOTAL + " MB");
        }

        int inicioHueco = 0;
        for (int i = 0; i <= bloques.size(); i++) {
           
            int finHueco = (i < bloques.size()) ? bloques.get(i).inicio : TAMANO_TOTAL;

            if (finHueco - inicioHueco >= tamano) {
                bloques.add(i, new Bloque(pcb.getPid(), inicioHueco, tamano));
                return; 
            }

            if (i < bloques.size()) {
                inicioHueco = bloques.get(i).inicio + bloques.get(i).tamano;
            }
        }

        throw new IllegalStateException("Out of Memory: el proceso " + pcb.getPid()
                + " pide " + tamano + " MB y solo hay " + getLibre() + " MB libres");
    }

    public void liberar(int pid) {
        bloques.removeIf(b -> b.pid == pid);
    }

    public int getOcupado() {
        int total = 0;
        for (Bloque b : bloques) {
            total += b.tamano;
        }
        return total;
    }

    public int getLibre() {
        return TAMANO_TOTAL - getOcupado();
    }

    public void mostrarMapa() {
        System.out.println("=== MAPA DE MEMORIA (" + getOcupado() + "/" + TAMANO_TOTAL + " MB ocupados) ===");
        int cursor = 0;
        for (Bloque b : bloques) {
            if (b.inicio > cursor) {
                System.out.println("  [" + cursor + " - " + (b.inicio - 1) + "] LIBRE (" + (b.inicio - cursor) + " MB)");
            }
            System.out.println("  [" + b.inicio + " - " + (b.inicio + b.tamano - 1) + "] Proceso " + b.pid + " (" + b.tamano + " MB)");
            cursor = b.inicio + b.tamano;
        }
        if (cursor < TAMANO_TOTAL) {
            System.out.println("  [" + cursor + " - " + (TAMANO_TOTAL - 1) + "] LIBRE (" + (TAMANO_TOTAL - cursor) + " MB)");
        }
    }
}