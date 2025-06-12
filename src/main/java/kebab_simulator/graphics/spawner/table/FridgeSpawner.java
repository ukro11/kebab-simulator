package kebab_simulator.graphics.spawner.table;

import kebab_simulator.Config;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.states.spawner.FridgeAnimationState;
import kebab_simulator.Wrapper;
import kebab_simulator.event.events.KeyPressedEvent;
import kebab_simulator.event.events.MouseClickedEvent;
import kebab_simulator.graphics.spawner.ObjectIdResolver;
import kebab_simulator.graphics.spawner.ObjectSpawner;
import kebab_simulator.model.entity.impl.player.EntityPlayer;
import kebab_simulator.model.visual.impl.gui.GuiScreen;
import kebab_simulator.physics.Collider;
import kebab_simulator.utils.game.CooldownManager;

import java.awt.event.KeyEvent;

public class FridgeSpawner extends ObjectSpawner<FridgeAnimationState> {

    private CooldownManager cooldown;

    public FridgeSpawner(FridgeType type, ObjectIdResolver id, Collider collider) {
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

        /*Wrapper.getEventManager().addEventListener("keypressed", (KeyPressedEvent event) -> {
            if (event.getKeyCode() == KeyEvent.VK_E && GuiScreen.getCurrentScreen() == null) {
                var player = this.programController.player;
                if (this.sensorCollider.collides(player.getBody())) {
                    if (this.cooldown.use()) {
                        this.getRenderer().switchState(FridgeAnimationState.OPEN);
                        player.switchState(CharacterAnimationState.IDLE_TOP);
                        var testScreen = new GuiTest();
                        testScreen.onClose((screen) -> this.getRenderer().switchState(FridgeAnimationState.CLOSED));
                        GuiScreen.open(testScreen);
                    }
                }
            }
        });

        Wrapper.getEventManager().addEventListener("mouseclicked", (MouseClickedEvent event) -> {
            // TODO: Mouse Interaction
        });*/
    }

    @Override
    public void onRegisterSensor(Collider sensor) {}

    @Override
    public void onRegisterPlayer(EntityPlayer player) {}

    @Override
    public double zIndex() {
        return this.getCollider().getY() + this.getCollider().getHeight() / 2 + 20 + -this.getCollider().getX() / Config.WINDOW_WIDTH;
    }

    public enum FridgeType {
        MEAT_FRIDGE,
        VEGETABLES_FRIDGE
    }
}
