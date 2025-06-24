package kebab_simulator.model.meal.ingredients;

import kebab_simulator.Wrapper;
import kebab_simulator.model.entity.impl.food.EntityMeat;
import kebab_simulator.model.meal.IngredientModel;
import kebab_simulator.model.meal.IngredientState;

public class IngredientMeat extends IngredientModel {

    private static IngredientMeat instance = new IngredientMeat();

    public IngredientMeat() {
        super(Wrapper.getImage("/graphic/item/food/meat.png").getSubimage(0, 0, 32, 32), new IngredientState(true, 20.0, true, 10.0), EntityMeat.class);
    }

    public static IngredientMeat getInstance() {
        return IngredientMeat.instance;
    }
}
