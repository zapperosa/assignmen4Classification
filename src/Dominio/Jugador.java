package Dominio;

import Dominio.Interficie.IEscenario;

public class Jugador extends Coche {

    public Jugador() {
        super(12, (IEscenario.LONGITUDENPIXELES / 3) * 2);
        super.setImgagen(Util.getInstancia().getSprite("Jugador.gif"));
    }
}

