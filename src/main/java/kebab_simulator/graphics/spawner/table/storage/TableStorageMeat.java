package kebab_simulator.graphics.spawner.table.storage;

import kebab_simulator.Wrapper;
import kebab_simulator.graphics.spawner.ObjectIdResolver;
import kebab_simulator.graphics.spawner.table.TableSpawner;
import kebab_simulator.graphics.spawner.table.TableStorageSpawner;
import kebab_simulator.model.KeyManagerModel;
import kebab_simulator.model.entity.impl.player.EntityPlayer;
import kebab_simulator.physics.Collider;

import java.awt.event.KeyEvent;

public class TableStorageMeat extends TableStorageSpawner {

    public TableStorageMeat(ObjectIdResolver id, Collider collider) {
        super(TableStorageType.MEAT, id, collider);
    }

    @Override
    public void onInteractTable(EntityPlayer player, KeyEvent event) {
        if (event.getKeyCode() == KeyManagerModel.KEY_TAKE_ITEM.getKey()) {
            player.getInventory().pickItem(Wrapper.getEntityManager().spawnMeat());
            TableSpawner.currentCollisionPlayer = null;
        }
    }
}
