package kebab_simulator.event.events;

import kebab_simulator.event.Event;

public class UpdateEvent extends Event {

    private double deltaTime;

    public UpdateEvent(double dt) {
        super("update");
        this.deltaTime = dt;
    }

    public double getDeltaTime() {
        return deltaTime;
    }
}
