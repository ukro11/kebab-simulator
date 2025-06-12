package kebab_simulator.physics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ColliderManager {

    public static boolean DEBUG = false;

    private final Map<String, Collider> colliders = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(ColliderManager.class);
    private static Map<String, Set<String>> classCollisionPrevention;

    static {
        ColliderManager.classCollisionPrevention = Map.of(
            //"entity_player", Set.of("map"),
            "map", Set.of("map"),
            "entity_item", Set.of("map", "entity_player", "entity_item"),
            "entity_plate", Set.of("map")
        );
    }

    public void queueCollisions() {
        for (Map.Entry<String, Collider> entry1 : colliders.entrySet()) {
            for (Map.Entry<String, Collider> entry2 : colliders.entrySet()) {
                if (!entry1.getKey().equals(entry2.getKey())) {
                    Collider body1 = entry1.getValue();
                    Collider body2 = entry2.getValue();
                    if (this.canCollide(body1, body2) && body1.collides(body2) && ColliderManager.DEBUG) {
                        this.logger.debug("Collision detected between Body {} and Body {}", body1.getId(), body2.getId());
                    }
                }
            }
        }
    }

    private boolean canCollide(Collider body1, Collider body2) {
        if (body1.getType() == BodyType.STATIC && body2.getType() == BodyType.STATIC) {
            return false;
        }

        Set<String> body1PreventionGroups = ColliderManager.classCollisionPrevention.get(body1.getColliderClass());
        if (body1PreventionGroups != null && body1PreventionGroups.contains(body2.getColliderClass())) {
            return false;
        }

        Set<String> body2PreventionGroups = ColliderManager.classCollisionPrevention.get(body2.getColliderClass());
        if (body2PreventionGroups != null && body2PreventionGroups.contains(body1.getColliderClass())) {
            return false;
        }

        return true;
    }

    public void updateColliders(double dt) {
        this.colliders.entrySet().forEach(c -> c.getValue().update(dt));
        this.queueCollisions();
    }

    public void createBody(Collider body) {
        if (this.colliders.containsKey(body.getId())) {
            this.logger.error("There is already a body with that the id {}", body.getId());
            return;
        }

        this.colliders.put(body.getId(), body);
        this.logger.debug("Body {} was created", body.getId());
    }

    public void destroyBody(Collider body) {
        this.colliders.remove(body.getId());
        if (body == null) {
            this.logger.warn("Invalid id {}, could not destroy body because it does not exist", body.getId());
            return;
        }
        this.logger.debug("Body {} was destroyed", body.getId());
    }

    public Collider findBodyById(String id) {
        return this.colliders.get(id);
    }

    public Map<String, Collider> getColliders() {
        return colliders;
    }
}
