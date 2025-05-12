package KAGO_framework.control;

import KAGO_framework.Config;
import KAGO_framework.view.DrawTool;
//import KAGO_scenario_framework.control.ScenarioController;
import KAGO_framework.view.KagoScene;
import com.google.common.util.concurrent.*;
import kebab_simulator.control.ProgramController;
import KAGO_framework.view.DrawFrame;
import kebab_simulator.control.Wrapper;
import kebab_simulator.model.MapManager;
import kebab_simulator.model.entity.Entity;
import kebab_simulator.model.scene.GameScene;
import kebab_simulator.model.scene.Scene;
import kebab_simulator.model.visual.VisualModel;
import kebab_simulator.utils.TimerUtils;
import kebab_simulator.view.InputManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Diese Klasse kontrolliert die DrawingPanels einer ihr zugewiesenen DrawingFrame.
 * Sie kann verschiedene Objekte erzeugen und den Panels hinzufuegen.
 * Vorgegebene Klasse des Frameworks. Modifikation auf eigene Gefahr.
 */
public class ViewController extends JPanel implements KeyListener, MouseListener, MouseMotionListener {

    private Logger logger = LoggerFactory.getLogger(ViewController.class);

    private DrawFrame drawFrame;
    private ProgramController programController;
    private static ArrayList<Integer> currentlyPressedKeys = new ArrayList<>();;
    private SoundController soundController;
    private DrawTool drawTool;

