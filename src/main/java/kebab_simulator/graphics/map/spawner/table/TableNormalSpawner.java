package kebab_simulator.graphics.map.spawner.table;

import KAGO_framework.view.DrawTool;
import kebab_simulator.physics.Collider;

public class TableNormalSpawner extends TableSpawner {

    private final TableNormalType type;

    public TableNormalSpawner(String id, Collider collider) {
        super(id, collider, TableNormalType.parse(id).getSheet());
        this.type = TableNormalType.parse(id);
    }

    @Override
    public double zIndex() {
        if (this.type == TableNormalType.NORMAL) {
            return this.getCollider().getY() - 32;
        }
        return super.zIndex() - 20;
    }

    @Override
    public void draw(DrawTool drawTool) {
        switch (this.type) {
            case NORMAL, NORMAL_END, NORMAL_LEFT_END, NORMAL_RIGHT_END -> drawTool.drawImage(this.renderer.getCurrentFrame(), this.collider.getX(), this.collider.getY());
            case NORMAL_TOP, NORMAL_LEFT_TOP, NORMAL_RIGHT_TOP -> drawTool.drawImage(this.renderer.getCurrentFrame(), this.collider.getX(), this.collider.getY() - 12);
        }
    }

    /*@Override
    public void draw(DrawTool drawTool) {
        drawTool.push();
        super.draw(drawTool);
        double width = this.focusImage.getWidth();
        double height = this.focusImage.getHeight();
        double centerX = this.collider.getX() + width / 2;
        double centerY = this.collider.getY() + height / 2;
        drawTool.getGraphics2D().translate(centerX, centerY);
        drawTool.getGraphics2D().scale(this.scale, this.scale);
        drawTool.getGraphics2D().translate(-centerX, -centerY);
        drawTool.getGraphics2D().setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) this.opacity));
        drawTool.drawImage(this.focusImage, this.collider.getX(), this.collider.getY());
        drawTool.pop();
    }*/

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

        public static TableNormalType parse(String id) {
            for (TableNormalType type : TableNormalType.values()) {
                String name = type.name().toLowerCase().replaceAll("_", "-");
                if (id.split("_")[1].equalsIgnoreCase(name)) {
                    return type;
                }
            }
            return null;
        }
    }
}
