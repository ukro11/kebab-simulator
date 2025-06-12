package kebab_simulator.graphics.spawner.table;

import KAGO_framework.view.DrawTool;
import kebab_simulator.Wrapper;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.states.FocusAnimationState;
import kebab_simulator.graphics.spawner.ObjectIdResolver;
import kebab_simulator.model.entity.Entity;
import kebab_simulator.model.entity.impl.item.EntityPlate;
import kebab_simulator.physics.Collider;

public class TableNormalSpawner extends TableItemIntegration {

    private final TableNormalType type;
    private boolean isLeft = false;

    public TableNormalSpawner(ObjectIdResolver id, Collider collider) {
        super(id, collider, new AnimationRenderer<FocusAnimationState>(
            TableNormalType.parse(id).getSheet(),
            id.getSpawnerType().contains("-end") ? 1 : 2,
            1,
            32,
            32,
            FocusAnimationState.DEFAULT
        ));
        this.type = TableNormalType.parse(id);

        if (collider.getX() < 620) {
            this.isLeft = true;
        }

        switch (this.type) {
            case NORMAL: {
                if (this.isLeft) {
                    this.directionToLook = Entity.EntityDirection.LEFT;

                } else {
                    this.directionToLook = Entity.EntityDirection.RIGHT;
                }
                break;
            }
            case NORMAL_TOP, NORMAL_LEFT_TOP, NORMAL_RIGHT_TOP: {
                this.directionToLook = Entity.EntityDirection.TOP;
                break;
            }
        }
    }

    @Override
    public boolean allowFocus() {
        if (!this.items.isEmpty()) {
            var firstItem = this.items.get(0);
            if (firstItem instanceof EntityPlate) {
                if (Wrapper.getLocalPlayer().getInventory().isFood() && Wrapper.getLocalPlayer().getInventory().getItemAsFood().allowPlaceOnPlate()) {
                    return true;

                } else if (Wrapper.getLocalPlayer().getInventory().getItemInHand() == null) {
                    return true;

                } else {
                    return false;
                }
            }
        }

        return super.allowFocus();
    }

    @Override
    public void draw(DrawTool drawTool) {
        super.draw(drawTool);
        switch (this.type) {
            case NORMAL, NORMAL_END, NORMAL_LEFT_END, NORMAL_RIGHT_END -> drawTool.drawImage(this.renderer.getCurrentFrame(), this.collider.getX(), this.collider.getY());
            case NORMAL_TOP, NORMAL_LEFT_TOP, NORMAL_RIGHT_TOP -> drawTool.drawImage(this.renderer.getCurrentFrame(), this.collider.getX(), this.collider.getY() - 5);
        }
    }

    @Override
    public double zIndex() {
        if (this.type == TableNormalType.NORMAL) {
            return this.getCollider().getY() - 32;
        }
        return super.zIndex() - 20;
    }

    public TableNormalType getType() {
        return this.type;
    }

    public boolean isLeft() {
        return this.isLeft;
    }

    public enum TableNormalType {
        NORMAL("/graphic/map/sprites/table/table_normal.png"),


        NORMAL_TOP("/graphic/map/sprites/table/table_normal-top.png"),
        NORMAL_LEFT_TOP("/graphic/map/sprites/table/table_normal-left-top.png"),
        NORMAL_RIGHT_TOP("/graphic/map/sprites/table/table_normal-right-top.png"),

        NORMAL_END("/graphic/map/sprites/table/table_normal-end.png"),
        NORMAL_LEFT_END("/graphic/map/sprites/table/table_normal-left-end.png"),
        NORMAL_RIGHT_END("/graphic/map/sprites/table/table_normal-right-end.png");

        private final String sheet;

        TableNormalType(String sheet) {
            this.sheet = sheet;
        }

        public String getSheet() {
            return this.sheet;
        }

        public static TableNormalType parse(ObjectIdResolver id) {
            for (TableNormalType type : TableNormalType.values()) {
                String name = type.name().toLowerCase().replaceAll("_", "-");
                if (id.getSpawnerType().equalsIgnoreCase(name)) {
                    return type;
                }
            }
            return null;
        }
    }
}
