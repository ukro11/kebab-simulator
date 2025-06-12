package kebab_simulator.model.meal.ingredients;

import kebab_simulator.model.entity.impl.food.EntityMeat;
import kebab_simulator.model.meal.IngredientModel;
import kebab_simulator.model.meal.IngredientState;

public class IngredientMeat extends IngredientModel {

    private static IngredientMeat instance = new IngredientMeat();

    public IngredientMeat() {
        super(new IngredientState(true, 20.0, true, 10.0), EntityMeat.class);
    }

    public static IngredientMeat getInstance() {
        return IngredientMeat.instance;
    }
}
