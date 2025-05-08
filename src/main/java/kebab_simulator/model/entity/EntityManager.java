package kebab_simulator.model.entity;

import java.util.HashMap;
import java.util.Map;

public class EntityManager {

    private final Map<String, Entity> entities = new HashMap<>();

    public void registerEntity(Entity entity) {
        if (entity != null) {
            this.entities.put(entity.getId(), entity);
        }
    }

    public Map<String, Entity> getEntities() {
        return this.entities;
    }
}
