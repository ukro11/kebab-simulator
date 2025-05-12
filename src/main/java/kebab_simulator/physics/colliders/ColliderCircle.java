package kebab_simulator.physics.colliders;

import KAGO_framework.view.DrawTool;
import kebab_simulator.control.Wrapper;
import kebab_simulator.physics.BodyType;
import kebab_simulator.physics.Collider;
import kebab_simulator.physics.ColliderForm;
import kebab_simulator.utils.MathUtils;

import java.awt.*;
import java.util.UUID;

public class ColliderCircle extends Collider {

    public ColliderCircle(BodyType type, double x, double y, double radius) {
        this(UUID.randomUUID().toString(), type, x, y, radius);
    }

    public ColliderCircle(String id, BodyType type, double x, double y, double radius) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.form = ColliderForm.CIRCLE;
        this.colliderClass = "default";
        Wrapper.getColliderManager().createBody(this);
    }

    @Override
    public boolean handleCollision(Collider other) {
        return MathUtils.isCircleCollided(this, other);
    }

    @Override
    public void renderHitbox(DrawTool drawTool) {
        drawTool.setCurrentColor(new Color(150, 0, 0));
        drawTool.drawCircle(this.x, this.y, this.radius);
        drawTool.resetColor();
    }
}
