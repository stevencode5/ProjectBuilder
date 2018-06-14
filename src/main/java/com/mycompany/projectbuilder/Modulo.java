package com.mycompany.projectbuilder;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JSGonzalez
 */
public class Modulo {

    private final String nombre;

    private List<Modulo> nodosDependientes;
    
    private boolean dependenciasCalculadas;

    public Modulo(String nombre) {
        this.nombre = nombre;
        this.nodosDependientes = new ArrayList<>();
    }

    public void agregarModuloDependiente(Modulo moduloDependiente) {
        this.nodosDependientes.add(moduloDependiente);
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

    public boolean isDependenciasCalculadas() {
        return dependenciasCalculadas;
    }

    public void setDependenciasCalculadas(boolean dependenciasCalculadas) {
        this.dependenciasCalculadas = dependenciasCalculadas;
    }
    
    @Override
    public String toString() {
        return this.nombre;
    }

}
