package Dominio;

import Aplicacion.ExceptionFinish;
import Dominio.TimerTask.*;
import Dominio.Interficie.*;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

public class Escenario extends Canvas implements IEscenario, KeyListener {

    private double step = 0;
    private Jugador jugador;
    private Carril arcenIzq;
    private Carril carrilIzq;
    private Carril carrilMed;
    private Carril carrilDer;
    private Carril ArcenDer;
    private boolean pausa;
    private long inicioJuegoTotal;
    private BufferStrategy strategy;
    private long tiempoDeJuego;
    private SimpleDateFormat simpleDateFormat;
    private Date tiempoDeJuegoDate;
    private int puntuacion;
    private int numeroColisiones;
    private BufferedImage fondo;
    private int fotogramaPintarFondo;
    private String MensajeSaliente;
    private String MensajeColision;
    private Timer timer;
    private Timer timerColision;
    private Timer timerTicks;
    private Timer timerMinutos;
    private Timer timerAddCochesCarrilIzquierdo;
    private Timer timerAddCochesCarrilMedio;
    private Timer timerAddCochesCarrilDerecho;
    private TimerTaskMensaje timerTaskMensaje;
    private TimerTaskColision timerTaskColision;
    private TimerTaskTick timerTaskTick;
    private IPersistible persistible;
    private IWeka weka;
    private long tiempoEnArcenIzquierdo;
    private long tiempoEnArcenDerecho;
    private long tiempoEnCarrilIzquierdo;
    private long tiempoEnCarrilMedio;
    private long tiempoEnCarrilDerecho;
    private boolean juegoActivo;
    private boolean juegoHumano;

    public Escenario(JPanel javaPanel, IWeka weka) {

        setBounds(0, 0, javaPanel.getWidth(), javaPanel.getHeight());

        javaPanel.add(this);
        javaPanel.validate();

        addKeyListener(this);
        requestFocus();

        this.arcenIzq = new Carril(IEscenario.CARRILARCENIZQ, IEscenario.XARCENIZQ);
        this.carrilIzq = new Carril(IEscenario.CARRILIZQ, IEscenario.XCARRILIZQ);
        this.carrilMed = new Carril(IEscenario.CARRILMED, IEscenario.XCARRIALMED);
        this.carrilDer = new Carril(IEscenario.CARRILDER, IEscenario.XCARRIALDER);
        this.ArcenDer = new Carril(IEscenario.CARRILARCENDER, IEscenario.XARCENDER);

        this.pausa = false;

        this.jugador = new Jugador();

        jugador.setCarrilDeCirculacion(carrilMed);

        createBufferStrategy(2);
        this.strategy = getBufferStrategy();

        this.numeroColisiones = 0;
        this.fotogramaPintarFondo = 0;

        this.MensajeSaliente = "";
        this.MensajeColision = "";

        timer = new Timer();
        timerColision = new Timer();
        timerTicks = new Timer();
        timerTaskTick = new TimerTaskTick(this);

        timerAddCochesCarrilIzquierdo = new Timer();
        timerAddCochesCarrilMedio = new Timer();
        timerAddCochesCarrilDerecho = new Timer();



        try {
            this.persistible = ((IPersistible) Class.forName("Persistencia.Persistible").newInstance());
        } catch (Exception e) {
        }

        this.weka = weka;

        tiempoDeJuegoDate = new Date(0);
        simpleDateFormat = new SimpleDateFormat("mm:ss");

        juegoActivo = true;
    }

    public void setMensajeColision(String MensajeColision) {
        this.MensajeColision = MensajeColision;
    }

    public void setMensajeSaliente(String MensajeSaliente) {
        this.MensajeSaliente = MensajeSaliente;
    }

    private void Movimiento(String movimiento) {
        String auxMoviment = getEstatString(movimiento) + "\n";

        boolean movimientoValido = true;
        if (movimiento.equals(Movimiento.DERECHA)) {
            if (jugador.getCarrilDeCirculacion().getNombre().equals(IEscenario.CARRILARCENIZQ)) {
                jugador.setCarrilDeCirculacion(carrilIzq);
            } else {
                if (jugador.getCarrilDeCirculacion().equals(IEscenario.CARRILIZQ)) {
                    jugador.setCarrilDeCirculacion(carrilMed);
                } else {
                    if (jugador.getCarrilDeCirculacion().equals(IEscenario.CARRILMED)) {
                        jugador.setCarrilDeCirculacion(carrilDer);
                    } else {
                        if (jugador.getCarrilDeCirculacion().equals(IEscenario.CARRILDER)) {
                            jugador.setCarrilDeCirculacion(ArcenDer);
                        } else {
                            if (jugador.getCarrilDeCirculacion().equals(IEscenario.CARRILARCENDER)) {
                                MuestraMensajeSaliente("¡No puedo ir mas a la derecha!");
                                movimientoValido = false;
                            }
                        }
                    }
                }
            }
        }

        if (movimiento.equals(Movimiento.IZQUIERDA)) {
            if (jugador.getCarrilDeCirculacion().equals(IEscenario.CARRILARCENIZQ)) {
                MuestraMensajeSaliente("¡No puedo ir mas a la izquierda!");
                movimientoValido = false;
            } else {
                if (jugador.getCarrilDeCirculacion().equals(IEscenario.CARRILIZQ)) {
                    jugador.setCarrilDeCirculacion(arcenIzq);
                } else {
                    if (jugador.getCarrilDeCirculacion().equals(IEscenario.CARRILMED)) {
                        jugador.setCarrilDeCirculacion(carrilIzq);
                    } else {
                        if (jugador.getCarrilDeCirculacion().equals(IEscenario.CARRILDER)) {
                            jugador.setCarrilDeCirculacion(carrilMed);
                        } else {
                            if (jugador.getCarrilDeCirculacion().equals(IEscenario.CARRILARCENDER)) {
                                jugador.setCarrilDeCirculacion(carrilDer);
                            }
                        }
                    }
                }
            }
        }
        if (movimientoValido && juegoHumano) {
            String[] aux = auxMoviment.split(",");
            getWeka().anyadeLinea(aux[0], Integer.parseInt(aux[1]), Integer.parseInt(aux[2]), Integer.parseInt(aux[3]), movimiento, Double.parseDouble(aux[5]), Double.parseDouble(aux[6]), Double.parseDouble(aux[7]));
        }
    }

    public void Pausa() {
        pausa = !pausa;
    }

