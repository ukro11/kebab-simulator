package KAGO_framework.control;

import KAGO_framework.Config;
import KAGO_framework.view.DrawTool;
//import KAGO_scenario_framework.control.ScenarioController;
import com.google.common.util.concurrent.*;
import kebab_simulator.control.ProgramController;
import KAGO_framework.view.DrawFrame;
import KAGO_framework.view.DrawingPanel;
import kebab_simulator.control.Wrapper;
import kebab_simulator.model.entity.Entity;
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
public class ViewController implements KeyListener, MouseListener, MouseMotionListener {

    /**
     * Die innere Klasse kapselt jeweils eine Szene.
     * Diese besteht aus einem Panel auf das gezeichnet wird und das Tastatur- und Mauseingaben empfängt.
     * Außerdem gibt es jeweils eine Liste von Objekte, die gezeichnet und aktualisiert werden sollen
     * und eine Liste von Objekten, die über Eingaben informiert werden sollen
     */
    private class Scene {

        DrawingPanel drawingPanel;
        ArrayList<Drawable> drawables;
        ArrayList<Interactable> interactables;

        Scene(ViewController viewController){
            drawingPanel = new DrawingPanel(viewController);
            drawingPanel.setBackground(new Color(255,255,255));
            drawables = new ArrayList<>();
            interactables = new ArrayList<>();
        }
    }

    private Logger logger = LoggerFactory.getLogger(ViewController.class);
    private DrawFrame drawFrame;    // das Fenster des Programms
    private ProgramController programController; // das Objekt, das das Programm steuern soll
    private static ArrayList<Integer> currentlyPressedKeys = new ArrayList<>();;
    private ArrayList<Scene> scenes;
    private SoundController soundController;

