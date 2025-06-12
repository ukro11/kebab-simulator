package kebab_simulator.graphics.spawner.table;

import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.states.spawner.OvenAnimationState;
import kebab_simulator.Wrapper;
import kebab_simulator.event.events.KeyPressedEvent;
import kebab_simulator.graphics.spawner.ObjectIdResolver;
import kebab_simulator.graphics.spawner.ObjectSpawner;
import kebab_simulator.model.entity.impl.player.EntityPlayer;
import kebab_simulator.physics.Collider;
import kebab_simulator.utils.game.CooldownManager;

import java.awt.event.KeyEvent;

public class OvenSpawner extends ObjectSpawner<OvenAnimationState> {

    private final CooldownManager cooldown;

    public OvenSpawner(ObjectIdResolver id, Collider collider) {
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
                /*var player = this.programController.player;
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
                }*/
            }
        });
    }

    @Override
    public void onRegisterSensor(Collider sensor) {}

    @Override
    public void onRegisterPlayer(EntityPlayer player) {}
}
