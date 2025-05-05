package kebab_simulator.control;

import kebab_simulator.event.EventManager;

public class Wrapper {

    private final static EventManager eventManager = new EventManager();

    public static EventManager getEventManager() {
        return eventManager;
    }
}
