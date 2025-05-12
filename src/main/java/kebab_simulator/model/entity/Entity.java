package kebab_simulator.model.entity;

import KAGO_framework.control.Drawable;
import KAGO_framework.control.Interactable;
import KAGO_framework.view.DrawTool;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.AnimationState;
import kebab_simulator.control.ProgramController;
import kebab_simulator.physics.Collider;

import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.UUID;

public class Entity implements Drawable, Interactable {

    private final String id;
    private final Collider body;
    private double bodyOffsetX;
    private double bodyOffsetY;
    private double x;
    private double y;
    private double width;
    private double height;
    private boolean showHitbox = false;

    private AnimationRenderer<AnimationState> renderer;

    public Entity(double x, double y, double width, double height) {
        this(null, x, y, width, height);
    }

    public Entity(Collider body, double x, double y, double width, double height) {
        this.id = UUID.randomUUID().toString();
        this.body = body;
        this.width = width;
        this.height = height;

        if (this.body != null) {
            this.bodyOffsetX = x;
            this.bodyOffsetY = y;
            this.x = body.getX() + this.bodyOffsetX;
            this.y = body.getY() + this.bodyOffsetY;
            this.body.setEntity(this);

        } else {
            this.x = x;
            this.y = y;
        }
    }

    @Override
    public void draw(DrawTool drawTool) {
        if (ProgramController.TEST_SCALE) drawTool.getGraphics2D().scale(4, 4);
        if (this.renderer != null) {
            drawTool.resetColor();
            this.renderer.getCurrentFrame().paintIcon(drawTool.getParent(), drawTool.getGraphics2D(), (int) this.x, (int) this.y);
            drawTool.resetColor();
        }
        if (this.showHitbox && this.body != null) this.body.renderHitbox(drawTool);
    }

    @Override
    public void update(double dt) {
        /*if (this.body != null) {
            this.x = body.getX() + this.bodyOffsetX;
            this.y = body.getY() + this.bodyOffsetY;
        }*/
        if (this.renderer != null) {
            if (!this.renderer.isRunning()) this.renderer.start();
            this.renderer.update(dt);
        }
    }

    public AnimationRenderer<AnimationState> getRenderer() {
        return this.renderer;
    }

    public void setRenderer(AnimationRenderer<AnimationState> renderer) {
        this.renderer = renderer;
    }

    public boolean showHitbox() {
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
    public void keyPressed(int key) {

    }

    @Override
    public void keyReleased(int key) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

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
}
