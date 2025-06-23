package kebab_simulator.model.entity.impl.player;

import kebab_simulator.model.entity.impl.EntityItemLocation;
import kebab_simulator.model.entity.impl.food.EntityFood;
import kebab_simulator.model.entity.impl.EntityItem;
import kebab_simulator.model.entity.impl.food.IEntityCookable;
import kebab_simulator.model.entity.impl.food.IEntityCuttable;
import kebab_simulator.model.entity.impl.item.EntityPlate;

public class PlayerInventory {

    private final EntityPlayer player;
    private EntityItem<?> item;

    public PlayerInventory(EntityPlayer player) {
        this.player = player;
    }

    public boolean isFood() {
        return this.item instanceof EntityFood;
    }

    public boolean isPlate() {
        return this.item instanceof EntityPlate;
    }

    public boolean hasItemInventory() {
        return this.item != null;
    }

    public boolean isFoodCuttable() {
        return this.isFood() && this.item instanceof IEntityCuttable;
    }

    public boolean isFoodCookable() {
        return this.isFood() && this.item instanceof IEntityCookable;
    }

    public EntityItem<?> getItemInHand() {
        return this.item;
    }

    public EntityPlate getItemAsPlate() {
        if (this.item != null) return (EntityPlate) this.item;
        return null;
    }

    public EntityFood<?> getItemAsFood() {
        if (this.item != null) return (EntityFood<?>) this.item;
        return null;
    }

    public void pickItem(EntityItem<?> item) {
        this.item = item;
        this.item.onPick(this.player);
    }

    public void dropItem(EntityItemLocation location) {
        this.item.onDrop(location);
        this.item = null;
    }
    public void removeItem(EntityItemLocation location) {
        this.item.onDrop(location);
        this.item = null;
    }
}
