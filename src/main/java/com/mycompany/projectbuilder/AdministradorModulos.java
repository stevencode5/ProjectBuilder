package com.mycompany.projectbuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

}
