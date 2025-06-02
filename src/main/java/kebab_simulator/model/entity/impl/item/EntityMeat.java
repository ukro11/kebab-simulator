package kebab_simulator.model.entity.impl.item;

import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.states.CharacterAnimationState;
import kebab_simulator.control.Wrapper;
import kebab_simulator.event.services.EventProcessCallback;
import kebab_simulator.event.services.process.EventLoadAssetsProcess;
import kebab_simulator.model.entity.impl.EntityItem;
import kebab_simulator.model.entity.impl.EntityPlayer;
import kebab_simulator.physics.BodyType;
import kebab_simulator.physics.colliders.ColliderRectangle;

public class EntityMeat extends EntityItem {

    public EntityMeat(EntityPlayer player, double x, double y) {
        super(player, new ColliderRectangle(BodyType.DYNAMIC, x, y, 32, 32), x, y, 32, 32);
        Wrapper.getProcessManager().queue(new EventLoadAssetsProcess<AnimationRenderer>("Loading EntityMeat animations", () -> new AnimationRenderer(
                "/graphic/item/ali.png", 3, 24, 16, 32,
                CharacterAnimationState.IDLE_BOTTOM
        ), new EventProcessCallback<AnimationRenderer>() {
            @Override
            public void onSuccess(AnimationRenderer data) {
                setRenderer(data);
            }
        }));
    }


}
