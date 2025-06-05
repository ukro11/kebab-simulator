package kebab_simulator.graphics.map.spawner.table;

import KAGO_framework.view.DrawTool;
import com.google.common.collect.Range;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.IAnimationState;
import kebab_simulator.physics.Collider;

public class TableServingSpawner extends TableSpawner {

    public TableServingSpawner(String id, Collider collider) {
        super(id, collider, new AnimationRenderer<TableServingAnimationState>(
            "/graphic/map/sprites/animated/animated_escalator_up_32x32_sheet.png",
            2,
            6,
            128,
            96,
            TableServingAnimationState.DEFAULT
        ));
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (TableSpawner.currentCollisionPlayer == this) {
            this.renderer.switchState(TableServingAnimationState.FOCUS);

        } else {
            this.renderer.switchState(TableServingAnimationState.DEFAULT);
        }
    }

    @Override
    public void draw(DrawTool drawTool) {
        drawTool.drawImage(this.renderer.getCurrentFrame(), this.collider.getX() - 16, this.collider.getY());
    }

    @Override
    public double zIndex() {
        return this.getCollider().getY() + 32;
    }

    public enum TableServingAnimationState implements IAnimationState {
        DEFAULT(0, Range.closed(0, 5), 6, 0.7, true),
        FOCUS(1, Range.closed(0, 5), 6, 0.7, true);

        private final int rowIndex;
        private final Range<Integer> columnRange;
        private final int frames;
        private final double duration;
        private final boolean loop;
        private final boolean reverse;

        TableServingAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration) {
            this(rowIndex, columnRange, frames, duration, true);
        }

        TableServingAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop) {
            this(rowIndex, columnRange, frames, duration, loop, false);
        }

        TableServingAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop, boolean reverse) {
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
}
