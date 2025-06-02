package kebab_simulator.graphics;

import KAGO_framework.view.DrawTool;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class OrderRenderer {

    private final CopyOnWriteArrayList<IOrder> drawables;

    public OrderRenderer() {
        this.drawables = new CopyOnWriteArrayList<>();
    }

    public void draw(DrawTool drawTool) {
        this.drawables.sort(Comparator.comparing(IOrder::zIndex));
        for (int i = 0; i < this.drawables.size(); i++) {
            this.drawables.get(i).draw(drawTool);
        }
    }

    public void register(IOrder integration) {
        this.drawables.add(integration);
    }

    public void registerAll(List<IOrder> integration) {
        this.drawables.addAll(integration);
    }

    public List<IOrder> getDrawables() {
        return this.drawables;
    }
}
