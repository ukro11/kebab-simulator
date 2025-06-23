package KAGO_framework.control;

import KAGO_framework.Config;
import KAGO_framework.view.DrawTool;
import com.google.common.util.concurrent.*;
import kebab_simulator.ProgramController;
import KAGO_framework.view.DrawFrame;
import kebab_simulator.Wrapper;
import kebab_simulator.event.events.KeyPressedEvent;
import kebab_simulator.model.scene.GameScene;
import kebab_simulator.model.scene.LoadingScene;
import kebab_simulator.model.scene.Scene;
import kebab_simulator.model.visual.impl.gui.GuiScreen;
import kebab_simulator.utils.misc.TimerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Diese Klasse kontrolliert die DrawingPanels einer ihr zugewiesenen DrawingFrame.
 * Sie kann verschiedene Objekte erzeugen und den Panels hinzufuegen.
 * Vorgegebene Klasse des Frameworks. Modifikation auf eigene Gefahr.
 */
public class ViewController extends JPanel implements KeyListener, MouseListener, MouseMotionListener {

    private static final Logger logger = LoggerFactory.getLogger(ViewController.class);
    private final ProgramController programController;
    private final SoundController soundController;
    private final DrawTool drawTool;
    private final ListeningExecutorService physicsExecutor;

    private final AtomicBoolean watchPhysics;
    private final AtomicBoolean initializing;
    private final int threadSleep = 1;

    private DrawFrame drawFrame;
    private boolean requested = false;

    // Instanzvariablen für gedrückte Tasten und Mausknöpfe
    private final static java.util.List<Integer> currentlyPressedKeys = new ArrayList<>();
    private final static java.util.List<Integer> currentlyPressedMouseButtons = new ArrayList<>();

    private static ViewController instance;

    /**
     * Erzeugt ein Objekt zur Kontrolle des Programmflusses.
     */
    public ViewController() {
        ViewController.instance = this;
        this.programController = new ProgramController(this);
        this.drawTool = new DrawTool();
        this.physicsExecutor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(2));
        this.watchPhysics = new AtomicBoolean(true);
        this.initializing = new AtomicBoolean(true);

        this.programController.preStartProgram();
        while (this.initializing.get()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        logger.info("PreStart Setup is finished.");
        logger.info("Starting Engine.");

        this.createWindow();

        if(kebab_simulator.Config.USE_SOUND || kebab_simulator.Config.RUN_ENV == kebab_simulator.Config.Environment.PRODUCTION) {
            this.soundController = new SoundController();
            Wrapper.loadSounds();

        } else {
            if ( Config.INFO_MESSAGES) System.out.println("** Achtung! Sound deaktiviert => soundController ist NULL (kann in Config geändert werden). **");
        }
        if (!kebab_simulator.Config.SHOW_DEFAULT_WINDOW) {
            this.setDrawFrameVisible(false);
            if(Config.INFO_MESSAGES) System.out.println("** Achtung! Standardfenster deaktiviert => wird nicht angezeigt.). **");
        }

        this.startProgram();
    }

    public static ViewController getInstance() {
        return instance;
    }

