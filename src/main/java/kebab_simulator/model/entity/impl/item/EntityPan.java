package kebab_simulator.model.entity.impl.item;

import KAGO_framework.view.DrawTool;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.states.FocusAnimationState;
import kebab_simulator.model.entity.impl.food.EntityFood;
import kebab_simulator.physics.Collider;

public class EntityPan extends EntityPlate {

    public EntityPan(Collider collider) {
        super(collider, 0, 0, 32, 64);
        this.setRenderer(new AnimationRenderer(
                "/graphic/item/pan.png", 2, 1, 32, 64,
                FocusAnimationState.DEFAULT
        ));
    }

    @Override
    protected void drawEntity(DrawTool drawTool) {
        if (this.player == null) {
            if (this.renderer != null && this.renderer.getCurrentFrame() != null) {
                drawTool.push();
                drawTool.getGraphics2D().rotate(Math.toRadians(this.rotation), this.getX() + this.width / 2, this.getY() + 16);
                drawTool.getGraphics2D().drawImage(this.renderer.getCurrentFrame(), (int) this.getX(), (int) this.getY(), (int) this.width, (int) this.height, null);
                drawTool.pop();
            }
        }
    }

    @Override
    public void addItem(EntityFood item) {
        if (this.getItems().size() < 1) {
            super.addItem(item);
        }
    }
}
