package kebab_simulator.event.services;

public interface EventProcessCallback<T> {
    default void onSuccess(T data) {}
    default void onFailure(Throwable e) {}
}
