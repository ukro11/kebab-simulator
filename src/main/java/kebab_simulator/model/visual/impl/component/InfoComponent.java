package kebab_simulator.model.visual.impl.component;

import KAGO_framework.view.DrawTool;
import kebab_simulator.Wrapper;
import kebab_simulator.model.visual.VisualConstants;
import kebab_simulator.model.visual.VisualModel;
import kebab_simulator.utils.misc.TimerUtils;

import java.awt.*;

public class InfoComponent extends VisualModel {

    private final Font debugFont;
    private final double margin;

    public InfoComponent() {
        super("fps-component");
        double size = 24;
        this.debugFont = VisualConstants.getFont(VisualConstants.Fonts.DEBUG_FONT, size);
        this.margin = size + 5;
    }

    @Override
    public void draw(DrawTool drawTool) {
        drawTool.push();
        drawTool.setCurrentColor(new Color(47, 29, 3));
        drawTool.getGraphics2D().setFont(this.debugFont);
        drawTool.drawText(String.format("FPS: %s", TimerUtils.getFPS()), 20, 20 + this.margin);
        if (Wrapper.getLocalPlayer() != null) {
            drawTool.drawText(String.format("X: %.2f", Wrapper.getLocalPlayer().getBody().getX()), 20, 20 + this.margin * 2);
            drawTool.drawText(String.format("Y: %.2f", Wrapper.getLocalPlayer().getBody().getY()), 20, 20 + this.margin * 3);
        }
        drawTool.resetColor();
        drawTool.pop();
    }

    @Override
    public void update(double dt) {}
}
