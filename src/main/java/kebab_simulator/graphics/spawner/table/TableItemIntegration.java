package kebab_simulator.graphics.spawner.table;

import kebab_simulator.Wrapper;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.graphics.spawner.ObjectIdResolver;
import kebab_simulator.model.KeyManagerModel;
import kebab_simulator.model.entity.impl.EntityItem;
import kebab_simulator.model.entity.impl.EntityItemLocation;
import kebab_simulator.model.entity.impl.item.EntityPlate;
import kebab_simulator.model.entity.impl.player.EntityPlayer;
import kebab_simulator.physics.Collider;

import java.awt.event.KeyEvent;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class TableItemIntegration extends TableSpawner implements EntityItemLocation<EntityItem> {

    protected CopyOnWriteArrayList<EntityItem> items;
    protected final int maxItems = 1;

    public TableItemIntegration(ObjectIdResolver id, Collider collider, AnimationRenderer<?> renderer) {
        super(id, collider, renderer);
        this.items = new CopyOnWriteArrayList<>();
    }

    @Override
    public void onInteractTable(EntityPlayer player, KeyEvent event) {
        if (event.getKeyCode() == KeyManagerModel.KEY_TAKE_ITEM.getKey()) {
            if (!this.items.isEmpty()) {
                var firstItem = this.items.get(0);
                if (firstItem instanceof EntityPlate && Wrapper.getLocalPlayer().getInventory().isFood()) {
                    this.onDropItem(TableItemDropEvent.ITEM_PLATE_DROP);

                } else {
                    this.onPickItem();
                }

            } else if (this.items.isEmpty() && Wrapper.getLocalPlayer().getInventory().hasItemInventory()) {
                this.onDropItem(TableItemDropEvent.ITEM_TABLE_DROP);
            }
        }
    }

    public void onPickItem() {
        Wrapper.getLocalPlayer().getInventory().pickItem(this.nextToRemove());
    }

    public void onDropItem(TableItemDropEvent event) {
        if (event == TableItemDropEvent.ITEM_PLATE_DROP) {
            Wrapper.getLocalPlayer().getInventory().dropItem(((EntityPlate) this.items.get(0)));

        } else if (event == TableItemDropEvent.ITEM_TABLE_DROP) {
            Wrapper.getLocalPlayer().getInventory().dropItem(this);
        }
    }

    public boolean filterItem(EntityItem item) {
        return true;
    }

    @Override
    public EntityItem nextToRemove() {
        return this.items.isEmpty() ? null : this.items.get(this.items.size() - 1);
    }

    @Override
    public CopyOnWriteArrayList<EntityItem> getItems() {
        return this.items;
    }

    @Override
    public void addItem(EntityItem item) {
        if (this.items.size() < this.maxItems) {
            this.items.add(item);
            item.setLocation(this);
        }
    }

    @Override
    public void removeItem(EntityItem item) {
        this.items.remove(item);
    }

    @Override
    public void setItems(CopyOnWriteArrayList<EntityItem> items) {
        this.items = items;
    }

    @Override
    public boolean allowFocus() {
        if (this.items.isEmpty()) {
            return Wrapper.getLocalPlayer().getInventory().hasItemInventory();

        } else {
            return !Wrapper.getLocalPlayer().getInventory().hasItemInventory() || Wrapper.getLocalPlayer().getInventory().isFood();
        }
    }

    protected enum TableItemDropEvent {
        ITEM_PLATE_DROP,
        ITEM_TABLE_DROP
    }
}
