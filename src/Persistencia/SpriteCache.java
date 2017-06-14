/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistencia;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;

public class SpriteCache implements Dominio.Interficie.ISpriteCache {

    private HashMap sprites;

    public SpriteCache(){
        sprites = new HashMap();
    }
    
    public BufferedImage getSprite(String nom) {
        BufferedImage img = (BufferedImage) sprites.get(nom);
        if (img == null) {
            img = loadImage("Recursos/" + nom);
            sprites.put(nom, img);
        }
        return img;
    }

    private BufferedImage loadImage(String nombre) {
        URL url = null;
        try {
            url = getClass().getClassLoader().getResource(nombre);
            return ImageIO.read(url);
        } catch (Exception e) {
            System.out.println("Error al cargar la imagen " + nombre + " de " + url + "\n " + e.getMessage());
            System.exit(0);
            return null;
        }
    }
}
