package kebab_simulator.model.entity.impl.food;

public interface IEntityCookable {
    EntityCookingState getCookingState();
    double getCookingProgress();
    double getCookingDuration();
    boolean allowCooking();

    enum EntityCookingState {
        IDLE,
        COOKING,
        COOKED,
        BURNING,
        BURNT
    }
}
