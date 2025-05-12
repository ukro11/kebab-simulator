package kebab_simulator.model.scene;

import KAGO_framework.control.Drawable;
import KAGO_framework.control.ViewController;
import KAGO_framework.view.DrawTool;
import kebab_simulator.control.ProgramController;
import kebab_simulator.model.visual.VisualModel;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class Scene {

    private static HashMap<String, Scene> scenes = new HashMap<>();
    private static Scene last;
    private static Scene current;
    protected static ViewController viewController;
    protected static ProgramController programController;

    private String id;
    protected List<VisualModel> visuals;
    private boolean resort = true;

    public Scene(String id) {
        this.id = id;
        this.visuals = new ArrayList<>();
        Scene.scenes.put(this.id, this);
    }

    public static void initialize(ViewController viewController) {
        Scene.viewController = viewController;
        Scene.programController = viewController.getProgramController();
    }

    public static void open(Scene scene) {
        if (Scene.current != null) {
            Scene.current.onClose(scene);
        }
        scene.onOpen(Scene.last);
        Scene.current = scene;
    }

    public static void close() {
        Scene.current.onClose(null);
        Scene.current = GameScene.getInstance();
    }

    public static HashMap<String, Scene> getScenes() {
        return Scene.scenes;
    }

    public static Scene getCurrentScene() {
        return Scene.current;
    }

    public String getId() {
        return this.id;
    }

    public List<VisualModel> getVisuals() {
        return this.visuals;
    }

    public void draw(DrawTool drawTool) {
        if (this.resort) {
            Collections.sort(this.visuals);
            this.resort = false;
        }
        for (VisualModel visual : this.visuals) {
            visual.draw(drawTool);
        }
    }

    public void update(double dt) {
        for (VisualModel visual : this.visuals) {
            visual.update(dt);
        }
    }

    public void onOpen(Scene last) {}
    public void onClose(Scene newScene) {}

    /**
     * Wird einmalig aufgerufen, wenn eine Taste heruntergedrückt wird. Nach der Anschlagverzögerung löst Windows den Tastendruck dann
     * in schneller Folge erneut aus. Eignet sich NICHT, um Bewegungen zu realisieren.
     * @param key Enthält den Zahlencode für die Taste. Kann direkt aus der Klasse KeyEvent geladen werden, z.B. KeyEvent_VK_3
     */
    public void keyPressed(int key) {}

    /**
     * Wird einmalig aufgerufen, wenn eine Taste losgelassen wird.
     * @param key Enthält den Zahlencode für die Taste. Kann direkt aus der Klasse KeyEvent geladen werden, z.B. KeyEvent_VK_3
     */
    public void keyReleased(int key) {}

    /**
     * Wird einmalig aufgerufen, wenn eine Maustaste losgelassen wurde.
     * @param e Das übergebene Objekt der Klasse MouseEvent enthält alle Information über das Ereignis.
     */
    public void mouseReleased(MouseEvent e) {}

    /**
     * Wird einmalig aufgerufen, wenn eine Maustaste geklickt wurde.
     * @param e Das übergebene Objekt der Klasse MouseEvent enthält alle Information über das Ereignis.
     */
    public void mouseClicked(MouseEvent e) {}

    /**
     * Wird einmalig aufgerufen, wenn eine Maustaste gehalten wird und die Maus bewegt wird.
     * @param e Das übergebene Objekt der Klasse MouseEvent enthält alle Information über das Ereignis.
     */
    public void mouseDragged(MouseEvent e) {}

    /**
     * Wird einmalig aufgerufen, wenn die Maus bewegt wurde.
     * @param e Das übergebene Objekt der Klasse MouseEvent enthält alle Information über das Ereignis.
     */
    public void mouseMoved(MouseEvent e) {}

    /**
     * Wird einmalig aufgerufen, wenn eine Maustaste heruntergedrückt wurde.
     * @param e Das übergebene Objekt der Klasse MouseEvent enthält alle Information über das Ereignis.
     */
    public void mousePressed(MouseEvent e) {}
}
