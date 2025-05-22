package kebab_simulator.animation;

import java.awt.image.BufferedImage;
import java.util.List;

public class Animation<T extends AnimationState> {

    private final T state;
    private final List<BufferedImage> frames;
    private final double duration;
    private final boolean loop;
    private final double durationPerFrame;

    public Animation(T state, List<BufferedImage> frames, double duration, boolean loop) {
        this.state = state;
        this.frames = frames;
        this.duration = duration;
        this.loop = loop;
        this.durationPerFrame = duration / frames.size();
    }

    public T getState() {
        return this.state;
    }

    public List<BufferedImage> getFrames() {
        return this.frames;
    }

    public double getDuration() {
        return this.duration;
    }

    public boolean isLoop() {
        return this.loop;
    }

    public double getDurationPerFrame() {
        return this.durationPerFrame;
    }
}
