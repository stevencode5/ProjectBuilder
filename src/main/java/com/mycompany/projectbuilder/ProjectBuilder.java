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
import org.primefaces.event.TransferEvent;

/**
 *
 * @author JSGonzalez
 */
@Named(value = "projectBuilder")
@ManagedBean
public class ProjectBuilder implements Serializable {

    private AdministradorModulos administradorModulos;

    private List<Modulo> modulos;
    
    private DualListModel<Modulo> modulosDual;

    private String directorioRaiz;

    private int tabindex;

    @PostConstruct
    public void init() {
        this.directorioRaiz = "/home/shernandez/Desktop/Steven/c4u/";
        administradorModulos = new AdministradorModulos();
        cargarModulos();
    }

    public void cargarModulos() {
        this.modulos = administradorModulos.cargarModulos(this.directorioRaiz);
        this.modulosDual = new DualListModel<>(modulos, new ArrayList<Modulo>());
    }

    public void construirModulos() {        
        administradorModulos.construirModulos(this.modulosDual.getTarget(), this.directorioRaiz);
        addMessage("Modulos Construidos !!");
    }

    public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void transferirModulos(TransferEvent event) {
        List<Modulo> modulosSeleccionados = (List<Modulo>) event.getItems();
        for (Modulo modulo : modulosSeleccionados) {
            agregarModulosDependientes(modulo);
        }
    }

    private void agregarModulosDependientes(Modulo modulo) {
        administradorModulos.llenarModulosDependientes(modulo, modulos, this.directorioRaiz);
        agregarModulo(modulo);
    }

    private void agregarModulo(Modulo modulo) {
        if (!this.modulosDual.getTarget().contains(modulo)) {
            this.modulosDual.getTarget().add(modulo);
        }
        for (Modulo moduloHijo : modulo.getNodosDependientes()) {
            agregarModulo(moduloHijo);
        }
    }
        
    public List<Modulo> getModulos() {
        return modulos;
    }

    public void setModulos(List<Modulo> modulos) {
        this.modulos = modulos;
    }

    public DualListModel<Modulo> getModulosDual() {
        return modulosDual;
    }

    public void setModulosDual(DualListModel<Modulo> modulosDual) {
        this.modulosDual = modulosDual;
    }
    
    public String getDirectorioRaiz() {
        return directorioRaiz;
    }

    public void setDirectorioRaiz(String directorioRaiz) {
        this.directorioRaiz = directorioRaiz;
    }

    public AdministradorModulos getAdministradorModulos() {
        return administradorModulos;
    }

    public void setAdministradorModulos(AdministradorModulos administradorModulos) {
        this.administradorModulos = administradorModulos;
    }

    public int getTabindex() {
        return tabindex;
    }

    public void setTabindex(int tabindex) {
        this.tabindex = tabindex;
    }

    public void foo() {
        System.out.println("Selected Tab  ->  " + this.tabindex);
    }

}
