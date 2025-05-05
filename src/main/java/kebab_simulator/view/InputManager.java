package kebab_simulator.view;

import KAGO_framework.model.InteractiveGraphicalObject;
import kebab_simulator.control.ProgramController;
import kebab_simulator.control.Wrapper;
import kebab_simulator.event.events.KeyPressedEvent;
import kebab_simulator.event.events.MouseClickedEvent;

import java.awt.event.MouseEvent;

/**
 * Realisiert ein Objekt, dass alle Eingaben empfängt und dann danach passende Methoden
 * im ProgramController aufruft.
 */
public class InputManager extends InteractiveGraphicalObject {

    private final ProgramController programController;

    /**
     * Objekterzeugung
     * @param programController Nötig als Objekt vom Controllerbereich, das informiert wird
     */
    public InputManager(ProgramController programController){
        this.programController = programController;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Wrapper.getEventManager().dispatchEvent(new MouseClickedEvent(e));
    }

    @Override
    public void keyPressed(int key) {
        Wrapper.getEventManager().dispatchEvent(new KeyPressedEvent(key));
    }
}
