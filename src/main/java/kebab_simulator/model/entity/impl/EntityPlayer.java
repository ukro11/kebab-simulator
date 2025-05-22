package kebab_simulator.model.entity.impl;

import KAGO_framework.control.ViewController;
import KAGO_framework.view.DrawTool;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.AnimationState;
import kebab_simulator.model.entity.Entity;
import kebab_simulator.physics.BodyType;
import kebab_simulator.physics.Collider;
import kebab_simulator.physics.colliders.ColliderPolygon;
import kebab_simulator.utils.Vec2;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Objects;

public class EntityPlayer extends Entity {

    private EntityDirection lastDirection;
    private EntityDirection direction = EntityDirection.BOTTOM;

    private final double speed = 120.0;
    private boolean freeze = false;

    public static EntityPlayer createPlayer(String id, double x, double y) {
        ColliderPolygon collider = ColliderPolygon.createCIPolygon(id, BodyType.DYNAMIC, x + 60, y + 64, 16, 12, 2.5);
        collider.setColliderClass("entity_player");
        EntityPlayer player = new EntityPlayer(collider, -60, -64, 120, 120);
        collider.setEntity(player);
        return player;
    }

    public static EntityPlayer createDummy(String id, double x, double y) {
        ColliderPolygon collider = ColliderPolygon.createCIPolygon(id, BodyType.DYNAMIC, x + 60, y + 64, 16, 12, 2.5);
        collider.setColliderClass("entity_player");
        EntityPlayer dummy = new EntityPlayer(collider, -60, -64, 120, 120);
        collider.setEntity(dummy);
        return dummy;
    }

    private EntityPlayer(Collider collider, double x, double y, double width, double height) {
        super(collider, x, y, width, height);
        String idlePath = "/graphic/test/idle/";
        var idleTop = AnimationRenderer.<AnimationState>createAnimation(
                AnimationState.IDLE_TOP, 0.5, true,
                idlePath + "test-character-8.png", idlePath + "test-character-9.png",
                idlePath + "test-character-10.png", idlePath + "test-character-11.png"
        );
        var idleBottom = AnimationRenderer.<AnimationState>createAnimation(
                AnimationState.IDLE_BOTTOM, 0.5, true,
                idlePath + "test-character-4.png", idlePath + "test-character-5.png",
                idlePath + "test-character-6.png", idlePath + "test-character-7.png"
        );
        var idleLeft = AnimationRenderer.<AnimationState>createAnimation(
                AnimationState.IDLE_LEFT, 0.5, true,
                idlePath + "test-character-0.png", idlePath + "test-character-1.png",
                idlePath + "test-character-2.png", idlePath + "test-character-3.png"
        );
        var idleRight = AnimationRenderer.<AnimationState>createAnimation(
                AnimationState.IDLE_RIGHT, 0.5, true,
                idlePath + "test-character-0.png", idlePath + "test-character-1.png",
                idlePath + "test-character-2.png", idlePath + "test-character-3.png"
        );

        String walkPath = "/graphic/test/walk/";
        var walkTop = AnimationRenderer.<AnimationState>createAnimation(
                AnimationState.WALK_TOP, 0.5, true,
                walkPath + "test-character-walk-16.png", walkPath + "test-character-walk-17.png",
                walkPath + "test-character-walk-18.png", walkPath + "test-character-walk-19.png",
                walkPath + "test-character-walk-20.png", walkPath + "test-character-walk-21.png",
                walkPath + "test-character-walk-22.png", walkPath + "test-character-walk-23.png"
        );
        var walkBottom = AnimationRenderer.<AnimationState>createAnimation(
                AnimationState.WALK_BOTTOM, 0.5, true,
                walkPath + "test-character-walk-8.png", walkPath + "test-character-walk-9.png",
                walkPath + "test-character-walk-10.png", walkPath + "test-character-walk-11.png",
                walkPath + "test-character-walk-12.png", walkPath + "test-character-walk-13.png",
                walkPath + "test-character-walk-14.png", walkPath + "test-character-walk-15.png"
        );
        var walkLeft = AnimationRenderer.<AnimationState>createAnimation(
                AnimationState.WALK_LEFT, 0.5, true,
                walkPath + "test-character-walk-0.png", walkPath + "test-character-walk-1.png",
                walkPath + "test-character-walk-2.png", walkPath + "test-character-walk-3.png",
                walkPath + "test-character-walk-4.png", walkPath + "test-character-walk-5.png",
                walkPath + "test-character-walk-6.png", walkPath + "test-character-walk-7.png"
        );
        var walkRight = AnimationRenderer.<AnimationState>createAnimation(
                AnimationState.WALK_RIGHT, 0.5, true,
                walkPath + "test-character-walk-0.png", walkPath + "test-character-walk-1.png",
                walkPath + "test-character-walk-2.png", walkPath + "test-character-walk-3.png",
                walkPath + "test-character-walk-4.png", walkPath + "test-character-walk-5.png",
                walkPath + "test-character-walk-6.png", walkPath + "test-character-walk-7.png"
        );
        this.highestPointOffset = new Vec2(this.width / 2, this.height / 2 - 10);
        this.setRenderer(new AnimationRenderer(
                List.of(
                    idleTop, idleBottom, idleLeft, idleRight,
                    walkTop, walkBottom, walkLeft, walkRight
                ),
                AnimationState.IDLE_BOTTOM
        ));
    }

