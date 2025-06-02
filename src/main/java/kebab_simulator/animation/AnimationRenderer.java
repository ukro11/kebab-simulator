package kebab_simulator.animation;

import kebab_simulator.animation.states.CharacterAnimationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public class AnimationRenderer<T extends Enum<T> & IAnimationState> {

    private Logger logger = LoggerFactory.getLogger(AnimationRenderer.class);
    private final HashMap<T, Animation<T>> animations;

    private BiConsumer<AnimationRenderer<T>, Integer> onStart;
    private BiConsumer<AnimationRenderer<T>, Integer> onCycle;
    private BiConsumer<AnimationRenderer<T>, Integer> onFinish;
    private Animation<T> currentAnimation;
    private int currentIndex = 0;
    private double elapsed;
    private boolean running = false;

    public AnimationRenderer(String spriteSheetPath, int rows, int maxColumns, int frameWidth, int frameHeight, T state) {
        try {
            BufferedImage spriteSheet = ImageIO.read(AnimationRenderer.class.getResource(spriteSheetPath));
            HashMap<T, Animation<T>> animations = new HashMap<>();
            Class<T> enumClass = state.getDeclaringClass();
            for (int i = 0; i < rows; i++) {
                List<BufferedImage> frames = new ArrayList<>();
                for (int j = 0; j < maxColumns; j++) {
                    List<T> animationStates = IAnimationState.fetch(enumClass, i, j);
                    if (animationStates == null) break;
                    BufferedImage animationImage = spriteSheet.getSubimage(j * frameWidth, i * frameHeight, frameWidth, frameHeight);
                    frames.add(animationImage);
                    int k = j;
                    AtomicBoolean clear = new AtomicBoolean(false);
                    animationStates.forEach(animationState -> {
                        if (animationState.getColumnRange().upperEndpoint() == k) {
                            var f = frames.subList(0, frames.size());
                            if (animationState.isReverse()) Collections.reverse(f);
                            animations.put(animationState, new Animation<T>(animationState, f, animationState.getDuration(), animationState.isLoop(), animationState.isReverse()));
                            clear.set(true);
                        }
                    });
                    if (clear.get()) frames.clear();
                }
            }
            this.animations = animations;

            if (this.animations.values().stream().anyMatch(f -> this.animations.values().stream().filter(_f -> f.getState().equals(_f.getState())).count() > 1)) {
                throw new InvalidParameterException("Atleast 2 framesLists have been found with the same state.");
            }

            this.currentAnimation = this.animations.values().stream().filter(s -> s.getState().equals(state)).findFirst().orElse(null);

            if (this.currentAnimation == null) throw new NullPointerException(String.format("You did not passed an animation with the state: %s", state.name()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

    public static <S extends CharacterAnimationState> Animation<S> createAnimation(S state, double duration, String... paths) {
        return AnimationRenderer.createAnimation(state, duration, false, false, paths);
    }

    public static <S extends CharacterAnimationState> Animation<S> createAnimation(S state, double duration, boolean loop, boolean reverse, String... paths) {
        List<BufferedImage> frames = new java.util.ArrayList<>();
        try {
            for (String path : paths) {
                var image = ImageIO.read(AnimationRenderer.class.getResource(path));
                frames.add(image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Animation<S>(state, frames, duration, loop, reverse);
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
                this.currentIndex = this.currentAnimation.isReverse() ? this.currentAnimation.getFrames().size() - 1 : 0;
                this.elapsed = 0;
            }
        }
    }

    public void update(double dt) {
        if (!this.running || this.currentAnimation == null) return;
        if (this.elapsed == 0 && this.currentIndex == 0 && this.onStart != null) this.onStart.accept(this, this.currentIndex);
        this.elapsed += dt;
        Animation animation = this.currentAnimation;
        int size = animation.getFrames().size();
        if (this.elapsed >= animation.getDurationPerFrame()) {
            boolean lastIndex = animation.isReverse() ? this.currentIndex == 0 : this.currentIndex == size - 1;
            if (lastIndex) {
                int max = animation.isReverse() ? size - 1 : 0;
                if (this.onFinish != null) this.onFinish.accept(this, this.currentIndex);
                if (animation.isLoop()) {
                    this.currentIndex = max;
                }
            } else {
                if (this.onCycle != null) this.onCycle.accept(this, this.currentIndex);
                if (animation.isReverse()) {
                    this.currentIndex--;
                } else {
                    this.currentIndex++;
                }
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
        return this.running;
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
