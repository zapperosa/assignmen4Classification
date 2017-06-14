/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistencia;

import Dominio.Configuracion;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import Dominio.Interficie.IPersistible;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Reader;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import java.nio.CharBuffer;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import weka.core.converters.ArffLoader;

/**
 *
 * @author Daniri
 */
public class Persistible implements IPersistible {

    public Persistible() {
    }

    public void Desar(String txtFitxer) {
        //Desa el txtFitxer a Disc
        try {
            txtFitxer = "Carril,di,dm,dd,class\n" + txtFitxer; // añado la cabecera

            FileWriter fw = new FileWriter("Prueba.csv");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter salida = new PrintWriter(bw);

            salida.print(txtFitxer);
            salida.close();
        } catch (java.io.IOException ioex) {
            System.out.println("Error al crear el Archivo: " + ioex.toString());
        }
    }

    public void Desar(String txtFitxer, String nomFitxer) {
        //Desa el txtFitxer a Disc
        try {
       //     txtFitxer = "Carril,di,dm,dd,class\n" + txtFitxer; // añado la cabecera

            FileWriter fw = new FileWriter(nomFitxer + ".txt");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter salida = new PrintWriter(bw);
            salida.print(txtFitxer);
            salida.close();
        } catch (java.io.IOException ioex) {
            System.out.println("Error al crear el Archivo: " + ioex.toString());
        }
    }

    public Reader Carregar() {
        //Desa el txtFitxer a Disc
        FileReader fr = null;
        try {

            //txtFitxer = "Carril,di,dm,dd,class\n" + txtFitxer; // añado la cabecera

            fr = new FileReader("C:/Documents and Settings/Daniri/Escritorio/20091210/JavaApplication1/p1/minut12.csv");
            BufferedReader br = new BufferedReader(fr);

            CharBuffer cb = null;
            br.read(cb);
            String aux = new String(cb.array());




        } catch (java.io.IOException ioex) {
            System.out.println("Error al crear el Archivo: " + ioex.toString());
        }
        return fr;
    }

    public String CarregarTxtDades() {
        FileReader fr = null;
        String aux = "";
        try {

            //txtFitxer = "Carril,di,dm,dd,class\n" + txtFitxer; // añado la cabecera

            fr = new FileReader("C:/Documents and Settings/Daniri/Escritorio/20091210/JavaApplication1/p1/minut12.csv");
            BufferedReader br = new BufferedReader(fr);

            String linia = "";

            while ((linia = br.readLine()) != null) {
                aux += linia + "\n";
            }

        } catch (java.io.IOException ioex) {
            System.out.println("Error al crear el Archivo: " + ioex.toString());
        }
        return aux;
    }

