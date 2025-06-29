package kebab_simulator.graphics.spawner.table;

import kebab_simulator.Wrapper;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.states.FocusAnimationState;
import kebab_simulator.graphics.spawner.ObjectIdResolver;
import kebab_simulator.graphics.spawner.table.storage.*;
import kebab_simulator.physics.Collider;

public abstract class TableStorageSpawner extends TableSpawner {

    protected TableStorageType type;

    protected TableStorageSpawner(TableStorageType type, ObjectIdResolver id, Collider collider) {
        super(id, collider, new AnimationRenderer<FocusAnimationState>(
            "/graphic/map/sprites/table/" +  String.format("%s_%s.png", id.getType().getName(), id.getSpawnerType()),
            3,
            3,
            32,
            32,
            FocusAnimationState.DEFAULT
        ));
        this.type = type;
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (this.renderer != null) {
            if (TableSpawner.currentCollisionPlayer == this) {
                this.renderer.switchState(FocusAnimationState.FOCUS);

            } else {
                this.renderer.switchState(FocusAnimationState.DEFAULT);
            }
        }
    }

    @Override
    public boolean allowFocus() {
        return Wrapper.getLocalPlayer().getInventory().getItemInHand() == null;
    }

    public static TableStorageSpawner fetchStorageSpawner(ObjectIdResolver id, Collider collider) {
        switch (id.getSpawnerType().split("-")[1].toLowerCase()) {
            case "meat":
                return new TableStorageMeat(id, collider);
            case "bread":
                return new TableStorageBread(id, collider);
            case "cabbage":
                return new TableStorageCabbage(id, collider);
            case "tomato":
                return new TableStorageTomato(id, collider);
            case "onion":
                return new TableStorageOnion(id, collider);
        }

        return null;
    }

    public TableStorageType getType() {
        return this.type;
    }

    public enum TableStorageType {
        MEAT,
        BREAD,
        CABBAGE,
        TOMATO,
        ONION,
    }
}
