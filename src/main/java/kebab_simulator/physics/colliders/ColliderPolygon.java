package kebab_simulator.physics.colliders;

import KAGO_framework.view.DrawTool;
import kebab_simulator.control.Wrapper;
import kebab_simulator.physics.BodyType;
import kebab_simulator.physics.Collider;
import kebab_simulator.physics.ColliderForm;
import kebab_simulator.utils.MathUtils;
import kebab_simulator.utils.Vec2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ColliderPolygon extends Collider {

    private final Logger logger = LoggerFactory.getLogger(ColliderPolygon.class);
    protected List<Vec2> polygonVertices = new ArrayList<>();
    private java.awt.Polygon polygonShape;

    public ColliderPolygon(BodyType type, List<Vec2> vertices) {
        this(UUID.randomUUID().toString(), type, vertices);
    }

    public ColliderPolygon(String id, BodyType type, List<Vec2> vertices) {
        this.id = id;
        this.type = type;
        this.polygonVertices = vertices;
        this.form = ColliderForm.POLYGON;
        this.colliderClass = "default";
        this.polygonShape = new Polygon();
        for (Vec2 vertice : this.polygonVertices) {
            this.polygonShape.addPoint((int) vertice.x, (int) vertice.y);
        }
        Wrapper.getColliderManager().createBody(this);
    }

    @Override
    public boolean handleCollision(Collider other) {
        return MathUtils.isPolygonCollided(this, other);
    }

    @Override
    public void overwriteEntity() {
        // TODO: polygon
    }

    @Override
    public void update(double dt) {
        if (this.type == BodyType.DYNAMIC && this.velocity.len() > 0) {
            for (int i = 0; i < this.polygonVertices.size(); i++) {
                Vec2 vertice = this.polygonVertices.get(i);
                vertice.add(this.velocity.x * dt, this.velocity.y * dt);
                this.polygonShape.xpoints[i] = (int) vertice.x;
                this.polygonShape.ypoints[i] = (int) vertice.y;
            }
        }
        if (this.entity != null) {
            this.overwriteEntity();
        }
    }

    @Override
    public void renderHitbox(DrawTool drawTool) {
        drawTool.setCurrentColor(Color.RED);
        drawTool.setLineWidth(1);
        drawTool.drawPolygon(this.polygonVertices);
        drawTool.resetColor();
    }

    private boolean isPointInRectangle(ColliderRectangle rectangle, Vec2 point) {
        return point.x >= rectangle.getX() && point.x <= rectangle.getX() + rectangle.getWidth() &&
                point.y >= rectangle.getY() && point.y <= rectangle.getY() + rectangle.getHeight();
    }

    public List<Vec2> getPolygonVertices() {
        return this.polygonVertices;
    }

    public Polygon getPolygonShape() {
        return this.polygonShape;
    }
}