    public void addCoche(String carril) {
        NoTripulado np = null;
        if (!pausa) {
            if (carril.equals(IEscenario.CARRILIZQ)) {
                np = carrilIzq.addCocheNoTripulado();
            } else {
                if (carril.equals(IEscenario.CARRILMED)) {
                    np = carrilMed.addCocheNoTripulado();
                } else {
                    if (carril.equals(IEscenario.CARRILDER)) {
                        np = carrilDer.addCocheNoTripulado();
                    }
                }
            }
        }
        // System.out.println("aux.add(new notripuarray(" + step + ", " + carril + "," + np.getVelocidad() + "));");

    }

    public String getEstatString(String movimiento) {
        Carril carrilAux = null;
        int i = 0;
        int distanciaCarrilIzquierdo = 250;
        int distanciaCarrilMedio = 250;
        int distanciaCarrilderecho = 250;
        double VelocidadCarrilIzquierdo = 0;
        double VelocidadCarrilMedio = 0;
        double VelocidadCarrilderecho = 0;
        for (int carr = 0; carr < 3; carr++) {
            switch (carr) {
                case 0:
                    carrilAux = carrilIzq;
                    i = 0;
                    while (i < carrilAux.getNoTripulados().size()) {
                        NoTripulado nt = (NoTripulado) carrilAux.getNoTripulados().get(i);
                        try {
                            if ((Math.abs(nt.getY() - jugador.getY()) * IEscenario.RATIOMETROSPIXELES) < distanciaCarrilIzquierdo) {
                                if ((int) ((jugador.getY() - nt.getY()) * IEscenario.RATIOMETROSPIXELES) > -55) {
                                    distanciaCarrilIzquierdo = (int) ((jugador.getY() - nt.getY()) * IEscenario.RATIOMETROSPIXELES);
                                    VelocidadCarrilIzquierdo = nt.getVelocidad();
                                }
                            }
                            i++;
                        } catch (Exception e) {
                            System.out.print(e.getMessage());
                        }
                    }
                    break;
                case 1:
                    carrilAux = carrilMed;
                    i = 0;
                    while (i < carrilAux.getNoTripulados().size()) {
                        NoTripulado nt = (NoTripulado) carrilAux.getNoTripulados().get(i);
                        if ((Math.abs(nt.getY() - jugador.getY()) * IEscenario.RATIOMETROSPIXELES) < distanciaCarrilMedio) {
                            if ((int) ((jugador.getY() - nt.getY()) * IEscenario.RATIOMETROSPIXELES) > -55) {
                                distanciaCarrilMedio = (int) ((jugador.getY() - nt.getY()) * IEscenario.RATIOMETROSPIXELES);
                                VelocidadCarrilMedio = nt.getVelocidad();
                            }
                        }
                        i++;
                    }
                    break;
                case 2:
                    carrilAux = carrilDer;
                    i = 0;
                    while (i < carrilAux.getNoTripulados().size()) {
                        NoTripulado nt = (NoTripulado) carrilAux.getNoTripulados().get(i);
                        if ((Math.abs(nt.getY() - jugador.getY()) * IEscenario.RATIOMETROSPIXELES) < distanciaCarrilderecho) {
                            if ((int) ((jugador.getY() - nt.getY()) * IEscenario.RATIOMETROSPIXELES) > -55) {
                                distanciaCarrilderecho = (int) ((jugador.getY() - nt.getY()) * IEscenario.RATIOMETROSPIXELES);
                                VelocidadCarrilderecho = nt.getVelocidad();
                            }
                        }
                        i++;
                    }
                    break;
            }
        }

        return "" + jugador.getCarrilDeCirculacion().getNombre() + "," + distanciaCarrilIzquierdo + "," + distanciaCarrilMedio + "," + distanciaCarrilderecho + "," + movimiento + "," + VelocidadCarrilIzquierdo + "," + VelocidadCarrilMedio + "," + VelocidadCarrilderecho;
    }

    private synchronized void MuestraMensajeSaliente(String mensaje) {
        if (timerTaskMensaje != null) {
            timerTaskMensaje.cancel();
            timer.purge();
        }
        timerTaskMensaje = new TimerTaskMensaje(this);
        this.MensajeSaliente = mensaje;
        timer.schedule(timerTaskMensaje, 1500);
    }

    private synchronized void MuestraColision() {
        if (timerTaskColision != null) {
            timerTaskColision.cancel();
            timerColision.purge();
        }
        timerTaskColision = new TimerTaskColision(this);
        this.MensajeColision = "¡HAS COLISIONADO!";
        timerColision.schedule(timerTaskColision, 1500);
    }

    private void ActualizarPosiciones() {
        int i = 0;
        int j = 0;
        Carril carrilAux = null;
        // Aqui actualizamos carril a carril.
        for (int carr = 0; carr < 3; carr++) {
            switch (carr) {
                case 0:
                    carrilAux = carrilIzq;
                    break;
                case 1:
                    carrilAux = carrilMed;
                    break;
                case 2:
                    carrilAux = carrilDer;
                    break;
            }
            i = 0;
            while (i < carrilAux.getNoTripulados().size()) {
                NoTripulado nt = (NoTripulado) carrilAux.getNoTripulados().get(i);
                j = 0;
                while (j < carrilAux.getNoTripulados().size()) {
                    NoTripulado ntAux = (NoTripulado) carrilAux.getNoTripulados().get(j);
                    if (nt != ntAux && nt.getBoundsConMargen().intersects(ntAux.getBoundsConMargen()) && nt.getVelocidad() != ntAux.getVelocidad()) {
                        //Ajustar la velocidad del vehiculo mas rapido al mas lento.
                        if (nt.getVelocidad() > ntAux.getVelocidad()) {
                            nt.setVelocidad(ntAux.getVelocidad());
                        } else {
                            ntAux.setVelocidad(nt.getVelocidad());
                        }
                    }
                    j++;
                }
                i++;
            }
            // Movimientos
            i = 0;
            while (i < carrilAux.getNoTripulados().size()) {
                NoTripulado nt = (NoTripulado) carrilAux.getNoTripulados().get(i);
                if (nt.getY() > IEscenario.LONGITUDENPIXELES) {
                    //puntuacion = puntuacion + nt.getVelocidad() * 2;
                    puntuacion++;
                    carrilAux.getNoTripulados().remove(nt);
                    i--;
                } else {
                    nt.setY(nt.getY() + nt.getVelocidad());
                }
                i++;
            }
        }
    }

    public synchronized IWeka getWeka() {
        return weka;
    }

    private void ComprobarColisiones() {
        Carril carrilAux = null;
        for (int carr = 0; carr < 3; carr++) {
            switch (carr) {
                case 0:
                    carrilAux = carrilIzq;
                    break;
                case 1:
                    carrilAux = carrilMed;
                    break;
                case 2:
                    carrilAux = carrilDer;
                    break;
            }
            int i = 0;
            while (i < carrilAux.getNoTripulados().size()) {
                NoTripulado nt = (NoTripulado) carrilAux.getNoTripulados().get(i);
                if (jugador.getBounds().intersects(nt.getBounds())) {
                    numeroColisiones++;
                    carrilAux.getNoTripulados().remove(nt); //Remuevo el vehiculo que ha colisionado.

                    MuestraColision();
                    i--;
                }
                i++;
            }
        }
    }

