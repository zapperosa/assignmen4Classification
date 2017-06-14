/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Aplicacion;

import Dominio.Escenario;
import Dominio.Util;
import Dominio.Configuracion;
import Dominio.NoTripulado;
import Dominio.Interficie.*;
import java.util.Timer;
import javax.swing.JPanel;
import Dominio.WekaAdapter;

/**
 *
 * @author Daniri
 */
public class ControladorAplicacion {

    private static ControladorAplicacion instancia;
    private Escenario escenario;
    private IPersistible persistible;
    private IWeka weka;

    private ControladorAplicacion() throws Exception {
        weka = new WekaAdapter();
        try {
            this.persistible = ((IPersistible) Class.forName("Persistencia.Persistible").newInstance());
        } catch (Exception e) {
        }
        ObtenerConfiguracion();
    }

    public static synchronized ControladorAplicacion getInstancia() throws Exception {
        if (instancia == null) {
            instancia = new ControladorAplicacion();
        }
        return instancia;
    }

    public void setSeed(int seed){
        Configuracion.getInstancia().setSeed(seed);
    }

    public String[] ObtenerConfiguracion() throws Exception {
        Configuracion configuracion;
        configuracion = persistible.CargaConfiguracion();
        if (configuracion == null) {
            configuracion = Configuracion.getInstancia();
            persistible.GrabaConfiguracion(configuracion);
        }
        /*  clasificador = "J48";
        rutaGrabacionFicheros = "C:/ProyectoFinalCarreraDani/";
        pesoDistIzquierda = 0;
        pesoDistDerecha = 0;
        escenarioPredifinido = false;
        relentizacion = 0;                                                  */
        String[] str = new String[10];
        str[0] = configuracion.getClasificador();
        str[1] = configuracion.getRutaGrabacionFicheros();
        str[2] = "" + configuracion.getPesoDistIzquierda();
        str[3] = "" + configuracion.getPesoDistDerecha();
        str[4] = "" + configuracion.isEscenarioPredifinido();
        str[5] = "" + configuracion.getRelentizacion();
        str[6] = "" + configuracion.getTiempoGrabacionFicheros();
        str[7] = "" + configuracion.getTiempoGrabacionFicherosEstadisticos();
        str[8] = "" + configuracion.getSeed();
        str[9] = "" + configuracion.isCrossValidation();
        
        return str;
    }

