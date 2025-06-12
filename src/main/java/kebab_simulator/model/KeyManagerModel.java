package kebab_simulator.model;

import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class KeyManagerModel {

    public static KeyManagerModel KEY_TAKE_ITEM = new KeyManagerModel(KeyEvent.VK_SPACE, "Gegenstände und Essen aufheben/fallen lassen");
    public static KeyManagerModel KEY_CUT_FOOD = new KeyManagerModel(KeyEvent.VK_CONTROL, "Lebensmittel schneiden");

    private final int key;
    private final String description;
    private BufferedImage icon;

    private KeyManagerModel(int key, String description) {
        this.key = key;
        this.description = description;
        try {
            String filename = KeyEvent.getKeyText(this.key).toUpperCase();
            switch (this.key) {
                case KeyEvent.VK_SPACE -> filename = "SPACE";
                case KeyEvent.VK_CONTROL -> filename = "CTRL";
            }
            this.icon = ImageIO.read(KeyManagerModel.class.getResourceAsStream(String.format("/graphic/keys/%s.png", filename)));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage getIcon() {
        return this.icon;
    }

    public int getKey() {
        return this.key;
    }

    public String getDescription() {
        return this.description;
    }
}
