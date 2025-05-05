package kebab_simulator.event;

public interface EventListener<T extends Event> {
    void handle(T event);
}
