package kebab_simulator.model.meal.ingredients;


import kebab_simulator.Wrapper;
import kebab_simulator.model.entity.impl.food.EntityTomato;
import kebab_simulator.model.meal.IngredientModel;
import kebab_simulator.model.meal.IngredientState;


public class IngredientTomato extends IngredientModel {

    private static IngredientTomato instance = new IngredientTomato();

    public IngredientTomato() {
        super(Wrapper.getImage("/graphic/item/food/tomato.png").getSubimage(0, 0, 32, 32), new IngredientState(false, 0, true, 5.0), EntityTomato.class);
    }

    public static IngredientTomato getInstance() {
        return IngredientTomato.instance;
    }
}
