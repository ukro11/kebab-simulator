package kebab_simulator.event.events.collider;

import kebab_simulator.event.Event;
import kebab_simulator.physics.Collider;

public class ColliderDestroyEvent extends Event {

    private Collider collider;

    public ColliderDestroyEvent(Collider collider) {
        super("collider_destroy");
        this.collider = collider;
    }

    public Collider getCollider() {
        return collider;
    }
}