    public void DesarARFF(Instances data) {
        try {
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);
            saver.setFile(new File(Configuracion.getInstancia().getRutaGrabacionFicheros() + "PartidaFinal.arff"));
            saver.writeBatch();
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    public void DesarARFF(Instances data, String ruta, String nombreArchivo) {
        try {
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);
            if (!ruta.endsWith("/")) {
                ruta = ruta + "/";
            }
            saver.setFile(new File(ruta + nombreArchivo + ".arff"));
            saver.writeBatch();
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    public Instances CargaArff(String ruta) {
        try {

            ArffLoader loader = new ArffLoader();
            if (!ruta.endsWith(".arff")) {
                ruta = ruta + ".arff";
            }
            File f = new File(ruta);
            loader.setFile(f);
            return loader.getDataSet();

        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return null;
    }

    public Configuracion CargaConfiguracion() {
        Configuracion configuracion = Configuracion.getInstancia(); 
        FileReader fr = null;

        SAXBuilder builder = new SAXBuilder(false);
        try {
            fr = new FileReader("configuracion.xml");
            Document document = builder.build(fr);

            // Se da por sentado que no se va a manipular el fichero XML desde un editor, pero si se manipula y ocurre un error, se cargará hasta el error
            // y después se guardará con los datos que haya anteriormente en memoria o con los iniciados por defecto.
            configuracion.setClasificador(document.getRootElement().getChild("Clasificador").getValue());
            configuracion.setEscenarioPredifinido(Boolean.parseBoolean(document.getRootElement().getChild("escenarioPredifinido").getValue()));
            configuracion.setPesoDistDerecha(Double.parseDouble(document.getRootElement().getChild("pesoDistDerecha").getValue()));
            configuracion.setPesoDistIzquierda(Double.parseDouble(document.getRootElement().getChild("pesoDistIzquierda").getValue()));
            configuracion.setEscenarioPredifinido(Boolean.parseBoolean(document.getRootElement().getChild("escenarioPredifinido").getValue()));
            configuracion.setRelentizacion(Integer.parseInt(document.getRootElement().getChild("relentizacion").getValue()));
            configuracion.setRutaGrabacionFicheros(document.getRootElement().getChild("rutaGrabacionFicheros").getValue());
            configuracion.setTiempoGrabacionFicheros(Integer.parseInt(document.getRootElement().getChild("tiempoGrabacionFicheros").getValue()));
            configuracion.setTiempoGrabacionFicherosEstadisticos(Integer.parseInt(document.getRootElement().getChild("tiempoGrabacionFicherosEstadisticos").getValue()));
            configuracion.setSeed(Integer.parseInt(document.getRootElement().getChild("Seed").getValue()));
            configuracion.setCrossValidation(Boolean.parseBoolean(document.getRootElement().getChild("crossValidation").getValue()));
            
            return configuracion;
            
        } catch (Exception e) {
            //Si se produce una exception se devolverá un null
        }
        return null;
    }

    public void GrabaConfiguracion(Configuracion configuracion) throws Exception {

        XMLOutputter xMLOutputter = new XMLOutputter();
        FileOutputStream fileOutputStream = new FileOutputStream("configuracion.xml");

        Element raiz = new Element("configuracion");

        Element clasificador = new Element("Clasificador");
        clasificador.setText(configuracion.getClasificador());

        Element rutaGrabacionFicheros = new Element("rutaGrabacionFicheros");
        rutaGrabacionFicheros.setText(configuracion.getRutaGrabacionFicheros());

        Element pesoDistIzquierda = new Element("pesoDistIzquierda");
        pesoDistIzquierda.setText("" + configuracion.getPesoDistIzquierda());

        Element pesoDistDerecha = new Element("pesoDistDerecha");
        pesoDistDerecha.setText("" + configuracion.getPesoDistDerecha());

        Element escenarioPredifinido = new Element("escenarioPredifinido");
        escenarioPredifinido.setText("" + configuracion.isEscenarioPredifinido());

        Element relentizacion = new Element("relentizacion");
        relentizacion.setText("" + configuracion.getRelentizacion());

        Element tiempoGrabacionFicheros = new Element("tiempoGrabacionFicheros");
        tiempoGrabacionFicheros.setText("" + configuracion.getTiempoGrabacionFicheros());

          Element tiempoGrabacionFicherosEstadisticos = new Element("tiempoGrabacionFicherosEstadisticos");
        tiempoGrabacionFicherosEstadisticos.setText("" + configuracion.getTiempoGrabacionFicherosEstadisticos());

          Element seed = new Element("Seed");
        seed.setText("" + configuracion.getSeed());

         Element crossValidation = new Element("crossValidation");
        crossValidation.setText("" + configuracion.isCrossValidation());

        raiz.addContent(clasificador);
        raiz.addContent(rutaGrabacionFicheros);
        raiz.addContent(pesoDistIzquierda);
        raiz.addContent(pesoDistDerecha);
        raiz.addContent(escenarioPredifinido);
        raiz.addContent(relentizacion);
        raiz.addContent(tiempoGrabacionFicheros);
        raiz.addContent(tiempoGrabacionFicherosEstadisticos);
        raiz.addContent(seed);
        raiz.addContent(crossValidation);

        Document document = new Document(raiz);

        xMLOutputter.output(document, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();

    }
       
}
