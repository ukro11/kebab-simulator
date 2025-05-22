package kebab_simulator.control;

import KAGO_framework.control.ViewController;
import kebab_simulator.Config;
import kebab_simulator.event.events.KeyPressedEvent;
import kebab_simulator.event.events.collider.ColliderCollisionEvent;
import kebab_simulator.model.entity.impl.EntityPlayer;
import kebab_simulator.model.scene.GameScene;
import kebab_simulator.model.visual.impl.component.InfoVisual;
import kebab_simulator.test.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;

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

    }

    private void startTest() {
        Test.setup(this.viewController);
        // ColliderTest.getInstance().test1(40, 0, new Vec2(0, 20));
        // ColliderTest.getInstance().test2(40, 0, new Vec2(0, 0), new Vec2(0, 20));
        // ColliderTest.getInstance().test3(80, 0, new Vec2(0, 20));
        // ColliderTest.getInstance().test4(80, 0, new Vec2(0, -20), new Vec2(0, 0));
    }

    /**
     * Diese Methode wird genau ein mal nach Programmstart aufgerufen. Hier sollte also alles geregelt werden,
     * was zu diesem Zeipunkt passieren muss.
     */
    public void startProgram() {
        if (Config.RUN_ENV == Config.Environment.DEVELOPMENT) {
            this.startTest();
            GameScene.getInstance().getVisuals().add(new InfoVisual());
        }

        Wrapper.getEventManager().addEventListener("keypressed", (KeyPressedEvent event) -> {
            if (event.getKeyCode() == KeyEvent.VK_SPACE) {
                viewController.setWatchPhyics(!viewController.watchPhysics());
            }
        });

        EntityPlayer dummy = EntityPlayer.createDummy("dummy1", 480, 500);
        dummy.getBody().setSensor(false);
        dummy.getBody().onCollision((event) -> {
            if (event.isBodyInvolved(player.getBody())) {
                //logger.info("{}", event.getState());
                if (event.getState() == ColliderCollisionEvent.CollisionState.COLLISION_BEGIN_CONTACT) {
                    dummy.setShowHitbox(true);
                    player.setShowHitbox(true);

                } else if (event.getState() == ColliderCollisionEvent.CollisionState.COLLISION_END_CONTACT) {
                    dummy.setShowHitbox(false);
                    player.setShowHitbox(false);
                }
            }
        });
        dummy.setShowHitbox(false);
        dummy.freeze(true);
        Wrapper.getEntityManager().registerEntity(dummy);

        this.player = EntityPlayer.createPlayer("player", 510, 570);
        this.player.setShowHitbox(false);
        Wrapper.getEntityManager().registerEntity(this.player);
        GameScene.getInstance().getCameraController().focusAtEntity(this.player);
    }

    /**
     * Diese Methode wird vom ViewController-Objekt automatisch mit jedem Frame aufgerufen (ca. 60mal pro Sekunde)
     * @param dt Zeit seit letztem Frame in Sekunden
     */
    public void updateProgram(double dt){
        // Wird zur Zeit nicht gebraucht bei Bedarf, kann es wieder auskommentiert werden
        // Wrapper.getEventManager().dispatchEvent(new UpdateEvent(dt));
    }
}
