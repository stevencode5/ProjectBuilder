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

    public List<String> cargarModulos(String directorioRaiz) {
        File directorio = new File(directorioRaiz);
        List<String> nodosRaiz = consultarNodosEnPomRaiz(directorio);
        return filtrarNodos(nodosRaiz);
    }

    private List<String> filtrarNodos(List<String> nodos) {
        List<String> nodosLimpios = new ArrayList<>();
        nodos = nodos.stream().filter(nodo -> nodo.contains("modulo")).collect(Collectors.toList());
        for (String nodo : nodos) {
            nodosLimpios.add(nodo.replace("modulos/", ""));
        }
        return nodosLimpios;
    }

    private List<String> consultarNodosEnPomRaiz(File directorioRaiz) {
        File pomRaiz = new File(directorioRaiz.getPath() + "/pom.xml");
        if (pomRaiz.exists()) {
            return calcularDependencias(pomRaiz);
        }
        return new ArrayList();
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

    // <editor-fold defaultstate="collapsed" desc="Métodos para Construir Modulos">
    public void construirModulos(List<String> modulos, String directorioRaiz) {
        for (String modulo : modulos) {
            eliminarDePomRaiz(modulo, directorioRaiz);
        }
        List<String> modulosEar = modulos.stream()
                .filter(modulo -> modulo.contains("negocio") || modulo.contains("persistencia") || modulo.contains("plugin"))
                .collect(Collectors.toList());
        for (String modulo : modulosEar) {
            eliminarDependenciaPorTipoModulo(modulo, "web", directorioRaiz);
        }
        List<String> modulosWeb = modulos.stream().filter(modulo -> modulo.contains("web")).collect(Collectors.toList());
        for (String modulo : modulosWeb) {
            eliminarDependenciaPorTipoModulo(modulo, "web", directorioRaiz);
        }
        List<String> modulosPersistence = modulos.stream().filter(modulo -> modulo.contains("entities")).collect(Collectors.toList());
        for (String modulo : modulosPersistence) {
            eliminarDependenciaPorTipoModulo(modulo, "entities", directorioRaiz);
        }
    }

    private void eliminarDependenciaPorTipoModulo(String modulo, String tipoModulo, String directorioRaiz) {
        switch (tipoModulo) {
            case "negocio":
                eliminarDePomEar(modulo, directorioRaiz);
                break;
            case "web":
                eliminarDePomWeb(modulo, directorioRaiz);
                break;
            case "entities":
                eliminarDePersistence(modulo, directorioRaiz);
                break;
        }
    }

    private void eliminarDePomRaiz(String modulo, String directorioRaiz) {
        File pom = new File(directorioRaiz + "/pom.xml");
        eliminarModuloArchivo(pom, modulo, "module", 0);
    }

    private void eliminarDePomEar(String modulo, String directorioRaiz) {
        File pomEar = new File(directorioRaiz + "/dist/ear/pom.xml");
        String dependenciaEar = modulo.replace("/", "-");
        eliminarModuloArchivo(pomEar, dependenciaEar, "artifactId", 1);
    }

    private void eliminarDePomWeb(String modulo, String directorioRaiz) {
        File pomWeb = new File(directorioRaiz + "/core/web/ui/pom.xml");
        String dependenciaWeb = modulo.replace("/", "-");
        eliminarModuloArchivo(pomWeb, dependenciaWeb, "artifactId", 1);
    }

    private void eliminarDePersistence(String modulo, String directorioRaiz) {
        File persistence = new File(directorioRaiz + "/core/persistencia/servicio/src/main/resources/META-INF/persistence.xml");
        String dependenciaPersistence = modulo.replace("/", "-");
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
