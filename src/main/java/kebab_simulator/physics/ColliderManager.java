package kebab_simulator.physics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ColliderManager {

    private static final Map<String, Set<String>> collisionPreventionGroups = new HashMap<>();
    public static boolean DEBUG = false;

    private final Map<String, Collider> colliders = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(ColliderManager.class);

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
        Set<String> body1PreventionGroups = ColliderManager.collisionPreventionGroups.get(body1.getColliderClass());
        if (body1PreventionGroups != null && body1PreventionGroups.contains(body2.getColliderClass())) {
            return false;
        }

        Set<String> body2PreventionGroups = ColliderManager.collisionPreventionGroups.get(body2.getColliderClass());
        if (body2PreventionGroups != null && body2PreventionGroups.contains(body1.getColliderClass())) {
            return false;
        }

        return true;
    }

    public void updateBodies(double dt) {
        this.colliders.entrySet().forEach(c -> c.getValue().update(dt));
        this.queueCollisions();
    }

    public void createBody(Collider body) {
        this.colliders.put(body.getId(), body);
        this.logger.debug("Body {} was created", body.getId());
    }

    public void destroyBody(Collider body) {
        destroyBody(body.getId());
    }

    public void destroyBody(String id) {
        Collider body = this.colliders.remove(id);
        if (body == null) {
            this.logger.warn("Invalid id {}, could not destroy body because it does not exist", id);
            return;
        }
        this.logger.debug("Body {} was destroyed", id);
    }

    public Collider findBodyById(String id) {
        return this.colliders.get(id);
    }
}
