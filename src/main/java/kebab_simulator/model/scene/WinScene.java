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

public class WinScene extends Scene {

    private final List<Drawable> drawables;
    private final List<Interactable> interactables;

    public WinScene() {
        super("lose");
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

        drawTool.drawCenteredText("Gewonnen!!!".toUpperCase(), 0, -130, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        int keyX = Config.WINDOW_WIDTH / 2 - 210 + KeyManagerModel.KEY_RESTART_GAME.getIcon().getWidth() / 2;
        int keyY = Config.WINDOW_HEIGHT - 210;

        drawTool.getGraphics2D().drawImage(KeyManagerModel.KEY_RESTART_GAME.getIcon(), keyX, keyY, 40, 40, null);

        drawTool.setCurrentColor(Color.BLACK);
        drawTool.getGraphics2D().setFont(VisualConstants.getFont(VisualConstants.Fonts.PIXEL_FONT, 18));
        drawTool.drawCenteredText(KeyManagerModel.KEY_RESTART_GAME.getDescription().toUpperCase(), KeyManagerModel.KEY_RESTART_GAME.getIcon().getWidth() / 2 + 20, (keyY - Config.WINDOW_HEIGHT / 2) + 20, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
    }
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            Scene.open(new LoadingScene());
        }
    }


}