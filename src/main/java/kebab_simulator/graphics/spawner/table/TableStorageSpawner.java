package kebab_simulator.graphics.spawner.table;

import kebab_simulator.Wrapper;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.states.FocusAnimationState;
import kebab_simulator.graphics.spawner.ObjectIdResolver;
import kebab_simulator.graphics.spawner.table.storage.TableStorageMeat;
import kebab_simulator.physics.Collider;

public abstract class TableStorageSpawner extends TableSpawner {

    protected TableStorageType type;

    protected TableStorageSpawner(TableStorageType type, ObjectIdResolver id, Collider collider) {
        super(id, collider, new AnimationRenderer<FocusAnimationState>(
            "/graphic/map/sprites/table/" +  String.format("%s_%s.png", id.getType().getName(), id.getSpawnerType()),
            2,
            1,
            32,
            32,
            FocusAnimationState.DEFAULT
        ));
        this.type = type;
    }

    @Override
    public boolean allowFocus() {
        return Wrapper.getLocalPlayer().getInventory().getItemInHand() == null;
    }

    public static TableStorageSpawner fetchStorageSpawner(ObjectIdResolver id, Collider collider) {
        switch (id.getSpawnerType().split("-")[1].toLowerCase()) {
            case "meat": {
                return new TableStorageMeat(id, collider);
            }
        }
        return null;
    }

    public TableStorageType getType() {
        return this.type;
    }

    public enum TableStorageType {
        MEAT,
    }
}
