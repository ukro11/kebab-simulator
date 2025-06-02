package kebab_simulator.graphics.map.spawner;

import com.google.common.collect.Range;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.IAnimationState;
import kebab_simulator.animation.states.CharacterAnimationState;
import kebab_simulator.control.Wrapper;
import kebab_simulator.event.events.KeyPressedEvent;
import kebab_simulator.graphics.map.ObjectSpawner;
import kebab_simulator.physics.Collider;
import kebab_simulator.utils.game.CooldownManager;

import java.awt.event.KeyEvent;

public class OvenSpawner extends ObjectSpawner<OvenSpawner.OvenAnimationState> {

    private final CooldownManager cooldown;

    public OvenSpawner(String id, Collider collider) {
        super(
            id,
            collider,
            new AnimationRenderer<OvenAnimationState>(
                "/graphic/map/sprites/animated/animated_kitchen_oven_32x32.png",
                2,
                4,
                32,
                67,
                OvenAnimationState.CLOSED
            )
        );
        this.cooldown = new CooldownManager(1.0);

        Wrapper.getEventManager().addEventListener("keypressed", (KeyPressedEvent event) -> {
            if (event.getKeyCode() == KeyEvent.VK_E) {
                var player = this.programController.player;
                if (this.sensorCollider.collides(player.getBody())) {
                    if (this.cooldown.use()) {
                        var state = this.getRenderer().getCurrentAnimation().getState();
                        if (state == OvenAnimationState.OPEN) {
                            this.getRenderer().switchState(OvenAnimationState.CLOSED);
                            player.switchState(CharacterAnimationState.IDLE_TOP);

                        } else {
                            this.getRenderer().switchState(OvenAnimationState.OPEN);
                            player.switchState(CharacterAnimationState.IDLE_TOP);
                        }
                    }
                }
            }
        });
    }

    public enum OvenAnimationState implements IAnimationState {
        OPEN(0, Range.closed(0, 3), 4, 0.2, false),
        CLOSED(1, Range.closed(0, 3), 4, 0.2, false);

        private final int rowIndex;
        private final Range<Integer> columnRange;
        private final int frames;
        private final double duration;
        private final boolean loop;
        private final boolean reverse;

        OvenAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration) {
            this(rowIndex, columnRange, frames, duration, true);
        }

        OvenAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop) {
            this(rowIndex, columnRange, frames, duration, loop, false);
        }

        OvenAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop, boolean reverse) {
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
