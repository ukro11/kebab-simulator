package kebab_simulator.model.entity.impl.item;

import KAGO_framework.view.DrawTool;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.states.FocusAnimationState;
import kebab_simulator.graphics.spawner.table.TableSpawner;
import kebab_simulator.model.entity.impl.EntityItem;
import kebab_simulator.model.entity.impl.EntityItemLocation;
import kebab_simulator.model.entity.impl.food.EntityFood;
import kebab_simulator.model.entity.impl.player.EntityPlayer;
import kebab_simulator.model.meal.MealModel;
import kebab_simulator.physics.BodyType;
import kebab_simulator.physics.Collider;

import java.util.concurrent.CopyOnWriteArrayList;

public class EntityPlate extends EntityItem<FocusAnimationState> implements EntityItemLocation<EntityFood> {

    protected CopyOnWriteArrayList<EntityFood> ingredients;
    protected MealModel mealModel;

    public EntityPlate(Collider collider) {
        this(collider, 0, 0, 32, 32);
        this.setRenderer(new AnimationRenderer(
                "/graphic/item/plate.png", 2, 1, 32, 32,
                FocusAnimationState.DEFAULT
        ));
    }

    protected EntityPlate(Collider collider, double x, double y, double width, double height) {
        super(collider, x, y, width, height);
        collider.setType(BodyType.DYNAMIC);
        this.ingredients = new CopyOnWriteArrayList<>();
        this.rotation = 0;
        this.showHitbox = false;
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        this.updatePlate(dt);
    }

    protected void updatePlate(double dt) {
        if (this.renderer != null) {
            if (this.location != null && this.location instanceof TableSpawner && TableSpawner.isTableFocused((TableSpawner) this.location)) {
                this.renderer.switchState(FocusAnimationState.FOCUS);

            } else {
                this.renderer.switchState(FocusAnimationState.DEFAULT);
            }
        }
    }

    @Override
    protected void drawEntity(DrawTool drawTool) {
        if (this.player == null) {
            if (this.renderer != null && this.renderer.getCurrentFrame() != null) {
                drawTool.push();
                drawTool.getGraphics2D().rotate(Math.toRadians(this.rotation), this.getX() + this.width / 2, this.getY() + this.height / 2);
                drawTool.getGraphics2D().drawImage(this.renderer.getCurrentFrame(), (int) this.getX(), (int) this.getY(), (int) this.width, (int) this.height, null);
                if (this.mealModel != null) {
                    drawTool.getGraphics2D().drawImage(this.mealModel.getIcon(), (int) this.getX(), (int) this.getY(), (int) this.mealModel.getIcon().getWidth(), (int) this.mealModel.getIcon().getHeight(), null);
                }
                drawTool.pop();
            }
        }
    }

    @Override
    public void onPick(EntityPlayer player) {
        super.onPick(player);
        this.ingredients.forEach(ing -> ing.onPickFromPlate(player));
    }

    @Override
    public void onDrop(EntityItemLocation location) {
        super.onDrop(location);
        this.positionItem(location);
        this.ingredients.forEach(ing -> ing.onDropFromPlate());
    }

    @Override
    public void destroy() {
        super.destroy();
        this.ingredients.forEach(ing -> ing.destroy());
        this.ingredients.clear();
    }

    public void convertToMeal() {
        this.mealModel = MealModel.findMeal(this);
    }

    @Override
    public Collider getCollider() {
        return this.body;
    }

    @Override
    public CopyOnWriteArrayList<EntityFood> getItems() {
        return this.ingredients;
    }

    @Override
    public EntityFood nextToRemove() {
        return this.ingredients.isEmpty() ? null : this.ingredients.get(this.ingredients.size() - 1);
    }

    @Override
    public void addItem(EntityFood item) {
        if (this.ingredients.stream().filter(ing -> ing.getClass().equals(item.getClass())).findFirst().orElse(null) != null) return;

        this.ingredients.add(item);
        item.getBody().setType(BodyType.DYNAMIC);
        this.getCollider().getChildren().add(item.getBody().getChildInstance());
        item.setLocation(this);
        item.positionItem(this.getLocation());
        this.convertToMeal();
    }

    @Override
    public void removeItem(EntityFood item) {
        this.ingredients.remove(item);
        this.getCollider().getChildren().remove(item.getBody().getChildInstance());
        this.convertToMeal();
    }

    @Override
    public void setItems(CopyOnWriteArrayList<EntityFood> items) {
        this.ingredients = items;
    }

    @Override
    public double getRotation() {
        return this.rotation;
    }

    public MealModel getMealModel() {
        return this.mealModel;
    }

    @Override
    public double zIndex() {
        return super.zIndex() + 32;
    }
}
