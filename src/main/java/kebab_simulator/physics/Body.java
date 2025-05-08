package kebab_simulator.physics;

import KAGO_framework.view.DrawTool;
import kebab_simulator.control.Wrapper;
import kebab_simulator.event.Event;
import kebab_simulator.event.EventListener;
import kebab_simulator.event.EventListenerIntegration;
import kebab_simulator.event.events.CollisionEvent;
import kebab_simulator.utils.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class Body implements EventListenerIntegration<CollisionEvent, Body.BodyEvents> {

    private final Logger logger = LoggerFactory.getLogger(Body.class);

    private final String id;
    private final BodyType type;
    private final BodyForm form;
    private double x;
    private double y;
    private double radius;
    private double width;
    private double height;
    private double velocityX;
    private double velocityY;
    private String bodyClass;
    private boolean sensor = false;

    private boolean wasColliding = false;

    public Body(BodyType type, double x, double y, double width, double height) {
        this(UUID.randomUUID().toString(), type, x, y, width, height);
    }

    public Body(BodyType type, double x, double y, double radius) {
        this(UUID.randomUUID().toString(), type, x, y, radius);
    }

    public Body(String id, BodyType type, double x, double y, double width, double height) {
        this.id = id;
        this.type = type;
        this.form = BodyForm.RECTANGLE;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bodyClass = "default";
        Wrapper.getColliderManager().createBody(this);
    }

    public Body(String id, BodyType type, double x, double y, double radius) {
        this.id = id;
        this.type = type;
        this.form = BodyForm.CIRCLE;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.bodyClass = "default";
        Wrapper.getColliderManager().createBody(this);
    }

    /**
     * Setzt die lineare Geschwindigkeit des Bodys in der x- und y-Richtung. Damit kann der Body bewegt werden.
     * @param velocityX
     * @param velocityY
     */
    public void setLinearVelocity(double velocityX, double velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    @Override
    public EventListener<CollisionEvent> addEventListener(BodyEvents event, Consumer<CollisionEvent> handle) {
        if (event.getEventClass().equals(CollisionEvent.class)) {
            EventListener<CollisionEvent> listener = (CollisionEvent e) -> handle.accept(e);
            Wrapper.getEventManager().addEventListener("bodyCollided", listener);
            return listener;
        }
        return null;
    }

    @Override
    public void once(BodyEvents event, Consumer<CollisionEvent> handle) {
        if (event.getEventClass().equals(CollisionEvent.class)) {
            Wrapper.getEventManager().once("bodyCollided", (CollisionEvent e) -> handle.accept(e));
        }
    }

    /**
     * Die Methode überprüft, ob das Body mit dem Body {@code other} kollidiert ist.
     * @param other Das andere Body, was überprüft werden soll, ob es eine Kollision mit dem Body detected hat.
     * @return {@code true}, wenn das Body {@code other} mit dem Body kollidiert ist.
     */
    public boolean collides(Body other) {
        if (other == null) {
            this.logger.error("Failed to check collision because parameter \"other\" was null.");
            return false;
        }
        boolean handle = false;
        switch (this.form) {
            case RECTANGLE -> {
                handle = this.handleCollisionRectangle(other);
            }
            case CIRCLE -> {
                handle = this.handleCollisionCircle(other);
            }
        }
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



    /**
     * Berechnet, ob eine Kollision vorliegt.<br>
     * <strong>Die Methode wird nur aufgerufen, wenn der Body (this) ein Rectangle ist.</strong>
     * @return {@code true}, wenn eine Kollision vorliegt.
     */
    private boolean handleCollisionRectangle(Body other) {
        // ! WICHTIG: Diese Methode sollte nur aufgerufen werden, wenn body (this) ein rectangle ist
        if (this.form != BodyForm.RECTANGLE) {
            throw new InvalidParameterException("To call checkCollisionRectangle(...) you need to make sure that your body is a rectangle!");
        }
        switch (other.getForm()) {
            case RECTANGLE -> {
                boolean result = this.x < other.getX() + other.getWidth() &&
                        this.x + this.width > other.getX() &&
                        this.y < other.getY() + other.getHeight() &&
                        this.y + this.height > other.getY();
                if (result) {
                    double overlapX = Math.min(this.getX() + this.getWidth() - other.getX(), other.getX() + other.getWidth() - this.getX());
                    double overlapY = Math.min(this.getY() + this.getHeight() - other.getY(), other.getY() + other.getHeight() - this.getY());

                    if (overlapX < overlapY) {
                        if (this.getX() < other.getX()) {
                            this.setX(this.getX() - overlapX);
                        } else {
                            this.setX(this.getX() + overlapX);
                        }
                    } else {
                        if (this.getY() < other.getY()) {
                            this.setY(this.getY() - overlapY);
                        } else {
                            this.setY(this.getY() + overlapY);
                        }
                    }
                }
                return result;
            }
            case CIRCLE -> {
                return this.calculateCollisionRectangleCircle(this, other);
            }
        }
        return false;
    }

    /**
     * Berechnet, ob eine Kollision vorliegt.<br>
     * <strong>Die Methode wird nur aufgerufen, wenn der Body (this) ein Kreis ist.</strong>
     * @return {@code true}, wenn eine Kollision vorliegt.
     */
    private boolean handleCollisionCircle(Body other) {
        // ! WICHTIG: Diese Methode sollte nur aufgerufen werden, wenn body (this) ein rectangle ist
        if (this.form != BodyForm.CIRCLE) {
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
    }

    /**
     * Berechnet, ob eine Kollision vorliegt zwischen einem {@code Body rectangle} und einem {@code Body circle}.
     * @return {@code true}, wenn eine Kollision vorliegt.
     */
    private boolean calculateCollisionRectangleCircle(Body rectangle, Body circle) {
        // Finde den nächstgelegenen Punkt auf dem Rechteck zum Mittelpunkt des Kreises
        double closestX = MathUtils.clamp(circle.getX(), rectangle.getX(), rectangle.getX() + rectangle.getWidth());
        double closestY = MathUtils.clamp(circle.getY(), rectangle.getY(), rectangle.getY() + rectangle.getHeight());

        // Berechne den Abstand vom Mittelpunkt des Kreises zum nächsten Punkt auf dem Rechteck
        double dx = circle.getX() - closestX;
        double dy = circle.getY() - closestY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        boolean collide = distance < circle.getRadius();

        if (collide) {
            // Berechne den Überlappungsbereich
            double overlap = circle.getRadius() - distance;

            // Verschiebe die Körper entlang der Linie zwischen dem Mittelpunkt des Kreises und dem Punkt
            double offsetX = dx / distance * overlap;
            double offsetY = dy / distance * overlap;

            circle.setX(circle.getX() + offsetX);
            circle.setY(circle.getY() + offsetY);
            return true;
        }
        return false;
    }

    public void setSensor(boolean sensor) {
        this.sensor = sensor;
    }

    public void setBodyClass(String bodyClass) {
        this.bodyClass = bodyClass;
    }

    public void destroy() {
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
        this.radius = 0;
        this.velocityX = 0;
        this.velocityY = 0;
        Wrapper.getColliderManager().destroyBody(this);
    }

    public void update(double dt) {
        //if (y > 100) this.logger.debug("UPDATE, {}", this.getId());
        this.x += this.getVelocityX() * dt;
        this.y += this.getVelocityY() * dt;
    }

    public void renderHitbox(DrawTool drawTool) {
        drawTool.setCurrentColor(new Color(150, 0, 0));
        switch (this.form) {
            case CIRCLE -> {
                drawTool.drawCircle(this.x, this.y, this.radius);
            }
            case RECTANGLE -> {
                drawTool.drawRectangle(this.x, this.y, this.width, this.height);
            }
        }
        drawTool.resetColor();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Body body = (Body) o;
        return Objects.equals(this.id, body.id);
    }

    public boolean isSensor() {
        return this.sensor;
    }

    public String getBodyClass() {
        return this.bodyClass;
    }

    public String getId() {
        return this.id;
    }

    public BodyType getType() {
        return this.type;
    }

    public BodyForm getForm() {
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

    public double getVelocityX() {
        return this.velocityX;
    }

    public double getVelocityY() {
        return this.velocityY;
    }

    public enum BodyForm {
        RECTANGLE("rectangle"),
        CIRCLE("circle");

        private String shape;

        private BodyForm(String shape) {
            this.shape = shape;
        }

        public String getShape() {
            return shape;
        }
    }

    public enum BodyEvents {
        BODY_COLISSION_EVENT("bodyCollided", CollisionEvent.class);

        private final String event;
        private final Class<? extends Event> eventClass;

        private BodyEvents(String event, Class<? extends Event> eventClass) {
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
