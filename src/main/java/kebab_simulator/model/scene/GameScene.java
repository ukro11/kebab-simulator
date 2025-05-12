package kebab_simulator.model.scene;

import KAGO_framework.control.Drawable;
import KAGO_framework.control.Interactable;
import KAGO_framework.view.DrawTool;
import kebab_simulator.animation.Easings;
import kebab_simulator.control.CameraController;
import kebab_simulator.control.Wrapper;
import kebab_simulator.model.MapManager;
import kebab_simulator.model.visual.impl.component.InfoVisual;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GameScene extends Scene {

    private CameraController cameraController;
    private List<Interactable> interactables;
    private List<Drawable> drawables;
    private MapManager gameMap;

    private static GameScene instance = new GameScene();

    public static GameScene getInstance() {
        return GameScene.instance;
    }

    private GameScene() {
        super("game");
        this.drawables = new ArrayList<>();
        this.interactables = new ArrayList<>();
        this.cameraController = CameraController
                .create(this.viewController, this.viewController.getProgramController(), 0, 0)
                .zoom(4)
                .smooth((x) -> Easings.easeInCubic(x))
                .delay(1.0);

        this.visuals.add(new InfoVisual());
    }

    @Override
    public void update(double dt) {
        Wrapper.getEntityManager().getEntities().values().forEach(e -> e.update(dt));
        this.getDrawables().forEach(d -> d.update(dt));
        super.update(dt);
    }

    public void drawGame(DrawTool drawTool) {
        this.cameraController.attach(drawTool);
        if (this.gameMap != null) {
            this.gameMap.draw(drawTool);
        }
        Wrapper.getEntityManager().getEntities().values().forEach(e -> e.draw(drawTool));
        this.getDrawables().forEach(d -> d.draw(drawTool));
        this.cameraController.detach(drawTool);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        Wrapper.getEntityManager().getEntities().values().forEach(entity -> entity.mouseClicked(e));
        this.interactables.forEach(entity -> entity.mouseClicked(e));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        Wrapper.getEntityManager().getEntities().values().forEach(entity -> entity.mouseReleased(e));
        this.interactables.forEach(entity -> entity.mouseReleased(e));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        Wrapper.getEntityManager().getEntities().values().forEach(entity -> entity.mouseDragged(e));
        this.interactables.forEach(entity -> entity.mouseDragged(e));
    }

    @Override
    public void keyPressed(int key) {
        super.keyPressed(key);
        Wrapper.getEntityManager().getEntities().values().forEach(entity -> entity.keyPressed(key));
        this.interactables.forEach(entity -> entity.keyPressed(key));

        if (key == KeyEvent.VK_Z) {
            this.cameraController.shake(1);
        }
    }

    @Override
    public void keyReleased(int key) {
        super.keyReleased(key);
        Wrapper.getEntityManager().getEntities().values().forEach(entity -> entity.keyReleased(key));
        this.interactables.forEach(entity -> entity.keyReleased(key));
    }

    public MapManager getGameMap() {
        return gameMap;
    }

    public void setGameMap(MapManager gameMap) {
        this.gameMap = gameMap;
    }

    public CameraController getCameraController() {
        return this.cameraController;
    }

    public List<Drawable> getDrawables() {
        return this.drawables;
    }

    public List<Interactable> getInteractables() {
        return this.interactables;
    }
}
