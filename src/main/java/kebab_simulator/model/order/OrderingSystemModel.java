package kebab_simulator.model.order;

import KAGO_framework.view.DrawTool;
import kebab_simulator.model.meal.meals.MealKebab;
import kebab_simulator.model.meal.meals.MealKebabMeat;
import kebab_simulator.model.meal.meals.MealSalatTasche;
import kebab_simulator.utils.misc.MathUtils;

import java.util.concurrent.CopyOnWriteArrayList;

public class OrderingSystemModel {

    private final CopyOnWriteArrayList<OrderingModelCard> queue;
    private final int max;

    public OrderingSystemModel() {
        this.queue = new CopyOnWriteArrayList();
        this.max = 3;
    }

    public void update(double dt) {
        if (this.queue.size() < this.max) {
            this.generateOrder();
        }
    }

    public void drawOrders(DrawTool drawTool) {
        for (int i = 0; i < this.queue.size(); i++) {
            var model = this.queue.get(i);
            model.draw(drawTool,5 + 180 * i + 5 * i, 5);
        }
    }

    private void generateOrder() {
        int random = MathUtils.random(1, 5);

        if (random == 1 || random == 2) {
            this.queue.add(new OrderingModelCard(new MealKebab()));

        } else if (random == 3 || random == 4) {
            this.queue.add(new OrderingModelCard(new MealKebabMeat()));

        } else if (random == 5) {
            this.queue.add(new OrderingModelCard(new MealSalatTasche()));
        }
    }

    public CopyOnWriteArrayList<OrderingModelCard> getQueue() {
        return this.queue;
    }
}
