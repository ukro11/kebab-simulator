package kebab_simulator.model.entity.impl;

import kebab_simulator.animation.IAnimationState;
import kebab_simulator.graphics.spawner.table.TableNormalSpawner;
import kebab_simulator.model.entity.Entity;
import kebab_simulator.model.entity.impl.player.EntityPlayer;
import kebab_simulator.physics.Collider;
import kebab_simulator.utils.misc.Vec2;

public abstract class EntityItem<T extends Enum<T> & IAnimationState> extends Entity<T> {

    protected EntityPlayer player;
    protected EntityItemLocation<EntityItem> location;
    protected double rotation;

    public EntityItem(Collider collider, double x, double y, double width, double height) {
        super(collider, x, y, width, height);
        this.body.setColliderClass("entity_item");
        this.offset = new Vec2();
    }

    public void onPick(EntityPlayer player) {
        if (this.location != null) this.location.removeItem(this);
        this.location = null;
        this.player = player;
        this.player.getBody().addChild(this.body);
        this.body.setPosition(this.player.getBody().getX() + this.body.getChildInstance().getOffsetX(), this.player.getBody().getY() + this.body.getChildInstance().getOffsetY());
    }

    public void onDrop(EntityItemLocation location) {
        if (location == null) {
            this.logger.warn("If you place a plate on table that's null, nothing will happen (entityId: {})", this.id);
            return;
        }
        if (this.player != null) {
            this.player.getBody().removeChild(this.body.getChildInstance());
            this.player = null;
        }
        this.location = location;
        this.body.setPosition(location.getCollider().getX(), location.getCollider().getY());
        location.addItem(this);
    }

    public EntityItemLocation getLocation() {
        return this.location;
    }

    public void setLocation(EntityItemLocation location) {
        this.location = location;
        this.positionItem(location);
    }

    public void positionItem(EntityItemLocation location) {
        if (location instanceof TableNormalSpawner) {
            var normal = (TableNormalSpawner) location;
            switch (normal.getType()) {
                case NORMAL -> {
                    if (normal.isLeft()) {
                        this.rotation = 270;

                    } else {
                        this.rotation = 90;
                    }
                }
                case NORMAL_TOP, NORMAL_LEFT_TOP, NORMAL_RIGHT_TOP -> {
                    this.body.setY(this.body.getY() - 1);
                    this.rotation = 0;
                }
                default -> this.rotation = 0;
            }

        } else if (location == null) {
            this.rotation = 0;
        }
    }
}
