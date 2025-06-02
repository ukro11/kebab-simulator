package kebab_simulator.event.events;

import kebab_simulator.event.Event;

import java.awt.event.KeyEvent;

public class KeyPressedEvent extends Event {

    private final KeyEvent keyEvent;

    public KeyPressedEvent(KeyEvent keyEvent) {
        super("keypressed");
        this.keyEvent = keyEvent;
    }

    public int getKeyCode() {
        return this.keyEvent.getKeyCode();
    }

    public KeyEvent getKeyEvent() {
        return keyEvent;
    }
}
