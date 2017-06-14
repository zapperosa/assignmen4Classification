/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Dominio.TimerTask;

import Dominio.*;
import java.util.TimerTask;

/**
 *
 * @author Daniri
 */
public class TimerTaskGrabacionFicheros extends TimerTask {
    private Escenario escenario;
    private int contador;

    public TimerTaskGrabacionFicheros(Escenario escenario) {
        this.escenario = escenario;
        contador = 1;
    }

    @Override
    public void run() {
        
        escenario.GrabaFichero("Tiempo" + Configuracion.getInstancia().getTiempoGrabacionFicheros() * contador);
        contador++;
    }

}
