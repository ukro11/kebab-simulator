package kebab_simulator.graphics.spawner.table;

import KAGO_framework.view.DrawTool;
import kebab_simulator.Wrapper;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.states.spawner.TableServingAnimationState;
import kebab_simulator.graphics.spawner.ObjectIdResolver;
import kebab_simulator.model.entity.Entity;
import kebab_simulator.model.entity.impl.player.EntityPlayer;
import kebab_simulator.physics.Collider;

import java.awt.event.KeyEvent;

public class TableServingSpawner extends TableItemIntegration {

    public TableServingSpawner(ObjectIdResolver id, Collider collider) {
        super(id, collider, new AnimationRenderer<TableServingAnimationState>(
            "/graphic/map/sprites/animated/animated_escalator_up_32x32_sheet.png",
            2,
            6,
            128,
            96,
            TableServingAnimationState.DEFAULT
        ));
        this.directionToLook = Entity.EntityDirection.TOP;
    }

    @Override
    public void onInteractTable(EntityPlayer player, KeyEvent event) {

    }

    @Override
    public boolean allowFocus() {
        // && Wrapper.getLocalPlayer().getInventory().getItemAsPlate().getMeal()
        // TODO: check if there is a meal on plate
        return Wrapper.getLocalPlayer().getInventory().isPlate();
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (TableSpawner.currentCollisionPlayer == this) {
            this.renderer.switchState(TableServingAnimationState.FOCUS);

        } else {
            this.renderer.switchState(TableServingAnimationState.DEFAULT);
        }
    }

    @Override
    public void draw(DrawTool drawTool) {
        drawTool.drawImage(this.renderer.getCurrentFrame(), this.collider.getX() - 16, this.collider.getY());
    }

    @Override
    public double zIndex() {
        return this.getCollider().getY() + 32;
    }
}
