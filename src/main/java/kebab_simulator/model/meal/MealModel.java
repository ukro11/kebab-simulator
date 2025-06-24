package kebab_simulator.model.meal;

import kebab_simulator.model.entity.impl.item.EntityPlate;
import kebab_simulator.model.meal.meals.MealKebab;
import kebab_simulator.model.meal.meals.MealKebabMeat;
import kebab_simulator.model.meal.meals.MealSalatTasche;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class MealModel {

    private static List<MealModel> meals = new ArrayList<>();
    private final BufferedImage icon;
    private final String name;
    private final CopyOnWriteArrayList<IngredientModel> ingredientsNeeded;

    public MealModel(BufferedImage icon, String name, List<IngredientModel> ingredients) {
        this.icon = icon;
        this.name = name;
        this.ingredientsNeeded = new CopyOnWriteArrayList(ingredients);
    }

    public static void init() {
        MealModel.meals.add(new MealKebab());
        MealModel.meals.add(new MealKebabMeat());
        MealModel.meals.add(new MealSalatTasche());
    }

    public BufferedImage getIcon() {
        return this.icon;
    }

    public String getName() {
        return this.name;
    }

    public List<IngredientModel> getIngredientsNeeded() {
        return this.ingredientsNeeded;
    }

    public boolean isMealValid(EntityPlate plate) {
        if (this.ingredientsNeeded.size() != plate.getItems().size()) return false;
        for (int i = 0; i < this.ingredientsNeeded.size(); i++) {
            var target = this.ingredientsNeeded.get(i);
            var filter = plate.getItems().stream().filter(ing -> ing.getClass().equals(target.getEntityClass())).findFirst().orElse(null);
            if (filter == null) return false;
        }
        return true;
    }

    public static boolean isMeal(EntityPlate plate) {
        for (MealModel meal : MealModel.meals) {
            if (meal.isMealValid(plate)) {
                return true;
            }
        }
        return false;
    }

    public static MealModel findMeal(EntityPlate plate) {
        for (MealModel meal : MealModel.meals) {
            if (meal.isMealValid(plate)) {
                return meal;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealModel mealModel = (MealModel) o;
        return Objects.equals(name, mealModel.name) && Objects.equals(ingredientsNeeded, mealModel.ingredientsNeeded);
    }

    @Override
    public int hashCode() {
        return Objects.hash(icon, name, ingredientsNeeded);
    }
}
