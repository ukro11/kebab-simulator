package kebab_simulator.model.entity.impl.food;

import KAGO_framework.control.SoundController;
import KAGO_framework.view.DrawTool;
import kebab_simulator.Wrapper;
import kebab_simulator.animation.Easings;
import kebab_simulator.animation.tween.Tween;
import kebab_simulator.model.visual.VisualConstants;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface IEntityCookable {
    Font timeFont = VisualConstants.getFont(10);
    double duration = 0.5;
    double start = 0.8;
    double target = 1.0;
    Tween scalingTween = Tween
            .to(start, target, duration)
            .ease((x) -> Easings.easeOutElastic(x));
    BufferedImage image = Wrapper.getImage("/graphic/icons/warn.png");

    EntityCookingState getCookingState();
    double getCookingProgress();
    double getCookingDuration();
    boolean allowCooking();
    void cook();
    void stopCook();
    default void drawCookingProgress(double x, double y, DrawTool drawTool) {
        boolean b = (this.getCookingState() == EntityCookingState.IDLE_COOKING && this.getCookingProgress() > 0);
        if (this.getCookingState() == EntityCookingState.COOKING || b) {
            double width = 40;
            double height = 10d;
            float stroke = 2.0f;
            double progress = Easings.easeOutQuad(this.getCookingProgress());
            double timeLeft = this.getCookingDuration() - (this.getCookingDuration() * this.getCookingProgress());

            if (scalingTween.isFinished()) {
                if ((double) scalingTween.getTweenValue().getTarget() == 1.0) {
                    scalingTween.redo(target, start, duration);
                    scalingTween.animate();
                } else {
                    scalingTween.redo(start, target, duration);
                    scalingTween.animate();
                }
            }
            if (this.getCookingState() == EntityCookingState.COOKED) {
                SoundController.stopSound("frying");
            }
            if (!scalingTween.isRunning()) {
                scalingTween.animate();
                SoundController.playSound("frying");
            }

            drawTool.push();

            double translateWidth = width + (stroke * 2) / 2 - 0.5;
            double translateHeight = height + (stroke * 2) / 2 - 0.5;
            double translateX = x - width / 2 - stroke / 2 + translateWidth / 2;
            double translateY = y - stroke / 2 + translateHeight / 2;

            drawTool.getGraphics2D().translate(translateX, translateY);
            drawTool.getGraphics2D().scale(scalingTween.getValueDouble(), scalingTween.getValueDouble());
            drawTool.getGraphics2D().translate(-translateX, -translateY);

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

        } else if (this.getCookingState() == EntityCookingState.BURNING) {
            if (scalingTween.isFinished()) {
                double duration = 1 - this.getCookingProgress() / this.getCookingDuration() * 1.5;
                if ((double) scalingTween.getTweenValue().getTarget() == 1.0) {
                    scalingTween.redo(target, start, duration);
                    scalingTween.animate();

                } else {
                    scalingTween.redo(start, target, duration);
                    scalingTween.animate();
                }
            }

            if (!scalingTween.isRunning()) {
                scalingTween.animate();
            }
            int imageX = (int) (x - 16 / 2);
            int imageY = (int) (y + 5 * scalingTween.getValueDouble());

            drawTool.push();
            drawTool.getGraphics2D().drawImage(image, imageX, imageY, 16, 16, null);
            drawTool.pop();
        }
    }

    enum EntityCookingState {
        IDLE, // IDLE = was never cooked
        IDLE_COOKING, // IDLE_COOKING = was cooking but is not now
        COOKING,
        COOKED,
        BURNING,
        BURNT
    }
}
