package kebab_simulator.model.scene;

import KAGO_framework.view.DrawTool;
import kebab_simulator.model.visual.VisualConstants;

import java.awt.*;

public class LoadingScene extends Scene {

    private double progress = 0;
    private double elapsed = 0;
    private double duration = 8;
    private Font font;

    public LoadingScene() {
        super("loading");
        this.font = VisualConstants.getFont(28);
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (elapsed < duration) {
            this.elapsed += dt;
            this.progress = this.elapsed / this.duration;
        }
    }

    @Override
    public void draw(DrawTool drawTool) {
        // Hintergrund
        drawTool.setCurrentColor(30, 30, 30, 255); // dunkles Grau
        drawTool.drawFilledRectangle(0, 0, 800, 600);

        // Text
        drawTool.setCurrentColor(255, 255, 255, 255);
        drawTool.getGraphics2D().setFont(this.font);
        drawTool.drawCenteredText("LÄDT...", 0, -50, 800, 600);

        // Ladebalken-Hintergrund
        drawTool.setCurrentColor(100, 100, 100, 255);
        drawTool.drawFilledRectangle(250, 300, 300, 30);

        drawTool.setCurrentColor(0, 200, 0, 255);
        drawTool.drawFilledRectangle(250, 300, 300 * progress, 30);

        // Rahmen
        drawTool.setCurrentColor(255, 255, 255, 255);
        drawTool.drawRectangle(250, 300, 300, 30);
    }
}
