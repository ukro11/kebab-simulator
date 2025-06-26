package kebab_simulator;

import KAGO_framework.control.ViewController;
import kebab_simulator.animation.tween.Tween;
import kebab_simulator.event.events.KeyPressedEvent;
import kebab_simulator.event.services.EventProcessCallback;
import kebab_simulator.event.services.process.EventLoadAssetsProcess;
import kebab_simulator.graphics.map.MapManager;
import kebab_simulator.model.entity.impl.player.EntityPlayer;
import kebab_simulator.model.meal.MealModel;
import kebab_simulator.model.scene.GameScene;
import kebab_simulator.model.sound.SoundManager;
import kebab_simulator.model.visual.impl.component.InfoComponent;
import kebab_simulator.utils.game.CooldownManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Ein Objekt der Klasse ProgramController dient dazu das Programm zu steuern.
 * Hinweise:
 * - Der Konstruktor sollte nicht geändert werden.
 * - Sowohl die startProgram()- als auch die updateProgram(...)-Methoden müssen vorhanden sein und ihre Signatur sollte
 *   nicht geändert werden
 * - Zusätzliche Methoden sind natürlich gar kein Problem
 */
public class ProgramController {

    private final Logger logger = LoggerFactory.getLogger(ProgramController.class);

    private final ViewController viewController;
    public EntityPlayer player;

    /**
     * Konstruktor
     * Dieser legt das Objekt der Klasse ProgramController an, das den Programmfluss steuert.
     * Damit der ProgramController auf das Fenster zugreifen kann, benötigt er eine Referenz auf das Objekt
     * der Klasse viewController. Diese wird als Parameter übergeben.
     * @param viewController das viewController-Objekt des Programms
     */
    public ProgramController(ViewController viewController){
        this.viewController = viewController;
    }

    /**
     * Wird als aller erstes aufgerufen beim starten. "startProgram" wird hingegen nur
     * nach Erstellen des Fensters, usw. erst aufgerufen.
     */
    public void preStartProgram() {
        Wrapper.getProcessManager().queue(new EventLoadAssetsProcess("Loading map", () -> {
            MapManager map = MapManager.importMap("/map/kitchen.json", List.of("ground"), List.of("light"));
            GameScene.getInstance().setGameMap(map);

        }, new EventProcessCallback() {
            @Override
            public void onSuccess(Object data) {
                viewController.continueStart();
            }
        }));
    }

    /**
     * Diese Methode wird genau ein mal nach Programmstart aufgerufen. Hier sollte also alles geregelt werden,
     * was zu diesem Zeipunkt passieren muss.
     */
    public void startProgram() {
        if (Config.RUN_ENV == Config.Environment.DEVELOPMENT) {
            Wrapper.getEventManager().addEventListener("keypressed", (KeyPressedEvent event) -> {
                if (event.getKeyCode() == KeyEvent.VK_F9) {
                    this.viewController.setWatchPhyics(!this.viewController.watchPhysics());
                }
            });
        }
        GameScene.getInstance().getVisuals().add(new InfoComponent());

        this.player = Wrapper.getEntityManager().spawnPlayer("player", 383, 682);
        this.player.setShowHitbox(false);
        GameScene.getInstance().getCameraRenderer().focusAtEntity(this.player);

        MealModel.init();
    }

    /**
     * Diese Methode wird vom ViewController-Objekt automatisch mit jedem Frame aufgerufen (ca. 60mal pro Sekunde)
     * @param dt Zeit seit letztem Frame in Sekunden
     */
    public void updateProgram(double dt){
        CooldownManager.update(dt);
        Tween.updateAll(dt);
    }
}
