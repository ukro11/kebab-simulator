package kebab_simulator.graphics;

import KAGO_framework.view.DrawTool;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrderRenderer {

    private final List<IOrder> drawables;

    public OrderRenderer() {
        this.drawables = new ArrayList<>();
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
