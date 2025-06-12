package kebab_simulator.model.meal;

import kebab_simulator.model.entity.impl.food.EntityFood;

public abstract class IngredientModel {

    private final IngredientState targetState;
    private final Class<? extends EntityFood> entityClass;

    public IngredientModel(IngredientState targetState, Class<? extends EntityFood> entityClass) {
        this.targetState = targetState;
        this.entityClass = entityClass;
    }

    public IngredientState getTargetState() {
        return this.targetState;
    }

    public Class<? extends EntityFood> getEntityClass() {
        return this.entityClass;
    }
}
