package kebab_simulator.physics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ColliderManager {

    private static final Map<String, Set<String>> collisionPreventionGroups = new HashMap<>();
    public static boolean DEBUG = false;

    private final Map<String, Body> colliders = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(ColliderManager.class);

    public void queueCollisions() {
        for (Map.Entry<String, Body> entry1 : colliders.entrySet()) {
            for (Map.Entry<String, Body> entry2 : colliders.entrySet()) {
                if (!entry1.getKey().equals(entry2.getKey())) {
                    Body body1 = entry1.getValue();
                    Body body2 = entry2.getValue();
                    if (this.canCollide(body1, body2) && body1.collides(body2) && ColliderManager.DEBUG) {
                        this.logger.debug("Collision detected between Body {} and Body {}", body1.getId(), body2.getId());
                    }
                }
            }
        }
    }

    private boolean canCollide(Body body1, Body body2) {
        Set<String> body1PreventionGroups = ColliderManager.collisionPreventionGroups.get(body1.getBodyClass());
        if (body1PreventionGroups != null && body1PreventionGroups.contains(body2.getBodyClass())) {
            return false;
        }

        Set<String> body2PreventionGroups = ColliderManager.collisionPreventionGroups.get(body2.getBodyClass());
        if (body2PreventionGroups != null && body2PreventionGroups.contains(body1.getBodyClass())) {
            return false;
        }

        return true;
    }

    public void updateBodies(double dt) {
        this.colliders.entrySet().forEach(c -> c.getValue().update(dt));
        this.queueCollisions();
    }

    public void createBody(Body body) {
        this.colliders.put(body.getId(), body);
        this.logger.debug("Body {} was created", body.getId());
    }

    public void destroyBody(Body body) {
        destroyBody(body.getId());
    }

    public void destroyBody(String id) {
        Body body = this.colliders.remove(id);
        if (body == null) {
            this.logger.warn("Invalid id {}, could not destroy body because it does not exist", id);
            return;
        }
        this.logger.debug("Body {} was destroyed", id);
    }

    public Body findBodyById(String id) {
        return this.colliders.get(id);
    }
}
