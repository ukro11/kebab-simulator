package kebab_simulator.model.visual;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CopyOnWriteArrayList;

public class VisualConstants {

    private static CopyOnWriteArrayList<Font> fontCache = new CopyOnWriteArrayList<>();

    public final static Color BUTTON_OUTLINE = new Color(190, 219, 255);
    public final static Color BUTTON_BACKGROUND = new Color(66, 135, 245);
    public final static Color BUTTON_FOREGROUND = new Color(190, 219, 255);

    private static InputStream getPath(Fonts font) {
        return VisualConstants.class.getResourceAsStream("/graphic/font/" + font.getName());
    }

    public static Font getFont(double size) {
        return VisualConstants.getFont(Fonts.PIXEL_FONT, size);
    }

    public static Font getFont(Fonts type, double size) {
        try {
            var font = VisualConstants.fontCache.stream().filter(f -> f.getFontName().equalsIgnoreCase(type.getName()) && f.getSize() == size).findFirst().orElse(Font.createFont(Font.TRUETYPE_FONT, VisualConstants.getPath(type)));
            return font.deriveFont((float) size);

        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public enum Fonts {
        LUCKIEST_GUY("LuckiestGuy-Regular.ttf"),
        PIXEL_FONT("pixelFont.ttf"),
        DEBUG_FONT("pixelFont.ttf");

        private final String name;

        Fonts(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
