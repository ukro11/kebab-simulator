package kebab_simulator.event.events;

import kebab_simulator.event.Event;
import kebab_simulator.physics.Body;

public class CollisionEvent extends Event {

    private final Body mainBody;
    private final Body collidedBody;
    private final CollisionState state;

    public CollisionEvent(Body mainBody, Body collidedBody, CollisionState state) {
        super("bodyCollided");
        this.mainBody = mainBody;
        this.collidedBody = collidedBody;
        this.state = state;
    }

    public Body getMainBody() {
        return this.mainBody;
    }

    public Body getCollidedBody() {
        return this.collidedBody;
    }

    public CollisionState getState() {
        return this.state;
    }

    /**
     * Die Methode überprüft, ob {@code body} dasselbe ist wie {@code mainBody} oder {@code collidedBody}.
     * @return {@code true}, wenn {@code body} {@code mainBody} oder {@code collidedBody} ist.
     */
    public boolean isBodyInvolved(Body body) {
        return this.mainBody.equals(body) || this.collidedBody.equals(body);
    }

    public enum CollisionState {
        COLLISION_BEGIN_CONTACT,
        COLLISION_NORMAL_CONTACT,
        COLLISION_END_CONTACT
    }
}