    private ListeningExecutorService gameExecutor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(2));
    private ListeningExecutorService physicsExecutor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(2));
    private ListeningExecutorService backgroundServiceExecutor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(2));

    private int totalFrames, fps;
    private long lastFramesTime;
    private int dt;
    private int lastScene;
    private long lastLoop_Drawables, elapsedTime_Drawables;
    private long lastLoop, elapsedTime;
    private long lastLoop_Physics, elapsedTime_Physics;
    private int currentScene;
    private boolean notChangingInteractables, notChangingDrawables;
    private final AtomicBoolean watchPhysics = new AtomicBoolean(true);

    /**
     * Erzeugt ein Objekt zur Kontrolle des Programmflusses.
     */
    public ViewController() {
        programController = new ProgramController(this);
        programController.preStartProgram();
        notChangingDrawables = true;
        notChangingInteractables = true;
        scenes = new ArrayList<>();
        // Erzeuge Fenster und erste Szene
        createWindow();
        // Setzt die Ziel-Zeit zwischen zwei aufeinander folgenden Frames in Millisekunden
        dt = 1; //Vernuenftiger Startwert
        if ( Config.INFO_MESSAGES) System.out.println("  > ViewController: Erzeuge ProgramController und starte Spielprozess (Min. dt = "+dt+"ms)...");
        if ( Config.INFO_MESSAGES) System.out.println("     > Es wird nun einmalig die Methode startProgram von dem ProgramController-Objekt aufgerufen.");
        if ( Config.INFO_MESSAGES) System.out.println("     > Es wird wiederholend die Methode updateProgram von dem ProgramController-Objekt aufgerufen.");
        if ( Config.INFO_MESSAGES) System.out.println("-------------------------------------------------------------------------------------------------\n");
        if ( Config.INFO_MESSAGES) System.out.println("** Ab hier folgt das Log zum laufenden Programm: **");
        if(kebab_simulator.Config.useSound) {
            soundController = new SoundController();
        } else {
            if ( Config.INFO_MESSAGES) System.out.println("** Achtung! Sound deaktiviert => soundController ist NULL (kann in Config geändert werden). **");
        }

        if (!kebab_simulator.Config.SHOW_DEFAULT_WINDOW){
            setDrawFrameVisible(false);
            if(Config.INFO_MESSAGES) System.out.println("** Achtung! Standardfenster deaktiviert => wird nicht angezeigt.). **");
        }
        this.startProgram();
    }

    /**
     * Startet das Programm, nachdem Vorarbeiten abgeschlossen sind.
     */
    private void startProgram() {
        this.startGameEngine();
        this.startPhysicsEngine();
        this.startBackgroundServices();
    }

    private void startGameEngine() {
        gameExecutor.submit(() -> {
            this.programController.startProgram();
            for (Map.Entry<String, Entity> entry : Wrapper.getEntityManager().getEntities().entrySet()) {
                Entity entity = entry.getValue();
                this.draw(entity);
                this.register(entity);
            }
            this.register(new InputManager(this.programController));
            this.lastLoop = System.nanoTime();
            var scene = this.scenes.get(this.currentScene);

            while (true) {
                totalFrames++;
                if (System.nanoTime() > this.lastFramesTime + 1000_000_000L) {
                    this.lastFramesTime = System.nanoTime();
                    this.fps = totalFrames;
                    this.totalFrames = 0;
                }

                this.elapsedTime = System.nanoTime() - this.lastLoop;
                this.lastLoop = System.nanoTime();
                int dt = (int) (elapsedTime / 1000000L);
                double dtSeconds = (double) dt / 1000;
                if (dtSeconds == 0) dtSeconds = 0.01;
                if (this.currentScene != this.lastScene) {
                    scene = this.scenes.get(this.currentScene);
                }

                scene.drawingPanel.repaint();
                this.programController.updateProgram(dtSeconds);
                if (this.soundController != null) this.soundController.update(dtSeconds);
                Thread.sleep(this.dt);
            }
        });
    }

    private void startPhysicsEngine() {
        physicsExecutor.submit(() -> {
            this.lastLoop_Physics = System.nanoTime();
            while (true) {
                if (!this.watchPhysics.get()) {
                    Thread.sleep(100);
                    continue;
                }

                this.elapsedTime_Physics = System.nanoTime() - this.lastLoop_Physics;
                this.lastLoop_Physics = System.nanoTime();
                int dt = (int) (elapsedTime_Physics / 1000000L);
                double dtSeconds = (double) dt / 1000;
                if (dtSeconds == 0) dtSeconds = 0.01;

                Wrapper.getColliderManager().updateBodies(dtSeconds);

                try {
                    Thread.sleep(this.dt);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void startBackgroundServices() {
        // Sobald Hintergrundprozesse benötigt werden, wird es auskommentiert
        // Zur Zeit useless
        // this.backgroundServiceExecutor.submit(() -> {});
    }

    private void shutdown() {
        physicsExecutor.shutdown();
        gameExecutor.shutdown();
        try {
            if (!physicsExecutor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                physicsExecutor.shutdownNow();
            }
            if (!gameExecutor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                gameExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            physicsExecutor.shutdownNow();
            gameExecutor.shutdownNow();
        }
    }

    public boolean watchPhysics() {
        return this.watchPhysics.get();
    }

    public void setWatchPhyics(boolean flag) {
        this.watchPhysics.set(flag);
        this.lastLoop_Physics = System.nanoTime();
    }

    public int getFps() {
        return this.fps;
    }

    /**
     * Setzt den ViewController in den Startzustand zurück.
     */
    public void reset(){
        scenes = new ArrayList<>();
        createScene();
        showScene(0);
    }

    /**
     * Erzeugt das Fenster und die erste Szene, die sofort angezeigt wird.
     */
    private void createWindow(){
        // Berechne Mitte des Bildschirms
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        int x = width / 2;
        int y = height / 2;
        // Berechne die beste obere linke Ecke für das Fenster so, dass es genau mittig erscheint
        x = x - kebab_simulator.Config.WINDOW_WIDTH / 2;
        y = y - kebab_simulator.Config.WINDOW_HEIGHT / 2;
        // Erzeuge die erste Szene
        createScene();
        // Erzeuge ein neues Fenster zum Zeichnen
        drawFrame = new DrawFrame(kebab_simulator.Config.WINDOW_TITLE, x, y, kebab_simulator.Config.WINDOW_WIDTH, kebab_simulator.Config.WINDOW_HEIGHT, scenes.get(0).drawingPanel);
        drawFrame.setResizable(false);
        if (kebab_simulator.Config.WINDOW_FULLSCREEN) {
            drawFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            gd.setFullScreenWindow(Window.getWindows()[0]);
        }
        showScene(0);
        // Übergibt den weiteren Programmfluss an das neue Objekt der Klasse ViewController
        if ( Config.INFO_MESSAGES) System.out.println("  > ViewController: Fenster eingerichtet. Startszene (Index: 0) angelegt.");
    }

    /**
     * Zeigt die entsprechende Szene in der DrawFrame an. Außerdem ist nur noch die Interaktion mit Objekten dieser Szene möglich.
     * @param index Gibt die Nummer des gewünschten Drawing-Panel-Objekts an.
     */
    public void showScene(int index){
        // Setze das gewuenschte DrawingPanel und lege eine Referenz darauf an.
        if (index < scenes.size()) {
            this.lastScene = currentScene;
            currentScene = index;
            drawFrame.setActiveDrawingPanel(scenes.get(currentScene).drawingPanel);
        } else {
            if ( Config.INFO_MESSAGES) System.out.println("  > ViewController: Fehler: Eine Szene mit dem Index "+index+" existiert nicht.");
        }
    }

    /**
     * Erzeugt ein neue, leere Szene. Diese wird nicht sofort angezeigt.
     */
    public void createScene(){
        scenes.add(new Scene(this));
    }

    /**
     * Erzeugt ein neue, leere Szene. Diese wird nicht sofort angezeigt.
     * Überschreibt eine bestehende Szene! Wenn der Index höher als die verfügbare
     * Szenenanzahl ist, passiert nichts.
     */
    public void replaceScene(int index){
        if(scenes.size()-1<=index){
            scenes.set(index,new Scene(this));
        }
    }

    public SoundController getSoundController(){
        return soundController;
    }

    /**
     * Zeichnet und aktualisiert ein neues Objekt in der gewünschten Szene
     * @param d Das zu zeichnende Objekt (Interface Drawable muss implementiert werden)
     * @param sceneIndex Die Nummer der Szene für das Objekt
     */
    public void draw(Drawable d, int sceneIndex){
        if ( sceneIndex < scenes.size() && d != null){
            SwingUtilities.invokeLater(() -> scenes.get(sceneIndex).drawables.add(d));
        }
    }

    /**
     * Zeichnet und aktualisiert ein neues Objekt in der aktuellen Szene
     * @param d Das zu zeichnende Objekt.
     */
    public void draw(Drawable d){
        draw(d,currentScene);
    }

    /**
     * Fügt ein Objekt, das das Interactable-Interface implementiert zur aktuellen Szene hinzu, so
     * dass es auf Events reagiert
     * @param i das gewünschte Objekt
     */
    public void register(Interactable i){
        register(i, currentScene);
    }

    /**
     * Fügt ein Objekt, das das Interactable-Interface implementiert zur indizierten Szene hinzu, so
     * dass es auf Events reagiert
     * @param i das gewünschte Objekt
     */
    public void register(Interactable i, int sceneIndex){
        if (sceneIndex < scenes.size() && i!=null){
            SwingUtilities.invokeLater(() -> scenes.get(sceneIndex).interactables.add(i));
        }
    }

    /**
     * Abkuerzende Methode, um ein Objekt vom aktuellen DrawingPanel zu entfernen. Dann wird auch
     * update vom Objekt nicht mehr aufgerufen.
     * @param d Das zu entfernende Objekt.
     */
    public void removeDrawable(Drawable d){
        removeDrawable(d,currentScene);
    }

    /**
     * Entfernt ein Objekt aus einem DrawingPanel. Die Update- und Draw-Methode des Objekts
     * wird dann nicht mehr aufgerufen.
     * @param d Das zu entfernende Objekt
     * @param sceneIndex Der Index des DrawingPanel-Objekts von dem entfernt werden soll
     */
    public void removeDrawable(Drawable d, int sceneIndex){
        if ( sceneIndex < scenes.size() && d != null){
            notChangingDrawables = false;
            SwingUtilities.invokeLater(() -> {
                scenes.get(sceneIndex).drawables.remove(d);
                notChangingDrawables = true;
            });
        }
    }

    /**
     * Abkuerzende Methode, um ein Objekt vom aktuellen DrawingPanel zu entfernen. Dann wird auch
     * update vom Objekt nicht mehr aufgerufen.
     * @param i Das zu entfernende Objekt.
     */
    public void removeInteractable(Interactable i){
        removeInteractable(i,currentScene);
    }

    /**
     * Entfernt ein Objekt aus einem DrawingPanel. Die Update- und Draw-Methode des Objekts
     * wird dann nicht mehr aufgerufen.
     * @param i Das zu entfernende Objekt
     * @param sceneIndex Der Index des DrawingPanel-Objekts von dem entfernt werden soll
     */
    public void removeInteractable(Interactable i, int sceneIndex){
        if ( sceneIndex < scenes.size() && i != null){
            notChangingInteractables = false;
            SwingUtilities.invokeLater(() -> {
                scenes.get(sceneIndex).interactables.remove(i);
                notChangingInteractables = true;
            });
        }
    }

    /**
     * Diese Methode wird vom aktuellen DrawingPanel aufgerufen, sobald es bereit ist, alle Objekte
     * in das Fenster zu zeichnen. Dieser Vorgang wird schnellstmöglich wiederholt.
     * @param drawTool das zur Verfügung gestellte DrawTool des Fensters
     */
    public void drawAndUpdateObjects(DrawTool drawTool){
        elapsedTime_Drawables = System.nanoTime() - lastLoop_Drawables;
        lastLoop_Drawables = System.nanoTime();
        int dt = (int) ((elapsedTime / 1000000L));
        double dtSeconds = (double)dt/1000;
        if ( dtSeconds == 0 ) dtSeconds = 0.01;
        Iterator<Drawable> drawIterator = scenes.get(currentScene).drawables.iterator();
        while (drawIterator.hasNext() && notChangingDrawables){
            Drawable currentObject = drawIterator.next();
            currentObject.draw(drawTool);
            currentObject.update(dtSeconds);
            if (kebab_simulator.Config.useSound && soundController != null) soundController.update(dtSeconds);
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
        Iterator<Interactable> iterator = scenes.get(currentScene).interactables.iterator();
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
        Iterator<Interactable> iterator = scenes.get(currentScene).interactables.iterator();
        while (iterator.hasNext() && notChangingInteractables){
            Interactable tmpInteractable = iterator.next();
            tmpInteractable.mouseClicked(e);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Iterator<Interactable> iterator = scenes.get(currentScene).interactables.iterator();
        while (iterator.hasNext() && notChangingInteractables){
            Interactable tmpInteractable = iterator.next();
            tmpInteractable.mouseDragged(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Iterator<Interactable> iterator = scenes.get(currentScene).interactables.iterator();
        while (iterator.hasNext() && notChangingInteractables){
            Interactable tmpInteractable = iterator.next();
            tmpInteractable.mouseMoved(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Iterator<Interactable> iterator = scenes.get(currentScene).interactables.iterator();
        while (iterator.hasNext() && notChangingInteractables){
            Interactable tmpInteractable = iterator.next();
            tmpInteractable.mousePressed(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // wird momentan nicht unterstützt => einfach keyReleased verwenden
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!currentlyPressedKeys.contains(e.getKeyCode())) currentlyPressedKeys.add(e.getKeyCode());
        Iterator<Interactable> iterator = scenes.get(currentScene).interactables.iterator();
        while (iterator.hasNext() && notChangingInteractables){
            Interactable tmpInteractable = iterator.next();
            tmpInteractable.keyPressed(e.getKeyCode());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (currentlyPressedKeys.contains(e.getKeyCode()))
            currentlyPressedKeys.remove(Integer.valueOf(e.getKeyCode()));
        Iterator<Interactable> iterator = scenes.get(currentScene).interactables.iterator();
        while (iterator.hasNext() && notChangingInteractables){
            Interactable tmpInteractable = iterator.next();
            tmpInteractable.keyReleased(e.getKeyCode());
        }
    }
}