package kebab_simulator.model.meal.ingredients;


import kebab_simulator.Wrapper;
import kebab_simulator.model.entity.impl.food.EntityCabbage;
import kebab_simulator.model.meal.IngredientModel;
import kebab_simulator.model.meal.IngredientState;


public class IngredientCabbage extends IngredientModel {

    private static IngredientCabbage instance = new IngredientCabbage();

    public IngredientCabbage() {
        super(Wrapper.getImage("/graphic/item/food/cabbage.png").getSubimage(0, 0, 32, 32), new IngredientState(false, 0, true, 5.0), EntityCabbage.class);
    }

    public static IngredientCabbage getInstance() {
        return IngredientCabbage.instance;
    }
}
