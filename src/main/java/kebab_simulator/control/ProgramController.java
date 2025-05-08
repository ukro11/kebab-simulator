package kebab_simulator.control;

import KAGO_framework.control.Drawable;
import KAGO_framework.control.ViewController;
import KAGO_framework.view.DrawTool;
import kebab_simulator.animation.AnimationFrame;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.AnimationState;
import kebab_simulator.event.events.CollisionEvent;
import kebab_simulator.event.events.KeyPressedEvent;
import kebab_simulator.model.entity.Entity;
import kebab_simulator.physics.Body;
import kebab_simulator.physics.BodyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
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

    private final ViewController viewController;
    private final static Logger logger = LoggerFactory.getLogger(ProgramController.class);
    public static boolean TEST_SCALE = false;

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

    /**
     * Diese Methode wird genau ein mal nach Programmstart aufgerufen. Hier sollte also alles geregelt werden,
     * was zu diesem Zeipunkt passieren muss.
     */
    public void startProgram() {
        // testEntity();
        // testBody();
    }

    /**
     * Body Klasse Test
     */
    private void testBody() {
        // TODO: Fix type 2 (error)
        int type = 1;

        Body test1 = new Body("test1", BodyType.DYNAMIC, 105, 100, 100, 100);

        Body test2 = new Body("test2", BodyType.DYNAMIC, 105, 400, 50, 50);
        test2.setLinearVelocity(0, -100);

        test1.addEventListener(Body.BodyEvents.BODY_COLISSION_EVENT, (event) -> {
            if (type == 1) {
                switch (event.getState()) {
                    case COLLISION_BEGIN_CONTACT, COLLISION_NORMAL_CONTACT -> test2.setLinearVelocity(50, -50);
                    case COLLISION_END_CONTACT -> test2.setLinearVelocity(0, -50);
                }
            } else if (type == 2) {
                if (event.getState() == CollisionEvent.CollisionState.COLLISION_BEGIN_CONTACT) {
                    test1.destroy();
                    test2.setLinearVelocity(0, -50);
                }
            }
        });

        if (type == 2) {
            test2.addEventListener(Body.BodyEvents.BODY_COLISSION_EVENT, (event) -> {
                ProgramController.logger.info("COLLISION");
            });
        }

        this.viewController.draw(new Drawable() {
            @Override
            public void draw(DrawTool drawTool) {
                if (test1 != null) test1.renderHitbox(drawTool);
                if (test2 != null) test2.renderHitbox(drawTool);
                drawTool.setCurrentColor(Color.BLACK);
                drawTool.drawText(10, 10, String.valueOf(viewController.getFps()));
                drawTool.resetColor();
            }
            @Override
            public void update(double dt) {

            }
        });

        Wrapper.getEventManager().addEventListener("keypressed", (KeyPressedEvent event) -> {
            if (event.getKeyCode() == KeyEvent.VK_SPACE) {
                this.viewController.setWatchPhyics(!this.viewController.watchPhysics());
            }
        });
    }

    /**
     * Entity Klasse Test
     */
    private void testEntity() {
        ProgramController.TEST_SCALE = true;
        Entity player = new Entity(
                new Body("player", BodyType.DYNAMIC, 50, 30, 16, 16),
                -8, -8, 32, 32
        );
        HashMap<AnimationState, AnimationFrame<AnimationState>> animations = new HashMap<>();
        animations.put(
                AnimationState.IDLE_BOTTOM,
                new AnimationFrame<AnimationState>(
                        AnimationState.IDLE_BOTTOM,
                        List.of(
                                new ImageIcon(getClass().getResource("/graphic/test/test-character-0.png")),
                                new ImageIcon(getClass().getResource("/graphic/test/test-character-1.png"))
                        )
                )
        );
        animations.put(
                AnimationState.IDLE_RIGHT,
                new AnimationFrame<AnimationState>(
                        AnimationState.IDLE_RIGHT,
                        List.of(
                                new ImageIcon(getClass().getResource("/graphic/test/test-character-4.png")),
                                new ImageIcon(getClass().getResource("/graphic/test/test-character-5.png"))
                        )
                )
        );
        animations.put(
                AnimationState.IDLE_LEFT,
                new AnimationFrame<AnimationState>(
                        AnimationState.IDLE_LEFT,
                        List.of(
                                new ImageIcon(getClass().getResource("/graphic/test/test-character-4.png")),
                                new ImageIcon(getClass().getResource("/graphic/test/test-character-5.png"))
                        )
                )
        );
        animations.put(
                AnimationState.IDLE_TOP,
                new AnimationFrame<AnimationState>(
                        AnimationState.IDLE_TOP,
                        List.of(
                                new ImageIcon(getClass().getResource("/graphic/test/test-character-8.png")),
                                new ImageIcon(getClass().getResource("/graphic/test/test-character-9.png"))
                        )
                )
        );
        player.setRenderer(new AnimationRenderer<>(animations, AnimationState.IDLE_BOTTOM, 0.5, true));
        Wrapper.getEntityManager().registerEntity(player);
        player.setShowHitbox(true);

        this.viewController.draw(new Drawable() {
            @Override
            public void draw(DrawTool drawTool) {
                drawTool.setCurrentColor(Color.BLACK);
                drawTool.drawText(10, 10, String.valueOf(viewController.getFps()));
                drawTool.resetColor();
            }
            @Override
            public void update(double dt) {

            }
        });

        Wrapper.getEventManager().addEventListener("keypressed", (KeyPressedEvent event) -> {
            switch (event.getKeyCode()) {
                case KeyEvent.VK_W: {
                    player.getRenderer().switchState(AnimationState.IDLE_TOP);
                    break;
                }
                case KeyEvent.VK_A: {
                    player.getRenderer().switchState(AnimationState.IDLE_RIGHT);
                    break;
                }
                case KeyEvent.VK_D: {
                    player.getRenderer().switchState(AnimationState.IDLE_LEFT);
                    break;
                }
                case KeyEvent.VK_S: {
                    player.getRenderer().switchState(AnimationState.IDLE_BOTTOM);
                    break;
                }
            }
        });
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
