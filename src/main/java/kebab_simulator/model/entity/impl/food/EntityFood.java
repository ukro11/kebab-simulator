package kebab_simulator.model.entity.impl.food;

import KAGO_framework.control.SoundController;
import KAGO_framework.view.DrawTool;
import kebab_simulator.animation.IAnimationState;
import kebab_simulator.model.entity.impl.EntityItem;
import kebab_simulator.model.entity.impl.player.EntityPlayer;
import kebab_simulator.physics.Collider;

public abstract class EntityFood<T extends Enum<T> & IAnimationState> extends EntityItem<T> {

    protected boolean renderSmall = false;

    public EntityFood(Collider collider, double x, double y, double width, double height) {
        super(collider, x, y, width, height);
        this.exitOnWrongRegistration();
    }

    public abstract boolean allowPlaceOnPlate();

    @Override
    protected void drawEntity(DrawTool drawTool) {
        super.drawEntity(drawTool);
        // TODO: Progress bar
        if (this instanceof IEntityCuttable) {
            ((IEntityCuttable) this).drawCuttingProgress(this.body.getX(), this.body.getY() - 10, drawTool);
        }
        if (this instanceof IEntityCookable) {
            ((IEntityCookable) this).drawCookingProgress(this.body.getX(), this.body.getY() - 10, drawTool);
        }
    }

    public boolean shouldRenderSmall() {
        return this.renderSmall;
    }

    public void renderSmall(boolean flag) {
        this.renderSmall = flag;
    }

    public void onPickFromPlate(EntityPlayer player) {
        this.player = player;
        this.rotation = 0;
    }

    public void onDropFromPlate() {
        this.player = null;
        //this.positionItem((EntityPlate.class.cast(this.location)).getLocation());
        this.positionItem(this.location);
    }
}
