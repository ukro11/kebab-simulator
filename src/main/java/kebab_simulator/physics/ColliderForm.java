package kebab_simulator.physics;

public enum ColliderForm {
    RECTANGLE("rectangle"),
    CIRCLE("circle"),
    POLYGON("polygon");

    private String shape;

    private ColliderForm(String shape) {
        this.shape = shape;
    }

    public String getShape() {
        return shape;
    }
}