    public void grabarConfiguracion(String[] configuracion) throws Exception {
        String clasificador;
        String rutaGrabacionFicheros;
        double pesoDistIzquierda = 0;
        double pesoDistDerecha = 0;
        boolean escenarioPredifinido = false;
        int relentizacion = 0;
        int tiempoGrabacionFicheros = 0;
        int tiempoGrabacionFicherosEstadistica = 0;
        int Seed = 0;
        boolean crossValidation = false;
        String errores = "";

        clasificador = configuracion[0];

        rutaGrabacionFicheros = configuracion[1];

        try {
            pesoDistIzquierda = Double.parseDouble(configuracion[2]) / 100;
        } catch (Exception e) {
            errores += "El valor del peso sobre la distribución a izquierda no es válido.\n";
        }
        try {
            pesoDistDerecha = Double.parseDouble(configuracion[3]) / 100;
        } catch (Exception e) {
            errores += "El valor del peso sobre la distribución a derecha no es válido.\n";
        }

        try {
            escenarioPredifinido = Boolean.parseBoolean(configuracion[4]);
        } catch (Exception e) {
            errores += "El valor del escenario predefinido debe ser 'true' o 'false'.\n";
        }

        try {
            crossValidation = Boolean.parseBoolean(configuracion[9]);
        } catch (Exception e) {
            errores += "El valor del Cross Validation debe ser 'true' o 'false'.\n";
        }

        try {
            relentizacion = Integer.parseInt(configuracion[5]);
            if (relentizacion < 0 || relentizacion > 32000) {
                throw new Exception();
            }
        } catch (Exception e) {
            errores += "El valor del tiempo entre dos frames por segundo no es válido, debe ser un valor positivo entre 0 y 32000.\n";
        }

        try {
            tiempoGrabacionFicheros = Integer.parseInt(configuracion[6]);
            if (tiempoGrabacionFicheros < 0 || tiempoGrabacionFicheros > 32000) {
                throw new Exception();
            }
        } catch (Exception e) {
            errores += "El valor del tiempo para grabar los ficheros no es válido, debe ser un valor positivo entre 0 y 32000.\n";
        }

        try {
            tiempoGrabacionFicherosEstadistica = Integer.parseInt(configuracion[7]);
            if (tiempoGrabacionFicherosEstadistica < 0 || tiempoGrabacionFicherosEstadistica > 32000) {
                throw new Exception();
            }
        } catch (Exception e) {
            errores += "El valor del tiempo para grabar los ficheros estadísticos no es válido, debe ser un valor positivo entre 0 y 32000.\n";
        }

        try {
            Seed = Integer.parseInt(configuracion[8]);
            if (Seed < 0 || Seed > 32000) {
                throw new Exception();
            }
        } catch (Exception e) {
            errores += "El valor de la semilla utilizada no es válido, debe ser un valor positivo entre 0 y 32000.\n";
        }

        if (!errores.equals("")) {
            throw new Exception(errores);
        }
        Configuracion cfg = Configuracion.getInstancia();
        cfg.setClasificador(clasificador);
        cfg.setEscenarioPredifinido(escenarioPredifinido);
        cfg.setPesoDistDerecha(pesoDistDerecha);
        cfg.setPesoDistIzquierda(pesoDistIzquierda);
        cfg.setRelentizacion(relentizacion);
        cfg.setRutaGrabacionFicheros(rutaGrabacionFicheros);
        cfg.setTiempoGrabacionFicheros(tiempoGrabacionFicheros);
        cfg.setTiempoGrabacionFicherosEstadisticos(tiempoGrabacionFicherosEstadistica);
        cfg.setSeed(Seed);
        cfg.setCrossValidation(crossValidation);

        persistible.GrabaConfiguracion(cfg);

    }

    public void iniciaElJoc(boolean humano) {
        if (humano) {
            ThreadJuego t = new ThreadJuego(escenario) {

                public void run() {
                    try{
                          getEscenario().Juego(true);
                    }catch (ExceptionFinish e){
                        System.out.println(getEscenario().DetenerJuego());
                    }
                }
            };
            t.start();
        } else {

            ThreadJuego t = new ThreadJuego(escenario) {

                public void run() {
                    try{
                    getEscenario().Juego(false);
                     }catch (ExceptionFinish e){
                     System.out.println(getEscenario().DetenerJuego());
                    }
                }
            };
            t.start();
        }
    }

    public String DetenerJuego() throws Exception {
        if (escenario == null) {
            throw new Exception("No puede detener el juego porque aun no ha sido iniciado.");
        }
        return escenario.DetenerJuego();

    }

    public void CreaUnJoc(JPanel jp) {
        jp.removeAll();
        this.escenario = new Escenario(jp, weka);
    }

    private class ThreadJuego extends Thread {

        private Escenario escenario;

        public ThreadJuego(Escenario escenario) {
            super();
            this.escenario = escenario;
        }

        public Escenario getEscenario() {
            return escenario;
        }
    }

    public void carregaFitxer(String ruta) throws Exception {
        try {
            weka = new WekaAdapter();
            weka.CargaArff(ruta);
        } catch (Exception e) {
            throw new Exception("El archivo que ha seleccionado no es un archivo ARFF válido.");
        }

    }

    public String probarWeka() {
        WekaAdapter wa = new WekaAdapter();
        return wa.carregaArxiuytotAixo();

    }
}
