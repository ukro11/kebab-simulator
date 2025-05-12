package kebab_simulator.model.visual.impl.component;

import KAGO_framework.view.DrawTool;
import kebab_simulator.control.ProgramController;
import kebab_simulator.model.visual.VisualModel;
import kebab_simulator.utils.TimerUtils;

import java.awt.*;

public class InfoVisual extends VisualModel {

    public InfoVisual() {
        super("fps-component");
    }

    @Override
    public void draw(DrawTool drawTool) {
        drawTool.setCurrentColor(Color.BLACK);
        drawTool.drawText(20, 20, String.format("FPS: %s", TimerUtils.getFPS()));
        if (ProgramController.player != null) {
            drawTool.drawText(20, 40, String.format("x: %.2f", ProgramController.player.getX()));
            drawTool.drawText(20, 60, String.format("y: %.2f", ProgramController.player.getY()));
        }
        drawTool.resetColor();
    }

    @Override
    public void update(double dt) {}
}
