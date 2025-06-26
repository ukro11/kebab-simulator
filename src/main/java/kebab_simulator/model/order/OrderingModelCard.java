package kebab_simulator.model.order;

import KAGO_framework.view.DrawTool;
import kebab_simulator.model.meal.MealModel;
import kebab_simulator.model.visual.VisualConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

public class OrderingModelCard {

    private static final Logger log = LoggerFactory.getLogger(OrderingModelCard.class);
    private final MealModel mealModel;

    private double lastX;

    private int iconWidth;
    private int iconHeight;
    private int cardWidth;
    private int cardHeight;

    private final Font CARD_FONT;
    private Color CARD_TEXT_COLOR = Color.decode("#b29f99");
    private Color CARD_OUTLINE_COLOR = Color.decode("#554544");

    public OrderingModelCard(MealModel mealModel) {
        this.mealModel = mealModel;
        this.iconWidth = 64;
        this.iconHeight = 64;
        this.cardWidth = 180;
        this.cardHeight = (int) (45 + (3 * 48) + this.iconHeight);

        this.CARD_FONT = VisualConstants.getFont(20);
    }

    public void update(double dt) {}

    public void draw(DrawTool drawTool, double x, double y) {
        BufferedImage icon = this.mealModel.getIcon();

        drawTool.push();
        drawTool.setCurrentColor(Color.decode("#6c6e85"));
        drawTool.drawFilledRectangle(
            x,
            y,
            this.cardWidth,
            this.cardHeight
        );

        drawTool.setCurrentColor(this.CARD_TEXT_COLOR);
        drawTool.drawFilledRectangle(
                x,
                y,
                this.cardWidth,
                this.iconHeight + 10
        );

        drawTool.setCurrentColor(Color.decode("#3a3a50"));
        drawTool.getGraphics2D().setStroke(new BasicStroke(3.0f));
        drawTool.drawRectangle(
                x,
                y,
                this.cardWidth,
                this.iconHeight + 10
        );

        drawTool.setCurrentColor(Color.decode("#3a3a50"));
        drawTool.getGraphics2D().setStroke(new BasicStroke(3.0f));
        drawTool.drawRectangle(
                x,
                y,
                this.cardWidth,
                this.cardHeight
        );

        drawTool.getGraphics2D().setFont(this.CARD_FONT);
        drawTool.drawCenteredTextOutline(
            this.mealModel.getName(),
            x,
            5,
            this.cardWidth,
            45 + this.iconHeight,
            this.CARD_TEXT_COLOR,
            5.0,
            this.CARD_OUTLINE_COLOR
        );

        int row = 0;
        for (int i = 0; i < this.mealModel.getIngredientsNeeded().size(); i++) {
            if (i != 0 && i % 2 == 0) row++;
            int ingWidth = 48;
            int ingHeight = 48;
            var ing = this.mealModel.getIngredientsNeeded().get(i);
            drawTool.getGraphics2D().drawImage(
                ing.getIcon(),
                (i % 2 == 0) ? (int) (x + 30) : (int) (x + this.cardWidth - ingWidth - 30),
                (int) (y + 64 + 16) + ingHeight * row,
                ingWidth,
                ingHeight,
                null
            );
        }

        drawTool.getGraphics2D().drawImage(icon, (int) x + (180 - this.iconWidth) / 2, (int) (y - 5), this.iconWidth, this.iconHeight, null);
        drawTool.pop();
    }

    public MealModel getMealModel() {
        return this.mealModel;
    }
}
