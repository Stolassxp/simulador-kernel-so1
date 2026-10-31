# Simulador de Funciones del Kernel - Sistemas Operativos I

Proyecto final, Universidad Mariano Gálvez de Guatemala.
Autor: [Hanz Alexander Chanchavac Gonzalez]  |  Catedrático: Ing. Julio Gómez

Simulador de consola (CLI) en Java que modela cinco funciones del kernel:

- Gestión de procesos con PCB (PID, estado, memoria, ráfaga total y restante)
- Planificación Round Robin con quantum q = 3
- Gestión de memoria: RAM de 100 MB, First Fit, error Out of Memory
- Exclusión mutua con semáforo binario (wait / signal)
- Deadlock: simulación, detección y resolución abortando un proceso

## Cómo ejecutarlo
1. Requisitos: JDK 8 o superior y NetBeans (probado en NetBeans 30).
2. Abrir la carpeta del proyecto en NetBeans y ejecutar `SimuladorKernel.java`.

## Estructura
- `PCB.java`: bloque de control de proceso
- `Memoria.java`: administrador de memoria
- `Planificador.java`: Round Robin
- `Semaforo.java`: semáforo binario
- `GestorRecursos.java`: recursos y deadlock
- `SimuladorKernel.java`: menú principal
