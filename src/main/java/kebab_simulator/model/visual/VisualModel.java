package kebab_simulator.model.visual;

import KAGO_framework.view.DrawTool;
import kebab_simulator.control.Wrapper;

public abstract class VisualModel implements Comparable<VisualModel> {

    private String id;
    private int zIndex;
    private boolean pauseGame = false;

    public VisualModel(String id) {
        this(id, 0);
    }

    public VisualModel(String id, int zIndex) {
        this.id = id;
        this.zIndex = zIndex;
    }

    public String getId() {
        return this.id;
    }

    public int getzIndex() {
        return zIndex;
    }

    public void setPauseGame(boolean pauseGame) {
        this.pauseGame = pauseGame;
    }

    public boolean shouldPauseGame() {
        return this.pauseGame;
    }

    public abstract void draw(DrawTool drawTool);
    public abstract void update(double dt);

    @Override
    public int compareTo(VisualModel v) {
        return Integer.compare(v.zIndex, this.zIndex);
    }
}
