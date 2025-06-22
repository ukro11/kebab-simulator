package kebab_simulator.graphics.spawner.table;

import KAGO_framework.view.DrawTool;
import kebab_simulator.Wrapper;
import kebab_simulator.animation.Animation;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.states.entity.MeatAnimationState;
import kebab_simulator.animation.states.spawner.TablePanAnimationState;
import kebab_simulator.event.services.process.EventPostGameLoadingProcess;
import kebab_simulator.graphics.IOrderRenderer;
import kebab_simulator.graphics.spawner.ObjectIdResolver;
import kebab_simulator.graphics.tooltip.Tooltip;
import kebab_simulator.model.KeyManagerModel;
import kebab_simulator.model.entity.impl.food.EntityMeat;
import kebab_simulator.model.entity.impl.food.IEntityCookable;
import kebab_simulator.model.entity.impl.player.EntityPlayer;
import kebab_simulator.model.scene.GameScene;
import kebab_simulator.physics.Collider;

import java.awt.event.KeyEvent;

public class TablePanSpawner extends TableItemIntegration implements ITableSide {

    public TablePanSpawner(ObjectIdResolver id, Collider collider) {
        super(id, collider, new AnimationRenderer<TablePanAnimationState>(
                "/graphic/map/sprites/table/table_pan_spritesheet.png",
                3,
                3,
                32,
                32,
                TablePanAnimationState.DEFAULT
        ));

        Wrapper.getProcessManager().queue(new EventPostGameLoadingProcess<>("Registering Tooltip (Cooking)", () -> {
            Wrapper.getTooltipManager().register(
                new Tooltip(
                    KeyManagerModel.KEY_COOK_FOOD,
                    (keyManager) -> {
                        if (TableSpawner.isTableFocused(this)
                                && !this.items.isEmpty()
                                && ((IEntityCookable) this.items.get(0)).getCookingState() != IEntityCookable.EntityCookingState.COOK) return KeyManagerModel.KEY_COOK_FOOD.getDescription();
                        return null;
                    }
                )
            );
        }));

        TablePanSpawner panSpawner = this;
        GameScene.getInstance().getRenderer().register(new IOrderRenderer() {
            @Override
            public double zIndex() {
                return panSpawner.zIndex() + 32 * 3;
            }
            @Override
            public void draw(DrawTool drawTool) {
                Animation<TablePanAnimationState> animation = panSpawner.getPanAnimation();
                drawTool.drawImage(
                    animation.getFrames().get(panSpawner.renderer.getCurrentIndex()),
                    panSpawner.collider.getX() - 16,
                    panSpawner.collider.getY() - 16
                );
            }
        });
    }

    @Override
    public void onFocusLost() {
        this.renderer.switchState(TablePanAnimationState.DEFAULT);
        if (!this.items.isEmpty()) ((IEntityCookable) this.items.get(0)).stopCook();
    }

    @Override
    public void onInteractTable(EntityPlayer player, KeyEvent event) {
        super.onInteractTable(player, event);
        if (event.getKeyCode() == KeyManagerModel.KEY_COOK_FOOD.getKey()
                && this.renderer.getCurrentAnimation().getState() != TablePanAnimationState.COOK) {
            this.renderer.switchState(TablePanAnimationState.COOK);
            ((IEntityCookable) this.items.get(0)).cook();
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
            if (item instanceof EntityMeat && item.getRenderer().getCurrentAnimation().getState() == MeatAnimationState.COOKED) {
                ((EntityMeat) item).setScale(0.7);
            }
        }
    }

    private Animation<TablePanAnimationState> getPanAnimation() {
        if (this.renderer.getCurrentAnimation().getState() == TablePanAnimationState.DEFAULT) {
            return (Animation<TablePanAnimationState>) this.renderer.getAnimations().get(TablePanAnimationState.DEFAULT);
        }
        if (this.renderer.getCurrentAnimation().getState() == TablePanAnimationState.FOCUS) {
            return (Animation<TablePanAnimationState>) this.renderer.getAnimations().get(TablePanAnimationState.FOCUS);
        }
        if (this.renderer.getCurrentAnimation().getState() == TablePanAnimationState.COOK) {
            return (Animation<TablePanAnimationState>) this.renderer.getAnimations().get(TablePanAnimationState.COOK);
        }
        return null;
    }

    @Override
    public boolean allowFocus() {
        if (this.items.isEmpty()
                && Wrapper.getLocalPlayer().getInventory().hasItemInventory()
                && Wrapper.getLocalPlayer().getInventory().isFoodCookable()) {
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
                && this.getItems().get(0) instanceof IEntityCookable
                && ((IEntityCookable) this.getItems().get(0)).getCookingState() == IEntityCookable.EntityCookingState.COOK) {
            this.renderer.switchState(TablePanAnimationState.COOK);

        } else if (TableSpawner.currentCollisionPlayer == this) {
            this.renderer.switchState(TablePanAnimationState.FOCUS);

        } else {
            this.renderer.switchState(TablePanAnimationState.DEFAULT);
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