    private ListeningExecutorService calculationExecutor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(2));
    private ListeningExecutorService backgroundServiceExecutor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    private int threadSleep;
    private boolean requested = false;
    private boolean notChangingInteractables, notChangingDrawables;
    private final AtomicBoolean watchPhysics = new AtomicBoolean(true);

    /**
     * Erzeugt ein Objekt zur Kontrolle des Programmflusses.
     */
    public ViewController() {
        this.programController = new ProgramController(this);
        Scene.initialize(this);
        this.drawTool = new DrawTool();
        this.programController.preStartProgram();
        this.notChangingDrawables = true;
        this.notChangingInteractables = true;
        // Erzeuge Fenster und erste Szene
        this.createWindow();
        // Setzt die Ziel-Zeit zwischen zwei aufeinander folgenden Frames in Millisekunden
        this.threadSleep = 1; //Vernuenftiger Startwert
        if ( Config.INFO_MESSAGES) System.out.println("  > ViewController: Erzeuge ProgramController und starte Spielprozess (Min. dt = "+this.threadSleep+"ms)...");
        if ( Config.INFO_MESSAGES) System.out.println("     > Es wird nun einmalig die Methode startProgram von dem ProgramController-Objekt aufgerufen.");
        if ( Config.INFO_MESSAGES) System.out.println("     > Es wird wiederholend die Methode updateProgram von dem ProgramController-Objekt aufgerufen.");
        if ( Config.INFO_MESSAGES) System.out.println("-------------------------------------------------------------------------------------------------\n");
        if ( Config.INFO_MESSAGES) System.out.println("** Ab hier folgt das Log zum laufenden Programm: **");
        if(kebab_simulator.Config.useSound) {
            this.soundController = new SoundController();
        } else {
            if ( Config.INFO_MESSAGES) System.out.println("** Achtung! Sound deaktiviert => soundController ist NULL (kann in Config geändert werden). **");
        }

        if (!kebab_simulator.Config.SHOW_DEFAULT_WINDOW){
            this.setDrawFrameVisible(false);
            if(Config.INFO_MESSAGES) System.out.println("** Achtung! Standardfenster deaktiviert => wird nicht angezeigt.). **");
        }
        this.startProgram();
    }

    /**
     * Startet das Programm, nachdem Vorarbeiten abgeschlossen sind.
     */
    private void startProgram() {
        try {
            this.startPhysicsEngine();
            this.startBackgroundServices();
            this.startGameEngine();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void startGameEngine() throws InterruptedException {
        this.programController.startProgram();
        this.register(new InputManager(this.programController));
        while (true) {
            TimerUtils.update();
            this.programController.updateProgram(TimerUtils.getDeltaTime());
            Scene.getCurrentScene().update(TimerUtils.getDeltaTime());
            if (this.soundController != null) this.soundController.update(TimerUtils.getDeltaTime());
            repaint();
            Thread.sleep(this.threadSleep);
        }
    }

    private void startPhysicsEngine() {
        this.calculationExecutor.submit(() -> {
            while (true) {
                if (!this.watchPhysics.get()) {
                    Thread.sleep(100);
                    continue;
                }
                Wrapper.getColliderManager().updateBodies(TimerUtils.getDeltaTime());
                Thread.sleep(this.threadSleep);
            }
        });
    }

    private void startBackgroundServices() {
        // Sobald Hintergrundprozesse benötigt werden, wird es auskommentiert
        // Zur Zeit useless
        var service = this.backgroundServiceExecutor.submit(() -> {
            MapManager map = MapManager.importMap("map.json");
            //GameScene.getInstance().setGameMap(map);
        });
        Futures.addCallback(service, new FutureCallback<Object>() {
            @Override
            public void onSuccess(Object result) {}
            @Override
            public void onFailure(Throwable t) {
                logger.error("Error", t);
            }
        }, this.backgroundServiceExecutor);
    }

    private void shutdown() {
        this.calculationExecutor.shutdown();
        try {
            if (!this.calculationExecutor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                this.calculationExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            this.calculationExecutor.shutdownNow();
        }
    }

    public boolean watchPhysics() {
        return this.watchPhysics.get();
    }

    public void setWatchPhyics(boolean flag) {
        this.watchPhysics.set(flag);
    }

    /**
     * Erzeugt das Fenster und die erste Szene, die sofort angezeigt wird.
     */
    private void createWindow(){
        // Berechne Mitte des Bildschirms
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        int x = width / 2;
        int y = height / 2;
        // Berechne die beste obere linke Ecke für das Fenster so, dass es genau mittig erscheint
        x = x - kebab_simulator.Config.WINDOW_WIDTH / 2;
        y = y - kebab_simulator.Config.WINDOW_HEIGHT / 2;
        // Erzeuge die erste Szene
        Scene.open(GameScene.getInstance());
        // Erzeuge ein neues Fenster zum Zeichnen
        this.drawFrame = new DrawFrame(kebab_simulator.Config.WINDOW_TITLE, x, y, kebab_simulator.Config.WINDOW_WIDTH, kebab_simulator.Config.WINDOW_HEIGHT, this);
        this.drawFrame.setResizable(false);
        if (kebab_simulator.Config.WINDOW_FULLSCREEN) {
            this.drawFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            gd.setFullScreenWindow(Window.getWindows()[0]);
        }
        this.drawFrame.setActiveDrawingPanel(this);
        // Übergibt den weiteren Programmfluss an das neue Objekt der Klasse ViewController
        if ( Config.INFO_MESSAGES) System.out.println("  > ViewController: Fenster eingerichtet. Startszene (Index: 0) angelegt.");
    }

    @Override
    public void paintComponent(Graphics g) {
        if(!requested){
            addMouseListener(this);
            addKeyListener(this);
            addMouseMotionListener(this);
            setFocusable(true);
            this.requestFocusInWindow();
            requested = ! requested;
        }
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.drawTool.setGraphics2D(g2d,this);
        if (this.soundController != null) this.soundController.update(TimerUtils.getDeltaTime());
        if (Scene.getCurrentScene() instanceof GameScene) {
            GameScene.getInstance().drawGame(this.drawTool);
        }
        Scene.getCurrentScene().draw(this.drawTool);
    }

    /**
     * Zeichnet und aktualisiert ein neues Objekt in der gewünschten Szene
     * @param d Das zu zeichnende Objekt (Interface Drawable muss implementiert werden)
     */
    public void draw(Drawable d) {
        if (d != null) {
            //SwingUtilities.invokeLater(() -> GameScene.getInstance().getDrawables().add(d));
        }
    }

    /**
     * Fügt ein Objekt, das das Interactable-Interface implementiert zur indizierten Szene hinzu, so
     * dass es auf Events reagiert
     * @param i das gewünschte Objekt
     */
    public void register(Interactable i) {
        if (i != null) {
            //SwingUtilities.invokeLater(() -> GameScene.getInstance().getInteractables().add(i));
        }
    }

    /**
     * Entfernt ein Objekt aus einem DrawingPanel. Die Update- und Draw-Methode des Objekts
     * wird dann nicht mehr aufgerufen.
     * @param d Das zu entfernende Objekt
     */
    public void removeDrawable(Drawable d) {
        if (d != null){
            /*notChangingDrawables = false;
            SwingUtilities.invokeLater(() -> {
                GameScene.getInstance().getDrawables().remove(d);
                notChangingDrawables = true;
            });*/
        }
    }

    /**
     * Entfernt ein Objekt aus einem DrawingPanel. Die Update- und Draw-Methode des Objekts
     * wird dann nicht mehr aufgerufen.
     * @param i Das zu entfernende Objekt
     */
    public void removeInteractable(Interactable i) {
        if (i != null) {
            /*notChangingInteractables = false;
            SwingUtilities.invokeLater(() -> {
                GameScene.getInstance().getInteractables().remove(i);
                notChangingInteractables = true;
            });*/
        }
    }

    /**
     * Diese Methode überprüft, ob die angebene Taste momentan heruntergedrückt ist.
     * @param key Der Tastecode der zu überprüfenden Taste.
     * @return True, falls die entsprechende Taste momentan gedrückt ist, andernfalls false.
     */
    public static boolean isKeyDown(int key){
        return currentlyPressedKeys.contains(key);
    }

    /**
     * Nötig zur Einbindung nativer Java-Fensterelemente
     * @return Liefert das DrawFrame-Objekt zurück (als Schnittstelle zu den JFrame-Methoden)
     */
    public DrawFrame getDrawFrame(){
        return this.drawFrame;
    }

    /**
     * Zeigt das Standardfenster an oder versteckt es.
     * @param b der gewünschte Sichtbarkeitsstatus
     */
    public void setDrawFrameVisible(boolean b){
        drawFrame.setVisible(b);
    }

    /* INTERFACE METHODEN */

    @Override
    public void mouseReleased(MouseEvent e) {
        Iterator<Interactable> iterator = GameScene.getInstance().getInteractables().iterator();
        while (iterator.hasNext() && notChangingInteractables){
            Interactable tmpInteractable = iterator.next();
            tmpInteractable.mouseReleased(e);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Wird momentan nicht unterstützt
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Wird momentan nicht unterstützt
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //programController.mouseClicked(e); entfernt 11.11.21 KNB - Simplifizierung & MVC für ProgramController
        Iterator<Interactable> iterator = GameScene.getInstance().getInteractables().iterator();
        while (iterator.hasNext() && notChangingInteractables){
            Interactable tmpInteractable = iterator.next();
            tmpInteractable.mouseClicked(e);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Iterator<Interactable> iterator = GameScene.getInstance().getInteractables().iterator();
        while (iterator.hasNext() && notChangingInteractables){
            Interactable tmpInteractable = iterator.next();
            tmpInteractable.mouseDragged(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().mouseMoved(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().mousePressed(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // wird momentan nicht unterstützt => einfach keyReleased verwenden
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!currentlyPressedKeys.contains(e.getKeyCode())) currentlyPressedKeys.add(e.getKeyCode());
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().keyPressed(e.getKeyCode());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (currentlyPressedKeys.contains(e.getKeyCode()))
            currentlyPressedKeys.remove(Integer.valueOf(e.getKeyCode()));
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().keyReleased(e.getKeyCode());
        }
    }

    public ProgramController getProgramController() {
        return this.programController;
    }
}