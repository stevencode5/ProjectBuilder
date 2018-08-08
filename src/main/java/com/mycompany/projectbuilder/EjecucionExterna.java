package com.mycompany.projectbuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 *
 * @author JSGonzalez
 */
public class EjecucionExterna {

    public static void main(String args[]) {
        try {
            String archivoDependencias = "/home/shernandez/Desktop/Steven/Projects/c4u/SinProgramacion";
            String directorioRaiz = "/home/shernandez/Desktop/Steven/c4u/";
            AdministradorModulos administradorModulos = new AdministradorModulos();
            InputStream in = new FileInputStream(new File(archivoDependencias));
            administradorModulos.cargarArchivo(in, directorioRaiz);
        } catch (Exception e) {
            System.err.println("Se presento un error al generar archivo en Ejecucion Externa");
            e.printStackTrace();
        }
    }

}
