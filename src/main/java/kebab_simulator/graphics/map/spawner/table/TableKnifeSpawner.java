package kebab_simulator.graphics.map.spawner.table;

import KAGO_framework.view.DrawTool;
import com.google.common.collect.Range;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.IAnimationState;
import kebab_simulator.physics.Collider;

public class TableKnifeSpawner extends TableSpawner {

    public TableKnifeSpawner(String id, Collider collider) {
        super(id, collider, new AnimationRenderer<TableKnifeAnimationState>(
                "/graphic/map/sprites/table/table_knife_spritesheet.png",
                4,
                6,
                64,
                64,
                TableKnifeAnimationState.DEFAULT
        ));
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (TableSpawner.currentCollisionPlayer == this) {
            this.renderer.switchState(TableKnifeAnimationState.FOCUS);

        } else {
            this.renderer.switchState(TableKnifeAnimationState.DEFAULT);
        }
    }

    @Override
    public void draw(DrawTool drawTool) {
        drawTool.drawImage(this.renderer.getCurrentFrame(), this.collider.getX() - 16, this.collider.getY() - 16);
    }

    public enum TableKnifeAnimationState implements IAnimationState {
        DEFAULT(0, Range.closed(0, 0), 1, 0.7, true),
        FOCUS(1, Range.closed(0, 0), 1, 0.7, true);
       // CUT(2, Range.closed(0, 5), 6, 0.7, true);

        private final int rowIndex;
        private final Range<Integer> columnRange;
        private final int frames;
        private final double duration;
        private final boolean loop;
        private final boolean reverse;

        TableKnifeAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration) {
            this(rowIndex, columnRange, frames, duration, true);
        }

        TableKnifeAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop) {
            this(rowIndex, columnRange, frames, duration, loop, false);
        }

        TableKnifeAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop, boolean reverse) {
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
