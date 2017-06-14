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
public class TimerTaskTick extends TimerTask {

    private Escenario escenario;
 
    public TimerTaskTick(Escenario escenario) {
this.escenario= escenario;
    }

    @Override
    public void run() {
        escenario.grabaLinea();
    }
}
