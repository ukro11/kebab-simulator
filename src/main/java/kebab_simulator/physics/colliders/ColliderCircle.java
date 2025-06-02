package kebab_simulator.physics.colliders;

import KAGO_framework.view.DrawTool;
import kebab_simulator.control.Wrapper;
import kebab_simulator.physics.*;
import kebab_simulator.utils.misc.MathUtils;
import kebab_simulator.utils.misc.Vec2;

import java.util.List;
import java.util.UUID;

public class ColliderCircle extends Collider {

    private final Vec2 center;

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
        this.center = new Vec2(this.x + radius, this.y + radius);
        Wrapper.getColliderManager().createBody(this);
    }

    @Override
    public boolean handleCollision(Collider other) {
        switch (other.getForm()) {
            case CIRCLE -> {
                return MathUtils.fastCircleDetection(this, (ColliderCircle) other);
            }
            case POLYGON, RECTANGLE -> {
                if (MathUtils.AABB(this, other)) {
                    return MathUtils.SAT(this, other);
                }
            }
        }
        return false;
    }

    @Override
    public AABB computeAABB() {
        Vec2 center = this.center;
        double minX = center.x - this.radius;
        double minY = center.y - this.radius;
        double maxX = center.x + this.radius;
        double maxY = center.y + this.radius;
        return new AABB(minX, minY, maxX, maxY);
    }

    @Override
    public void update(double dt) {
        if (this.type == BodyType.DYNAMIC) {
            if (this.velocity != null && !this.isDestroyed() && this.velocity.magnitude() > 0) {
                this.center.add(this.velocity.x * dt, this.velocity.y * dt);
            }
        }
    }

    @Override
    public void renderHitbox(DrawTool drawTool) {
        if (!this.isDestroyed()) {
            drawTool.setCurrentColor(this.hitboxColor);
            drawTool.drawCircle(this.getCenter().x, this.getCenter().y, this.radius);
            drawTool.resetColor();
        }
    }

    @Override
    public List<Vec2> getAxes() {
        return null;
    }

    @Override
    public Interval project(Vec2 vector) {
        // project the center onto the given axis
        double c = this.center.clone().dot(vector);
        double r = this.radius;
        // the interval is defined by the radius
        return new Interval(c - r, c + r);
    }

    @Override
    public Vec2 getCenter() {
        return this.center;
    }
}
