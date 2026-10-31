package simuladorkernel;

public class PCB {

    public enum Estado { NUEVO, LISTO, EJECUCION, BLOQUEADO, TERMINADO }

    private final int pid;             
    private Estado estado;              
    private final int tamanoMemoria;    
    private final int rafagaTotal;      
    private int rafagaRestante;         

    public PCB(int pid, int tamanoMemoria, int rafagaTotal) {
        if (pid <= 0) {
            throw new IllegalArgumentException("El PID debe ser mayor que 0");
        }
        if (tamanoMemoria <= 0) {
            throw new IllegalArgumentException("El tamaño en memoria debe ser mayor que 0");
        }
        if (rafagaTotal <= 0) {
            throw new IllegalArgumentException("La ráfaga de CPU debe ser mayor que 0");
        }
        this.pid = pid;
        this.tamanoMemoria = tamanoMemoria;
        this.rafagaTotal = rafagaTotal;
        this.rafagaRestante = rafagaTotal; 
        this.estado = Estado.NUEVO;        
    }

    public int ejecutar(int tiempo) {
        int usado = Math.min(tiempo, rafagaRestante);
        rafagaRestante -= usado;
        return usado;
    }

    public boolean estaTerminado() {
        return rafagaRestante == 0;
    }

    public int getPid() { return pid; }
    public Estado getEstado() { return estado; }
    public int getTamanoMemoria() { return tamanoMemoria; }
    public int getRafagaTotal() { return rafagaTotal; }
    public int getRafagaRestante() { return rafagaRestante; }

    public void setEstado(Estado estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "PCB[PID=" + pid + ", Estado=" + estado
                + ", Memoria=" + tamanoMemoria + "MB"
                + ", Ráfaga=" + rafagaRestante + "/" + rafagaTotal + "]";
    }
}
