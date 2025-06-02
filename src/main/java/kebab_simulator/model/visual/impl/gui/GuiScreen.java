package kebab_simulator.model.visual.impl.gui;

import KAGO_framework.control.ViewController;
import KAGO_framework.view.DrawTool;
import kebab_simulator.Config;
import kebab_simulator.control.ProgramController;
import kebab_simulator.model.visual.VisualConstants;
import kebab_simulator.model.visual.VisualModel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public abstract class GuiScreen {

    private static GuiScreen last;
    private static GuiScreen current;

    public static void open(GuiScreen screen) {
        if (GuiScreen.current != null) {
            GuiScreen.current.onClose(screen);
        }
        screen.onOpen(GuiScreen.last);
        GuiScreen.current = screen;
    }

    public static void close() {
        GuiScreen.current.onClose((GuiScreen) null);
        GuiScreen.current = null;
    }

    public static GuiScreen getCurrentScreen() {
        return GuiScreen.current;
    }

    protected ViewController viewController;
    protected ProgramController programController;

    protected final String title;
    protected final double width;
    protected final double height;
    protected final List<VisualModel> visuals;
    protected boolean shouldPause = true;
    private final BufferedImage background;
    private final Font titleFont;
    protected double opacity;

    protected Consumer<GuiScreen> onOpen;
    protected Consumer<GuiScreen> onClose;

    public GuiScreen(String title, double width) {
        try {
            this.title = title;
            this.width = width;
            this.height = width * 9 / 16;
            this.opacity = 1.0;
            this.visuals = new ArrayList<>();
            this.background = ImageIO.read(GuiScreen.class.getResourceAsStream("/graphic/ui/ui_panel.png"));
            this.titleFont = VisualConstants.getFont(VisualConstants.Fonts.PIXEL_FONT, 40);

            this.viewController = ViewController.getInstance();
            this.programController = this.viewController.getProgramController();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(double dt) {
        for (VisualModel visual : this.visuals) {
            visual.update(dt);
        }
    }

    public void draw(DrawTool drawTool) {
        drawTool.push();
        drawTool.setCurrentColor(new Color(0, 0, 0, (int) (100 * this.opacity)));
        drawTool.drawFilledRectangle(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        drawTool.resetColor();
        drawTool.getGraphics2D().drawImage(this.background, (int) (Config.WINDOW_WIDTH - this.width) / 2, (int) (Config.WINDOW_HEIGHT - this.height) / 2, (int) this.width, (int) this.height, null, null);
        drawTool.setCurrentColor(Color.decode("#cac3d5"), (int) (255 * this.opacity));
        drawTool.drawCenteredText(this.titleFont, this.title.toUpperCase(), (Config.WINDOW_WIDTH - this.width) / 2, (Config.WINDOW_HEIGHT - this.height) / 2 + 90, this.width, 0);
        for (VisualModel visual : this.visuals) {
            visual.draw(drawTool);
        }
        drawTool.pop();
    }

    public void addVisual(VisualModel visual) {
        this.visuals.add(visual);
        this.visuals.sort(Comparator.comparing(VisualModel::zIndex));
    }

    public boolean shouldPause() {
        return this.shouldPause;
    }

    public String getTitle() {
        return this.title;
    }

    public List<VisualModel> getVisuals() {
        return this.visuals;
    }

    public void onOpen(Consumer<GuiScreen> onOpen) {
        this.onOpen = onOpen;
    }

    public void onClose(Consumer<GuiScreen> onClose) {
        this.onClose = onClose;
    }

    protected void onOpen(GuiScreen last) {
        if (this.onOpen != null) this.onOpen.accept(last);
        if (this.shouldPause) this.programController.player.freeze(true);
    }

    protected void onClose(GuiScreen newScreen) {
        if (this.onClose != null) this.onClose.accept(newScreen);
        if (this.shouldPause) this.programController.player.freeze(false);
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            GuiScreen.close();
        }
    }

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
}
