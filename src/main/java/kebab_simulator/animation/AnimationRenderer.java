package kebab_simulator.animation;

import javax.swing.*;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class AnimationRenderer<T extends AnimationState> {

    private final HashMap<T, AnimationFrame<T>> animations;
    private final double duration;
    private final boolean loop;

    private BiConsumer<AnimationRenderer<T>, Integer> onStart;
    private BiConsumer<AnimationRenderer<T>, Integer> onCycle;
    private BiConsumer<AnimationRenderer<T>, Integer> onFinish;
    private AnimationFrame<T> currentAnimation;
    private int currentIndex = 0;
    private double durationPerFrame;
    private double elapsed;
    private boolean running = false;

    public AnimationRenderer(HashMap<T, AnimationFrame<T>> frames, T state, double duration) {
        this(frames, state, duration, false);
    }

    public AnimationRenderer(HashMap<T, AnimationFrame<T>> animations, T state, double duration, boolean loop) {
        this.animations = animations;
        this.duration = duration;
        this.loop = loop;

        if (this.animations.values().stream().anyMatch(f -> this.animations.values().stream().filter(_f -> f.getState().equals(_f.getState())).count() > 1)) {
            throw new InvalidParameterException("Atleast 2 framesLists have been found with the same state.");
        }

        this.durationPerFrame = this.duration / animations.size();
        this.currentAnimation = this.animations.get(state);

        if (currentAnimation == null) throw new NullPointerException(String.format("You did not passed an animation with the state: %s", state.name()));
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
        this.pause();
        this.currentIndex = this.currentAnimation.getFrames().size() - 1;
        this.elapsed = this.durationPerFrame;
    }

    public void resume() {
        this.running = true;
    }

    public void gotoFrame(int index) {
        this.currentIndex = index;
        this.elapsed = 0;
    }

    public void switchState(T state) {
        this.currentAnimation = this.animations.get(state);
        this.currentIndex = 0;
        this.elapsed = 0;
    }

    public void update(double dt) {
        if (!this.running) return;
        if (this.elapsed == 0 && this.currentIndex == 0 && this.onStart != null) this.onStart.accept(this, this.currentIndex);
        this.elapsed += dt;
        if (this.elapsed >= this.durationPerFrame) {
            if (this.currentIndex == this.currentAnimation.getFrames().size() - 1) {
                if (this.onFinish != null) this.onFinish.accept(this, this.currentIndex);
                if (this.loop) this.currentIndex = 0;
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
        return this.duration;
    }

    public boolean isLoop() {
        return this.loop;
    }

    public boolean isRunning() {
        return running;
    }

    public int getCurrentIndex() {
        return this.currentIndex;
    }

    public AnimationFrame<T> getCurrentAnimation() {
        return this.currentAnimation;
    }

    public ImageIcon getCurrentFrame() {
        return this.currentAnimation.getFrames().get(this.currentIndex);
    }
}
