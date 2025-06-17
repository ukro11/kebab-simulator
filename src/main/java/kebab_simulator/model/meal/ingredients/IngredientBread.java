package kebab_simulator.model.meal.ingredients;


import kebab_simulator.model.entity.impl.food.EntityBread;
import kebab_simulator.model.meal.IngredientModel;
import kebab_simulator.model.meal.IngredientState;


public class IngredientBread extends IngredientModel {


    private static IngredientBread instance = new IngredientBread();


    public IngredientBread() {
        super(new IngredientState(false, 0, true, 5.0), EntityBread.class);
    }


    public static IngredientBread getInstance() {
        return IngredientBread.instance;
    }
}
