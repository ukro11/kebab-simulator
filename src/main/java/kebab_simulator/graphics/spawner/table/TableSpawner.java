package kebab_simulator.graphics.spawner.table;

import kebab_simulator.Wrapper;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.states.FocusAnimationState;
import kebab_simulator.event.events.collider.ColliderCollisionEvent;
import kebab_simulator.graphics.spawner.ObjectIdResolver;
import kebab_simulator.graphics.spawner.ObjectSpawner;
import kebab_simulator.model.entity.Entity;
import kebab_simulator.model.entity.impl.player.EntityPlayer;
import kebab_simulator.physics.Collider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;

public abstract class TableSpawner extends ObjectSpawner {

    private static final Logger log = LoggerFactory.getLogger(TableSpawner.class);
    protected static TableSpawner currentCollisionPlayer = null;
    protected Entity.EntityDirection directionToLook;

    public TableSpawner(ObjectIdResolver id, Collider collider, AnimationRenderer<?> renderer) {
        super(id, collider, renderer);
        if (collider.getX() < 620) {
            this.directionToLook = Entity.EntityDirection.LEFT;

        } else {
            this.directionToLook = Entity.EntityDirection.RIGHT;
        }
    }

    public abstract boolean allowFocus();

    // Wenn der Spieler den Table anklickt
    public void onInteractTable(EntityPlayer player, KeyEvent keyEvent) {}
    public void onFocus() {}
    public void onFocusLost() {}

    @Override
    public void onRegisterSensor(Collider sensor) {
        sensor.onCollision(c -> {
            var player = Wrapper.getLocalPlayer();
            if (this.allowFocus() && c.isBodyInvolved(player.getBody())) {
                if (c.getState() == ColliderCollisionEvent.CollisionState.COLLISION_NORMAL_CONTACT && TableSpawner.currentCollisionPlayer == null) {
                    if (this.directionToLook != null && player.getDirection() != this.directionToLook) {
                        TableSpawner.currentCollisionPlayer = null;
                        this.onFocusLost();

                    } else {
                        TableSpawner.currentCollisionPlayer = this;
                        this.onFocus();
                    }

                } else if (c.getState() == ColliderCollisionEvent.CollisionState.COLLISION_END_CONTACT) {
                    TableSpawner.currentCollisionPlayer = null;
                }
            }
        });
    }

    @Override
    public void onRegisterPlayer(EntityPlayer player) {
        player.onDirectionChange((p) -> {
            if (this.directionToLook != null && p.getDirection() != this.directionToLook) {
                TableSpawner.currentCollisionPlayer = null;
            }
        });
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (this.renderer != null) {
            if (TableSpawner.isTableFocused(this)) {
                this.onInteractTable(Wrapper.getLocalPlayer(), event);
            }
        }
    }

    public static boolean isTableFocused(TableSpawner table) {
        return TableSpawner.currentCollisionPlayer == table;
    }

    public static boolean isCurrentlyFocused() {
        return TableSpawner.currentCollisionPlayer != null;
    }

    public static TableSpawner getCurrentFocusedTable() {
        return TableSpawner.currentCollisionPlayer;
    }
}
