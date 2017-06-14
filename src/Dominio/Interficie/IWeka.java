/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Dominio.Interficie;


public interface IWeka {
    public String ObtenerEstadisticaModelo();
    public void CargaArff(String ruta); // Cargo y creo el arbol de decisiones.
    public String Movimineto(String carril, int di, int dm, int dd, double velocitatDI, double velocitatDM, double velocitatDD); // Cargo y creo el arbol de decisiones.

    public void anyadeLinea(String carril, int di, int dm, int dd, String moviment, double velocitatDI, double velocitatDM, double velocitatDD);
    public void guardaArff();
    public void guardaArff(String ruta, String nombreArchivo);
    }
