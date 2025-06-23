package kebab_simulator;

import KAGO_framework.control.SoundController;
import KAGO_framework.control.ViewController;
import kebab_simulator.event.EventManager;
import kebab_simulator.event.services.EventProcessingQueue;
import kebab_simulator.graphics.tooltip.TooltipManager;
import kebab_simulator.model.entity.EntityManager;
import kebab_simulator.model.entity.impl.food.IEntityCookable;
import kebab_simulator.model.entity.impl.player.EntityPlayer;
import kebab_simulator.physics.ColliderManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Wrapper {

    private final static EventManager eventManager = new EventManager();
    private final static ColliderManager colliderManager = new ColliderManager();
    private final static EntityManager entityManager = new EntityManager();
    private final static EventProcessingQueue processManager = new EventProcessingQueue();
    private final static TooltipManager tooltipManager = new TooltipManager();

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

    public static void loadSounds() {
        Wrapper.getSoundController().loadSound("/sound/whoosh.mp3", "whoosh", false);
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
