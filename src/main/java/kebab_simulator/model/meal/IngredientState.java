package kebab_simulator.model.meal;

public class IngredientState {

    private final double cookDuration;
    private final boolean cook;
    private final double cutDuration;
    private final boolean cut;

    public IngredientState(boolean cook, double cookDuration, boolean cut, double cutDuration) {
        this.cook = cook;
        this.cookDuration = cookDuration;
        this.cut = cut;
        this.cutDuration = cutDuration;
    }

    public boolean needsToBeCooked() {
        return this.cook;
    }

    public double getCookDuration() {
        return this.cookDuration;
    }

    public boolean needsToBeCut() {
        return this.cut;
    }

    public double getCutDuration() {
        return this.cutDuration;
    }
}
