package kebab_simulator.model.meal;

import kebab_simulator.model.entity.impl.food.EntityFood;

import java.awt.image.BufferedImage;

public abstract class IngredientModel {

    private final IngredientState targetState;
    private final Class<? extends EntityFood> entityClass;
    private final BufferedImage icon;

    public IngredientModel(BufferedImage icon, IngredientState targetState, Class<? extends EntityFood> entityClass) {
        this.icon = icon;
        this.targetState = targetState;
        this.entityClass = entityClass;
    }

    public BufferedImage getIcon() {
        return this.icon;
    }

    public IngredientState getTargetState() {
        return this.targetState;
    }

    public Class<? extends EntityFood> getEntityClass() {
        return this.entityClass;
    }
}
