package kebab_simulator.animation.value;

import kebab_simulator.animation.Easings;
import kebab_simulator.utils.game.CooldownManager;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class ObjectAnimation<T> {

    private T object;
    private double duration;
    private CooldownManager cooldownManager;
    private Map<String, Double> target;
    private Function<Double, Double> easing;

    private Consumer<T> before;
    private Consumer<T> after;

    private ObjectAnimation(T object, double duration, Map<String, Double> target) {
        this.object = object;
        this.duration = duration;
        this.target = target;
        this.easing = (x) -> Easings.linear(x);
    }

    public static <T> ObjectAnimation to(T object, double duration, Map<String, Double> target) {
        return new ObjectAnimation(object, duration, target);
    }

    public ObjectAnimation<T> ease(Function<Double, Double> easing) {
        this.easing = easing;
        return this;
    }

    public ObjectAnimation<T> duration(double duration) {
        this.duration = duration;
        return this;
    }

    public ObjectAnimation<T> before(Consumer<T> before) {
        this.before = before;
        return this;
    }

    public ObjectAnimation<T> after(Consumer<T> after) {
        this.after = after;
        return this;
    }
}
