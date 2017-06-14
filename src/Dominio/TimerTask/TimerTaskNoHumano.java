/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Dominio.TimerTask;

import Dominio.Escenario;
import Dominio.Movimiento;
import java.util.TimerTask;

/**
 *
 * @author Daniri
 */
public class TimerTaskNoHumano extends TimerTask {

    private Escenario escenario;
    private boolean pausa;

    public TimerTaskNoHumano(Escenario escenario) {
        this.escenario = escenario;
        pausa = false;
    }

    @Override
    public void run() {
        
                escenario.realizaMovimiento();
            
    }
}