    public void freeze(boolean flag) {
        this.freeze = flag;
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (!this.body.isDestroyed()) {
            this.onMove();
            if (this.body.getVelocity().magnitude() == 0) {
                switch (this.direction) {
                    case TOP -> this.renderer.switchState(AnimationState.IDLE_TOP);
                    case LEFT -> this.renderer.switchState(AnimationState.IDLE_LEFT);
                    case BOTTOM -> this.renderer.switchState(AnimationState.IDLE_BOTTOM);
                    case RIGHT -> this.renderer.switchState(AnimationState.IDLE_RIGHT);
                }
            }
        }
    }

    @Override
    public void draw(DrawTool drawTool) {
        super.draw(drawTool);
    }

    private void onMove() {
        if (this.freeze) return;

        boolean verticalKeyDown = false;
        Vec2 moveVelocity = new Vec2();
        if (ViewController.isKeyDown(KeyEvent.VK_W) && !ViewController.isKeyDown(KeyEvent.VK_S)) {
            moveVelocity.set(null, -this.speed);
            this.direction = EntityDirection.TOP;
            this.renderer.switchState(AnimationState.WALK_TOP);
            verticalKeyDown = true;

        } else if (ViewController.isKeyDown(KeyEvent.VK_S) && !ViewController.isKeyDown(KeyEvent.VK_W)) {
            moveVelocity.set(null, this.speed);
            this.direction = EntityDirection.BOTTOM;
            this.renderer.switchState(AnimationState.WALK_BOTTOM);
            verticalKeyDown = true;
        }

        if (ViewController.isKeyDown(KeyEvent.VK_A) && !ViewController.isKeyDown(KeyEvent.VK_D)) {
            moveVelocity.set(-this.speed, null);
            this.direction = EntityDirection.LEFT;
            if (this.renderer.getCurrentAnimation().getState() != AnimationState.WALK_TOP && this.renderer.getCurrentAnimation().getState() != AnimationState.WALK_BOTTOM) {
                this.renderer.switchState(AnimationState.WALK_LEFT);

            } else if (!verticalKeyDown) {
                this.renderer.switchState(AnimationState.WALK_LEFT);
            }

        } else if (ViewController.isKeyDown(KeyEvent.VK_D) && !ViewController.isKeyDown(KeyEvent.VK_A)) {
            moveVelocity.set(this.speed, null);
            this.direction = EntityDirection.RIGHT;
            if (this.renderer.getCurrentAnimation().getState() != AnimationState.WALK_TOP && this.renderer.getCurrentAnimation().getState() != AnimationState.WALK_BOTTOM) {
                this.renderer.switchState(AnimationState.WALK_RIGHT);

            } else if (!verticalKeyDown) {
                this.renderer.switchState(AnimationState.WALK_RIGHT);
            }
        }
        if (moveVelocity.magnitude() > 0) {
            moveVelocity.normalize().mul(this.speed, this.speed);
        }
        this.body.setLinearVelocity(moveVelocity.x, moveVelocity.y);
    }

    private void onDirectionChange() {
        if (this.direction == EntityDirection.LEFT && this.scaleX != -1) {
            this.scaleX = -1;

        } else if (this.direction == EntityDirection.RIGHT && this.scaleX != 1) {
            this.scaleX = 1;
        }
    }

    @Override
    public void keyPressed(int key) {
        this.lastDirection = this.direction;
        switch (key) {
            case KeyEvent.VK_W: {
                this.direction = EntityDirection.TOP;
                this.onDirectionChange();
                break;
            }
            case KeyEvent.VK_A: {
                this.direction = EntityDirection.LEFT;
                this.onDirectionChange();
                break;
            }
            case KeyEvent.VK_S: {
                this.direction = EntityDirection.BOTTOM;
                this.onDirectionChange();
                break;
            }
            case KeyEvent.VK_D: {
                this.direction = EntityDirection.RIGHT;
                this.onDirectionChange();
                break;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!(o instanceof Entity) && !(o instanceof EntityPlayer)) return false;
        EntityPlayer entity = (EntityPlayer) o;
        return Objects.equals(this.id, entity.getId());
    }
}
