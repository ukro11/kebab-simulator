package kebab_simulator.graphics;

import KAGO_framework.view.DrawTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class OrderRenderer {

    private final Logger logger = LoggerFactory.getLogger(OrderRenderer.class);
    private final CopyOnWriteArrayList<IOrderRenderer> drawables;

    public OrderRenderer() {
        this.drawables = new CopyOnWriteArrayList<>();
    }

    public void draw(DrawTool drawTool) {
        this.drawables.sort(Comparator.comparing(IOrderRenderer::zIndex));
        for (int i = 0; i < this.drawables.size(); i++) {
            this.drawables.get(i).draw(drawTool);
        }
    }

    public void register(IOrderRenderer renderer) {
        if (renderer == null) {
            this.logger.warn("Registered renderer is null");
            return;
        }
        if (!this.drawables.contains(renderer)) {
            this.drawables.add(renderer);
        }
    }

    public void unregister(IOrderRenderer renderer) {
        this.drawables.remove(renderer);
    }

    public void registerAll(List<IOrderRenderer> integration) {
        this.drawables.addAll(integration);
    }

    public List<IOrderRenderer> getDrawables() {
        return this.drawables;
    }
}
