package kebab_simulator.event.events;

import kebab_simulator.event.Event;
import kebab_simulator.physics.Collider;

public class CollisionEvent extends Event {

    private final Collider mainBody;
    private final Collider collidedBody;
    private final CollisionState state;

    public CollisionEvent(Collider mainBody, Collider collidedBody, CollisionState state) {
        super("bodyCollided");
        this.mainBody = mainBody;
        this.collidedBody = collidedBody;
        this.state = state;
    }

    public Collider getMainBody() {
        return this.mainBody;
    }

    public Collider getCollidedBody() {
        return this.collidedBody;
    }

    public CollisionState getState() {
        return this.state;
    }

    /**
     * Die Methode überprüft, ob {@code body} dasselbe ist wie {@code mainBody} oder {@code collidedBody}.
     * @return {@code true}, wenn {@code body} {@code mainBody} oder {@code collidedBody} ist.
     */
    public boolean isBodyInvolved(Collider body) {
        return this.mainBody.equals(body) || this.collidedBody.equals(body);
    }

    public enum CollisionState {
        COLLISION_BEGIN_CONTACT,
        COLLISION_NORMAL_CONTACT,
        COLLISION_END_CONTACT
    }
}
