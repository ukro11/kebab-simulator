package kebab_simulator;

/**
 * In dieser Klasse werden globale, statische Einstellungen verwaltet.
 * Die Werte können nach eigenen Wünschen angepasst werden.
 */
public class Config {

    // Titel des Programms (steht oben in der Fenstertitelzeile)
    public final static String WINDOW_TITLE = "Kebab Simulator";

    // Konfiguration des Standardfensters: Anzeige und Breite des Programmfensters (Width) und Höhe des Programmfensters (Height)
    public final static boolean SHOW_DEFAULT_WINDOW = true;
    public final static int WINDOW_WIDTH = 1280;
    public final static int WINDOW_HEIGHT = 720;   // Effektive Höhe ist etwa 29 Pixel geringer (Titelleiste wird mitgezählt)
    public final static boolean WINDOW_FULLSCREEN = false;

    public final static Environment RUN_ENV = Environment.DEVELOPMENT;

    // Weitere Optionen für das Projekt
    public final static boolean USE_SOUND = true;

    public enum Environment {
        DEVELOPMENT,
        PRODUCTION
    }
}
