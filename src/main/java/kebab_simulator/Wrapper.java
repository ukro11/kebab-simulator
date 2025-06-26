package kebab_simulator;

import KAGO_framework.control.SoundController;
import KAGO_framework.control.ViewController;
import kebab_simulator.event.EventManager;
import kebab_simulator.event.services.EventProcessingQueue;
import kebab_simulator.graphics.tooltip.TooltipManager;
import kebab_simulator.model.GameHandlerModel;
import kebab_simulator.model.entity.EntityManager;
import kebab_simulator.model.entity.impl.food.IEntityCookable;
import kebab_simulator.model.entity.impl.player.EntityPlayer;
import kebab_simulator.physics.ColliderManager;
import kebab_simulator.utils.misc.TimerUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Wrapper {

    private final static EventManager eventManager = new EventManager();
    private final static ColliderManager colliderManager = new ColliderManager();
    private final static EntityManager entityManager = new EntityManager();
    private final static EventProcessingQueue processManager = new EventProcessingQueue();
    private final static TooltipManager tooltipManager = new TooltipManager();
    private final static GameHandlerModel gameHandlerModel = new GameHandlerModel();
    private final static TimerUtils timer = new TimerUtils();
    private final static TimerUtils physicsTimer = new TimerUtils();

    public static EventManager getEventManager() {
        return eventManager;
    }

    public static ColliderManager getColliderManager() {
        return colliderManager;
    }

    public static EntityManager getEntityManager() {
        return entityManager;
    }

    public static EventProcessingQueue getProcessManager() { return processManager; }

    public static TooltipManager getTooltipManager() { return tooltipManager; }

    public static EntityPlayer getLocalPlayer() { return ViewController.getInstance().getProgramController().player; }

    public static SoundController getSoundController() { return ViewController.getInstance().getSoundController(); }

    public static TimerUtils getTimer() { return timer; }

    public static TimerUtils getPhysicsTimer() { return physicsTimer; }

    public static GameHandlerModel getGameHandler() { return gameHandlerModel; }

    public static void loadSounds() {
        Wrapper.getSoundController().loadSound("/sound/whoosh.mp3", "whoosh", false);
        Wrapper.getSoundController().loadSound("/sound/plop.mp3", "pick-up", false);
        Wrapper.getSoundController().loadSound("/sound/cutting.mp3", "cutting", false);
        Wrapper.getSoundController().loadSound("/sound/frying.mp3", "frying", false);
        Wrapper.getSoundController().loadSound("/sound/Lobby Time.mp3", "background", true);
    }

    public static BufferedImage getImage(String src) {
        try {
            return ImageIO.read(IEntityCookable.class.getResourceAsStream(src));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
