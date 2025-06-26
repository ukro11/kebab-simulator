package kebab_simulator.utils.misc;

public class TimerUtils {

    private double elapsedTime = 0;
    private double lastTime = System.nanoTime() / 1e9;
    private double deltaTime = 0.0;
    private int fps = 0;
    private int frameCount = 0;
    public int TARGET_FPS = 300;
    private double FPS_UPDATE_INTERVAL = 1.0;
    private boolean updated = false;

    public void update() {
        double currentTime = System.nanoTime() / 1e9;
        this.deltaTime = (currentTime - this.lastTime);
        this.lastTime = currentTime;

        if (this.deltaTime == 0) this.deltaTime = 0.01;

        this.frameCount++;
        this.elapsedTime += this.deltaTime;

        if (this.elapsedTime >= this.FPS_UPDATE_INTERVAL) {
            this.fps = this.frameCount;
            this.frameCount = 0;
            this.elapsedTime = 0;
            this.updated = true;
        }
    }

    public int getFPSCap() {
        return this.TARGET_FPS;
    }

    public double getDeltaTime() {
        return this.deltaTime;
    }

    public int getFPS() {
        return this.fps;
    }

    public boolean fpsUpdated() {
        return this.updated;
    }
}

