package kebab_simulator.utils;

public class TimerUtils {

    private static double elapsedTime = 0;
    private static double lastTime = System.nanoTime() / 1e9;
    private static double deltaTime = 0.0;
    private static int fps = 0;
    private static int frameCount = 0;
    private static double fpsUpdateInterval = 1.0;

    public static void update() {
        double currentTime = System.nanoTime() / 1e9;
        TimerUtils.deltaTime = (currentTime - TimerUtils.lastTime);
        if (TimerUtils.deltaTime == 0) TimerUtils.deltaTime = 0.01;

        TimerUtils.lastTime = currentTime;
        TimerUtils.frameCount++;
        TimerUtils.elapsedTime += TimerUtils.deltaTime;

        if (TimerUtils.elapsedTime >= TimerUtils.fpsUpdateInterval) {
            TimerUtils.fps = TimerUtils.frameCount;
            TimerUtils.frameCount = 0;
            TimerUtils.elapsedTime = 0;
        }
    }

    public static double getDeltaTime() {
        return TimerUtils.deltaTime;
    }

    public static int getFPS() {
        return TimerUtils.fps;
    }
}

