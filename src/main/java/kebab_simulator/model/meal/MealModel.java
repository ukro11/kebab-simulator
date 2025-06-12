package kebab_simulator.model.meal;

import kebab_simulator.model.entity.impl.food.EntityFood;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public abstract class MealModel {

    private final BufferedImage icon;
    private final String name;
    private final List<IngredientModel> ingredientsNeeded;
    private final List<EntityFood<?>> currentIngredients;

    public MealModel(BufferedImage icon, String name, IngredientModel... ingredients) {
        this.icon = icon;
        this.name = name;
        this.ingredientsNeeded = List.of(ingredients);
        this.currentIngredients = new ArrayList<>();
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

    public List<IngredientModel> getCurrentIngredients() {
        return this.ingredientsNeeded;
    }

    public boolean isMealValid() {
        if (this.ingredientsNeeded.size() != this.currentIngredients.size()) return false;
        for (int i = 0; i < this.ingredientsNeeded.size(); i++) {
            var target = this.ingredientsNeeded.get(i);
            var filter = this.currentIngredients.stream().filter(ing -> ing.getClass().equals(target.getEntityClass())).findFirst().orElse(null);

            if (filter == null) return false;
            /*if (target.getTargetState().needsToBeCooked()) {
                if (!(filter instanceof IEntityCookable)) return false;
                var cookable = (IEntityCookable) filter;

                if (!cookable.isCooked() || cookable.isFoodBurnt()) return false;

            } else {
                if (filter instanceof IEntityCookable) {
                    var cookable = (IEntityCookable) filter;
                    if (cookable.isFoodBurnt() || cookable.isCooked()) return false;
                }
            }*/
        }
        return true;
    }
}
