package kebab_simulator.animation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

public class AnimationRenderer<T extends AnimationState> {

    private Logger logger = LoggerFactory.getLogger(AnimationRenderer.class);
    private final HashMap<T, Animation<T>> animations;

    private BiConsumer<AnimationRenderer<T>, Integer> onStart;
    private BiConsumer<AnimationRenderer<T>, Integer> onCycle;
    private BiConsumer<AnimationRenderer<T>, Integer> onFinish;
    private Animation<T> currentAnimation;
    private int currentIndex = 0;
    private double elapsed;
    private boolean running = false;

    public AnimationRenderer(List<Animation<T>> animations, T state) {
        HashMap<T, Animation<T>> map = new HashMap<>();
        for (Animation<T> animation : animations) {
            map.put(animation.getState(), animation);
        }
        this.animations = map;

        if (this.animations.values().stream().anyMatch(f -> this.animations.values().stream().filter(_f -> f.getState().equals(_f.getState())).count() > 1)) {
            throw new InvalidParameterException("Atleast 2 framesLists have been found with the same state.");
        }

        this.currentAnimation = this.animations.values().stream().filter(s -> s.getState().equals(state)).findFirst().get();

        if (this.currentAnimation == null) throw new NullPointerException(String.format("You did not passed an animation with the state: %s", state.name()));
    }

    public static <S extends AnimationState> Animation<S> createAnimation(S state, double duration, String... paths) {
        return AnimationRenderer.createAnimation(state, duration, false, paths);
    }

    public static <S extends AnimationState> Animation<S> createAnimation(S state, double duration, boolean loop, String... paths) {
        List<BufferedImage> frames = new java.util.ArrayList<>();
        try {
            for (String path : paths) {
                var image = ImageIO.read(AnimationRenderer.class.getResource(path));
                frames.add(image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Animation<S>(state, frames, duration, loop);
    }

    public void start() {
        this.currentIndex = 0;
        this.elapsed = 0;
        this.running = true;
    }

    public void pause() {
        this.running = false;
    }

    public void pauseAtEnd() {
        if (this.currentAnimation != null) {
            this.pause();
            this.currentIndex = this.currentAnimation.getFrames().size() - 1;
            this.elapsed = this.currentAnimation.getDuration();
        }
    }

    public void resume() {
        this.running = true;
    }

    public void gotoFrame(int index) {
        this.currentIndex = index;
        this.elapsed = 0;
    }

    public void switchState(T state) {
        if (this.currentAnimation == null || this.currentAnimation.getState() != state) {
            if (this.animations.get(state) != null) {
                this.currentAnimation = this.animations.get(state);
                this.currentIndex = 0;
                this.elapsed = 0;
            }
        }
    }

    public void update(double dt) {
        if (!this.running || this.currentAnimation == null) return;
        if (this.elapsed == 0 && this.currentIndex == 0 && this.onStart != null) this.onStart.accept(this, this.currentIndex);
        this.elapsed += dt;
        Animation animation = this.currentAnimation;
        if (this.elapsed >= animation.getDurationPerFrame()) {
            if (this.currentIndex == animation.getFrames().size() - 1) {
                if (this.onFinish != null) this.onFinish.accept(this, this.currentIndex);
                if (animation.isLoop()) this.currentIndex = 0;
            } else {
                if (this.onCycle != null) this.onCycle.accept(this, this.currentIndex);
                this.currentIndex++;
            }
            this.elapsed = 0;
        }
    }

    public void onStart(BiConsumer<AnimationRenderer<T>, Integer> onStart) {
        this.onStart = onStart;
    }

    public void onCycle(BiConsumer<AnimationRenderer<T>, Integer> onCycle) {
        this.onCycle = onCycle;
    }

    public void onFinish(BiConsumer<AnimationRenderer<T>, Integer> onFinish) {
        this.onFinish = onFinish;
    }

    public double getDuration() {
        if (this.currentAnimation == null) {
            return 0;
        }
        return this.currentAnimation.getDuration();
    }

    public boolean isLoop() {
        if (this.currentAnimation == null) {
            return false;
        }
        return this.currentAnimation.isLoop();
    }

    public boolean isRunning() {
        return running;
    }

    public int getCurrentIndex() {
        return this.currentIndex;
    }

    public Animation<T> getCurrentAnimation() {
        return this.currentAnimation;
    }

    public BufferedImage getCurrentFrame() {
        if (this.currentAnimation == null) {
            return null;
        }
        return this.currentAnimation.getFrames().get(this.currentIndex);
    }
}
