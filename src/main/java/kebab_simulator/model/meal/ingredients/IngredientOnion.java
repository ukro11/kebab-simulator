package kebab_simulator.model.meal.ingredients;


import kebab_simulator.model.entity.impl.food.EntityOnion;
import kebab_simulator.model.meal.IngredientModel;
import kebab_simulator.model.meal.IngredientState;


public class IngredientOnion extends IngredientModel {


    private static IngredientOnion instance = new IngredientOnion();


    public IngredientOnion() {
        super(new IngredientState(false, 0, true, 4.0), EntityOnion.class);
    }


    public static IngredientOnion getInstance() {
        return IngredientOnion.instance;
    }
}
