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
public class TimerTaskColision extends TimerTask {

    private Escenario escenario;

    public TimerTaskColision(Escenario escenario) {
        this.escenario = escenario;
    }
    @Override
    public void run() {
        escenario.setMensajeColision("");
    }
}
