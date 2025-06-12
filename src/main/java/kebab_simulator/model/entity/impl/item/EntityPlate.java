package kebab_simulator.model.entity.impl.item;

import KAGO_framework.view.DrawTool;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.states.FocusAnimationState;
import kebab_simulator.model.entity.impl.EntityItem;
import kebab_simulator.model.entity.impl.EntityItemLocation;
import kebab_simulator.model.entity.impl.food.EntityFood;
import kebab_simulator.graphics.spawner.table.TableSpawner;
import kebab_simulator.model.entity.impl.player.EntityPlayer;
import kebab_simulator.physics.BodyType;
import kebab_simulator.physics.Collider;

import java.util.concurrent.CopyOnWriteArrayList;

public class EntityPlate extends EntityItem<FocusAnimationState> implements EntityItemLocation<EntityFood> {

    private CopyOnWriteArrayList<EntityFood> ingredients;

    public EntityPlate(Collider collider) {
        super(collider, 0, 0, 32, 32);
        collider.setType(BodyType.DYNAMIC);
        this.setRenderer(new AnimationRenderer(
            "/graphic/item/plate.png", 2, 1, 32, 32,
            FocusAnimationState.DEFAULT
        ));
        this.ingredients = new CopyOnWriteArrayList<>();
        this.rotation = 0;
        this.showHitbox = false;
    }

    @Override
    public void update(double dt) {
        super.update(dt);
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
        this.ingredients.add(item);
        item.getBody().setType(BodyType.DYNAMIC);
        this.getCollider().getChildren().add(item.getBody().getChildInstance());
        item.setLocation(this);
        item.positionItem(this.getLocation());
    }

    @Override
    public void removeItem(EntityFood item) {
        this.ingredients.remove(item);
        this.getCollider().getChildren().remove(item.getBody().getChildInstance());
    }

    @Override
    public void setItems(CopyOnWriteArrayList<EntityFood> items) {
        this.ingredients = items;
    }

    @Override
    public double zIndex() {
        return super.zIndex() + 32;
    }

    public CopyOnWriteArrayList<EntityFood> getIngredients() {
        return this.ingredients;
    }
}
