package kebab_simulator.animation.states.spawner;

import com.google.common.collect.Range;
import kebab_simulator.animation.IAnimationState;

public enum OvenAnimationState implements IAnimationState {
    OPEN(0, Range.closed(0, 3), 4, 0.2, false),
    CLOSED(1, Range.closed(0, 3), 4, 0.2, false);

    private final int rowIndex;
    private final Range<Integer> columnRange;
    private final int frames;
    private final double duration;
    private final boolean loop;
    private final boolean reverse;
    private final int frameWidth;
    private final int frameHeight;

    OvenAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration) {
        this(rowIndex, columnRange, frames, duration, true);
    }

    OvenAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop) {
        this(rowIndex, columnRange, frames, duration, loop, false);
    }

    OvenAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop, boolean reverse) {
        this(rowIndex, columnRange, frames, duration, loop, reverse, 0, 0);
    }

    OvenAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop, boolean reverse, int frameWidth, int frameHeight) {
        this.rowIndex = rowIndex;
        this.columnRange = columnRange;
        this.frames = frames;
        this.duration = duration;
        this.loop = loop;
        this.reverse = reverse;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
    }

    @Override
    public int getRowIndex() {
        return this.rowIndex;
    }

    @Override
    public Range<Integer> getColumnRange() {
        return this.columnRange;
    }

    @Override
    public int getFrames() {
        return this.frames;
    }

    @Override
    public double getDuration() {
        return this.duration;
    }

    @Override
    public boolean isLoop() {
        return this.loop;
    }

    @Override
    public boolean isReverse() {
        return this.reverse;
    }

    @Override
    public int getFrameWidth() {
        return this.frameWidth;
    }

    @Override
    public int getFrameHeight() {
        return this.frameHeight;
    }

    @Override
    public String toString() {
        return getClass().getTypeName() + " {" +
                "\n   rowIndex=" + this.rowIndex +
                "\n   , columnRange=" + this.columnRange +
                "\n   , frames=" + this.frames +
                "\n   , duration=" + this.duration +
                "\n   , loop=" + this.loop +
                "\n   , reverse=" + this.reverse +
                "\n   , frameWidth=" + this.frameWidth +
                "\n   , frameHeight=" + this.frameHeight +
                '}';
    }
}
