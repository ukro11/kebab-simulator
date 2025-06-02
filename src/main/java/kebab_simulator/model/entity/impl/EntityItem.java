package kebab_simulator.model.entity.impl;

import kebab_simulator.model.entity.Entity;
import kebab_simulator.physics.Collider;

public abstract class EntityItem extends Entity {

    protected EntityPlayer player;

    public EntityItem(EntityPlayer player, Collider collider, double x, double y, double width, double height) {
        super(collider, x, y, width, height);
        if (player == null) throw new NullPointerException("Player is null, not allowed.");
        this.player = player;
        this.player.getBody().addChild(collider);
        this.body.setColliderClass("entity_item");
        this.highestPoint = this.player.getHighestPoint();
    }
}
