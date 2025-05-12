package kebab_simulator.physics;

import KAGO_framework.view.DrawTool;
import kebab_simulator.control.Wrapper;
import kebab_simulator.event.Event;
import kebab_simulator.event.EventListener;
import kebab_simulator.event.EventListenerIntegration;
import kebab_simulator.event.events.CollisionEvent;
import kebab_simulator.model.entity.Entity;
import kebab_simulator.utils.MathUtils;
import kebab_simulator.utils.Vec2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class Collider implements EventListenerIntegration<CollisionEvent, Collider.ColliderEvents> {

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

    private boolean wasColliding = false;

    /**
     * Setzt die lineare Geschwindigkeit des Bodys in der x- und y-Richtung. Damit kann der Body bewegt werden.
     * @param velocityX
     * @param velocityY
     */
    public void setLinearVelocity(double velocityX, double velocityY) {
        this.velocity.set(velocityX, velocityY);
    }

    @Override
    public EventListener<CollisionEvent> addEventListener(ColliderEvents event, Consumer<CollisionEvent> handle) {
        if (event.getEventClass().equals(CollisionEvent.class)) {
            EventListener<CollisionEvent> listener = (CollisionEvent e) -> handle.accept(e);
            Wrapper.getEventManager().addEventListener("bodyCollided", listener);
            return listener;
        }
        return null;
    }

    @Override
    public void once(ColliderEvents event, Consumer<CollisionEvent> handle) {
        if (event.getEventClass().equals(CollisionEvent.class)) {
            Wrapper.getEventManager().once("bodyCollided", (CollisionEvent e) -> handle.accept(e));
        }
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
        if (handle && !this.wasColliding) {
            this.wasColliding = true;
            Wrapper.getEventManager().dispatchEvent(new CollisionEvent(this, other, CollisionEvent.CollisionState.COLLISION_BEGIN_CONTACT));

        } else if (!handle && this.wasColliding) {
            this.wasColliding = false;
            Wrapper.getEventManager().dispatchEvent(new CollisionEvent(this, other, CollisionEvent.CollisionState.COLLISION_END_CONTACT));

        } else if (handle && this.wasColliding) {
            Wrapper.getEventManager().dispatchEvent(new CollisionEvent(this, other, CollisionEvent.CollisionState.COLLISION_NORMAL_CONTACT));
        }
        return handle;
    }

    public abstract boolean handleCollision(Collider other);
    public abstract void renderHitbox(DrawTool drawTool);

    /**
     * Berechnet, ob eine Kollision vorliegt.<br>
     * <strong>Die Methode wird nur aufgerufen, wenn der Body (this) ein Rectangle ist.</strong>
     * @return {@code true}, wenn eine Kollision vorliegt.
     */
    private boolean handleCollisionRectangle(Collider other) {
        return false;
    }

    /**
     * Berechnet, ob eine Kollision vorliegt.<br>
     * <strong>Die Methode wird nur aufgerufen, wenn der Body (this) ein Kreis ist.</strong>
     * @return {@code true}, wenn eine Kollision vorliegt.
     */
    /*private boolean handleCollisionCircle(Collider other) {
        // ! WICHTIG: Diese Methode sollte nur aufgerufen werden, wenn body (this) ein rectangle ist
        if (this.form != ColliderForm.CIRCLE) {
            throw new InvalidParameterException("To call checkCollisionRectangle(...) you need to make sure that your body is a circle!");
        }
        switch (other.getForm()) {
            case CIRCLE -> {
                double dx = this.x - other.getX();
                double dy = this.y - other.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                boolean res = distance < (this.radius + other.getRadius());
                if (res) {
                    double overlap = this.radius + other.getRadius() - distance;
                    double offsetX = dx / distance * overlap / 2;
                    double offsetY = dy / distance * overlap / 2;
                    this.setX(this.x + offsetX);
                    this.setY(this.y + offsetY);
                    other.setX(other.getX() - offsetX);
                    other.setY(other.getY() - offsetY);
                }
                return res;
            }
            case RECTANGLE -> {
                return this.calculateCollisionRectangleCircle(other, this);
            }
        }
        return false;
    }*/

    public void setSensor(boolean sensor) {
        this.sensor = sensor;
    }

    public void setColliderClass(String colliderClass) {
        this.colliderClass = colliderClass;
    }

    public void destroy() {
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
        this.radius = 0;
        this.velocity = new Vec2();
        Wrapper.getColliderManager().destroyBody(this);
    }

    public void overwriteEntity() {
        this.entity.setX(this.getX() + this.entity.getBodyOffsetX());
        this.entity.setY(this.getY() + this.entity.getBodyOffsetY());
    }

    public void update(double dt) {
        if (this.type == BodyType.DYNAMIC && this.velocity.len() > 0) {
            this.x += this.velocity.x * dt;
            this.y += this.velocity.y * dt;
        }
        if (this.entity != null) {
            this.overwriteEntity();
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
        return velocity;
    }

    public enum ColliderEvents {
        BODY_COLISSION_EVENT("bodyCollided", CollisionEvent.class);

        private final String event;
        private final Class<? extends Event> eventClass;

        private ColliderEvents(String event, Class<? extends Event> eventClass) {
            this.event = event;
            this.eventClass = eventClass;
        }

        public String getEvent() {
            return event;
        }

        public Class<? extends Event> getEventClass() {
            return eventClass;
        }
    }
}
