package kebab_simulator.model.scene;

import KAGO_framework.control.Drawable;
import KAGO_framework.control.Interactable;
import KAGO_framework.view.DrawTool;
import kebab_simulator.animation.Easings;
import kebab_simulator.control.CameraController;
import kebab_simulator.control.Wrapper;
import kebab_simulator.event.services.EventProcessCallback;
import kebab_simulator.event.services.process.EventLoadAssetsProcess;
import kebab_simulator.graphics.OrderRenderer;
import kebab_simulator.graphics.map.MapManager;
import kebab_simulator.model.visual.impl.component.InfoVisual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GameScene extends Scene {

    private final Logger logger = LoggerFactory.getLogger(GameScene.class);
    private CameraController cameraController;
    private List<Interactable> interactables;
    private List<Drawable> drawables;
    private OrderRenderer renderer;
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
                .zoom(3)
                .smooth((x) -> Easings.easeInCubic(x))
                .delay(1.0);
        this.renderer = new OrderRenderer();

        this.visuals.add(new InfoVisual());

        Wrapper.getProcessManager().queue(new EventLoadAssetsProcess("Loading map", () -> {
            MapManager map = MapManager.importMap("map.json", List.of("floor", "grass"));
            GameScene.getInstance().setGameMap(map);
            return map;

        }, new EventProcessCallback<MapManager>() {
            @Override
            public void onSuccess(MapManager data) {
                viewController.continueStart();
            }
            @Override
            public void onFailure(Throwable e) {}
        }));
    }

    public void updatePhysics(double dt) {
        this.cameraController.update(dt);
        Wrapper.getEntityManager().getEntities().values().forEach(e -> e.update(dt));
    }

    @Override
    public void update(double dt) {
        this.getDrawables().forEach(d -> d.update(dt));
        super.update(dt);
    }

    public void drawGame(DrawTool drawTool) {
        this.cameraController.attach(drawTool);
        if (this.gameMap != null) {
            drawTool.push();
            this.gameMap.draw(drawTool);
            drawTool.pop();
        }

        GameScene.getInstance().getRenderer().draw(drawTool);
        this.getDrawables().forEach(d -> d.draw(drawTool));

        Wrapper.getColliderManager().getColliders().values().forEach(r -> {
            r.renderHitbox(drawTool);
        });

        this.cameraController.detach(drawTool);
    }

    @Override
    public void draw(DrawTool drawTool) {
        GameScene.getInstance().drawGame(drawTool);
        super.draw(drawTool);
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
    public void keyPressed(KeyEvent key) {
        super.keyPressed(key);
        Wrapper.getEntityManager().getEntities().values().forEach(entity -> entity.keyPressed(key.getKeyCode()));
        this.interactables.forEach(entity -> entity.keyPressed(key.getKeyCode()));

        if (key.getKeyCode() == KeyEvent.VK_Z) {
            this.cameraController.shake(1);
        }
    }

    @Override
    public void keyReleased(KeyEvent key) {
        super.keyReleased(key);
        Wrapper.getEntityManager().getEntities().values().forEach(entity -> entity.keyReleased(key.getKeyCode()));
        this.interactables.forEach(entity -> entity.keyReleased(key.getKeyCode()));
    }

    public MapManager getGameMap() {
        return this.gameMap;
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

    public OrderRenderer getRenderer() {
        return this.renderer;
    }
}
