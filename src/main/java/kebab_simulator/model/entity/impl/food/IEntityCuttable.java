package kebab_simulator.model.entity.impl.food;

import KAGO_framework.view.DrawTool;
import kebab_simulator.Wrapper;
import kebab_simulator.animation.Easings;
import kebab_simulator.animation.tween.Tween;
import kebab_simulator.graphics.spawner.table.TableKnifeSpawner;
import kebab_simulator.model.entity.impl.EntityItem;
import kebab_simulator.model.sound.SoundManager;
import kebab_simulator.model.visual.VisualConstants;

import java.awt.*;

public interface IEntityCuttable {
    Font timeFont = VisualConstants.getFont(10);
    double duration = 0.5;
    double start = 0.8;
    double target = 1.0;
    Tween scalingTween = Tween
            .to(start, target, duration)
            .ease((x) -> Easings.easeOutElastic(x));

    EntityCuttingState getCuttingState();
    double getCuttingProgress();
    double getCuttingDuration();
    boolean allowCutting();
    default void cut() {
        int tableIndex = ((TableKnifeSpawner) ((EntityItem) this).getLocation()).getId().getIndex() - 1;
        SoundManager.playSound(Wrapper.getSoundConstants().SOUND_CUTTING[tableIndex], true);
    }
    default void stopCut() {
        int tableIndex = ((TableKnifeSpawner) ((EntityItem) this).getLocation()).getId().getIndex() - 1;
        SoundManager.stopSound(Wrapper.getSoundConstants().SOUND_CUTTING[tableIndex]);
    }
    default void drawCuttingProgress(double x, double y, DrawTool drawTool) {
        boolean b = (this.getCuttingState() == EntityCuttingState.IDLE && this.getCuttingProgress() > 0);
        if (this.getCuttingState() == EntityCuttingState.CUTTING || b) {
            double width = 40;
            double height = 10d;
            float stroke = 2.0f;
            double progress = Easings.easeOutQuad(this.getCuttingProgress());
            double timeLeft = this.getCuttingDuration() - (this.getCuttingDuration() * this.getCuttingProgress());
            if (scalingTween.isFinished()) {
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


        }
    }

    enum EntityCuttingState {
        IDLE,
        CUTTING,
        CUT
    }
}
