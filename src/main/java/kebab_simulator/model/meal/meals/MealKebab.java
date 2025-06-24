package kebab_simulator.model.meal.meals;

import kebab_simulator.Wrapper;
import kebab_simulator.model.meal.MealModel;
import kebab_simulator.model.meal.ingredients.*;

import java.awt.image.BufferedImage;
import java.util.List;

public class MealKebab extends MealModel {

    private static MealKebab instance = new MealKebab();

    public static MealKebab getInstance() {
        return MealKebab.instance;
    }

    public MealKebab() {
        super(
            (BufferedImage) Wrapper.getImage("/graphic/item/food/kebab.png"),
            "Döner",
            List.of(
                IngredientMeat.getInstance(),
                IngredientBread.getInstance(),
                IngredientOnion.getInstance(),
                IngredientTomato.getInstance(),
                IngredientCabbage.getInstance()
            )
        );
    }
}
