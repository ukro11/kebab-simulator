package kebab_simulator.model.entity;

import kebab_simulator.model.entity.impl.EntityPlayer;
import kebab_simulator.physics.BodyType;
import kebab_simulator.physics.colliders.ColliderPolygon;

import java.util.HashMap;
import java.util.Map;

public class EntityManager {

    private final Map<String, Entity> entities = new HashMap<>();

    public EntityPlayer spawnPlayer(String id, double x, double y) {
        ColliderPolygon collider = ColliderPolygon.createCIPolygon(id, BodyType.DYNAMIC, x, y, 20, 14, 4.5);
        collider.setColliderClass("entity_player");
        EntityPlayer player = new EntityPlayer(collider, -16, -52, 32, 64);
        collider.setEntity(player);
        return player;
    }

    public void registerEntity(Entity entity) {
        if (entity != null) {
            this.entities.put(entity.getId(), entity);
        }
    }

    public void unregister(Entity entity) {
        if (entity != null) {
            this.entities.remove(entity.getId());
        }
    }

    public Map<String, Entity> getEntities() {
        return this.entities;
    }
}
