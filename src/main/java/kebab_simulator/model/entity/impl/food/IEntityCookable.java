package kebab_simulator.model.entity.impl.food;

public interface IEntityCookable {
    EntityCookingState getCookingState();
    double getCookingProgress();
    double getCookingDuration();
    boolean allowCooking();
    void cook();
    void stopCook();

    enum EntityCookingState {
        IDLE,
        COOKING,
        COOKED,
        BURNING,
        BURNT
    }
}
