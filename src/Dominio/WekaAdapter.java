/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Dominio;

import Dominio.Interficie.IEscenario;
import Dominio.Interficie.IPersistible;
import Dominio.Interficie.IWeka;
import java.io.File;
import weka.classifiers.*;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.J48graft;
import weka.classifiers.trees.NBTree;
import weka.core.converters.CSVLoader;
import weka.filters.unsupervised.attribute.*;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.unsupervised.attribute.ClassAssigner;

/**
 *
 * @author Daniri
 */
public class WekaAdapter implements IWeka {

    private IPersistible persistible;
    private Instances data;
    private Classifier cModel;
    private FilteredClassifier cModelClasificat;
    private Evaluation eTest;
    // private Instances pepe = new Instances(String txtFitxer);

    public WekaAdapter() {
        try {
            this.persistible = ((IPersistible) Class.forName("Persistencia.Persistible").newInstance());
        } catch (Exception e) {
        }

        FastVector carril = new FastVector();
        carril.addElement(IEscenario.CARRILARCENIZQ);
        carril.addElement(IEscenario.CARRILIZQ);
        carril.addElement(IEscenario.CARRILMED);
        carril.addElement(IEscenario.CARRILDER);
        carril.addElement(IEscenario.CARRILARCENDER);

        FastVector jclass = new FastVector();
        jclass.addElement(Movimiento.RECTO);
        jclass.addElement(Movimiento.IZQUIERDA);
        jclass.addElement(Movimiento.DERECHA);
        //Carril,di,dm,dd,class
        FastVector atts = new FastVector();
        atts.addElement(new Attribute("Carril", carril));
        atts.addElement(new Attribute("di"));
        atts.addElement(new Attribute("dm"));
        atts.addElement(new Attribute("dd"));
        atts.addElement(new Attribute("class", jclass));
        /* atts.addElement(new Attribute("velocitatDI"));
        atts.addElement(new Attribute("velocitatDM"));
        atts.addElement(new Attribute("velocitatDD"));*/

        data = new Instances("Atributos", atts, 0);

        data.setClass(data.attribute(4));

    }

