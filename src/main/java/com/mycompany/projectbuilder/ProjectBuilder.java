package com.mycompany.projectbuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import org.primefaces.model.DualListModel;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

/**
 *
 * @author JSGonzalez
 */
@Named(value = "projectBuilder")
@ManagedBean
public class ProjectBuilder implements Serializable {

    private AdministradorModulos administradorModulos;
    
    private DualListModel<String> modulos;

    private String directorioRaiz;

    @PostConstruct
    public void init() {
        this.directorioRaiz = "/home/shernandez/Desktop/Steven/c4u/";
        administradorModulos = new AdministradorModulos();
        cargarModulos();
    }

    public void cargarModulos() {
        List<String> modulosCargados = administradorModulos.cargarModulos(this.directorioRaiz);
        this.modulos = new DualListModel<>(modulosCargados, new ArrayList());
    }
    
    public void construirModulos(ActionEvent actionEvent) {
        administradorModulos.construirModulos(this.modulos.getTarget(), this.directorioRaiz);
        addMessage("Modulos Construidos !!");
    }

    public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
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
