package kebab_simulator.model.scene;

import KAGO_framework.control.Drawable;
import KAGO_framework.control.Interactable;
import KAGO_framework.view.DrawTool;
import kebab_simulator.Config;
import kebab_simulator.model.KeyManagerModel;
import kebab_simulator.model.visual.VisualConstants;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class StartScene extends Scene {

    private final List<Drawable> drawables;
    private final List<Interactable> interactables;

    public StartScene() {
        super("start");
        this.drawables = new ArrayList<>();
        this.interactables = new ArrayList<>();
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void draw(DrawTool drawTool) {
        // Hintergrund
        drawTool.setCurrentColor(new Color(238, 195, 154));
        drawTool.drawFilledRectangle(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        // Titel (zentriert)
        drawTool.setCurrentColor(VisualConstants.TEXT_COLOR);
        drawTool.getGraphics2D().setFont(VisualConstants.getFont(VisualConstants.Fonts.PIXEL_FONT, 100));
        String title = "Kebab      Simulator";
        int titleX = Config.WINDOW_WIDTH / 2 - 270;
        int titleY = Config.WINDOW_HEIGHT / 2 - 100;

        drawTool.drawText("          Kebab", titleX, titleY);
        drawTool.drawText("Simulator", titleX, titleY + 80);

        int keyX = Config.WINDOW_WIDTH / 2 - 150;
        int keyY = Config.WINDOW_HEIGHT - 200;

        drawTool.getGraphics2D().drawImage(KeyManagerModel.KEY_START_GAME.getIcon(), keyX, keyY, 40, 40, null);

        drawTool.setCurrentColor(Color.BLACK);
        drawTool.getGraphics2D().setFont(VisualConstants.getFont(VisualConstants.Fonts.PIXEL_FONT, 18));
        drawTool.drawText(KeyManagerModel.KEY_START_GAME.getDescription(), keyX + 50, keyY + 28);
    }
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            Scene.open(GameScene.getInstance());
        }
    }


}