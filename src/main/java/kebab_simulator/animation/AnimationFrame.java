package kebab_simulator.animation;

import javax.swing.*;
import java.util.List;

public class AnimationFrame<T extends AnimationState> {

    private final T state;
    private final List<ImageIcon> frames;

    public AnimationFrame(T state, List<ImageIcon> frames) {
        this.state = state;
        this.frames = frames;
    }

    public T getState() {
        return state;
    }

    public List<ImageIcon> getFrames() {
        return frames;
    }
}
