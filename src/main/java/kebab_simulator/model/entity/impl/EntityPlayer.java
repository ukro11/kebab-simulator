package kebab_simulator.model.entity.impl;

import KAGO_framework.control.ViewController;
import KAGO_framework.view.DrawTool;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.states.CharacterAnimationState;
import kebab_simulator.control.Wrapper;
import kebab_simulator.event.services.EventProcessCallback;
import kebab_simulator.event.services.process.EventLoadAssetsProcess;
import kebab_simulator.model.entity.Entity;
import kebab_simulator.physics.Collider;
import kebab_simulator.utils.misc.Vec2;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Objects;

public class EntityPlayer extends Entity {

    private static List<CharacterAnimationState> IDLE_STATES = List.of(CharacterAnimationState.IDLE_TOP, CharacterAnimationState.IDLE_LEFT, CharacterAnimationState.IDLE_BOTTOM, CharacterAnimationState.IDLE_RIGHT);
    private static List<CharacterAnimationState> WALKING_STATES = List.of(CharacterAnimationState.WALK_TOP, CharacterAnimationState.WALK_LEFT, CharacterAnimationState.WALK_BOTTOM, CharacterAnimationState.WALK_BOTTOM);

    private EntityDirection lastDirection;
    private EntityDirection direction = EntityDirection.BOTTOM;

    private final double speed = 140.0;
    private boolean freeze = false;

    public EntityPlayer(Collider collider, double x, double y, double width, double height) {
        super(collider, x, y, width, height);

        Wrapper.getProcessManager().queue(new EventLoadAssetsProcess<AnimationRenderer>("Loading animations", () -> new AnimationRenderer(
                "/graphic/character/ali.png", 3, 24, 16, 32,
                CharacterAnimationState.IDLE_BOTTOM
        ), new EventProcessCallback<AnimationRenderer>() {
            @Override
            public void onSuccess(AnimationRenderer data) {
                setRenderer(data);
            }
        }));
        this.highestPointOffset = new Vec2(this.width / 2, this.height / 2);
    }

    public void freeze(boolean flag) {
        this.freeze = flag;
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (!this.body.isDestroyed() && this.renderer != null) {
            if (this.freeze && EntityPlayer.IDLE_STATES.contains(this.renderer.getCurrentAnimation().getState())) return;
            this.onMove();
            if (this.body.getVelocity().magnitude() == 0) {
                switch (this.direction) {
                    case TOP -> this.renderer.switchState(CharacterAnimationState.IDLE_TOP);
                    case LEFT -> this.renderer.switchState(CharacterAnimationState.IDLE_LEFT);
                    case BOTTOM -> this.renderer.switchState(CharacterAnimationState.IDLE_BOTTOM);
                    case RIGHT -> this.renderer.switchState(CharacterAnimationState.IDLE_RIGHT);
                }
            }
        }
    }

    @Override
    public void draw(DrawTool drawTool) {
        super.draw(drawTool);
    }

    private void onMove() {
        if (!this.viewController.getDrawFrame().isFocused()) {
            // ONLY LOCAL PLAYER
            this.body.setLinearVelocity(0, 0);
            return;
        }
        if (this.freeze) return;

        boolean verticalKeyDown = false;
        Vec2 moveVelocity = new Vec2();
        if (ViewController.isKeyDown(KeyEvent.VK_W) && !ViewController.isKeyDown(KeyEvent.VK_S)) {
            moveVelocity.set(null, -this.speed);
            this.direction = EntityDirection.TOP;
            this.renderer.switchState(CharacterAnimationState.WALK_TOP);
            verticalKeyDown = true;

        } else if (ViewController.isKeyDown(KeyEvent.VK_S) && !ViewController.isKeyDown(KeyEvent.VK_W)) {
            moveVelocity.set(null, this.speed);
            this.direction = EntityDirection.BOTTOM;
            this.renderer.switchState(CharacterAnimationState.WALK_BOTTOM);
            verticalKeyDown = true;
        }

        if (ViewController.isKeyDown(KeyEvent.VK_A) && !ViewController.isKeyDown(KeyEvent.VK_D)) {
            moveVelocity.set(-this.speed, null);
            this.direction = EntityDirection.LEFT;
            if (this.renderer.getCurrentAnimation().getState() != CharacterAnimationState.WALK_TOP && this.renderer.getCurrentAnimation().getState() != CharacterAnimationState.WALK_BOTTOM) {
                this.renderer.switchState(CharacterAnimationState.WALK_LEFT);

            } else if (!verticalKeyDown) {
                this.renderer.switchState(CharacterAnimationState.WALK_LEFT);
            }

        } else if (ViewController.isKeyDown(KeyEvent.VK_D) && !ViewController.isKeyDown(KeyEvent.VK_A)) {
            moveVelocity.set(this.speed, null);
            this.direction = EntityDirection.RIGHT;
            if (this.renderer.getCurrentAnimation().getState() != CharacterAnimationState.WALK_TOP && this.renderer.getCurrentAnimation().getState() != CharacterAnimationState.WALK_BOTTOM) {
                this.renderer.switchState(CharacterAnimationState.WALK_RIGHT);

            } else if (!verticalKeyDown) {
                this.renderer.switchState(CharacterAnimationState.WALK_RIGHT);
            }
        }
        if (moveVelocity.magnitude() > 0) {
            moveVelocity.normalize().mul(this.speed, this.speed);
        }
        this.body.setLinearVelocity(moveVelocity.x, moveVelocity.y);
    }

    private void onDirectionChange() {
        if (this.isInvertLeft()) {
            if (this.direction == EntityDirection.LEFT && this.scaleX != -1) {
                this.scaleX = -1;

            } else if (this.direction == EntityDirection.RIGHT && this.scaleX != 1) {
                this.scaleX = 1;
            }
        } else {
            if (this.scaleX == -1) {
                this.scaleX = 1;
            }
        }
    }

    public void switchState(CharacterAnimationState state) {
        this.lastDirection = this.direction;
        switch (state) {
            case IDLE_TOP, WALK_TOP -> this.direction = EntityDirection.TOP;
            case IDLE_LEFT, WALK_LEFT -> this.direction = EntityDirection.LEFT;
            case IDLE_BOTTOM, WALK_BOTTOM -> this.direction = EntityDirection.BOTTOM;
            case IDLE_RIGHT, WALK_RIGHT -> this.direction = EntityDirection.RIGHT;
        }
        this.getRenderer().switchState(state);
    }

    @Override
    public void keyPressed(int key) {
        if (this.freeze && EntityPlayer.IDLE_STATES.contains(this.renderer.getCurrentAnimation().getState())) return;
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
