package kebab_simulator.graphics.spawner.table;

import kebab_simulator.Wrapper;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.states.FocusAnimationState;
import kebab_simulator.graphics.spawner.ObjectIdResolver;
import kebab_simulator.model.entity.Entity;
import kebab_simulator.model.entity.impl.EntityItem;
import kebab_simulator.model.entity.impl.item.EntityPan;
import kebab_simulator.model.entity.impl.item.EntityPlate;
import kebab_simulator.model.entity.impl.player.EntityPlayer;
import kebab_simulator.physics.Collider;

import java.awt.event.KeyEvent;

public class TableGarbageSpawner extends TableItemIntegration {

    public TableGarbageSpawner(ObjectIdResolver id, Collider collider) {
        super(id, collider, new AnimationRenderer<FocusAnimationState>(
            "/graphic/map/sprites/table/table_garbage.png",
            2,
            1,
            32,
            32,
            FocusAnimationState.DEFAULT
        ));
        this.directionToLook = Entity.EntityDirection.RIGHT;
        this.maxItems = 80;
    }

    @Override
    public void onInteractTable(EntityPlayer player, KeyEvent event) {
        super.onInteractTable(player, event);
    }

    @Override
    public void onDropItem(TableItemDropEvent event) {
        Wrapper.getLocalPlayer().getInventory().dropItem(this);
        var item = this.items.get(0);
        this.items.get(0).destroy();
        if (item instanceof EntityPan) {
            Wrapper.getEntityManager().spawnPan();

        } else if (item instanceof EntityPlate) {
            Wrapper.getEntityManager().spawnPlate();
        }
        this.items.clear();
    }

    @Override
    public void onPickItem() {}

    @Override
    public boolean filterItem(TableItemDropEvent event, EntityItem item) {
        return true;
    }

    @Override
    public boolean allowFocus() {
        return Wrapper.getLocalPlayer().getInventory().getItemInHand() != null;
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
    public double zIndex() {
        return super.zIndex() - 1;
    }
}
