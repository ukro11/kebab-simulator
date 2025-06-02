package kebab_simulator.graphics.map;

import KAGO_framework.control.ViewController;
import KAGO_framework.view.DrawTool;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.IAnimationState;
import kebab_simulator.control.ProgramController;
import kebab_simulator.graphics.IOrder;
import kebab_simulator.physics.Collider;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class ObjectSpawner<T extends Enum<T> & IAnimationState> implements IOrder {

    public static final CopyOnWriteArrayList<ObjectSpawner<?>> objects = new CopyOnWriteArrayList<>();
    private static final HashMap<String, Collider> mapper = new HashMap<>();

    protected ViewController viewController;
    protected ProgramController programController;

    protected final String id;
    protected final Collider collider;
    protected Collider sensorCollider;
    protected final AnimationRenderer<T> renderer;

    public ObjectSpawner(String id, Collider collider, AnimationRenderer<T> renderer) {
        this.viewController = ViewController.getInstance();
        this.programController = this.viewController.getProgramController();

        this.id = id;
        this.collider = collider;
        this.renderer = renderer;
        this.renderer.start();
        if (ObjectSpawner.mapper.containsKey(collider.getId())) {
            this.sensorCollider = ObjectSpawner.mapper.get(collider.getId());
        }
        ObjectSpawner.objects.add(this);
    }

    public void update(double dt) {
        this.renderer.update(dt);
    }

    @Override
    public void draw(DrawTool drawTool) {
        drawTool.drawImage(this.renderer.getCurrentFrame(), this.collider.getX(), this.collider.getY());
    }

    @Override
    public double zIndex() {
        return this.collider.getY();
    }

    public String getId() {
        return this.id;
    }

    public Collider getCollider() {
        return this.collider;
    }

    public Collider getSensorCollider() {
        return this.sensorCollider;
    }

    public void setSensorCollider(Collider sensorCollider) {
        this.sensorCollider = sensorCollider;
    }

    public AnimationRenderer<T> getRenderer() {
        return this.renderer;
    }

    public static ObjectSpawner<?> fetchById(String id) {
        for (ObjectSpawner<?> spawner : ObjectSpawner.objects) {
            if (spawner.getId().equals(id)) {
                return spawner;
            }
        }
        return null;
    }

    public static void mapSensor(String id, Collider collider) {
        ObjectSpawner.mapper.put(id, collider);
    }
}
