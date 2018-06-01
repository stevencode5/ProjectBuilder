package com.mycompany.projectbuilder;

/**
 *
 * @author JSGonzalez
 */
public enum TipoModulo {

    NEGOCIO("negocio"),
    PERSISTENCIA("persistencia"),
    ENTITIES("entities"),
    PLUGIN("plugin"),
    WEB("web");

    private final String etiquetaModulo;

    TipoModulo(String etiquetaModulo) {
        this.etiquetaModulo = etiquetaModulo;
    }

    public String getEtiquetaModulo() {
        return etiquetaModulo;
    }
}
