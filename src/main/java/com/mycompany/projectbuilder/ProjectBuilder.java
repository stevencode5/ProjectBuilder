package com.mycompany.projectbuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.inject.Named;
import org.primefaces.model.DualListModel;

/**
 *
 * @author JSGonzalez
 */
@Named(value = "projectBuilder")
@ManagedBean
public class ProjectBuilder implements Serializable {

    private DualListModel<String> modulos;

    private String directorioRaiz;

    @PostConstruct
    public void init() {
        directorioRaiz = "";
        cargarModulos();
    }

    public void cargarModulos() {
        AdministradorModulos administrador = new AdministradorModulos();
        List<String> modulosCargados = administrador.cargarModulos(directorioRaiz);
        modulos = new DualListModel<>(modulosCargados, new ArrayList());
    }

    public DualListModel<String> getModulos() {
        return modulos;
    }

    public void setModulos(DualListModel<String> modulos) {
        this.modulos = modulos;
    }

    public String getDirectorioRaiz() {
        return directorioRaiz;
    }

    public void setDirectorioRaiz(String directorioRaiz) {
        this.directorioRaiz = directorioRaiz;
    }

}
