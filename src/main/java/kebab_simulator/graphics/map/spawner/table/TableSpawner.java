package kebab_simulator.graphics.map.spawner.table;

import com.google.common.collect.Range;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.IAnimationState;
import kebab_simulator.event.events.collider.ColliderCollisionEvent;
import kebab_simulator.graphics.map.ObjectSpawner;
import kebab_simulator.physics.Collider;

public abstract class TableSpawner extends ObjectSpawner {

    protected static TableSpawner currentCollisionPlayer = null;

    public TableSpawner(String id, Collider collider, String sheet) {
        super(
            id,
            collider,
            new AnimationRenderer<TableAnimationState>(
                sheet,
                id.contains("-end") ? 1 : 2,
                1,
                32,
                32,
                TableAnimationState.DEFAULT
            )
        );
        this.onRegisterSensor = () -> {
            this.sensorColliders.forEach((sensor) -> {
                ((Collider) sensor).onCollision(c -> {
                    var player = this.programController.player;
                    if (c.isBodyInvolved(player.getBody())) {
                        if (c.getState() == ColliderCollisionEvent.CollisionState.COLLISION_NORMAL_CONTACT && TableSpawner.currentCollisionPlayer == null) {
                            TableSpawner.currentCollisionPlayer = this;

                        } else if (c.getState() == ColliderCollisionEvent.CollisionState.COLLISION_END_CONTACT) {
                            TableSpawner.currentCollisionPlayer = null;
                        }
                    }
                });
            });
        };
        System.out.println("TEST");
    }

    public TableSpawner(String id, Collider collider, AnimationRenderer<?> renderer) {
        super(
            id,
            collider,
            renderer
        );
        this.onRegisterSensor = () -> {
            this.sensorColliders.forEach((sensor) -> {
                ((Collider) sensor).onCollision(c -> {
                    var player = this.programController.player;
                    if (c.isBodyInvolved(player.getBody())) {
                        if (c.getState() == ColliderCollisionEvent.CollisionState.COLLISION_NORMAL_CONTACT && TableSpawner.currentCollisionPlayer == null) {
                            TableSpawner.currentCollisionPlayer = this;

                        } else if (c.getState() == ColliderCollisionEvent.CollisionState.COLLISION_END_CONTACT) {
                            TableSpawner.currentCollisionPlayer = null;
                        }
                    }
                });
            });
        };
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (this.renderer != null) {
            if (TableSpawner.currentCollisionPlayer == this) {
                this.renderer.switchState(TableAnimationState.FOCUS);

            } else {
                this.renderer.switchState(TableAnimationState.DEFAULT);
            }
        }
    }

    public enum TableAnimationState implements IAnimationState {
        DEFAULT(0, Range.closed(0, 0), 1, 0.1, true),
        FOCUS(1, Range.closed(0, 0), 1, 0.1, true);

        private final int rowIndex;
        private final Range<Integer> columnRange;
        private final int frames;
        private final double duration;
        private final boolean loop;
        private final boolean reverse;

        TableAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration) {
            this(rowIndex, columnRange, frames, duration, true);
        }

        TableAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop) {
            this(rowIndex, columnRange, frames, duration, loop, false);
        }

        TableAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop, boolean reverse) {
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
