package kebab_simulator.model.meal.meals;

import kebab_simulator.Wrapper;
import kebab_simulator.model.meal.MealModel;
import kebab_simulator.model.meal.ingredients.*;

import java.awt.image.BufferedImage;
import java.util.List;

public class MealKebabMeat extends MealModel {

    private static MealKebabMeat instance = new MealKebabMeat();

    public static MealKebabMeat getInstance() {
        return MealKebabMeat.instance;
    }

    public MealKebabMeat() {
        super(
            (BufferedImage) Wrapper.getImage("/graphic/item/food/kebab_meat.png"),
            "Döner",
            List.of(
                IngredientMeat.getInstance(),
                IngredientBread.getInstance()
            )
        );
    }
}
