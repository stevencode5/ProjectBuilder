package com.mycompany.projectbuilder;

import java.util.List;

/**
 *
 * @author JSGonzalez
 */
public class Modulo {

    private final String nombre;

    private List<Modulo> nodosDependientes;

    public Modulo(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Modulo> getNodosDependientes() {
        return nodosDependientes;
    }

    public void setNodosDependientes(List<Modulo> nodosDependientes) {
        this.nodosDependientes = nodosDependientes;
    }
    
    @Override
    public String toString() {
        return this.nombre;
    }

}
