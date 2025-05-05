package kebab_simulator.event.events;

import kebab_simulator.event.Event;

public class KeyPressedEvent extends Event {

    private int keyCode;

    public KeyPressedEvent(int keyCode) {
        super("keypressed");
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }
}
