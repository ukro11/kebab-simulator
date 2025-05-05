package kebab_simulator.control;

import KAGO_framework.control.ViewController;
import kebab_simulator.event.events.KeyPressedEvent;
import kebab_simulator.event.events.MouseClickedEvent;
import kebab_simulator.event.events.UpdateEvent;
import kebab_simulator.view.InputManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Ein Objekt der Klasse ProgramController dient dazu das Programm zu steuern.
 * Hinweise:
 * - Der Konstruktor sollte nicht geändert werden.
 * - Sowohl die startProgram()- als auch die updateProgram(...)-Methoden müssen vorhanden sein und ihre Signatur sollte
 *   nicht geändert werden
 * - Zusätzliche Methoden sind natürlich gar kein Problem
 */
public class ProgramController {

    private final ViewController viewController;
    private Logger logger;
    private Wrapper wrapper;

    /**
     * Konstruktor
     * Dieser legt das Objekt der Klasse ProgramController an, das den Programmfluss steuert.
     * Damit der ProgramController auf das Fenster zugreifen kann, benötigt er eine Referenz auf das Objekt
     * der Klasse viewController. Diese wird als Parameter übergeben.
     * @param viewController das viewController-Objekt des Programms
     */
    public ProgramController(ViewController viewController){
        this.viewController = viewController;
    }

    /**
     * Wird als aller erstes aufgerufen beim starten. "startProgram" wird hingegen nur
     * nach Erstellen des Fensters, usw. erst aufgerufen.
     */
    public void preStartProgram() {
        this.logger = LoggerFactory.getLogger(ProgramController.class);
        this.wrapper = new Wrapper();

        /* Beispiel mit Update Event
        Wrapper.getEventManager().addEventListener("update", (UpdateEvent e) -> {
            this.logger.info("Update: {}", e.getDeltaTime());
        });
        */

        /* Beispiel mit Keypressed Event

        */
        Wrapper.getEventManager().addEventListener("keypressed", (KeyPressedEvent e) -> {
            this.logger.info("Key wurde gedrückt: {}", KeyEvent.getKeyText(e.getKeyCode()));
            this.logger.warn("Key wurde gedrückt: {}", KeyEvent.getKeyText(e.getKeyCode()));
            this.logger.error("Key wurde gedrückt: {}", KeyEvent.getKeyText(e.getKeyCode()));
        });

        /* Beispiel mit Mouseclicked Event

        */
        Wrapper.getEventManager().addEventListener("mouseclicked", (MouseClickedEvent e) -> {
            this.logger.info("Button wurde gedrückt: {}", e.toString());
        });
    }

    /**
     * Diese Methode wird genau ein mal nach Programmstart aufgerufen. Hier sollte also alles geregelt werden,
     * was zu diesem Zeipunkt passieren muss.
     */
    public void startProgram() {
        viewController.register(new InputManager(this));
    }

    /**
     * Diese Methode wird vom ViewController-Objekt automatisch mit jedem Frame aufgerufen (ca. 60mal pro Sekunde)
     * @param dt Zeit seit letztem Frame in Sekunden
     */
    public void updateProgram(double dt){
        Wrapper.getEventManager().dispatchEvent(new UpdateEvent(dt));
    }
}