    /**
     * Startet das Programm, nachdem Vorarbeiten abgeschlossen sind.
     */
    private void startProgram() {
        try {
            this.setWatchPhyics(false);
            this.startPhysicsEngine();
            this.startGameEngine();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void startGameEngine() throws InterruptedException {
        this.programController.startProgram();
        this.setWatchPhyics(true);
        Wrapper.getProcessManager().processPostGame();
        while (true) {
            TimerUtils.update();
            this.programController.updateProgram(TimerUtils.getDeltaTime());
            if (Scene.getCurrentScene() != null) Scene.getCurrentScene().update(TimerUtils.getDeltaTime());
            if (GuiScreen.getCurrentScreen() != null) GuiScreen.getCurrentScreen().update(TimerUtils.getDeltaTime());
            // if (this.soundController != null) this.soundController.update(TimerUtils.getDeltaTime());
            repaint();
            Thread.sleep(this.threadSleep);
        }
    }

    private void startPhysicsEngine() {
        var physicService = this.physicsExecutor.submit(() -> {
            while (true) {
                if (!this.watchPhysics.get()) {
                    Thread.sleep(100);
                    continue;
                }
                Wrapper.getColliderManager().updateColliders(TimerUtils.getDeltaTime());
                if (Scene.getCurrentScene() instanceof GameScene) GameScene.getInstance().updatePhysics(TimerUtils.getDeltaTime());
                Thread.sleep(this.threadSleep);
            }
        });
        Futures.addCallback(physicService, new FutureCallback<Object>() {
            @Override
            public void onSuccess(Object result) {}
            @Override
            public void onFailure(Throwable t) {
                logger.error("Exception in thread \"{}\" {}", Thread.currentThread().getName(), t.getClass().getName());
                t.printStackTrace();
            }
        }, this.physicsExecutor);
    }

    private void shutdown() {
        this.physicsExecutor.shutdown();
        Wrapper.getProcessManager().shutdown();
        try {
            if (!this.physicsExecutor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                this.physicsExecutor.shutdownNow();
            }
            if (!Wrapper.getProcessManager().getServicesExecutor().awaitTermination(800, TimeUnit.MILLISECONDS)) {
                Wrapper.getProcessManager().getServicesExecutor().shutdownNow();
            }
        } catch (InterruptedException e) {
            this.physicsExecutor.shutdownNow();
            Wrapper.getProcessManager().getServicesExecutor().shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public boolean watchPhysics() {
        return this.watchPhysics.get();
    }

    public void setWatchPhyics(boolean flag) {
        this.watchPhysics.set(flag);
    }

    public void continueStart() {
        this.initializing.set(false);
    }

    private void createWindow(){
        //
        // #947052
        this.setBackground(Color.decode("#d0b99c"));
        this.setDoubleBuffered(true);

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        int x = width / 2 - kebab_simulator.Config.WINDOW_WIDTH / 2;
        int y = height / 2 - kebab_simulator.Config.WINDOW_HEIGHT / 2;
        Scene.open(new LoadingScene());
        //Scene.open(new StartScene());
        //Scene.open(GameScene.getInstance());
        this.drawFrame = new DrawFrame(kebab_simulator.Config.WINDOW_TITLE, x, y, kebab_simulator.Config.WINDOW_WIDTH, kebab_simulator.Config.WINDOW_HEIGHT, this);
        this.drawFrame.setResizable(false);

        if (kebab_simulator.Config.RUN_ENV == kebab_simulator.Config.Environment.PRODUCTION) {
            this.drawFrame.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {}
                @Override
                public void windowClosing(WindowEvent e) {
                    shutdown();
                }
                @Override
                public void windowClosed(WindowEvent e) {}
                @Override
                public void windowIconified(WindowEvent e) {}
                @Override
                public void windowDeiconified(WindowEvent e) {}
                @Override
                public void windowActivated(WindowEvent e) {}
                @Override
                public void windowDeactivated(WindowEvent e) {}
            });
        }
        this.drawFrame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {

            }
            @Override
            public void windowLostFocus(WindowEvent e) {
                currentlyPressedKeys.clear();
                currentlyPressedMouseButtons.clear();
            }
        });

        if (kebab_simulator.Config.WINDOW_FULLSCREEN) {
            this.drawFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            gd.setFullScreenWindow(Window.getWindows()[0]);
        }
        this.drawFrame.setActiveDrawingPanel(this);
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
        this.drawTool.setGraphics2D(g2d,this);
        if (this.soundController != null) this.soundController.update(TimerUtils.getDeltaTime());
        if (Scene.getCurrentScene() != null) Scene.getCurrentScene().draw(this.drawTool);
        if (GuiScreen.getCurrentScreen() != null) GuiScreen.getCurrentScreen().draw(this.drawTool);
    }

    /**
     * Diese Methode überprüft, ob die angebene Taste momentan heruntergedrückt ist.
     * @param key Der Tastecode der zu überprüfenden Taste.
     * @return True, falls die entsprechende Taste momentan gedrückt ist, andernfalls false.
     */
    public static boolean isKeyDown(int key){
        return ViewController.currentlyPressedKeys.contains(key);
    }

    /**
     * Diese Methode überprüft, ob die angebene Taste momentan heruntergedrückt ist.
     * @param key Der Tastecode der zu überprüfenden Taste.
     * @return True, falls die entsprechende Taste momentan gedrückt ist, andernfalls false.
     */
    public static boolean isMouseDown(int key){
        return ViewController.currentlyPressedMouseButtons.contains(key);
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

    @Override
    public void mouseEntered(MouseEvent e) {
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().mouseEntered(e);
        }
        if (GuiScreen.getCurrentScreen() != null) {
            GuiScreen.getCurrentScreen().mouseEntered(e);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().mouseExited(e);
        }
        if (GuiScreen.getCurrentScreen() != null) {
            GuiScreen.getCurrentScreen().mouseExited(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!ViewController.currentlyPressedMouseButtons.contains(e.getButton()))
            ViewController.currentlyPressedMouseButtons.add(e.getButton());
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().mouseReleased(e);
        }
        if (GuiScreen.getCurrentScreen() != null) {
            GuiScreen.getCurrentScreen().mouseReleased(e);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        ViewController.currentlyPressedMouseButtons.remove(Integer.valueOf(e.getButton()));
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().mouseClicked(e);
        }
        if (GuiScreen.getCurrentScreen() != null) {
            GuiScreen.getCurrentScreen().mouseClicked(e);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().mouseDragged(e);
        }
        if (GuiScreen.getCurrentScreen() != null) {
            GuiScreen.getCurrentScreen().mouseDragged(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().mouseMoved(e);
        }
        if (GuiScreen.getCurrentScreen() != null) {
            GuiScreen.getCurrentScreen().mouseMoved(e);
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
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().keyTyped(e);
        }
        if (GuiScreen.getCurrentScreen() != null) {
            GuiScreen.getCurrentScreen().keyTyped(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!ViewController.currentlyPressedKeys.contains(e.getKeyCode()))
            ViewController.currentlyPressedKeys.add(e.getKeyCode());
        Wrapper.getEventManager().dispatchEvent(new KeyPressedEvent(e));
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().keyPressed(e);
        }
        if (GuiScreen.getCurrentScreen() != null) {
            GuiScreen.getCurrentScreen().keyPressed(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        currentlyPressedKeys.remove(Integer.valueOf(e.getKeyCode()));
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().keyReleased(e);
        }
        if (GuiScreen.getCurrentScreen() != null) {
            GuiScreen.getCurrentScreen().keyReleased(e);
        }
    }

    public ProgramController getProgramController() {
        return this.programController;
    }

    public SoundController getSoundController() {
        return this.soundController;
    }
}