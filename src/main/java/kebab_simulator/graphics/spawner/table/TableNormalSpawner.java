package kebab_simulator.graphics.spawner.table;

import KAGO_framework.view.DrawTool;
import kebab_simulator.Wrapper;
import kebab_simulator.animation.AnimationRenderer;
import kebab_simulator.animation.states.FocusAnimationState;
import kebab_simulator.graphics.spawner.ObjectIdResolver;
import kebab_simulator.model.entity.Entity;
import kebab_simulator.model.entity.impl.EntityItem;
import kebab_simulator.model.entity.impl.item.EntityPan;
import kebab_simulator.model.entity.impl.item.EntityPlate;
import kebab_simulator.physics.Collider;

public class TableNormalSpawner extends TableItemIntegration implements ITableSide {

    private final TableNormalType type;

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

        switch (this.type) {
            case NORMAL: {
                if (this.isLeft()) {
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
    public void onDropItem(TableItemDropEvent event) {
        if (event == TableItemDropEvent.ITEM_PLATE_DROP) {
            EntityItem<?> item = Wrapper.getLocalPlayer().getInventory().getItemInHand();
            if (item instanceof EntityPlate) {
                item = ((EntityPlate) item).getItems().get(0);
                if (!this.filterItem(event, item)) return;
                item.getLocation().removeItem(item);
                item.onDrop(((EntityPlate) this.items.get(0)));

            } else {
                Wrapper.getLocalPlayer().getInventory().dropItem(((EntityPlate) this.items.get(0)));
            }

        } else if (event == TableItemDropEvent.ITEM_TABLE_DROP) {
            if (this.getId().getRawId().equals("table_normal_9") || this.getId().getRawId().equals("table_normal_10")) {
                if (Wrapper.getLocalPlayer().getInventory().isPlate() && !(Wrapper.getLocalPlayer().getInventory().getItemInHand() instanceof EntityPan)) {
                    if (!this.filterItem(event, Wrapper.getLocalPlayer().getInventory().getItemInHand())) return;
                    Wrapper.getLocalPlayer().getInventory().dropItem(this);
                }

            } else {
                if (!this.filterItem(event, Wrapper.getLocalPlayer().getInventory().getItemInHand())) return;
                Wrapper.getLocalPlayer().getInventory().dropItem(this);
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

                } else if (Wrapper.getLocalPlayer().getInventory().isPlate()
                        && !Wrapper.getLocalPlayer().getInventory().getItemAsPlate().getItems().isEmpty()
                        && Wrapper.getLocalPlayer().getInventory().getItemAsPlate().getItems().get(0).allowPlaceOnPlate()) {
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
    public void update(double dt) {
        super.update(dt);
        if (this.renderer != null) {
            if (TableSpawner.currentCollisionPlayer == this) {
                this.renderer.switchState(FocusAnimationState.FOCUS);

            } else {
                this.renderer.switchState(FocusAnimationState.DEFAULT);
            }
        }
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

    @Override
    public boolean isLeft() {
        return this.collider.getX() < 620;
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
