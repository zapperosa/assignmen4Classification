package Dominio;

public class NoTripulado extends Coche {

    public NoTripulado () {
        super(Util.getInstancia().getRandomConduccioVelocitat(3) + 6 ,0);
        int aux = Util.getInstancia().getRandom(21) + 1;
        super.setImgagen(Util.getInstancia().getSprite("" + aux + ".png"));
    }
  public NoTripulado (int velocidad) {
        super(velocidad,0);
        int aux = Util.getInstancia().getRandom(21) + 1;
        super.setImgagen(Util.getInstancia().getSprite("" + aux + ".png"));
    }
}

