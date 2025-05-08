package kebab_simulator.event;

import java.util.function.Consumer;

public interface EventListenerIntegration<T extends Event, V extends Enum> {
    EventListener<T> addEventListener(V event, Consumer<T> handle);
    void once(V event, Consumer<T> handle);
}
