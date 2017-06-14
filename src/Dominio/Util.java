
package Dominio;

import Dominio.Interficie.ISpriteCache;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author Daniri
 */
public class Util {
    private static Util instancia;
    private Random random;
    private Random randomConduccio;
    private Random randomConduccioVelocitat;
    private ImageObserver imageObserver;
    private ISpriteCache spriteCache;
        
    private Util(){
        this.random =new Random(1);
        this.randomConduccioVelocitat = new Random(Configuracion.getInstancia().getSeed());
        this.randomConduccio = new Random(Configuracion.getInstancia().getSeed());
     //   this.random =new Random(aux.getTime());
        imageObserver = new ImageObserver() {
            public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        try {
            this.spriteCache = ((ISpriteCache) Class.forName("Persistencia.SpriteCache").newInstance());
        } catch (Exception e) {
        }
    }
    
    public BufferedImage getSprite(String nom) {
       return spriteCache.getSprite(nom);
    }
    
    public static synchronized Util getInstancia(){
        if (instancia == null){
            instancia = new Util();
        }
        return instancia;
    }
    
    public int getRandom(int numMax){
        return random.nextInt(numMax);
    }

    public int getRandomConduccio(int numMax){
        return randomConduccio.nextInt(numMax);
    }

    public int getRandomConduccioVelocitat(int numMax){
        return randomConduccioVelocitat.nextInt(numMax);
    }
    
    public void ReiniciaContadors(){
         this.randomConduccioVelocitat = new Random(Configuracion.getInstancia().getSeed());
        this.randomConduccio = new Random(Configuracion.getInstancia().getSeed());
    }

    public ImageObserver getImageObserver(){
        return imageObserver;
    }
}
