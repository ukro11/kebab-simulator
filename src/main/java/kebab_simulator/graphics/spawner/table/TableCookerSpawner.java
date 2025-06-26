package kebab_simulator.graphics.spawner.table;

import KAGO_framework.view.DrawTool;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.states.spawner.CookerAnimationState;
import kebab_simulator.Wrapper;
import kebab_simulator.graphics.spawner.ObjectIdResolver;
import kebab_simulator.model.entity.Entity;
import kebab_simulator.model.entity.impl.EntityItem;
import kebab_simulator.model.entity.impl.food.EntityFood;
import kebab_simulator.model.entity.impl.food.IEntityCookable;
import kebab_simulator.model.entity.impl.food.IEntityCuttable;
import kebab_simulator.model.entity.impl.item.EntityPan;
import kebab_simulator.model.entity.impl.item.EntityPlate;
import kebab_simulator.model.sound.SoundManager;
import kebab_simulator.physics.Collider;

public class TableCookerSpawner extends TableItemIntegration {

    public TableCookerSpawner(ObjectIdResolver id, Collider collider) {
        super(
            id,
            collider,
            new AnimationRenderer<CookerAnimationState>(
                "/graphic/map/sprites/table/table_cooker.png",
                2,
                2,
                32,
                64,
                CookerAnimationState.OFF
            )
        );

        this.directionToLook = Entity.EntityDirection.TOP;
    }

    @Override
    public boolean filterItem(TableItemDropEvent event, EntityItem item) {
        if (event != TableItemDropEvent.ITEM_TABLE_DROP && item instanceof IEntityCookable) {
            if (item instanceof IEntityCuttable) {
                if (((IEntityCuttable) item).getCuttingState() != IEntityCuttable.EntityCuttingState.CUT) return false;
            }
            return true;
        }
        if (item instanceof EntityPan) return true;
        return false;
    }

    @Override
    public void onPickItem() {
        if (!this.getPan().getItems().isEmpty()) {
            ((IEntityCookable) this.getPan().getItems().get(0)).stopCook();
            SoundManager.stopSound(Wrapper.getSoundConstants().SOUND_FRYING[this.id.getIndex() - 1]);
        }
        super.onPickItem();
    }

    @Override
    public void onDropItem(TableItemDropEvent event) {
        if (event == TableItemDropEvent.ITEM_PLATE_DROP) {
            var item = (IEntityCookable) Wrapper.getLocalPlayer().getInventory().getItemAsFood();
            item.cook();
            SoundManager.playSound(Wrapper.getSoundConstants().SOUND_FRYING[this.id.getIndex() - 1], true);
            Wrapper.getLocalPlayer().getInventory().dropItem(((EntityPlate) this.items.get(0)));

        } else if (event == TableItemDropEvent.ITEM_TABLE_DROP) {
            if (!this.filterItem(event, Wrapper.getLocalPlayer().getInventory().getItemInHand())) return;
            if (Wrapper.getLocalPlayer().getInventory().getItemInHand() instanceof EntityPan
                    && !Wrapper.getLocalPlayer().getInventory().getItemAsPlate().getItems().isEmpty()) {
                ((IEntityCookable) Wrapper.getLocalPlayer().getInventory().getItemAsPlate().getItems().get(0)).cook();
                SoundManager.playSound(Wrapper.getSoundConstants().SOUND_FRYING[this.id.getIndex() - 1], true);
            }
            Wrapper.getLocalPlayer().getInventory().dropItem(this);
        }
    }

    private boolean isCookerEnabled() {
        return !this.items.isEmpty() && this.getPan() != null && !this.getPan().getItems().isEmpty();
    }

    @Override
    public boolean allowFocus() {
        var inHand = Wrapper.getLocalPlayer().getInventory().getItemInHand();
        // When player wants to pick up the pan without any food
        if (!this.items.isEmpty() && inHand == null) return true;
        // When player puts pan back
        if (this.items.isEmpty() && inHand != null && inHand instanceof EntityPan) return true;
        // When player wants to pick up the pan
        if (!this.items.isEmpty() && !this.getPan().getItems().isEmpty() && inHand == null) return true;
        // When player wants to drop food on the pan
        if (!this.items.isEmpty() && this.getPan().getItems().isEmpty() && inHand != null && inHand instanceof EntityFood) {
            if (inHand instanceof IEntityCuttable) {
                if (((IEntityCuttable) inHand).getCuttingState() != IEntityCuttable.EntityCuttingState.CUT) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (this.isCookerEnabled()) {
            if (TableSpawner.isTableFocused(this)) {
                this.renderer.switchState(CookerAnimationState.ON_FOCUS);

            } else {
                this.renderer.switchState(CookerAnimationState.ON);
            }

        } else {
            if (TableSpawner.isTableFocused(this)) {
                this.renderer.switchState(CookerAnimationState.OFF_FOCUS);

            } else {
                this.renderer.switchState(CookerAnimationState.OFF);
            }
        }
    }

    @Override
    public void draw(DrawTool drawTool) {
        drawTool.drawImage(this.renderer.getCurrentFrame(), this.collider.getX(), this.collider.getY() - 1);
    }

    public EntityPan getPan() {
        return !this.items.isEmpty() ? (EntityPan) this.items.get(0) : null;
    }

    public enum TableCookerState {
        ON,
        OFF
    }
}
