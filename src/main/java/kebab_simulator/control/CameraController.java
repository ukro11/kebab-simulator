package kebab_simulator.control;

import KAGO_framework.control.ViewController;
import KAGO_framework.view.DrawTool;
import kebab_simulator.Config;
import kebab_simulator.animation.Easings;
import kebab_simulator.model.entity.Entity;
import kebab_simulator.utils.MathUtils;
import kebab_simulator.utils.Vec2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

public class CameraController {

    private Logger logger = LoggerFactory.getLogger(CameraController.class);
    private ViewController viewController;
    private ProgramController programController;
    private Vec2 anchor;
    private double x;
    private double y;
    private boolean smooth = false;
    private Function<Double, Double> easing = (x) -> Easings.easeInCubic(x);
    private double delay = 0.0;
    private double zoom = 1.0;
    private double angle = 0;
    private double duration = 1.0;
    private Vec2 offset = new Vec2();
    private double elapsed = this.duration;
    private Vec2 prevScale;
    private Entity focusEntity;
    private Vec2 cameraMax = new Vec2(10000, 10000);

    private Map.Entry<Instant, Double> currentShake;
    private double shakeElapsed = 0.0;
    private Map<Instant, Double> queue = new HashMap<>();

    private CameraController(ViewController view, ProgramController controller, double startX, double startY) {
        this.viewController = view;
        this.programController = controller;
        this.x = startX;
        this.y = startY;
        this.anchor = new Vec2(Config.WINDOW_WIDTH / 2, Config.WINDOW_HEIGHT / 2);
    }

    public static CameraController create(ViewController view, ProgramController controller, double startX, double startY) {
        return new CameraController(view, controller, startX, startY);
    }

    public CameraController smooth(Function<Double, Double> easing) {
        this.smooth = true;
        this.easing = easing;
        return this;
    }

    public CameraController delay(double delay) {
        this.delay = delay;
        return this;
    }

    public CameraController zoom(double zoom) {
        this.zoom = zoom;
        return this;
    }

    public CameraController angle(double angle) {
        this.angle = angle;
        return this;
    }

    public CameraController duration(double duration) {
        this.duration = duration;
        return this;
    }

    public CameraController offset(Vec2 offset) {
        this.offset = offset;
        return this;
    }

    public void attach(DrawTool drawTool) {
        if (this.prevScale == null) {
            this.prevScale = new Vec2();
            this.prevScale.set(drawTool.getGraphics2D().getTransform().getScaleX(), drawTool.getGraphics2D().getTransform().getScaleY());
        }
        drawTool.push();
        drawTool.getGraphics2D().translate(-Math.floor(this.x), -Math.floor(this.y));
        drawTool.getGraphics2D().scale(this.zoom, this.zoom);
        drawTool.getGraphics2D().rotate(this.angle);
    }

    public void detach(DrawTool drawTool) {
        drawTool.getGraphics2D().scale(this.prevScale.x, this.prevScale.y);
        drawTool.pop();
    }

    public void centerLines(DrawTool drawTool) {
        drawTool.setCurrentColor(Color.RED);
        drawTool.drawLine(0, Config.WINDOW_HEIGHT / 2, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT / 2);
        drawTool.drawLine(Config.WINDOW_WIDTH / 2, 0, Config.WINDOW_WIDTH / 2, Config.WINDOW_HEIGHT);
        drawTool.resetColor();
    }

    public void shake(double duration) {
        this.queue.put(Instant.now(), duration);
    }

    public void focusNoLimit(double x, double y, double dt) {
        double camX = x * this.zoom - Config.WINDOW_WIDTH / 2 + this.offset.x;
        double camY = y * this.zoom - Config.WINDOW_HEIGHT / 2 + this.offset.y;
        double diffX = camX - this.x;
        double diffY = camY - this.y;
        double tempX = this.x + diffX;
        double tempY = this.y + diffY;
        this.x = tempX;
        this.y = tempY;
    }

    private void focus(double x, double y, double dt) {
        double camX = x * this.zoom - Config.WINDOW_WIDTH / 2 + this.offset.x;
        double camY = y * this.zoom - Config.WINDOW_HEIGHT / 2 + this.offset.y;
        double diffX = camX - this.x;
        double diffY = camY - this.y;
        double tempX = this.x + diffX;
        double tempY = this.y + diffY;
        this.x = MathUtils.clamp(tempX, 0, cameraMax.x * this.zoom);
        this.y = MathUtils.clamp(tempY, 0, cameraMax.y * this.zoom);
    }

    public void focusAt(double x, double y, double dt) {
        if (this.focusEntity == null) this.focus(x, y, dt);
    }

    public void focusAtEntity(Entity entity) {
        this.focusEntity = entity;
    }

    public Map.Entry<Instant, Double> get() {
        return this.queue.entrySet().stream().findFirst().get();
    }

    public void update(double dt) {
        if (this.focusEntity != null) {
            this.focus(this.focusEntity.getBody().getX(), this.focusEntity.getBody().getY(), dt);
        }
        if (this.queue.size() > 0) {
            if (this.currentShake == null) {
                this.currentShake = this.get();
                this.shakeElapsed = this.currentShake.getValue();
            }

            this.shakeElapsed = Math.max(this.shakeElapsed - dt, 0);

            if (this.shakeElapsed == 0) {
                this.queue.remove(this.currentShake.getKey());
                this.currentShake = null;
                this.shakeElapsed = 0;

            } else {
                double shakeProgress = this.shakeElapsed / this.currentShake.getValue();

                double shakeMagnitude = Math.max(0.5, Math.min(1.5, shakeProgress));

                double shakeOffsetX = (Math.random() - 0.5) * shakeMagnitude * 10;
                double shakeOffsetY = (Math.random() - 0.5) * shakeMagnitude * 10;

                this.x = MathUtils.clamp(this.x + shakeOffsetX, 0, Config.WINDOW_WIDTH);
                this.y = MathUtils.clamp(this.y + shakeOffsetY, 0, Config.WINDOW_HEIGHT);
            }
        }
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZoom() {
        return this.zoom;
    }
}
