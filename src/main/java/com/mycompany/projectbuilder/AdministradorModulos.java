package com.mycompany.projectbuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author JSGonzalez
 */
public class AdministradorModulos {

    public AdministradorModulos() {

    }

    // <editor-fold defaultstate="collapsed" desc="Métodos para Consulta de Modulos">
    public List<Modulo> cargarModulos(String directorioRaiz) {
        File directorio = new File(directorioRaiz);
        List<String> nodosRaiz = consultarNodosEnPomRaiz(directorio);
        List<Modulo> modulosBase = crearModulosPorNombre(nodosRaiz);
        return modulosBase;
    }

    private List<String> consultarNodosEnPomRaiz(File directorioRaiz) {
        File pomRaiz = new File(directorioRaiz.getPath() + "/pom.xml");
        if (pomRaiz.exists()) {
            return calcularDependencias(pomRaiz);
        }
        return new ArrayList();
    }

    private List<Modulo> crearModulosPorNombre(List<String> nombresModulos) {
        List<Modulo> modulosPorNombre = new ArrayList();
        for (String nombreModulo : nombresModulos) {
            modulosPorNombre.add(new Modulo(nombreModulo));
        }
        return modulosPorNombre;
    }

    private List<String> calcularDependencias(File pomRaiz) {
        List<String> dependencias = new ArrayList();
        Document doc = consultarDocumento(pomRaiz);
        NodeList nList = doc.getElementsByTagName("module");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nodo = nList.item(temp);
            dependencias.add(nodo.getTextContent());
        }
        return dependencias;
    }

    private Document consultarDocumento(File archivo) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivo);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            return null;
        }
    }
        
    public void llenarModulosDependientes(Modulo modulo, List<Modulo> modulosBase, String directorioRaiz) {
        if (!modulo.isDependenciasCalculadas()) {
            modulo.setDependenciasCalculadas(true);
            calcularModulosDependientes(modulo, modulosBase, directorioRaiz);
        }
    }
    
    private void calcularModulosDependientes(Modulo modulo, List<Modulo> modulosBase, String directorioRaiz) {
        File directorio = new File(directorioRaiz);
        List<String> modulosDependientes = calcularModulosDenpendientes(modulo, directorio);
        for (String modulosDependiente : modulosDependientes) {
            Modulo moduloHijo = consultarModuloPorNombreEnListaBase(modulosDependiente, modulosBase);
            modulo.agregarModuloDependiente(moduloHijo);
            llenarModulosDependientes(modulo, modulosBase, directorioRaiz);
        }
    }
    
    private Modulo consultarModuloPorNombreEnListaBase(String nombreModulo, List<Modulo> modulosBase) {
        return modulosBase.stream()
                .filter(modulo -> modulo.getNombre().equals(nombreModulo))
                .findAny().get();
    }
    
    private List<String> calcularModulosDenpendientes(Modulo modulo, File directorio) {
        List<String> modulos = calcularDependenciasModuloPorRama(modulo, directorio, "modulos");
        List<String> core = calcularDependenciasModuloPorRama(modulo, directorio, "core");
        modulos.addAll(core);
        return modulos;
    }
    
    private List<String> calcularDependenciasModuloPorRama(Modulo modulo, File directorioRaiz, String rama) {
        List<String> dependenciasModulo = new ArrayList();
        File carpetaModulos = new File(directorioRaiz.getPath() + "/" + rama);
        for (File carpetaModulo : carpetaModulos.listFiles()) {
            for (File moduloInterno : carpetaModulo.listFiles()) {
                if (esModuloDependiente(moduloInterno, modulo)) {
                    dependenciasModulo.add(rama + "/" + moduloInterno.getParentFile().getName() + "/" + moduloInterno.getName());
                }
            }
        }
        dependenciasModulo.removeIf(dependencia -> dependencia.contains("web-ui")); // Excepciones
        return dependenciasModulo;
    }
    
    private boolean esModuloDependiente(File moduloInterno, Modulo modulo) {
        File pom = new File(moduloInterno.getPath() + "/pom.xml");
        if (pom.exists()) {
            return esNodoEnPomModulo(pom, modulo);
        }
        return false;
    }

    private boolean esNodoEnPomModulo(File pom, Modulo modulo) {
        List<Element> dependencias = consultarDependencias(pom);
        for (Element dependencia : dependencias) {
            String dependenciaPom = dependencia.getElementsByTagName("artifactId").item(0).getTextContent();
            String nombreModulo = modulo.getNombre();
            if (compararNombreModulos(dependenciaPom, nombreModulo)) {
                return true;
            }
        }
        return false;
    }

    private boolean compararNombreModulos(String nodoPom, String nodoRaiz) {
        nodoPom = nodoPom.replace("c4u-modulos-", "");
        nodoPom = eliminarPrimerGuion(nodoPom);
        nodoRaiz = nodoRaiz.replace("modulos/", "").replace("/", "-");
        return nodoPom.equals(nodoRaiz);
    }

    private String eliminarPrimerGuion(String cadena) {
        String cadenaSinPrimerGuion = cadena.replaceFirst("-", "");
        if (cadenaSinPrimerGuion.contains("-")) {
            return cadenaSinPrimerGuion;
        } else {
            return cadena;
        }
    }

    private List<Element> consultarDependencias(File pom) {
        List<Element> dependencias = new ArrayList();
        Document doc = consultarDocumento(pom);
        NodeList nList = doc.getElementsByTagName("dependency");
        for (int i = 0; i < nList.getLength(); i++) {
            Element element = (Element) nList.item(i);
            dependencias.add(element);
        }
        return filtrarNodosModulos(dependencias);
    }

    private List<Element> filtrarNodosModulos(List<Element> dependencias) {
        return dependencias.stream()
                .filter(nodo -> nodo.getElementsByTagName("artifactId").item(0).getTextContent()
                        .contains("modulos")).collect(Collectors.toList());
    }
        
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Métodos para Construir Modulos">
    public void construirModulos(List<Modulo> modulos, String directorioRaiz) {
        for (Modulo modulo : modulos) {
            eliminarDePomRaiz(modulo, directorioRaiz);
        }
        List<Modulo> modulosEar = modulos.stream()
                .filter(modulo -> modulo.getNombre().contains(TipoModulo.NEGOCIO.getEtiquetaModulo())
                        || modulo.getNombre().contains(TipoModulo.SERVICIO.getEtiquetaModulo())
                        || modulo.getNombre().contains(TipoModulo.PERSISTENCIA.getEtiquetaModulo())
                        || modulo.getNombre().contains(TipoModulo.ENTITIES.getEtiquetaModulo())
                        || modulo.getNombre().contains(TipoModulo.PLUGIN.getEtiquetaModulo()))
                .collect(Collectors.toList());
        for (Modulo modulo : modulosEar) {
            eliminarDependenciaPorTipoModulo(modulo, TipoModulo.NEGOCIO, directorioRaiz);
        }
        List<Modulo> modulosWeb = modulos.stream()
                .filter(modulo -> modulo.getNombre().contains(TipoModulo.WEB.getEtiquetaModulo()))
                .collect(Collectors.toList());
        for (Modulo modulo : modulosWeb) {
            eliminarDependenciaPorTipoModulo(modulo, TipoModulo.WEB, directorioRaiz);
        }
        List<Modulo> modulosPersistence = modulos.stream()
                .filter(modulo -> modulo.getNombre().contains(TipoModulo.ENTITIES.getEtiquetaModulo()))
                .collect(Collectors.toList());
        for (Modulo modulo : modulosPersistence) {
            eliminarDependenciaPorTipoModulo(modulo, TipoModulo.ENTITIES, directorioRaiz);
        }
    }

    private void eliminarDependenciaPorTipoModulo(Modulo modulo, TipoModulo tipoModulo, String directorioRaiz) {
        switch (tipoModulo) {
            case NEGOCIO:
                eliminarDePomEar(modulo, directorioRaiz);
                break;
            case WEB:
                eliminarDePomWeb(modulo, directorioRaiz);
                break;
            case ENTITIES:
                eliminarDePersistence(modulo, directorioRaiz);
                break;
        }
    }

    private void eliminarDePomRaiz(Modulo modulo, String directorioRaiz) {
        File pom = new File(directorioRaiz + "/pom.xml");
        eliminarModuloArchivo(pom, modulo.getNombre(), "module", 0);
    }

    private void eliminarDePomEar(Modulo modulo, String directorioRaiz) {
        File pomEar = new File(directorioRaiz + "/dist/ear/pom.xml");
        String dependenciaEar = modulo.getNombre().replace("/", "-");
        eliminarModuloArchivo(pomEar, dependenciaEar, "artifactId", 1);
    }

    private void eliminarDePomWeb(Modulo modulo, String directorioRaiz) {
        File pomWeb = new File(directorioRaiz + "/core/web/ui/pom.xml");
        String dependenciaWeb = modulo.getNombre().replace("/", "-");
        eliminarModuloArchivo(pomWeb, dependenciaWeb, "artifactId", 1);
    }

    private void eliminarDePersistence(Modulo modulo, String directorioRaiz) {
        File persistence = new File(directorioRaiz + "/core/persistencia/servicio/src/main/resources/META-INF/persistence.xml");
        String dependenciaPersistence = modulo.getNombre().replace("/", "-");
        eliminarModuloArchivo(persistence, dependenciaPersistence, "jar-file", 0);
    }

    private void eliminarModuloArchivo(File pom, String depedenciaEliminar, String llave, int nivel) {
        Document documento = consultarDocumento(pom);
        eliminarModulo(documento, depedenciaEliminar, llave, nivel);
        modificarDocumento(documento, pom);
    }

    private void eliminarModulo(Document documento, String dependenciaEliminar, String llave, int nivel) {
        NodeList nList = documento.getElementsByTagName(llave);
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nodo = nList.item(temp);
            String lag = nodo.getTextContent();
            if (lag.contains(dependenciaEliminar)) {
                Node nodoEliminar = buscarNodoPadre(nodo, nivel);
                eliminarNodo(nodoEliminar);
            }
        }
    }

    private Node buscarNodoPadre(Node nodo, int nivel) {
        for (int i = 0; i < nivel; i++) {
            nodo = nodo.getParentNode();
        }
        return nodo;
    }

    private void eliminarNodo(Node nodo) {
        NodeList nodosHijos = nodo.getChildNodes();
        for (int i = 0; i < nodosHijos.getLength(); i++) {
            eliminarNodo(nodosHijos.item(i));
        }
        nodo.getParentNode().removeChild(nodo);
    }

    private void modificarDocumento(Document documento, File archivo) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(documento);
            StreamResult result = new StreamResult(archivo);
            transformer.transform(source, result);
        } catch (TransformerException e) {
            System.out.println("Se presento un problema al eliminar el documento");
        }
    }

    // </editor-fold>
}
