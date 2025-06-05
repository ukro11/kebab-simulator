package kebab_simulator.graphics.map.spawner;

import com.google.common.collect.Range;
import kebab_simulator.Config;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.IAnimationState;
import kebab_simulator.control.Wrapper;
import kebab_simulator.event.events.KeyPressedEvent;
import kebab_simulator.event.events.MouseClickedEvent;
import kebab_simulator.graphics.map.ObjectSpawner;
import kebab_simulator.model.visual.impl.gui.GuiScreen;
import kebab_simulator.physics.Collider;
import kebab_simulator.utils.game.CooldownManager;

import java.awt.event.KeyEvent;

public class FridgeSpawner extends ObjectSpawner<FridgeSpawner.FridgeAnimationState> {

    private CooldownManager cooldown;

    public FridgeSpawner(FridgeType type, String id, Collider collider) {
        super(
            id,
            collider,
            new AnimationRenderer<FridgeAnimationState>(
                type == FridgeType.MEAT_FRIDGE ? "/graphic/map/sprites/animated/animated_fridge_grey_32x32.png" : "/graphic/map/sprites/animated/animated_fridge_white_32x32.png",
                1,
                6,
                64,
                96,
                FridgeAnimationState.CLOSED
            )
        );
        this.cooldown = new CooldownManager(0.5);

        Wrapper.getEventManager().addEventListener("keypressed", (KeyPressedEvent event) -> {
            if (event.getKeyCode() == KeyEvent.VK_E && GuiScreen.getCurrentScreen() == null) {
                /*var player = this.programController.player;
                if (this.sensorCollider.collides(player.getBody())) {
                    if (this.cooldown.use()) {
                        this.getRenderer().switchState(FridgeAnimationState.OPEN);
                        player.switchState(CharacterAnimationState.IDLE_TOP);
                        var testScreen = new GuiTest();
                        testScreen.onClose((screen) -> this.getRenderer().switchState(FridgeAnimationState.CLOSED));
                        GuiScreen.open(testScreen);
                    }
                }*/
            }
        });

        Wrapper.getEventManager().addEventListener("mouseclicked", (MouseClickedEvent event) -> {
            // TODO: Mouse Interaction
        });
    }

    @Override
    public double zIndex() {
        return this.getCollider().getY() + this.getCollider().getHeight() / 2 + 20 + -this.getCollider().getX() / Config.WINDOW_WIDTH;
    }

    public enum FridgeType {
        MEAT_FRIDGE,
        VEGETABLES_FRIDGE
    }

    public enum FridgeAnimationState implements IAnimationState {
        OPEN(0, Range.closed(0, 5), 6, 0.2, false, true),
        CLOSED(0, Range.closed(0, 5), 6, 0.2, false);

        private final int rowIndex;
        private final Range<Integer> columnRange;
        private final int frames;
        private final double duration;
        private final boolean loop;
        private final boolean reverse;

        FridgeAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration) {
            this(rowIndex, columnRange, frames, duration, true);
        }

        FridgeAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop) {
            this(rowIndex, columnRange, frames, duration, loop, false);
        }

        FridgeAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop, boolean reverse) {
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

        @Override
        public String toString() {
            return "FridgeAnimationState{" +
                    "rowIndex=" + rowIndex +
                    ", columnRange=" + columnRange +
                    ", frames=" + frames +
                    ", duration=" + duration +
                    ", loop=" + loop +
                    ", reverse=" + reverse +
                    '}';
        }
    }
}
