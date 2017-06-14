package Dominio;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

public class Coche {

    private Image imgagen;
    private double velocidad;
    private Carril carrilDeCirculacion;
    private double y;

    public Coche(int velocidad, int y) {
        this.velocidad = velocidad;
        this.y = y;
    }

    public double getVelocidad() {
        return this.velocidad;
    }

    public void setVelocidad(double velocidad) {
        this.velocidad = velocidad;
    }

    public Carril getCarrilDeCirculacion() {
        return carrilDeCirculacion;
    }

    public void setCarrilDeCirculacion(Carril carrilDeCirculacion) {
        this.carrilDeCirculacion = carrilDeCirculacion;
    }

    public void setImgagen(Image imgagen) {
        this.imgagen = imgagen;
    }

    public Image getImgagen() {
        return imgagen;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Rectangle getBoundsConMargen() { //Le doy a la altura un 10% mas de largo para que al ajustar las velocidades no sea demasiado cerca del coche que le antecede.
        return new Rectangle(carrilDeCirculacion.getX(), (int)y, imgagen.getWidth(Util.getInstancia().getImageObserver()), (int)(imgagen.getHeight(Util.getInstancia().getImageObserver()) + imgagen.getHeight(Util.getInstancia().getImageObserver()) * 0.15));
    }
    public Rectangle getBounds() { 
        return new Rectangle(carrilDeCirculacion.getX(), (int)y, imgagen.getWidth(Util.getInstancia().getImageObserver()),imgagen.getHeight(Util.getInstancia().getImageObserver()));
    }
}

