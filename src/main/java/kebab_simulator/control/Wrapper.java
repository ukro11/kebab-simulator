package kebab_simulator.control;

import kebab_simulator.event.EventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wrapper {

    private static EventManager eventManager;

    public Wrapper() {
        Wrapper.eventManager = new EventManager();
    }

    public static EventManager getEventManager() {
        return eventManager;
    }
}