    public String carregaArxiuytotAixo() {
        String aux = "";
        try {

            String txtData = persistible.CarregarTxtDades();
            String[] txtDataArray;

            txtDataArray = txtData.split("\n");

            FastVector atts = new FastVector();
            FastVector carril = new FastVector();
            carril.addElement("CARRILARCENIZQ");
            carril.addElement("CARRILIZQ");
            carril.addElement("CARRIALMED");
            carril.addElement("CARRIALDER");
            carril.addElement("CARRILARCENDER");
            FastVector jclass = new FastVector();
            jclass.addElement("Recte");
            jclass.addElement("Esquerra");
            jclass.addElement("Dreta");
            //Carril,di,dm,dd,class
            atts.addElement(new Attribute("Carril", carril));
            atts.addElement(new Attribute("di"));
            atts.addElement(new Attribute("dm"));
            atts.addElement(new Attribute("dd"));
            atts.addElement(new Attribute("class", jclass));

            data = new Instances("Atributos", atts, 0);

            for (int i = 0; i < txtDataArray.length; i++) {
                if (i != 0) {
                    String[] auxLinia = txtDataArray[i].split(",");
                    Instance intance = new Instance(5);
                    intance.setValue(data.attribute(0), auxLinia[0]);
                    intance.setValue(data.attribute(1), Double.valueOf(auxLinia[1]));
                    intance.setValue(data.attribute(2), Double.valueOf(auxLinia[2]));
                    intance.setValue(data.attribute(3), Double.valueOf(auxLinia[3]));
                    intance.setValue(data.attribute(4), auxLinia[4]);
                    data.add(intance);
                }

            }

            /*    ArffSaver saver = new ArffSaver();
            saver.setInstances(data);
            saver.setFile(new File("c:/pepito.arff"));
            saver.writeBatch();*/


            data.setClass(data.attribute(4));

            cModel = (Classifier) new J48();
            cModel.buildClassifier(data);

            Evaluation eTest = new Evaluation(data);
            eTest.evaluateModel(cModel, data);




            aux = "Sumario:\n";
            aux = aux + eTest.toSummaryString() + "\n";

            // Get the confusion matrix
            double[][] cmMatrix = eTest.confusionMatrix();

            aux = aux + "\n";
            aux = aux + "Confusion Matrix:\n";

            for (int i = 0; i < cmMatrix.length; i++) {
                for (int j = 0; j < cmMatrix[i].length; j++) {
                    aux = aux + (cmMatrix[i][j] + "    ");
                }
                aux = aux + "\n";
            }

            Instance instance = new Instance(4);
            instance.setValue(data.attribute(0), "CARRIALDER");
            instance.setValue(data.attribute(1), 250);
            instance.setValue(data.attribute(2), 250);
            instance.setValue(data.attribute(3), 250);
            //instance.setValue(data.attribute(4), "Recte");
            //data.add(instance);


            instance.setDataset(data);

            double[] fDistribution = cModel.distributionForInstance(instance);

            aux = aux + "\n";
            aux = aux + "Para " + instance.toString() + "\n";
            aux = aux + data.attribute(4).value(0) + ": " + fDistribution[0] + "\n";
            aux = aux + data.attribute(4).value(1) + ": " + fDistribution[1] + "\n";
            aux = aux + data.attribute(4).value(2) + ": " + fDistribution[2] + "\n";
            aux = aux + "\n";

        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return aux;
    }

    public String ObtenerEstadisticaModelo() {
        if (cModel == null && cModelClasificat == null) {
            CargaClasificador();
        }
        // System.out.println(cModel);

        String aux = "";
        try {
            aux += eTest.toClassDetailsString();
            aux = "Detalle de la clase:\n" + aux;
        } catch (Exception e) {
        }

        aux += "Sumario:\n";
        aux = aux + eTest.toSummaryString() + "\n";

        // Get the confusion matrix
        double[][] cmMatrix = eTest.confusionMatrix();

        aux = aux + "\n";
        aux = aux + "Confusion Matrix:\n";

        for (int i = 0; i < cmMatrix.length; i++) {
            for (int j = 0; j < cmMatrix[i].length; j++) {
                aux = aux + (cmMatrix[i][j] + "    ");
            }
            aux = aux + "\n";
        }
        return aux;
    }

    public synchronized void anyadeLinea(String carril, int di, int dm, int dd, String moviment, double velocitatDI, double velocitatDM, double velocitatDD) {
        try {
            Instance intance = new Instance(5);
            intance.setValue(data.attribute(0), carril);
            intance.setValue(data.attribute(1), Double.valueOf(di));
            intance.setValue(data.attribute(2), Double.valueOf(dm));
            intance.setValue(data.attribute(3), Double.valueOf(dd));
            intance.setValue(data.attribute(4), moviment);
            /*    intance.setValue(data.attribute(5), Double.valueOf(velocitatDI));
            intance.setValue(data.attribute(6), Double.valueOf(velocitatDM));
            intance.setValue(data.attribute(7), Double.valueOf(velocitatDD));*/

            data.add(intance);

        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    public void guardaArff() {
        persistible.DesarARFF(data);
    }

    public void guardaArff(String ruta, String nombreArchivo) {
        persistible.DesarARFF(data, ruta, nombreArchivo);
    }

    public void CargaArff(String ruta) {
        data = persistible.CargaArff(ruta);
        data.setClass(data.attribute(4));
    }

    private synchronized void CargaClasificador() {
        try {
            Discretize filtre = new Discretize();
            //   data.setClass(data.attribute(4));

            if (Configuracion.getInstancia().getClasificador().equalsIgnoreCase("bayesnet")) {
                cModel = (Classifier) new BayesNet();
            } else {
                if (Configuracion.getInstancia().getClasificador().equalsIgnoreCase("naivebayes")) {
                    cModel = (Classifier) new NaiveBayes();
                } else {
                    if (Configuracion.getInstancia().getClasificador().equalsIgnoreCase("j48")) {
                        cModel = (Classifier) new J48();
                    } else {
                        if (Configuracion.getInstancia().getClasificador().equalsIgnoreCase("j48graft")) {
                            cModel = (Classifier) new J48graft();
                        } else {
                            if (Configuracion.getInstancia().getClasificador().equalsIgnoreCase("nbtree")) {
                                cModel = (Classifier) new NBTree();
                            } else {
                                if (Configuracion.getInstancia().getClasificador().equalsIgnoreCase("bayesnetTAN")) {
                                    cModelClasificat = new FilteredClassifier();
                                    BayesNet bNetTan = new BayesNet();
                                    String aux[] = new String[3];
                                    aux[0] = "-B 70";
                                    aux[1] = "-M -1.0";
                                    aux[2] = "-R first-last";
                                    filtre.setOptions(aux);
                                    //-B 70 -M -1.0 -R first-last
                                    if (Configuracion.getInstancia().isCrossValidation()) {
                                        weka.classifiers.bayes.net.search.global.TAN tan = new weka.classifiers.bayes.net.search.global.TAN();
                                        bNetTan.setSearchAlgorithm(tan);
                                    } else {
                                        weka.classifiers.bayes.net.search.local.TAN tan = new weka.classifiers.bayes.net.search.local.TAN();
                                        bNetTan.setSearchAlgorithm(tan);
                                    }
                                    cModel = (Classifier) bNetTan;

                                }
                            }
                        }
                    }
                }
            }
            if (!Configuracion.getInstancia().getClasificador().equalsIgnoreCase("bayesnetTAN")) {
                cModel.buildClassifier(data);
                eTest = new Evaluation(data);
                eTest.evaluateModel(cModel, data);
            } else {
                cModelClasificat.setClassifier(cModel);
                cModelClasificat.setFilter(filtre);
                cModelClasificat.buildClassifier(data);
                eTest = new Evaluation(data);
                eTest.evaluateModel(cModelClasificat, data);
            }
            // System.out.println(cModel);

        } catch (Exception e) {
            System.out.print("Error: " + e.getMessage());
        }

    }

    public String Movimineto(String carril, int di, int dm, int dd, double velocitatDI, double velocitatDM, double velocitatDD) {

        String aux = "";
        // Classifier auxCl = null;
        try {
            // auxCl = Classifier.makeCopy(cModel);
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }

        Instance instance = new Instance(5);
        instance.setDataset(data);
        instance.setValue(data.attribute(0), carril);
        instance.setValue(data.attribute(1), di);
        instance.setValue(data.attribute(2), dm);
        instance.setValue(data.attribute(3), dd);
        /*  instance.setValue(data.attribute(5), velocitatDI);
        instance.setValue(data.attribute(6), velocitatDM);
        instance.setValue(data.attribute(7), velocitatDD);*/


        if (cModel == null && cModelClasificat == null) {
            CargaClasificador();
        }
        try {
            //System.out.println(cModel.classifyInstance(instance));
            if (!Configuracion.getInstancia().getClasificador().equalsIgnoreCase("bayesnetTAN")) {
                double[] fDistribution = cModel.distributionForInstance(instance);
                //Ponderación, se ignora si es mas pequeño

                aux = data.attribute(4).value(0); //Recto

                if (fDistribution[1] > fDistribution[0] && fDistribution[1] > fDistribution[2]) {
                    if (fDistribution[1] > Configuracion.getInstancia().getPesoDistIzquierda()) {
                        //    System.out.println(Configuracion.getInstancia().getPesoDistIzquierda());
                        aux = data.attribute(4).value(1);
                        //   System.out.println("Esquerra: " + fDistribution[1]);
                    }
                }

                if (fDistribution[2] > fDistribution[0] && fDistribution[2] > fDistribution[1]) {
                    if (fDistribution[2] > Configuracion.getInstancia().getPesoDistDerecha()) {
                        aux = data.attribute(4).value(2);
                        // System.out.println("Dreta: " + fDistribution[2]);
                    }
                }
            }else{
                double[] fDistribution = cModelClasificat.distributionForInstance(instance);
                //Ponderación, se ignora si es mas pequeño

                aux = data.attribute(4).value(0); //Recto

                if (fDistribution[1] > fDistribution[0] && fDistribution[1] > fDistribution[2]) {
                    if (fDistribution[1] > Configuracion.getInstancia().getPesoDistIzquierda()) {
                        //    System.out.println(Configuracion.getInstancia().getPesoDistIzquierda());
                        aux = data.attribute(4).value(1);
                        //   System.out.println("Esquerra: " + fDistribution[1]);
                    }
                }

                if (fDistribution[2] > fDistribution[0] && fDistribution[2] > fDistribution[1]) {
                    if (fDistribution[2] > Configuracion.getInstancia().getPesoDistDerecha()) {
                        aux = data.attribute(4).value(2);
                        // System.out.println("Dreta: " + fDistribution[2]);
                    }
                }
            }


        } catch (Exception e) {
            System.out.print(e.getMessage());
        }

        return aux;
    }
}
