package kebab_simulator.event;

import java.util.*;

public class EventManager {
    private final Map<String, List<EventListener>> listeners = new HashMap<>();

    public void addEventListener(String type, EventListener<? extends Event> listener) {
        listeners.computeIfAbsent(type, k -> new ArrayList<>()).add(listener);
    }

    public void removeEventListener(String type, EventListener listener) {
        List<EventListener> eventListeners = listeners.get(type);
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    public void dispatchEvent(Event event) {
        List<EventListener> eventListeners = listeners.get(event.getType());
        if (eventListeners != null) {
            for (EventListener listener : new ArrayList<>(eventListeners)) {
                listener.handle(event);
            }
        }
    }
}
