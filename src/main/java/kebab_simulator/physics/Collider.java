package kebab_simulator.physics;

import KAGO_framework.view.DrawTool;
import kebab_simulator.control.Wrapper;
import kebab_simulator.event.events.collider.ColliderCollisionEvent;
import kebab_simulator.event.events.collider.ColliderDestroyEvent;
import kebab_simulator.model.entity.Entity;
import kebab_simulator.utils.misc.Vec2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public abstract class Collider {

    protected final Logger logger = LoggerFactory.getLogger(Collider.class);

    protected String id;
    protected BodyType type;
    protected ColliderForm form;
    protected double x;
    protected double y;
    protected double radius;
    protected double width;
    protected double height;
    protected Vec2 velocity = new Vec2();
    protected Entity entity;
    protected String colliderClass;
    protected boolean sensor = false;
    protected Color hitboxColor = Color.RED;
    private boolean destroyed = false;
    private Vec2 lastVelocity = new Vec2();
    protected Collider parent;
    protected List<Collider> children = new ArrayList<>();

    protected Consumer<ColliderCollisionEvent> onCollision;
    protected Consumer<ColliderDestroyEvent> onDestroy;

    private HashMap<String, Boolean> wasColliding = new HashMap<>();

    /**
     * Setzt die lineare Geschwindigkeit des Bodys in der x- und y-Richtung. Damit kann der Body bewegt werden.
     * @param velocityX
     * @param velocityY
     */
    public void setLinearVelocity(double velocityX, double velocityY) {
        this.velocity.set(velocityX, velocityY);
    }

    /**
     * Die Methode überprüft, ob das Body mit dem Body {@code other} kollidiert ist.
     * @param other Das andere Body, was überprüft werden soll, ob es eine Kollision mit dem Body detected hat.
     * @return {@code true}, wenn das Body {@code other} mit dem Body kollidiert ist.
     */
    public boolean collides(Collider other) {
        if (other == null) {
            this.logger.error("Failed to check collision because parameter \"other\" was null.");
            return false;
        }
        boolean handle = this.handleCollision(other);
        boolean check = this.wasColliding.getOrDefault(other.getId(), false);
        if (handle && !check) {
            var event = new ColliderCollisionEvent(this, other, ColliderCollisionEvent.CollisionState.COLLISION_BEGIN_CONTACT);
            this.wasColliding.put(other.getId(), true);
            if (this.onCollision != null) this.onCollision.accept(event);
            Wrapper.getEventManager().dispatchEvent(event);

        } else if (!handle && check) {
            var event = new ColliderCollisionEvent(this, other, ColliderCollisionEvent.CollisionState.COLLISION_END_CONTACT);
            this.wasColliding.put(other.getId(), false);
            if (this.onCollision != null) this.onCollision.accept(event);
            Wrapper.getEventManager().dispatchEvent(event);

        } else if (handle && check) {
            var event = new ColliderCollisionEvent(this, other, ColliderCollisionEvent.CollisionState.COLLISION_NORMAL_CONTACT);
            if (this.onCollision != null) this.onCollision.accept(event);
            Wrapper.getEventManager().dispatchEvent(event);
        }
        return handle;
    }

    public boolean queueCollision(Collider other) {
        // TODO
        return false;
    }

    public abstract boolean handleCollision(Collider other);
    public abstract void renderHitbox(DrawTool drawTool);
    public abstract Vec2 getCenter();
    public abstract Interval project(Vec2 vector);
    public abstract List<Vec2> getAxes();
    public abstract AABB computeAABB();

    public void addChild(Collider collider) {
        if (collider.getType() == BodyType.DYNAMIC) {
            this.children.add(collider);

        } else {
            this.logger.info("Collider {} could not be added as child because collider is not dynamic", collider.getId());
        }
    }

    public void removeChild(Collider collider) {
        this.children.remove(collider);
    }

    public boolean isDestroyed() {
        return this.destroyed;
    }

    public void setSensor(boolean sensor) {
        this.sensor = sensor;
    }

    public void setColliderClass(String colliderClass) {
        this.colliderClass = colliderClass;
    }

    public void destroy() {
        Wrapper.getColliderManager().destroyBody(this);
        this.destroyed = true;
        if (this.onDestroy != null) this.onDestroy.accept(new ColliderDestroyEvent(this));
    }

    public void onCollision(Consumer<ColliderCollisionEvent> onCollision) {
        this.onCollision = onCollision;
    }

    public void onDestroy(Consumer<ColliderDestroyEvent> onDestroy) {
        this.onDestroy = onDestroy;
    }

    public void update(double dt) {
        if (this.type == BodyType.DYNAMIC && this.velocity.magnitude() > 0) {
            this.x += this.velocity.x * dt;
            this.y += this.velocity.y * dt;
            this.entity.setX(this.x);
            this.entity.setY(this.y);
        }
        if (!this.lastVelocity.equals(this.velocity)) {
            this.children.forEach(child -> {
                child.setLinearVelocity(this.velocity.x, this.velocity.y);
            });
            this.lastVelocity = this.velocity.clone();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collider body = (Collider) o;
        return Objects.equals(this.id, body.id);
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public boolean isSensor() {
        return this.sensor;
    }

    public String getColliderClass() {
        return this.colliderClass;
    }

    public String getId() {
        return this.id;
    }

    public BodyType getType() {
        return this.type;
    }

    public ColliderForm getForm() {
        return this.form;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public double getRadius() {
        return this.radius;
    }

    public Vec2 getVelocity() {
        return this.velocity;
    }

    public Color getHitboxColor() {
        return this.hitboxColor;
    }

    public void setHitboxColor(Color hitboxColor) {
        this.hitboxColor = hitboxColor;
    }
}
