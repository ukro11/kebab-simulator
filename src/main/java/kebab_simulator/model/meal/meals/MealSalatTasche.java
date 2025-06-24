package kebab_simulator.model.meal.meals;

import kebab_simulator.Wrapper;
import kebab_simulator.model.meal.MealModel;
import kebab_simulator.model.meal.ingredients.*;

import java.awt.image.BufferedImage;
import java.util.List;

public class MealSalatTasche extends MealModel {

    private static MealSalatTasche instance = new MealSalatTasche();

    public static MealSalatTasche getInstance() {
        return MealSalatTasche.instance;
    }

    public MealSalatTasche() {
        super(
            (BufferedImage) Wrapper.getImage("/graphic/item/food/kebab_salad.png"),
            "Salat Tasche",
            List.of(
                IngredientBread.getInstance(),
                IngredientOnion.getInstance(),
                IngredientTomato.getInstance(),
                IngredientCabbage.getInstance()
            )
        );
    }
}
