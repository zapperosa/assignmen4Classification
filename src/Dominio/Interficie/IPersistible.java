/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Dominio.Interficie;

import Dominio.Configuracion;
import java.io.Reader;
import weka.core.Instances;

/**
 *
 * @author Daniri
 */
public interface IPersistible {

    public void Desar(String txtFitxer);
    public void Desar(String txtFitxer, String nomFitxer);
    public Reader Carregar();
    public String CarregarTxtDades();

    public Instances CargaArff(String ruta);
    public void DesarARFF(Instances data);
    public void DesarARFF(Instances data, String txtFitxer, String nomFitxer);
    
    public Configuracion CargaConfiguracion() throws Exception ;
    public void GrabaConfiguracion(Configuracion configuracion) throws Exception ;
}
