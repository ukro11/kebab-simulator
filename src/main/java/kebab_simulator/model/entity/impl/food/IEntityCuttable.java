package kebab_simulator.model.entity.impl.food;

import KAGO_framework.view.DrawTool;
import kebab_simulator.Wrapper;
import kebab_simulator.animation.Easings;
import kebab_simulator.model.visual.VisualConstants;

import java.awt.*;

public interface IEntityCuttable {
    Font timeFont = VisualConstants.getFont(10);

    EntityCuttingState getCuttingState();
    double getCuttingProgress();
    double getCuttingDuration();
    boolean allowCutting();
    void cut();
    void stopCut();
    default void drawInteraction(double x, double y, DrawTool drawTool) {
        boolean b = (this.getCuttingState() == EntityCuttingState.IDLE && this.getCuttingProgress() > 0);
        if (this.getCuttingState() == EntityCuttingState.CUTTING || b) {
            double width = 40;
            double height = 10d;
            float stroke = 2.0f;
            double progress = Easings.easeOutQuad(this.getCuttingProgress());
            double timeLeft = this.getCuttingDuration() - (this.getCuttingDuration() * this.getCuttingProgress());

            drawTool.push();
            drawTool.setCurrentColor(Color.decode("#4c7247"));
            drawTool.drawFilledRectangle(x - width / 2, y, width, height);

            drawTool.setCurrentColor(Color.decode("#65c658"));
            drawTool.drawFilledRectangle(x - width / 2, y, width * progress, height);

            drawTool.setCurrentColor(Color.decode("#2c211a"));
            drawTool.getGraphics2D().setStroke(new BasicStroke(stroke));

            drawTool.drawRectangle(x - width / 2 - stroke / 2, y - stroke / 2, width + (stroke * 2) / 2 - 0.5, height + (stroke * 2) / 2 - 0.5);

            drawTool.setCurrentColor(Color.decode("#2c211a"));
            drawTool.getGraphics2D().setFont(timeFont);
            drawTool.drawCenteredText(String.format("%dS", (int) timeLeft), x - width / 2, y - stroke / 2 - 1, width, height + (stroke * 2) / 2);

            drawTool.pop();
        }
    }

    enum EntityCuttingState {
        IDLE,
        CUTTING,
        CUT
    }
}
