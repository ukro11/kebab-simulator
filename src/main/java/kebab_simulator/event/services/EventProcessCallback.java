package kebab_simulator.event.services;

public interface EventProcessCallback<T> {
    void onSuccess(T data);
    void onFailure(Throwable e);
}
