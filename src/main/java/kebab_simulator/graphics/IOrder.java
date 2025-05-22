package kebab_simulator.graphics;

import KAGO_framework.view.DrawTool;

public interface IOrder {
    double zIndex();
    void draw(DrawTool drawTool);
}
