package kebab_simulator.model.scene;

import KAGO_framework.control.Drawable;
import KAGO_framework.control.Interactable;
import KAGO_framework.view.DrawTool;
import kebab_simulator.Config;
import kebab_simulator.Wrapper;
import kebab_simulator.model.visual.VisualConstants;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class LoadingScene extends Scene {

    private final List<Drawable> drawables;
    private final List<Interactable> interactables;
    private double loadingProgress = 0;  // Wert zwischen 0.0 und 1.0
    private boolean loadingComplete = false;

    public LoadingScene() {
        super("loading");
        this.drawables = new ArrayList<>();
        this.interactables = new ArrayList<>();
    }

    @Override
    public void update(double dt) {
        // Fortschritt erhöhen (max 1.0)
        if (loadingProgress < 1.0) {
            loadingProgress += dt * 0.25;  // Geschwindigkeit anpassen (0.25 = ca. 4s)
            if (loadingProgress >= 1.0) {
                loadingProgress = 1.0;
                loadingComplete = true;

                // Beispiel: Wechsle zur GameScene oder MainMenuScene
                // Wrapper.getSceneManager().changeScene(GameScene.getInstance());
            }
        }

        this.drawables.forEach(d -> d.update(dt));
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

        // Ladebalken
        int barWidth = 400;
        int barHeight = 20;
        int barX = Config.WINDOW_WIDTH / 2 - barWidth / 2;
        int barY = Config.WINDOW_HEIGHT - 150;
        int progressWidth = (int) (loadingProgress * barWidth);

        // Balkenhintergrund
        drawTool.setCurrentColor(Color.DARK_GRAY);
        drawTool.drawFilledRectangle(barX, barY, barWidth, barHeight);

        // Ladefortschritt
        drawTool.setCurrentColor(Color.ORANGE);
        drawTool.drawFilledRectangle(barX, barY, progressWidth, barHeight);

        // Prozentanzeige
        drawTool.setCurrentColor(Color.BLACK);
        drawTool.getGraphics2D().setFont(VisualConstants.getFont(VisualConstants.Fonts.PIXEL_FONT, 16));
        String percentText = (int) (loadingProgress * 100) + "%";
        drawTool.drawText(percentText, barX + barWidth / 2 - 15, barY - 10);

        // Andere Elemente
        this.drawables.forEach(d -> d.draw(drawTool));
        super.draw(drawTool);

        if (loadingComplete) {
            Scene.open(new StartScene());
        }
    }


    public List<Drawable> getDrawables() {
        return drawables;
    }

    public List<Interactable> getInteractables() {
        return interactables;
    }

    public boolean isLoadingComplete() {
        return loadingComplete;
    }
}