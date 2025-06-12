package kebab_simulator.model.entity.impl.food;

public interface IEntityCuttable {
    EntityCuttingState getCuttingState();
    double getCuttingProgress();
    double getCuttingDuration();
    boolean allowCutting();

    enum EntityCuttingState {
        IDLE,
        CUTTING,
        CUT
    }
}
