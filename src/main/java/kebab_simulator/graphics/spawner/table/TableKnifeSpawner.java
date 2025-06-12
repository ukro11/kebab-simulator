package kebab_simulator.graphics.spawner.table;

import KAGO_framework.view.DrawTool;
import kebab_simulator.Wrapper;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.states.FocusAnimationState;
import kebab_simulator.animation.states.spawner.TableKnifeAnimationState;
import kebab_simulator.event.services.process.EventPostGameLoadingProcess;
import kebab_simulator.graphics.spawner.ObjectIdResolver;
import kebab_simulator.graphics.tooltip.Tooltip;
import kebab_simulator.model.KeyManagerModel;
import kebab_simulator.model.entity.impl.player.EntityPlayer;
import kebab_simulator.physics.Collider;

import java.awt.event.KeyEvent;

public class TableKnifeSpawner extends TableItemIntegration {

    public TableKnifeSpawner(ObjectIdResolver id, Collider collider) {
        super(id, collider, new AnimationRenderer<TableKnifeAnimationState>(
                "/graphic/map/sprites/table/table_knife_spritesheet.png",
                4,
                6,
                64,
                64,
                TableKnifeAnimationState.DEFAULT
        ));

        Wrapper.getProcessManager().queue(new EventPostGameLoadingProcess<>("Registering Tooltip (Cutting)", () -> {
            Wrapper.getTooltipManager().register(
                new Tooltip(
                    KeyManagerModel.KEY_CUT_FOOD,
                    (keyManager) -> {
                        if (TableSpawner.isTableFocused(this)) return KeyManagerModel.KEY_CUT_FOOD.getDescription();
                        return null;
                    }
                )
            );
        }));
    }

    @Override
    public void onInteractTable(EntityPlayer player, KeyEvent event) {
        super.onInteractTable(player, event);
    }

    @Override
    public boolean allowFocus() {
        if (!this.items.isEmpty()) return !Wrapper.getLocalPlayer().getInventory().hasItemInventory();
        if (this.items.isEmpty()) return !Wrapper.getLocalPlayer().getInventory().hasItemInventory();
        return Wrapper.getLocalPlayer().getInventory().isFoodCuttable();
    }

    @Override
    public double zIndex() {
        return this.getCollider().getY() - 32;
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (TableSpawner.currentCollisionPlayer == this) {
            this.renderer.switchState(FocusAnimationState.FOCUS);

        } else {
            this.renderer.switchState(FocusAnimationState.DEFAULT);
        }
    }

    @Override
    public void draw(DrawTool drawTool) {
        drawTool.drawImage(this.renderer.getCurrentFrame(), this.collider.getX() - 16, this.collider.getY() - 16);
    }
}
