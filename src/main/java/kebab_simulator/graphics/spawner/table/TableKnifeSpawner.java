package kebab_simulator.graphics.spawner.table;

import KAGO_framework.view.DrawTool;
import kebab_simulator.Wrapper;
import kebab_simulator.animation.Animation;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.states.entity.MeatAnimationState;
import kebab_simulator.animation.states.spawner.TableKnifeAnimationState;
import kebab_simulator.event.services.process.EventPostGameLoadingProcess;
import kebab_simulator.graphics.IOrderRenderer;
import kebab_simulator.graphics.spawner.ObjectIdResolver;
import kebab_simulator.graphics.tooltip.Tooltip;
import kebab_simulator.model.KeyManagerModel;
import kebab_simulator.model.entity.impl.food.EntityMeat;
import kebab_simulator.model.entity.impl.food.IEntityCuttable;
import kebab_simulator.model.entity.impl.player.EntityPlayer;
import kebab_simulator.model.scene.GameScene;
import kebab_simulator.physics.Collider;

import java.awt.event.KeyEvent;

public class TableKnifeSpawner extends TableItemIntegration implements ITableSide {

    public TableKnifeSpawner(ObjectIdResolver id, Collider collider) {
        super(id, collider, new AnimationRenderer<TableKnifeAnimationState>(
                "/graphic/map/sprites/table/table_knife_spritesheet.png",
                4,
                12,
                64,
                64,
                TableKnifeAnimationState.DEFAULT
        ));

        Wrapper.getProcessManager().queue(new EventPostGameLoadingProcess<>("Registering Tooltip (Cutting)", () -> {
            Wrapper.getTooltipManager().register(
                new Tooltip(
                    KeyManagerModel.KEY_CUT_FOOD,
                    (keyManager) -> {
                        if (TableSpawner.isTableFocused(this)
                                && !this.items.isEmpty()
                                && ((IEntityCuttable) this.items.get(0)).getCuttingState() != IEntityCuttable.EntityCuttingState.CUT) return KeyManagerModel.KEY_CUT_FOOD.getDescription();
                        return null;
                    }
                )
            );
        }));

        TableKnifeSpawner knifeSpawner = this;
        GameScene.getInstance().getRenderer().register(new IOrderRenderer() {
            @Override
            public double zIndex() {
                return knifeSpawner.zIndex() + 32 * 3;
            }
            @Override
            public void draw(DrawTool drawTool) {
                Animation<TableKnifeAnimationState> animation = knifeSpawner.getKnifeAnimation();
                drawTool.drawImage(
                    animation.getFrames().get(knifeSpawner.renderer.getCurrentIndex()),
                    knifeSpawner.collider.getX() - 16,
                    knifeSpawner.collider.getY() - 16
                );
            }
        });
    }

    @Override
    public void onFocusLost() {
        this.renderer.switchState(TableKnifeAnimationState.DEFAULT);
        if (!this.items.isEmpty()) ((IEntityCuttable) this.items.get(0)).stopCut();
    }

    @Override
    public void onInteractTable(EntityPlayer player, KeyEvent event) {
        super.onInteractTable(player, event);
        if (event.getKeyCode() == KeyManagerModel.KEY_CUT_FOOD.getKey()
                && this.renderer.getCurrentAnimation().getState() != TableKnifeAnimationState.CUT) {
            this.renderer.switchState(TableKnifeAnimationState.CUT);
            ((IEntityCuttable) this.items.get(0)).cut();
        }
    }

    @Override
    public void onPickItem() {
        if (!this.items.isEmpty()) {
            var item = this.items.get(0);
            if (item instanceof EntityMeat) {
                ((EntityMeat) item).setScale(0.9);
            }
        }
        super.onPickItem();
    }

    @Override
    public void onDropItem(TableItemDropEvent event) {
        super.onDropItem(event);
        if (!this.items.isEmpty()) {
            var item = this.items.get(0);
            if (item instanceof EntityMeat && item.getRenderer().getCurrentAnimation().getState() == MeatAnimationState.RAW_CUT) {
                ((EntityMeat) item).setScale(0.7);
            }
        }
    }

    private Animation<TableKnifeAnimationState> getKnifeAnimation() {
        if (this.renderer.getCurrentAnimation().getState() == TableKnifeAnimationState.DEFAULT) {
            return (Animation<TableKnifeAnimationState>) this.renderer.getAnimations().get(TableKnifeAnimationState.KNIFE_DEFAULT);
        }
        if (this.renderer.getCurrentAnimation().getState() == TableKnifeAnimationState.FOCUS) {
            return (Animation<TableKnifeAnimationState>) this.renderer.getAnimations().get(TableKnifeAnimationState.KNIFE_FOCUS);
        }
        if (this.renderer.getCurrentAnimation().getState() == TableKnifeAnimationState.CUT) {
            return (Animation<TableKnifeAnimationState>) this.renderer.getAnimations().get(TableKnifeAnimationState.KNIFE_CUT);
        }
        return null;
    }

    @Override
    public boolean allowFocus() {
        if (this.items.isEmpty()
                && Wrapper.getLocalPlayer().getInventory().hasItemInventory()
                && Wrapper.getLocalPlayer().getInventory().isFoodCuttable()) {
            return true;

        } else if (!this.items.isEmpty() && !Wrapper.getLocalPlayer().getInventory().hasItemInventory()) {
            return true;
        }

        return false;
    }

    @Override
    public double zIndex() {
        return this.getCollider().getY() - 32;
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (!this.getItems().isEmpty()
                && this.getItems().get(0) instanceof IEntityCuttable
                && ((IEntityCuttable) this.getItems().get(0)).getCuttingState() == IEntityCuttable.EntityCuttingState.CUTTING) {
            this.renderer.switchState(TableKnifeAnimationState.CUT);

        } else if (TableSpawner.currentCollisionPlayer == this) {
            this.renderer.switchState(TableKnifeAnimationState.FOCUS);

        } else {
            this.renderer.switchState(TableKnifeAnimationState.DEFAULT);
        }
    }

    @Override
    public void draw(DrawTool drawTool) {
        drawTool.drawImage(this.renderer.getCurrentFrame(), this.collider.getX() - 16, this.collider.getY() - 16);
    }

    @Override
    public boolean isLeft() {
        return this.collider.getX() < 620;
    }
}
