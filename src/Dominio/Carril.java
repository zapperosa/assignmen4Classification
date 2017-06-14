/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Dominio;

import java.util.ArrayList;

public class Carril {
    private String nombre;
    private int x;
    private ArrayList<NoTripulado> noTripulados;

    public Carril(String nombre, int x){
        this.nombre = nombre;
        this.x = x;
        this.noTripulados = new ArrayList<NoTripulado>();
    }

    public String getNombre() {
        return nombre;
    }

    public int getX() {
        return x;
    }

    public ArrayList<NoTripulado> getNoTripulados() {
        return noTripulados;
    }
    
    public NoTripulado addCocheNoTripulado(){
        NoTripulado np = new NoTripulado();
        np.setCarrilDeCirculacion(this);
        noTripulados.add(np);
        return np;
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof String){
            return this.nombre.equals(o);
        }
        return false;
    }
    
    
}