    private void Pintar() throws ExceptionFinish {
        int i = 0;
        Carril carrilAux = null;
        Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
        fondo = Util.getInstancia().getSprite("Fondo2.gif");
        g.setPaint(new TexturePaint(fondo, new Rectangle(0, fotogramaPintarFondo, fondo.getWidth(), fondo.getHeight())));
        g.fillRect(0, 0, IEscenario.ANCHURAENPIXELES, IEscenario.LONGITUDENPIXELES);

        // pintem els cotxes
        for (int carr = 0; carr < 3; carr++) {
            switch (carr) {
                case 0:
                    carrilAux = carrilIzq;
                    break;
                case 1:
                    carrilAux = carrilMed;
                    break;
                case 2:
                    carrilAux = carrilDer;
                    break;
            }
            i = 0;
            int x = 0;
            while (i < carrilAux.getNoTripulados().size()) {
                NoTripulado nt = (NoTripulado) carrilAux.getNoTripulados().get(i);
                x = 50;
                if ((int) nt.getImgagen().getWidth(this) != 50) {
                    x = (int) (50 - nt.getImgagen().getWidth(this)) / 2;
                }
                g.drawImage(nt.getImgagen(), carrilAux.getX() + x, (int) nt.getY(), this);
                i++;
            }
        }
        // pintem al jugador
        g.drawImage(jugador.getImgagen(), jugador.getCarrilDeCirculacion().getX(), (int) jugador.getY(), this);
        pintarTextPantalla(g);
        strategy.show();

        //aumentamos el contador del carril del jugador
        if (jugador.getCarrilDeCirculacion().getNombre().equalsIgnoreCase(IEscenario.CARRILARCENIZQ)) {
            tiempoEnArcenIzquierdo++;
        } else {
            if (jugador.getCarrilDeCirculacion().getNombre().equalsIgnoreCase(IEscenario.CARRILIZQ)) {
                tiempoEnCarrilIzquierdo++;
            } else {
                if (jugador.getCarrilDeCirculacion().getNombre().equalsIgnoreCase(IEscenario.CARRILMED)) {
                    tiempoEnCarrilMedio++;
                } else {
                    if (jugador.getCarrilDeCirculacion().getNombre().equalsIgnoreCase(IEscenario.CARRILDER)) {
                        tiempoEnCarrilDerecho++;
                    } else {
                        if (jugador.getCarrilDeCirculacion().getNombre().equalsIgnoreCase(IEscenario.CARRILARCENDER)) {
                            tiempoEnArcenDerecho++;
                        }
                    }
                }
            }
        }
    }

