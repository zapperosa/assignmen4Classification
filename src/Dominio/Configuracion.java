/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Dominio;

/**
 *
 * @author Daniri
 */
public class Configuracion {

    private static Configuracion instancia;
    private String clasificador;
    private String rutaGrabacionFicheros;
    private double pesoDistIzquierda;
    private double pesoDistDerecha;
    private boolean escenarioPredifinido;
    private int relentizacion;
    private int tiempoGrabacionFicheros;
    private int tiempoGrabacionFicherosEstadisticos;
    private int seed;
    private boolean crossValidation;

    private Configuracion() {
        clasificador = "J48";
        rutaGrabacionFicheros = "C:/ProyectoFinalCarreraDani/";
        pesoDistIzquierda = 0;
        pesoDistDerecha = 0;
        escenarioPredifinido = false;
        relentizacion = 0;
        tiempoGrabacionFicheros = 30;
        tiempoGrabacionFicherosEstadisticos=30;
        seed = 0;
        crossValidation = false;
    }

    public static synchronized Configuracion getInstancia() {
        if (instancia == null) {
            instancia = new Configuracion();
        }
        return instancia;
    }

    public int getTiempoGrabacionFicheros() {
        return tiempoGrabacionFicheros;
    }

    public void setTiempoGrabacionFicheros(int tiempoGrabacionFicheros) {
        this.tiempoGrabacionFicheros = tiempoGrabacionFicheros;
    }

    public String getClasificador() {
        return clasificador;
    }

    public void setClasificador(String clasificador) {
        this.clasificador = clasificador;
    }

    public boolean isEscenarioPredifinido() {
        return escenarioPredifinido;
    }

    public void setEscenarioPredifinido(boolean escenarioPredifinido) {
        this.escenarioPredifinido = escenarioPredifinido;
    }

    public double getPesoDistDerecha() {
        return pesoDistDerecha;
    }

    public void setPesoDistDerecha(double pesoDistDerecha) {
        this.pesoDistDerecha = pesoDistDerecha;
    }

    public String getRutaGrabacionFicheros() {
        return rutaGrabacionFicheros;
    }

    public void setRutaGrabacionFicheros(String rutaGrabacionFicheros) {
        this.rutaGrabacionFicheros = rutaGrabacionFicheros;
    }

    public double getPesoDistIzquierda() {
        return pesoDistIzquierda;
    }

    public void setPesoDistIzquierda(double pesoDistIzquierda) {
        this.pesoDistIzquierda = pesoDistIzquierda;
    }

    public int getRelentizacion() {
        return relentizacion;
    }

    public void setRelentizacion(int relentizacion) {
        this.relentizacion = relentizacion;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public int getTiempoGrabacionFicherosEstadisticos() {
        return tiempoGrabacionFicherosEstadisticos;
    }

    public void setTiempoGrabacionFicherosEstadisticos(int tiempoGrabacionFicherosEstadisticos) {
        this.tiempoGrabacionFicherosEstadisticos = tiempoGrabacionFicherosEstadisticos;
    }

    public boolean isCrossValidation() {
        return crossValidation;
    }

    public void setCrossValidation(boolean crossValidation) {
        this.crossValidation = crossValidation;
    }

}
