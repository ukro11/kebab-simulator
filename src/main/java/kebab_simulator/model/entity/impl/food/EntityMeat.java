package kebab_simulator.model.entity.impl.food;

import KAGO_framework.view.DrawTool;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.states.entity.MeatAnimationState;
import kebab_simulator.model.entity.impl.item.EntityPlate;
import kebab_simulator.model.meal.ingredients.IngredientMeat;
import kebab_simulator.physics.BodyType;
import kebab_simulator.physics.colliders.ColliderRectangle;

public class EntityMeat extends EntityFood implements IEntityCookable, IEntityCuttable {

    private EntityCuttingState cuttingState;
    private double cuttingProgress;
    private double cuttingElapsed;

    private EntityCookingState cookingState;
    private double cookingProgress;
    private double cookingElapsed;

    private double scale;

    public EntityMeat() {
        this(0, 0);
    }

    public EntityMeat(double x, double y) {
        super(new ColliderRectangle(BodyType.DYNAMIC, x, y, 32, 32), 0, 0, 32, 32);
        this.cuttingState = EntityCuttingState.IDLE;
        this.cuttingProgress = 0;
        this.cookingState = EntityCookingState.IDLE;
        this.cookingProgress = 0;
        this.scale = 0.9;
        this.setRenderer(new AnimationRenderer(
            "/graphic/item/food/meat.png", 2, 1, 32, 32,
            MeatAnimationState.RAW_DEFAULT
        ));
    }

    @Override
    public boolean allowPlaceOnPlate() {
        return this.cuttingState == EntityCuttingState.CUT && this.cookingState == EntityCookingState.COOKED;
    }

    @Override
    public double zIndex() {
        if (this.location instanceof EntityPlate) return super.zIndex() + 32 + (this.location.getItems().indexOf(this) + 1);
        return super.zIndex() + 32;
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (this.cuttingState == EntityCuttingState.CUTTING) {
            this.cuttingElapsed += dt;
            this.cuttingProgress = this.cuttingElapsed / this.getCuttingDuration();

            if (this.cuttingProgress >= 1) {
                this.cuttingProgress = 1;
                this.cuttingState = EntityCuttingState.CUT;
            }

        } else if (this.cuttingState == EntityCuttingState.CUT) {
            this.renderer.switchState(MeatAnimationState.RAW_CUT);
        }
    }

    @Override
    protected void drawEntity(DrawTool drawTool) {
        if (this.player == null && this.renderer != null && this.renderer.getCurrentFrame() != null) {
            drawTool.push();
            drawTool.getGraphics2D().translate(this.getX() + this.width / 2, this.getY() + this.height / 2);
            drawTool.getGraphics2D().scale(this.scale, this.scale);
            drawTool.getGraphics2D().rotate(Math.toRadians(this.rotation));
            drawTool.getGraphics2D().translate(-(this.getX() + this.width / 2), -(this.getY() + this.height / 2));
            drawTool.getGraphics2D().drawImage(
                this.renderer.getCurrentFrame(),
                (int) this.getX(),
                (int) this.getY(),
                (int) this.width,
                (int) this.height,
                null
            );
            drawTool.pop();
            this.drawInteraction(this.body.getX() + this.width / 2, this.body.getY() - 15, drawTool);
        }
    }

    @Override
    public EntityCookingState getCookingState() {
        return this.cookingState;
    }

    @Override
    public double getCookingProgress() {
        return this.cookingProgress;
    }

    @Override
    public double getCookingDuration() {
        return IngredientMeat.getInstance().getTargetState().getCookDuration();
    }

    @Override
    public boolean allowCooking() {
        return this.cookingState == EntityCookingState.IDLE || this.cookingState == EntityCookingState.COOKING;
    }

    @Override
    public void cook() {

    }

    @Override
    public void stopCook() {

    }

    @Override
    public EntityCuttingState getCuttingState() {
        return this.cuttingState;
    }

    @Override
    public double getCuttingProgress() {
        return this.cuttingProgress;
    }

    @Override
    public double getCuttingDuration() {
        return IngredientMeat.getInstance().getTargetState().getCutDuration();
    }

    @Override
    public boolean allowCutting() {
        return this.cuttingState == EntityCuttingState.IDLE || this.cuttingState == EntityCuttingState.CUTTING;
    }

    @Override
    public void cut() {
        if (this.cuttingState != EntityCuttingState.CUT) {
            this.cuttingState = EntityCuttingState.CUTTING;
        }
    }

    @Override
    public void stopCut() {
        if (this.cuttingState != EntityCuttingState.CUT) {
            this.cuttingState = EntityCuttingState.IDLE;
        }
    }

    public double getScale() {
        return this.scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }
}
