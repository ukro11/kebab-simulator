package kebab_simulator.physics.colliders;

import KAGO_framework.view.DrawTool;
import kebab_simulator.control.Wrapper;
import kebab_simulator.physics.BodyType;
import kebab_simulator.physics.Collider;
import kebab_simulator.physics.ColliderForm;
import kebab_simulator.utils.MathUtils;

import java.awt.*;
import java.util.UUID;

public class ColliderRectangle extends Collider {

    private Polygon rectangleShape;

    public ColliderRectangle(BodyType type, double x, double y, double width, double height) {
        this(UUID.randomUUID().toString(), type, x, y, width, height);
    }

    public ColliderRectangle(String id, BodyType type, double x, double y, double width, double height) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rectangleShape = new Polygon();
        rectangleShape.addPoint((int) this.getX(), (int) this.getY());
        rectangleShape.addPoint((int) this.getX() + (int) this.getWidth(), (int) this.getY());
        rectangleShape.addPoint((int) this.getX() + (int) this.getWidth(), (int) this.getY() + (int) this.getHeight());
        rectangleShape.addPoint((int) this.getX(), (int) this.getY() + (int) this.getHeight());
        this.form = ColliderForm.RECTANGLE;
        this.colliderClass = "default";
        Wrapper.getColliderManager().createBody(this);
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (this.type == BodyType.DYNAMIC && this.velocity.len() > 0) {
            this.rectangleShape.xpoints[0] = (int) this.getX();
            this.rectangleShape.xpoints[1] = (int) (this.getX() + this.getWidth());
            this.rectangleShape.xpoints[2] = (int) (this.getX() + this.getWidth());
            this.rectangleShape.xpoints[3] = (int) this.getX();

            this.rectangleShape.ypoints[0] = (int) this.getY();
            this.rectangleShape.ypoints[1] = (int) this.getY();
            this.rectangleShape.ypoints[2] = (int) (this.getY() + this.getHeight());
            this.rectangleShape.ypoints[3] = (int) (this.getY() + this.getHeight());
        }
    }

    @Override
    public boolean handleCollision(Collider other) {
        return MathUtils.isRectangleCollided(this, other);
    }

    @Override
    public void renderHitbox(DrawTool drawTool) {
        drawTool.setCurrentColor(new Color(150, 0, 0));
        drawTool.drawRectangle(this.x, this.y, this.width, this.height);
        drawTool.resetColor();
    }

    public Polygon getRectangleShape() {
        return this.rectangleShape;
    }
}
