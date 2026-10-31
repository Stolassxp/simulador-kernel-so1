package simuladorkernel;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class SimuladorKernel {

    static final Scanner sc = new Scanner(System.in);
    static final Memoria ram = new Memoria();
    static final Planificador cpu = new Planificador(ram);
    static final GestorRecursos gestor = new GestorRecursos();
    static final Map<Integer, PCB> procesos = new LinkedHashMap<>();

    public static void main(String[] args) {
        int opcion = -1;
        while (opcion != 0) {
            mostrarMenu();
            opcion = leerEntero("Elige una opción: ");
            System.out.println();
            switch (opcion) {
                case 1: crearProceso(); break;
                case 2: listarProcesos(); break;
                case 3: ram.mostrarMapa(); break;
                case 4: cpu.ejecutar(); break;
                case 5: demoSemaforo(); break;
                case 6: forzarDeadlock(); break;
                case 7: detectarDeadlock(); break;
                case 8: abortarProceso(); break;
                case 0: System.out.println("Cerrando el simulador. ¡Hasta luego!"); break;
                default: System.out.println("Opción inválida: elige un número del menú.");
            }
        }
    }

    static void mostrarMenu() {
        System.out.println("\n===== SIMULADOR DEL KERNEL =====");
        System.out.println("1. Crear proceso (PCB + memoria)");
        System.out.println("2. Listar procesos");
        System.out.println("3. Ver mapa de memoria");
        System.out.println("4. Ejecutar Round Robin (q = " + Planificador.QUANTUM + ")");
        System.out.println("5. Demo del semáforo (condición de carrera)");
        System.out.println("6. Forzar Deadlock");
        System.out.println("7. Detectar Deadlock");
        System.out.println("8. Abortar un proceso (resolver Deadlock)");
        System.out.println("0. Salir");
    }

    static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = sc.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida: escribe un número entero.");
            }
        }
    }

    static void crearProceso() {
        int pid = leerEntero("PID: ");
        if (procesos.containsKey(pid)) {
            System.out.println("Ya existe un proceso con el PID " + pid);
            return;
        }
        int memoria = leerEntero("Tamaño en memoria (MB): ");
        int rafaga = leerEntero("Ráfaga de CPU: ");
        try {
            PCB p = new PCB(pid, memoria, rafaga);
            cpu.admitir(p);          // pide RAM; si no cabe, lanza Out of Memory
            procesos.put(pid, p);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("RECHAZADO -> " + e.getMessage());
        }
    }

    static void listarProcesos() {
        if (procesos.isEmpty()) {
            System.out.println("No hay procesos creados.");
            return;
        }
        for (PCB p : procesos.values()) {
            System.out.println(p);
        }
    }

    static void demoSemaforo() {
        PCB p1 = new PCB(901, 1, 1);
        PCB p2 = new PCB(902, 1, 1);
        int compartida = 0;

        System.out.println("=== SIN SEMAFORO ===");
        int lee1 = compartida;
        int lee2 = compartida;
        System.out.println("P1 lee: " + lee1 + " | P2 lee: " + lee2);
        compartida = lee1 + 1;
        compartida = lee2 + 1;
        System.out.println("Resultado: " + compartida + " (esperado: 2) -> ERROR, se perdió una actualización\n");

        System.out.println("=== CON SEMAFORO ===");
        Semaforo mutex = new Semaforo();
        compartida = 0;
        mutex.wait(p1);
        int a = compartida;
        System.out.println("P1 lee: " + a);
        mutex.wait(p2);
        System.out.println("Estado de P2: " + p2.getEstado());
        compartida = a + 1;
        System.out.println("P1 escribe: " + compartida);
        mutex.signal();
        int b = compartida;
        System.out.println("P2 lee: " + b);
        compartida = b + 1;
        System.out.println("P2 escribe: " + compartida);
        mutex.signal();
        System.out.println("Resultado: " + compartida + " (esperado: 2) -> CORRECTO");
    }

    static PCB pedirProceso(String mensaje) {
        int pid = leerEntero(mensaje);
        PCB p = procesos.get(pid);
        if (p == null) {
            System.out.println("No existe el proceso " + pid);
        } else if (p.getEstado() == PCB.Estado.TERMINADO) {
            System.out.println("El proceso " + pid + " ya terminó");
            return null;
        }
        return p;
    }

    static void forzarDeadlock() {
        System.out.println("Elige dos procesos distintos (A y B) que no hayan terminado.");
        PCB a = pedirProceso("PID del Proceso A: ");
        if (a == null) return;
        PCB b = pedirProceso("PID del Proceso B: ");
        if (b == null) return;
        if (a == b) {
            System.out.println("A y B deben ser procesos distintos.");
            return;
        }
        try {
            gestor.forzarDeadlock(a, b);
            gestor.reportarDeadlock();
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("No se pudo forzar: " + e.getMessage());
        }
    }

    static void detectarDeadlock() {
        gestor.reportarDeadlock();
        gestor.mostrarEstado();
    }

    static void abortarProceso() {
        PCB p = pedirProceso("PID del proceso a abortar: ");
        if (p == null) return;
        try {
            gestor.abortar(p);
            ram.liberar(p.getPid());   // también se libera su memoria RAM
            System.out.println("  [MEMORIA] Se liberó la RAM de P" + p.getPid());
            gestor.reportarDeadlock();
        } catch (IllegalStateException e) {
            System.out.println("No se pudo abortar: " + e.getMessage());
        }
    }

}