    private void pintarTextPantalla(Graphics2D g) throws ExceptionFinish {
        // pintar la puntuacion
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setPaint(Color.green);
        g.drawString("Puntuacion:", 20, IEscenario.LONGITUDENPIXELES - 30);
        g.setPaint(Color.red);
        g.drawString(puntuacion + "", 145, IEscenario.LONGITUDENPIXELES - 30);
        g.setPaint(Color.green);
        g.drawString("Numero Colisiones: ", 20, 30);
        g.setPaint(Color.red);
        g.drawString(numeroColisiones + "", 225, 30);

        tiempoDeJuegoDate.setTime(System.currentTimeMillis() - inicioJuegoTotal);

        g.setPaint(Color.green);
        g.drawString("Tiempo de Juego: ", 20, 50);
        g.setPaint(Color.red);
        g.drawString(simpleDateFormat.format(tiempoDeJuegoDate) + "", 225, 50);

        // pintar los fps (Frames Por Segundo)
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(Color.white);
        if (tiempoDeJuego > 0) {
            g.drawString(String.valueOf(1000 / tiempoDeJuego) + " fps", IEscenario.ANCHURAENPIXELES - 50, 30);
        } else {
            g.drawString("--- fps", Escenario.ANCHURAENPIXELES - 50, 30);
        }
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        if (!MensajeSaliente.equals("")) {
            g.drawString(MensajeSaliente, (int) ((IEscenario.ANCHURAENPIXELES / 2) - MensajeSaliente.length() * 4), (IEscenario.LONGITUDENPIXELES / 3));
        }
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        if (!MensajeColision.equals("")) {
            g.drawString(MensajeColision, (int) ((IEscenario.ANCHURAENPIXELES / 2) - MensajeColision.length() * 9.5), (IEscenario.LONGITUDENPIXELES / 3) - 20);
        }
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 45));
        if (pausa) {
            g.drawString("PAUSA", (int) ((IEscenario.ANCHURAENPIXELES / 2) - "PAUSA".length() * 18), (IEscenario.LONGITUDENPIXELES / 3) - 20);
        }

    }

    public String DetenerJuego() {
        juegoActivo = false;
        String aux = "";
        long contadorTotalCarriles;
        contadorTotalCarriles = tiempoEnArcenDerecho + tiempoEnArcenIzquierdo + tiempoEnCarrilDerecho + tiempoEnCarrilMedio + tiempoEnCarrilIzquierdo;
        //preparar estadistica WEKA
       // aux = weka.ObtenerEstadisticaModelo();
        aux += "";
        aux += "\n";
        aux += "Estadísticas del juego\n";
        aux += "______________________\n";
        aux += "Tiempo total de juego: " + simpleDateFormat.format(tiempoDeJuegoDate) + "\n";
        aux += "Numero total de colisiones: " + numeroColisiones + "\n";
        if (tiempoEnArcenIzquierdo != 0) {
            aux += "Porcentaje en carril Arcen Izquierdo: " + (int) (((double) tiempoEnArcenIzquierdo / contadorTotalCarriles) * 100) + "\n";
        } else {
            aux += "Porcentaje en carril Arcen Izquierdo: 0\n";
        }
        if (tiempoEnCarrilIzquierdo != 0) {
            aux += "Porcentaje en carril Izquierdo: " + (int) (((double) tiempoEnCarrilIzquierdo / contadorTotalCarriles) * 100) + "\n";
        } else {
            aux += "Porcentaje en carril Izquierdo: 0\n";
        }
        if (tiempoEnCarrilMedio != 0) {
            aux += "Porcentaje en carril del medio: " + (int) (((double) tiempoEnCarrilMedio / contadorTotalCarriles) * 100) + "\n";
        } else {
            aux += "Porcentaje en carril del medio: 0\n";
        }
        if (tiempoEnCarrilDerecho != 0) {
            aux += "Porcentaje en carril derecho: " + (int) (((double) tiempoEnCarrilDerecho / contadorTotalCarriles) * 100) + "\n";
        } else {
            aux += "Porcentaje en carril derecho: 0\n";
        }
        if (tiempoEnArcenDerecho != 0) {
            aux += "Porcentaje en carril Arcen derecho: " + (int) (((double) tiempoEnArcenDerecho / contadorTotalCarriles) * 100) + "\n";
        } else {
            aux += "Porcentaje en carril Arcen derecho: 0\n";
        }
        aux += "Puntuación: " + puntuacion + "\n";

     //   System.out.print(aux);

        timer.cancel();
        timerAddCochesCarrilDerecho.cancel();
        timerAddCochesCarrilIzquierdo.cancel();
        timerAddCochesCarrilMedio.cancel();
        timerColision.cancel();
        if (timerMinutos != null) {
            timerMinutos.cancel();
        }
        timerTicks.cancel();

        return aux;
    }

    public void Juego(boolean humano) throws ExceptionFinish {
        juegoHumano = humano;
        juegoActivo = true;
        tiempoDeJuego = 1000;
        inicioJuegoTotal = System.currentTimeMillis();
        timerTicks.scheduleAtFixedRate(timerTaskTick, 1000, 1000);

        if (humano) {
            timerMinutos = new Timer();
            TimerTask grabaCadaMinut = new TimerTaskGrabacionFicheros(this);
            timerMinutos.scheduleAtFixedRate(grabaCadaMinut, Configuracion.getInstancia().getTiempoGrabacionFicheros() * 1000, Configuracion.getInstancia().getTiempoGrabacionFicheros() * 1000);
        } else {
       /*     Timer tt = new Timer();
           TimerTaskNoHumano ttnh = new TimerTaskNoHumano(this);
           tt.scheduleAtFixedRate(ttnh, 2000, 300);*/
        }

        TimerTaskADDCochesCarrilDrecho ttacd = new TimerTaskADDCochesCarrilDrecho(this);
        TimerTaskADDCochesCarrilIzquierdo ttaci = new TimerTaskADDCochesCarrilIzquierdo(this);
        TimerTaskADDCochesCarrilMedio ttacm = new TimerTaskADDCochesCarrilMedio(this);

        if (Configuracion.getInstancia().isEscenarioPredifinido()) {
            timerAddCochesCarrilDerecho.scheduleAtFixedRate(ttacd, 4000, 4000);
            timerAddCochesCarrilIzquierdo.scheduleAtFixedRate(ttaci, 4000, 4000);
            timerAddCochesCarrilMedio.scheduleAtFixedRate(ttacm, 2000, 4000);
        } else {
            timerAddCochesCarrilDerecho.scheduleAtFixedRate(ttacd, 500, Util.getInstancia().getRandomConduccio(3000) + 3000);
            timerAddCochesCarrilIzquierdo.scheduleAtFixedRate(ttaci, 500, Util.getInstancia().getRandomConduccio(3000) + 3800);
            timerAddCochesCarrilMedio.scheduleAtFixedRate(ttacm, 500, Util.getInstancia().getRandomConduccio(3000) + 1500);
        }
        //  ArrayList<notripuarray> auxnotripuarray = cargaCochesParaSeed(Configuracion.getInstancia().getSeed());
        while (juegoActivo) {
            if (isVisible() && !pausa) {
            /*   if (step > 5100) {
                    throw new Aplicacion.ExceptionFinish();
                }
                ArrayList<notripuarray> coches = ObtenerCochesStep(step, auxnotripuarray);
                if (!coches.isEmpty()) {
                    for (Iterator<notripuarray> it = coches.iterator(); it.hasNext();) {
                        notripuarray object = it.next();
                        if (object.getCarril().equals(IEscenario.CARRILIZQ)) {
                            NoTripulado npAux = new NoTripulado(object.getVelocidad());
                            npAux.setCarrilDeCirculacion(carrilIzq);
                            carrilIzq.getNoTripulados().add(npAux);
                        }
                        if (object.getCarril().equals(IEscenario.CARRILDER)) {
                            NoTripulado npAux = new NoTripulado(object.getVelocidad());
                            npAux.setCarrilDeCirculacion(carrilDer);
                            carrilDer.getNoTripulados().add(npAux);
                        }
                        if (object.getCarril().equals(IEscenario.CARRILMED)) {
                            NoTripulado npAux = new NoTripulado(object.getVelocidad());
                            npAux.setCarrilDeCirculacion(carrilMed);
                            carrilMed.getNoTripulados().add(npAux);
                        }
                    }
                }*/

                fotogramaPintarFondo = fotogramaPintarFondo + 20;
                long inicioJuego = System.currentTimeMillis();
                step++;
                ActualizarPosiciones();
                ComprobarColisiones();
                Pintar();
                if (jugador.getCarrilDeCirculacion().getNombre().equalsIgnoreCase(IEscenario.CARRILARCENIZQ)) {
                    tiempoEnArcenIzquierdo++;
                } else {
                    if (jugador.getCarrilDeCirculacion().getNombre().equalsIgnoreCase(IEscenario.CARRILIZQ)) {
                        tiempoEnCarrilIzquierdo++;
                    } else {
                        if (jugador.getCarrilDeCirculacion().getNombre().equalsIgnoreCase(IEscenario.CARRILMED)) {
                            tiempoEnCarrilMedio++;
                        } else {
                            if (jugador.getCarrilDeCirculacion().getNombre().equalsIgnoreCase(IEscenario.CARRILDER)) {
                                tiempoEnCarrilDerecho++;
                            } else {
                                if (jugador.getCarrilDeCirculacion().getNombre().equalsIgnoreCase(IEscenario.CARRILARCENDER)) {
                                    tiempoEnArcenDerecho++;
                                }
                            }
                        }
                    }
                }


                tiempoDeJuego = System.currentTimeMillis() - inicioJuego;
                if (Configuracion.getInstancia().getRelentizacion() > 0) {
                    try {
                        Thread.sleep(Configuracion.getInstancia().getRelentizacion());
                    } catch (InterruptedException e) {
                    }
                }
                if (!humano) {
                    String[] aux = this.getEstatString(Movimiento.RECTO).split(",");
                    String p = weka.Movimineto(aux[0], Integer.parseInt(aux[1]), Integer.parseInt(aux[2]), Integer.parseInt(aux[3]), Double.parseDouble(aux[5]), Double.parseDouble(aux[6]), Double.parseDouble(aux[7]));

                    this.Movimiento(p);
                }
            }
        }
    }

    private void GrabaFichero() {
        weka.guardaArff();
    }

    public void GrabaFichero(String nom) {
        weka.guardaArff(Configuracion.getInstancia().getRutaGrabacionFicheros(), nom);
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (juegoHumano) {
                    Movimiento(Movimiento.IZQUIERDA);
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (juegoHumano) {
                    Movimiento(Movimiento.DERECHA);
                }
                break;
            case KeyEvent.VK_P:
                Pausa();
                break;
            case KeyEvent.VK_S:
                GrabaFichero();
                break;
        }
    }

    public void realizaMovimiento() {
        if (!pausa) {
            if (getWeka() != null) {
                String[] aux = getEstatString(Movimiento.RECTO).split(",");
                String p = getWeka().Movimineto(aux[0], Integer.parseInt(aux[1]), Integer.parseInt(aux[2]), Integer.parseInt(aux[3]), Double.parseDouble(aux[5]), Double.parseDouble(aux[6]), Double.parseDouble(aux[7]));
                Movimiento(p);
            }
        }
    }

    public void grabaLinea() {
        if (!pausa) {
            String[] aux = getEstatString(Movimiento.RECTO).split(",");
            getWeka().anyadeLinea(aux[0], Integer.parseInt(aux[1]), Integer.parseInt(aux[2]), Integer.parseInt(aux[3]), aux[4], Double.parseDouble(aux[5]), Double.parseDouble(aux[6]), Double.parseDouble(aux[7]));
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    private ArrayList<notripuarray> ObtenerCochesStep(double dStep, ArrayList<notripuarray> aux) {
        ArrayList<notripuarray> retorno = new ArrayList<notripuarray>();
        for (Iterator<notripuarray> it1 = aux.iterator(); it1.hasNext();) {
            notripuarray object = it1.next();
            if (object.getStep() == dStep) {
                retorno.add(object);
            }
        }
        return retorno;
    }

    private ArrayList<notripuarray> cargaCochesParaSeed(int seed) {
        ArrayList<notripuarray> aux = new ArrayList<notripuarray>();
        String CARRIALDER = "CARRIALDER";
        String CARRIALMED = "CARRIALMED";
        switch (seed) {
            case (0): {
            }
            ;
            break;
            case (1): {
                aux.add(new notripuarray(12.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(12.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(12.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(76.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(140.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(160.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(176.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(205.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(271.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(309.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(336.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(342.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(401.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(459.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(466.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(508.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(531.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(596.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(608.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(662.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(674.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(726.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(757.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(791.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(840.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(856.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(907.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(921.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(987.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1006.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(1052.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1057.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(1118.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1173.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1183.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1207.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(1249.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1314.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1340.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1357.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(1380.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1445.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1508.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1509.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(1511.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1577.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1644.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1660.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(1677.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1710.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1776.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1812.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(1842.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1846.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1909.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1965.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(1975.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2015.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2041.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2108.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2117.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(2174.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2184.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2241.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2270.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(2308.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2354.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2374.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2423.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(2441.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2507.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2524.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2574.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2576.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(2641.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2694.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(2707.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2729.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(2774.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2840.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2864.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2881.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(2907.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2973.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3033.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3034.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(3039.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3106.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3173.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3187.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(3203.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3239.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3306.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3340.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3372.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3372.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(3439.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3492.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3505.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3542.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3572.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3639.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3645.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(3705.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3712.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3772.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3798.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(3839.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3882.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3906.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3951.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3972.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4039.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4053.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4104.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(4105.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4172.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4222.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4239.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4257.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4306.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4372.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4393.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4411.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4439.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4506.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4563.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4564.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4573.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4640.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4707.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4718.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(4733.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4773.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4839.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4870.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4903.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4906.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4973.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(5024.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(5040.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(5073.0, CARRIALDER, 8.0));
            }
            ;
            break;
            case (2): {
                aux.add(new notripuarray(12.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(12.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(12.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(82.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(125.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(127.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(152.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(222.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(239.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(242.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(293.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(353.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(358.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(363.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(434.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(467.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(474.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(504.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(575.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(581.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(590.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(645.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(695.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(706.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(716.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(786.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(809.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(821.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(857.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(922.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(927.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(937.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(997.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1037.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(1053.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(1068.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1139.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1151.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1169.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(1210.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1265.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(1280.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1285.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(1351.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1380.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1402.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(1422.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1493.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1495.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1518.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(1565.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1610.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1636.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(1636.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1707.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1726.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1753.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(1779.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1842.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(1851.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1871.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(1922.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1958.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1988.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(1994.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2066.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2074.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(2106.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(2137.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2190.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2209.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2224.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(2281.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2306.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2342.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(2353.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2422.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2425.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2460.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(2497.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2538.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(2568.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2578.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(2640.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2654.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2696.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(2712.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2770.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(2783.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2813.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(2855.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2886.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2927.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2932.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(2999.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3003.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3050.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(3071.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3119.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3143.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3168.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(3214.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3235.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(3286.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(3287.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3351.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3358.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3403.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3430.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3467.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(3502.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3521.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(3574.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3583.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3640.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(3646.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3700.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3718.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3758.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(3789.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3816.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(3861.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3876.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3933.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3934.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3995.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(4006.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4049.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4078.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4113.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(4150.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4166.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4222.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4231.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4282.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4294.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4350.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(4366.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4399.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4438.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4468.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4510.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4516.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4582.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4586.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4632.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4654.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4705.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(4726.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4749.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4798.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4823.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(4865.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4871.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4942.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4943.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4982.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(5015.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(5060.0, CARRILIZQ, 8.0));
            }
            ;
            break;
            case (3): {
                aux.add(new notripuarray(12.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(12.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(12.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(59.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(106.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(115.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(153.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(190.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(201.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(218.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(248.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(296.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(322.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(343.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(370.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(391.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(425.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(438.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(486.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(529.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(533.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(549.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(581.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(628.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(633.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(675.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(723.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(728.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(736.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(770.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(818.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(840.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(865.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(907.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(913.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(944.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(960.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1008.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1048.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1055.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1087.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(1103.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1150.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1151.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1198.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1245.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1255.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(1267.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(1291.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1339.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1357.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1386.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1434.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1445.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(1462.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1483.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1530.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1567.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1578.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1627.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(1627.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1672.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1675.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1722.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1770.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1776.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1807.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(1818.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1865.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1880.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(1914.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1962.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1985.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1989.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(2010.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2058.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2091.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2106.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2155.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2171.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(2196.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2203.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2251.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2299.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2301.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2348.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2353.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(2396.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2407.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2444.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2493.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2512.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2536.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(2541.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2589.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2617.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2637.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2685.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2718.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(2723.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2734.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2782.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2828.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2830.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2878.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2899.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(2926.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2933.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2975.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3024.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3039.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3072.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3083.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3120.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3145.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3169.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3217.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3251.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(3266.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3266.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(3314.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3357.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3363.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3411.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3449.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3459.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3462.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3508.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3556.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3568.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(3605.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3631.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3653.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3674.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3701.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3750.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3780.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3799.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3815.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(3847.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3885.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(3895.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3944.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3992.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3993.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3998.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(4041.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4090.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4097.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4138.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4181.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4187.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4203.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4235.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4284.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4309.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4332.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4365.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(4381.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4415.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4429.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4478.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4522.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4527.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4548.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4575.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4624.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4628.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4673.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4721.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4732.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(4734.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4770.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4819.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4840.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4867.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4916.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4916.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(4946.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4964.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(5013.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(5053.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(5061.0, CARRIALMED, 7.0));
            }
            ;
            break;
            case (4): {
                aux.add(new notripuarray(12.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(12.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(12.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(61.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(111.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(146.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(161.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(184.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(211.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(261.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(281.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(311.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(358.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(361.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(411.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(416.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(461.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(511.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(531.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(551.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(561.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(611.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(661.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(686.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(705.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(711.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(761.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(811.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(820.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(861.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(878.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(911.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(955.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(961.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1012.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1052.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(1062.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1091.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(1112.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1162.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1212.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1225.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1225.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(1262.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1312.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1360.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1362.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1399.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(1412.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1462.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1496.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(1513.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1563.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1574.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(1614.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1632.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(1665.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1715.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1750.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(1766.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1769.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1817.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1868.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1906.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1919.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1926.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(1969.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2020.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2043.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(2071.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2102.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(2122.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2173.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2180.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2223.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2274.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2278.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(2317.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(2325.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2376.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2427.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2454.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(2455.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(2478.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2529.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2580.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2592.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2631.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2631.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(2682.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2729.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2733.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2783.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2808.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(2835.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2867.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(2886.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2937.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2985.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(2988.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3004.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3039.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3090.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3141.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3142.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3162.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(3192.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3243.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3280.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3293.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3339.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3344.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3396.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3417.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(3447.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3498.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3516.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(3549.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3555.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3600.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3651.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3693.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(3693.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(3702.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3753.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3804.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3830.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3855.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3870.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(3906.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3957.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3968.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4008.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4047.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(4060.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4106.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4111.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4162.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4213.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4224.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4243.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4264.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4315.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4367.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4382.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4402.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4418.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4469.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4520.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4520.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4572.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4580.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(4623.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4658.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4674.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4725.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4757.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(4777.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4796.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4828.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4879.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4931.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4935.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4935.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(4982.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(5033.0, CARRIALMED, 8.0));
            }
            ;
            break;
            case (5): {
                aux.add(new notripuarray(12.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(13.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(13.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(122.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(135.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(174.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(232.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(260.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(338.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(342.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(385.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(452.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(502.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(509.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(563.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(634.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(665.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(673.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(758.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(783.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(829.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(883.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(893.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(993.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(1005.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1008.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1116.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1134.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1158.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(1227.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1260.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1323.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(1339.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1384.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1449.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1488.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(1511.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1561.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1638.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1654.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(1674.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1765.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(1786.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1821.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(1892.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1899.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1989.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(2012.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2020.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2125.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2147.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2156.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(2238.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2275.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2323.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(2351.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2402.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2463.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2491.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(2529.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2576.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2657.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(2658.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(2689.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2784.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2802.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2826.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(2912.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2915.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2993.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(3028.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3039.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3141.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3161.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(3167.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3254.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3294.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3328.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3367.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3422.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(3480.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3496.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3550.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3593.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3663.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(3677.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3706.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3805.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3820.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3831.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3933.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3933.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3999.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(4046.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4061.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4159.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4166.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4188.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4272.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4316.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4334.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(4385.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4444.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4499.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4502.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4572.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4612.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4670.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(4700.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4725.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4827.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4838.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(4838.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4952.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4955.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(5006.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(5065.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(5083.0, CARRIALDER, 7.0));
            }
            ;
            break;
            case (6): {
                aux.add(new notripuarray(12.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(13.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(13.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(126.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(140.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(166.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(242.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(270.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(322.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(357.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(400.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(473.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(478.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(530.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(589.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(633.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(659.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(704.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(789.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(789.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(820.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(919.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(936.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(945.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1050.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(1052.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1103.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1169.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1181.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(1259.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(1285.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1312.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(1402.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1416.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(1443.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(1519.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1574.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1575.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(1636.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1706.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(1733.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1755.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1839.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(1873.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1892.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(1972.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(1991.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2051.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2105.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(2109.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2211.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2228.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2238.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(2347.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2370.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2371.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(2465.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2504.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(2530.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2583.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2636.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(2689.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2702.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2770.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(2820.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2849.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(2903.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(2939.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3009.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3035.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(3057.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3168.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3168.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(3175.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3294.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3301.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3328.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3413.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3434.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(3488.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3531.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3567.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(3647.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3649.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3700.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3768.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3806.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(3833.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3886.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3966.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3966.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4005.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4099.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(4124.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4126.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4233.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(4243.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4286.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4361.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4366.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4446.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4480.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4499.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(4599.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4606.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4633.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4717.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4766.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4766.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(4836.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4899.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4925.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4955.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(5032.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(5074.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(5085.0, CARRIALDER, 8.0));
            }
            ;
            break;
            case (7): {
                aux.add(new notripuarray(12.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(13.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(13.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(94.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(121.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(156.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(177.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(231.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(259.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(301.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(341.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(342.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(425.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(447.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(451.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(508.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(561.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(591.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(592.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(671.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(674.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(737.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(756.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(781.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(839.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(883.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(891.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(922.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1001.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(1005.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1029.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1089.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1112.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(1172.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1175.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1223.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(1255.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1321.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1333.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(1338.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1421.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1443.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(1467.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1506.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1556.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(1590.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1615.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1668.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(1674.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1759.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1763.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1780.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(1844.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1892.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(1912.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1928.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2004.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(2013.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2060.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(2097.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2117.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(2182.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2208.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(2229.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(2267.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2342.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(2351.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2357.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2436.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2454.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(2506.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2521.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2567.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(2606.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2655.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2680.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(2691.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2776.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2792.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(2803.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2860.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2905.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(2945.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2952.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3018.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3030.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3100.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3114.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3130.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(3199.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3242.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3249.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3284.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3355.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(3368.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3397.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(3453.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3467.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(3538.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3546.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3580.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(3623.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3693.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(3695.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3708.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3792.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3805.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(3844.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3877.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3918.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(3962.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3993.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4031.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4047.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4132.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4142.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4143.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(4217.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4256.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(4291.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4302.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4369.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(4387.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4440.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4472.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4482.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(4557.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4589.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4595.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4642.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4708.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(4727.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4738.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4812.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4821.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(4888.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4897.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4934.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4983.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(5037.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(5047.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(5068.0, CARRIALMED, 7.0));
            }
            ;
            break;
            case (8): {
                aux.add(new notripuarray(12.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(12.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(12.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(103.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(105.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(196.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(197.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(199.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(289.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(295.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(383.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(384.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(390.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(476.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(485.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(570.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(572.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(580.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(663.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(676.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(756.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(759.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(771.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(850.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(866.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(943.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(947.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(961.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1037.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1057.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1132.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(1136.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(1154.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1226.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(1250.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1320.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1326.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(1346.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1415.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1442.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1510.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(1516.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(1539.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1603.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1634.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1698.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(1705.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(1731.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1793.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(1829.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1889.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1896.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(1926.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1984.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2023.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2079.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2088.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(2121.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2175.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(2218.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2270.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2280.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(2315.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2366.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(2413.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2461.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2472.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(2510.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2557.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(2607.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2652.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(2664.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(2704.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2747.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(2802.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2843.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(2855.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(2899.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2939.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(2996.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3034.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3047.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3094.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3129.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3191.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3225.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3239.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3289.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3321.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3387.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3417.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(3431.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3484.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3512.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(3582.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3608.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(3623.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3680.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3704.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(3777.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3800.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3816.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(3875.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3896.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(3973.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3991.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4008.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4070.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4087.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4168.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4183.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4201.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(4266.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4279.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4363.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4375.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4393.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(4461.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4471.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4559.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4567.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4586.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(4657.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4663.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4755.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4758.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4778.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(4852.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4854.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4950.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4950.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4971.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(5046.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(5048.0, CARRIALMED, 6.0));
            }
            ;
            break;
            case (9): {
                aux.add(new notripuarray(13.0, IEscenario.CARRILIZQ, 7.0));
                aux.add(new notripuarray(13.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(13.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(74.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(137.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(178.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(189.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(199.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(261.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(324.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(344.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(367.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(386.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(448.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(510.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(511.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(544.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(573.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(635.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(677.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(698.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(722.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(760.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(823.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(842.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(885.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(899.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(947.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1009.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1010.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1073.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1078.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(1136.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1176.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(1198.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1256.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(1261.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1323.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1343.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1386.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1434.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(1448.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1510.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1512.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1575.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1610.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(1635.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1675.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(1698.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(1762.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1791.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(1826.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1845.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(1889.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(1953.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(1972.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(2014.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2016.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2079.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2143.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2152.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(2184.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(2207.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2271.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2334.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(2334.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2353.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2398.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2462.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2515.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(2523.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(2526.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2589.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2653.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2693.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(2697.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(2717.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(2780.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2844.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(2862.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(2877.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(2908.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(2971.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3032.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(3035.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3059.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3099.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3162.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3202.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3226.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3240.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(3290.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3354.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3372.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3417.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3421.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3481.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3542.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(3545.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3603.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(3609.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3673.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3712.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(3737.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(3785.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(3801.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3865.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(3882.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(3928.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(3966.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(3992.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4052.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4056.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4120.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4148.0, CARRILIZQ, 6.0));
                aux.add(new notripuarray(4184.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4223.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4248.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4312.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4330.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4376.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4393.0, CARRIALDER, 7.0));
                aux.add(new notripuarray(4440.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4504.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4512.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4564.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4568.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4632.0, CARRIALMED, 8.0));
                aux.add(new notripuarray(4694.0, CARRILIZQ, 8.0));
                aux.add(new notripuarray(4696.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4734.0, CARRIALDER, 8.0));
                aux.add(new notripuarray(4760.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4824.0, CARRIALMED, 6.0));
                aux.add(new notripuarray(4876.0, CARRILIZQ, 7.0));
                aux.add(new notripuarray(4888.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(4905.0, CARRIALDER, 6.0));
                aux.add(new notripuarray(4952.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(5016.0, CARRIALMED, 7.0));
                aux.add(new notripuarray(5058.0, CARRILIZQ, 8.0));
            }
            ;
            break;
            case (10): {
                aux.add(new notripuarray(12.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(12.0, "CARRILIZQ", 6.0));
                aux.add(new notripuarray(12.0, "CARRIALDER", 6.0));
                aux.add(new notripuarray(88.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(124.0, "CARRIALDER", 7.0));
                aux.add(new notripuarray(152.0, "CARRILIZQ", 7.0));
                aux.add(new notripuarray(163.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(235.0, "CARRIALDER", 7.0));
                aux.add(new notripuarray(240.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(294.0, "CARRILIZQ", 7.0));
                aux.add(new notripuarray(316.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(348.0, "CARRIALDER", 8.0));
                aux.add(new notripuarray(392.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(435.0, "CARRILIZQ", 6.0));
                aux.add(new notripuarray(460.0, "CARRIALDER", 6.0));
                aux.add(new notripuarray(468.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(545.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(572.0, "CARRIALDER", 7.0));
                aux.add(new notripuarray(577.0, "CARRILIZQ", 7.0));
                aux.add(new notripuarray(621.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(684.0, "CARRIALDER", 8.0));
                aux.add(new notripuarray(697.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(717.0, "CARRILIZQ", 6.0));
                aux.add(new notripuarray(773.0, "CARRIALMED", 8.0));
                aux.add(new notripuarray(796.0, "CARRIALDER", 8.0));
                aux.add(new notripuarray(849.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(859.0, "CARRILIZQ", 8.0));
                aux.add(new notripuarray(910.0, "CARRIALDER", 8.0));
                aux.add(new notripuarray(927.0, "CARRIALMED", 8.0));
                aux.add(new notripuarray(1003.0, "CARRILIZQ", 8.0));
                aux.add(new notripuarray(1004.0, "CARRIALMED", 8.0));
                aux.add(new notripuarray(1023.0, "CARRIALDER", 8.0));
                aux.add(new notripuarray(1081.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(1138.0, "CARRIALDER", 8.0));
                aux.add(new notripuarray(1147.0, "CARRILIZQ", 7.0));
                aux.add(new notripuarray(1160.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(1237.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(1252.0, "CARRIALDER", 7.0));
                aux.add(new notripuarray(1290.0, "CARRILIZQ", 6.0));
                aux.add(new notripuarray(1314.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(1365.0, "CARRIALDER", 8.0));
                aux.add(new notripuarray(1390.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(1433.0, "CARRILIZQ", 8.0));
                aux.add(new notripuarray(1468.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(1479.0, "CARRIALDER", 7.0));
                aux.add(new notripuarray(1547.0, "CARRIALMED", 8.0));
                aux.add(new notripuarray(1579.0, "CARRILIZQ", 6.0));
                aux.add(new notripuarray(1596.0, "CARRIALDER", 6.0));
                aux.add(new notripuarray(1626.0, "CARRIALMED", 8.0));
                aux.add(new notripuarray(1705.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(1712.0, "CARRIALDER", 8.0));
                aux.add(new notripuarray(1725.0, "CARRILIZQ", 8.0));
                aux.add(new notripuarray(1784.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(1829.0, "CARRIALDER", 8.0));
                aux.add(new notripuarray(1863.0, "CARRIALMED", 8.0));
                aux.add(new notripuarray(1872.0, "CARRILIZQ", 8.0));
                aux.add(new notripuarray(1941.0, "CARRIALMED", 8.0));
                aux.add(new notripuarray(1943.0, "CARRIALDER", 6.0));
                aux.add(new notripuarray(2016.0, "CARRILIZQ", 8.0));
                aux.add(new notripuarray(2019.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(2059.0, "CARRIALDER", 8.0));
                aux.add(new notripuarray(2097.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(2162.0, "CARRILIZQ", 8.0));
                aux.add(new notripuarray(2174.0, "CARRIALDER", 7.0));
                aux.add(new notripuarray(2176.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(2254.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(2289.0, "CARRIALDER", 7.0));
                aux.add(new notripuarray(2307.0, "CARRILIZQ", 8.0));
                aux.add(new notripuarray(2333.0, "CARRIALMED", 8.0));
                aux.add(new notripuarray(2405.0, "CARRIALDER", 8.0));
                aux.add(new notripuarray(2411.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(2452.0, "CARRILIZQ", 7.0));
                aux.add(new notripuarray(2489.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(2521.0, "CARRIALDER", 8.0));
                aux.add(new notripuarray(2568.0, "CARRIALMED", 8.0));
                aux.add(new notripuarray(2599.0, "CARRILIZQ", 6.0));
                aux.add(new notripuarray(2637.0, "CARRIALDER", 7.0));
                aux.add(new notripuarray(2648.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(2727.0, "CARRIALMED", 8.0));
                aux.add(new notripuarray(2745.0, "CARRILIZQ", 8.0));
                aux.add(new notripuarray(2754.0, "CARRIALDER", 6.0));
                aux.add(new notripuarray(2806.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(2870.0, "CARRIALDER", 6.0));
                aux.add(new notripuarray(2885.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(2892.0, "CARRILIZQ", 7.0));
                aux.add(new notripuarray(2963.0, "CARRIALMED", 8.0));
                aux.add(new notripuarray(2986.0, "CARRIALDER", 8.0));
                aux.add(new notripuarray(3038.0, "CARRILIZQ", 8.0));
                aux.add(new notripuarray(3042.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(3102.0, "CARRIALDER", 7.0));
                aux.add(new notripuarray(3121.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(3185.0, "CARRILIZQ", 8.0));
                aux.add(new notripuarray(3201.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(3219.0, "CARRIALDER", 7.0));
                aux.add(new notripuarray(3280.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(3332.0, "CARRILIZQ", 6.0));
                aux.add(new notripuarray(3336.0, "CARRIALDER", 6.0));
                aux.add(new notripuarray(3356.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(3436.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(3450.0, "CARRIALDER", 8.0));
                aux.add(new notripuarray(3476.0, "CARRILIZQ", 6.0));
                aux.add(new notripuarray(3515.0, "CARRIALMED", 8.0));
                aux.add(new notripuarray(3567.0, "CARRIALDER", 7.0));
                aux.add(new notripuarray(3594.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(3623.0, "CARRILIZQ", 7.0));
                aux.add(new notripuarray(3674.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(3683.0, "CARRIALDER", 8.0));
                aux.add(new notripuarray(3753.0, "CARRIALMED", 8.0));
                aux.add(new notripuarray(3770.0, "CARRILIZQ", 8.0));
                aux.add(new notripuarray(3800.0, "CARRIALDER", 8.0));
                aux.add(new notripuarray(3832.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(3912.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(3917.0, "CARRIALDER", 6.0));
                aux.add(new notripuarray(3918.0, "CARRILIZQ", 7.0));
                aux.add(new notripuarray(3991.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(4034.0, "CARRIALDER", 7.0));
                aux.add(new notripuarray(4065.0, "CARRILIZQ", 6.0));
                aux.add(new notripuarray(4071.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(4150.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(4151.0, "CARRIALDER", 7.0));
                aux.add(new notripuarray(4212.0, "CARRILIZQ", 7.0));
                aux.add(new notripuarray(4230.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(4268.0, "CARRIALDER", 6.0));
                aux.add(new notripuarray(4309.0, "CARRIALMED", 8.0));
                aux.add(new notripuarray(4360.0, "CARRILIZQ", 7.0));
                aux.add(new notripuarray(4385.0, "CARRIALDER", 8.0));
                aux.add(new notripuarray(4388.0, "CARRIALMED", 8.0));
                aux.add(new notripuarray(4468.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(4502.0, "CARRIALDER", 8.0));
                aux.add(new notripuarray(4507.0, "CARRILIZQ", 6.0));
                aux.add(new notripuarray(4547.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(4618.0, "CARRIALDER", 7.0));
                aux.add(new notripuarray(4626.0, "CARRIALMED", 8.0));
                aux.add(new notripuarray(4653.0, "CARRILIZQ", 7.0));
                aux.add(new notripuarray(4705.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(4735.0, "CARRIALDER", 8.0));
                aux.add(new notripuarray(4784.0, "CARRIALMED", 8.0));
                aux.add(new notripuarray(4800.0, "CARRILIZQ", 6.0));
                aux.add(new notripuarray(4851.0, "CARRIALDER", 7.0));
                aux.add(new notripuarray(4863.0, "CARRIALMED", 6.0));
                aux.add(new notripuarray(4943.0, "CARRIALMED", 7.0));
                aux.add(new notripuarray(4947.0, "CARRILIZQ", 8.0));
                aux.add(new notripuarray(4968.0, "CARRIALDER", 8.0));
                aux.add(new notripuarray(5022.0, "CARRIALMED", 8.0));
            }
            ;
            break;


        }
        return aux;
    }

    private class notripuarray {

        private String carril;
        private double step;
        private double velocidad;

        public notripuarray(double step, String carril, double velocidad) {
            this.velocidad = velocidad;
            this.step = step;
            this.carril = carril;
        }

        public String getCarril() {
            return carril;
        }

        public void setCarril(String carril) {
            this.carril = carril;
        }

        public double getStep() {
            return step;
        }

        public void setStep(double step) {
            this.step = step;
        }

        public int getVelocidad() {
            return (int) velocidad;
        }

        public void setVelocidad(double velocidad) {
            this.velocidad = velocidad;
        }
    }
}

