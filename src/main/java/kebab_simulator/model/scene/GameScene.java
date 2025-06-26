package kebab_simulator.model.scene;

import KAGO_framework.control.Drawable;
import KAGO_framework.control.Interactable;
import KAGO_framework.control.SoundController;
import KAGO_framework.view.DrawTool;
import kebab_simulator.Config;
import kebab_simulator.Wrapper;
import kebab_simulator.animation.Easings;
import kebab_simulator.graphics.CameraRenderer;
import kebab_simulator.graphics.OrderRenderer;
import kebab_simulator.graphics.map.MapManager;
import kebab_simulator.graphics.spawner.ObjectSpawner;
import kebab_simulator.graphics.spawner.table.TableItemIntegration;
import kebab_simulator.graphics.spawner.table.TableSpawner;
import kebab_simulator.graphics.spawner.table.TableStorageSpawner;
import kebab_simulator.graphics.tooltip.Tooltip;
import kebab_simulator.model.KeyManagerModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GameScene extends Scene {

    private final Logger logger = LoggerFactory.getLogger(GameScene.class);
    private CameraRenderer cameraRenderer;
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
        this.cameraRenderer = CameraRenderer
                .create(this.viewController, this.viewController.getProgramController(), 0, 0)
                .zoom(2)
                .smooth((x) -> Easings.easeInCubic(x));
        this.renderer = new OrderRenderer();

        Wrapper.getTooltipManager().register(
            new Tooltip(
                KeyManagerModel.KEY_TAKE_ITEM,
                (keyManager) -> {
                    if (TableSpawner.isCurrentlyFocused()) {
                        if (TableSpawner.getCurrentFocusedTable() instanceof TableItemIntegration) {
                            var t = ((TableItemIntegration) TableSpawner.getCurrentFocusedTable());
                            if (t == null) return null;

                            if (!t.getItems().isEmpty() && Wrapper.getLocalPlayer().getInventory().getItemInHand() == null)
                                return "Gegenstand aufheben";

                            else if (t.getItems().isEmpty() && Wrapper.getLocalPlayer().getInventory().getItemInHand() != null)
                                return "Gegenstand fallen lassen";

                        } else if (TableSpawner.getCurrentFocusedTable() instanceof TableStorageSpawner) {
                            if (Wrapper.getLocalPlayer().getInventory().getItemInHand() != null) return null;
                            return "Gegenstand aufheben";
                        }
                    }

                    return null;
                }
            )
        );
    }

    public void updatePhysics(double dt) {
        this.cameraRenderer.update(dt);
        Wrapper.getEntityManager().getEntities().values().forEach(e -> e.update(dt));
    }

    @Override
    public void update(double dt) {
        this.getDrawables().forEach(d -> d.update(dt));
        for (ObjectSpawner<?> spawner : ObjectSpawner.objects) {
            spawner.update(dt);
        }
        Wrapper.getTooltipManager().update(dt);
        Wrapper.getGameHandler().update(dt);
        super.update(dt);
    }

    public void drawGame(DrawTool drawTool) {
        this.cameraRenderer.attach(drawTool);
        if (this.gameMap != null) {
            drawTool.push();
            this.gameMap.draw(drawTool);
            drawTool.pop();
        }

        GameScene.getInstance().getRenderer().draw(drawTool);
        this.getDrawables().forEach(d -> d.draw(drawTool));

        /*Wrapper.getColliderManager().getColliders().values().forEach(r -> {
            r.drawHitbox(drawTool);
        });*/

        this.gameMap.drawAfterPlayer(drawTool);

        this.cameraRenderer.detach(drawTool);
    }

    @Override
    public void draw(DrawTool drawTool) {
        GameScene.getInstance().drawGame(drawTool);
        Wrapper.getGameHandler().draw(drawTool);
        Wrapper.getTooltipManager().draw(drawTool);
        super.draw(drawTool);
        drawTool.setCurrentColor(new Color(154, 75, 24), 50);
        drawTool.drawFilledRectangle(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        drawTool.resetColor();
    }

    @Override
    public void onOpen(Scene scene) {
        SoundController.playSound("background");
    }

    @Override
    public void onClose(Scene scene) {
        SoundController.stopSound("background");
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
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        Wrapper.getEntityManager().getEntities().values().forEach(entity -> entity.mouseMoved(e));
        this.interactables.forEach(entity -> entity.mouseMoved(e));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        Wrapper.getEntityManager().getEntities().values().forEach(entity -> entity.mouseDragged(e));
        this.interactables.forEach(entity -> entity.mouseDragged(e));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        Wrapper.getEntityManager().getEntities().values().forEach(entity -> entity.keyPressed(e));
        this.interactables.forEach(entity -> entity.keyPressed(e));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
        Wrapper.getEntityManager().getEntities().values().forEach(entity -> entity.keyReleased(e));
        this.interactables.forEach(entity -> entity.keyReleased(e));
    }

    public MapManager getGameMap() {
        return this.gameMap;
    }

    public void setGameMap(MapManager gameMap) {
        this.gameMap = gameMap;
    }

    public CameraRenderer getCameraRenderer() {
        return this.cameraRenderer;
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
