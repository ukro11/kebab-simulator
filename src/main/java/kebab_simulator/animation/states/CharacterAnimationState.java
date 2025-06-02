package kebab_simulator.animation.states;

import com.google.common.collect.Range;
import kebab_simulator.animation.IAnimationState;

public enum CharacterAnimationState implements IAnimationState {
    IDLE_TOP(1, Range.closed(6, 11), 6, 0.5),
    IDLE_BOTTOM(1, Range.closed(18, 23), 6, 0.5),
    IDLE_LEFT(1, Range.closed(12, 17), 6, 0.5),
    IDLE_RIGHT(1, Range.closed(0, 5), 6, 0.5),

    WALK_TOP(2, Range.closed(6, 11), 6, 0.5),
    WALK_BOTTOM(2, Range.closed(18, 23), 6, 0.5),
    WALK_LEFT(2, Range.closed(12, 17), 6, 0.5),
    WALK_RIGHT(2, Range.closed(0, 5), 6, 0.5);

    private final int rowIndex;
    private final Range<Integer> columnRange;
    private final int frames;
    private final double duration;
    private final boolean loop;
    private final boolean reverse;

    CharacterAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration) {
        this(rowIndex, columnRange, frames, duration, true);
    }

    CharacterAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop) {
        this(rowIndex, columnRange, frames, duration, loop, false);
    }

    CharacterAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop, boolean reverse) {
        this.rowIndex = rowIndex;
        this.columnRange = columnRange;
        this.frames = frames;
        this.duration = duration;
        this.loop = loop;
        this.reverse = reverse;
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
}
