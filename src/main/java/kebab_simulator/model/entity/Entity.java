package kebab_simulator.model.entity;

import KAGO_framework.control.Drawable;
import KAGO_framework.control.Interactable;
import KAGO_framework.control.ViewController;
import KAGO_framework.view.DrawTool;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.states.CharacterAnimationState;
import kebab_simulator.control.ProgramController;
import kebab_simulator.graphics.IOrder;
import kebab_simulator.model.scene.GameScene;
import kebab_simulator.physics.Collider;
import kebab_simulator.utils.misc.Vec2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.UUID;

public abstract class Entity implements Drawable, Interactable, IOrder {

    protected ViewController viewController;
    protected ProgramController programController;
    protected final Logger logger = LoggerFactory.getLogger(Entity.class);

    protected final String id;
    protected final Collider body;
    protected double bodyOffsetX;
    protected double bodyOffsetY;
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected Vec2 highestPoint;
    protected Vec2 highestPointOffset;
    protected boolean showHitbox = false;
    protected boolean invertLeft = false;
    protected double scaleX = 1;
    protected double scaleY = 1;

    protected AnimationRenderer<CharacterAnimationState> renderer;

    public Entity(double x, double y, double width, double height) {
        this(null, x, y, width, height);
    }

    public Entity(Collider body, double x, double y, double width, double height) {
        try {
            StackTraceElement stackTrace1 = Thread.currentThread().getStackTrace()[3];
            StackTraceElement stackTrace2 = Thread.currentThread().getStackTrace()[4];
            if (!stackTrace1.getClassName().equals(EntityManager.class.getName()) && !stackTrace2.getClassName().equals(EntityManager.class.getName())) {
                throw new RuntimeException(String.format("To create an entity (%s) use \"Wrapper.getEntityManager().spawnPlayer(...)\"", this.getClass().getSimpleName()));
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        this.viewController = ViewController.getInstance();
        this.programController = this.viewController.getProgramController();

        this.id = UUID.randomUUID().toString();
        this.body = body;
        this.width = width;
        this.height = height;

        if (this.body != null) {
            this.body.setEntity(this);
            this.bodyOffsetX = x;
            this.bodyOffsetY = y;
            this.x = body.getX() + this.bodyOffsetX;
            this.y = body.getY() + this.bodyOffsetY;

        } else {
            this.x = x;
            this.y = y;
        }
        this.highestPoint = new Vec2(body.getX() + this.bodyOffsetX, body.getY() + this.bodyOffsetY);
        this.highestPointOffset = new Vec2();

        GameScene.getInstance().getRenderer().register(this);
    }

    @Override
    public void draw(DrawTool drawTool) {
        if (this.renderer != null && this.renderer.getCurrentFrame() != null) {
            drawTool.push();
            drawTool.getGraphics2D().scale(this.scaleX, this.scaleY);
            if (this.scaleX == -1 && this.isInvertLeft()) {
                drawTool.getGraphics2D().translate(-((int) this.x) * 2 - this.width, 0);
            }
            drawTool.getGraphics2D().drawImage(this.renderer.getCurrentFrame(), (int) this.x, (int) this.y, (int) this.width, (int) this.height, null);
            drawTool.pop();
        }
        if (this.showHitbox && this.body != null) {
            drawTool.setCurrentColor(this.getBody().getHitboxColor());
            drawTool.drawFilledCircle(this.highestPoint.x, this.highestPoint.y, 1);
            this.body.renderHitbox(drawTool);
        }
    }

    @Override
    public void update(double dt) {
        if (this.renderer != null) {
            if (!this.renderer.isRunning()) this.renderer.start();
            this.renderer.update(dt);
            this.highestPoint.set(this.x + this.highestPointOffset.x, this.y + this.highestPointOffset.y);
        }
    }

    @Override
    public double zIndex() {
        return this.highestPoint.y;
    }

    public void setHighestPointOffset(Vec2 offset) {
        this.highestPointOffset.set(offset);
    }

    public Vec2 getHighestPoint() {
        return highestPoint;
    }

    public AnimationRenderer<CharacterAnimationState> getRenderer() {
        return this.renderer;
    }

    public void setInvertLeft(boolean invertLeft) { this.invertLeft = invertLeft; }

    public boolean isInvertLeft() { return this.invertLeft; }

    public void setRenderer(AnimationRenderer<CharacterAnimationState> renderer) {
        this.renderer = renderer;
    }

    public boolean shouldShowHitbox() {
        return this.showHitbox;
    }

    public void setShowHitbox(boolean showHitbox) {
        this.showHitbox = showHitbox;
    }

    public Collider getBody() {
        return body;
    }

    public String getId() {
        return this.id;
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

    public double getBodyOffsetX() {
        return bodyOffsetX;
    }

    public double getBodyOffsetY() {
        return bodyOffsetY;
    }

    @Override
    public void keyPressed(int key) {}

    @Override
    public void keyReleased(int key) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(this.id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public enum EntityDirection {
        TOP,
        LEFT,
        BOTTOM,
        RIGHT,
    }
}
