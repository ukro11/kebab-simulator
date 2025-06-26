package KAGO_framework.view;

import kebab_simulator.graphics.KebabGraphics;
import kebab_simulator.utils.misc.Vec2;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.text.AttributedString;
import java.util.List;

/**
 * Diese Klasse dient als vereinfachte Schnittstelle zum Zeichnen. Es handelt sich um eine BlackBox fuer die
 * Graphics2D-Klasse.
 * Vorgegebene Klasse des Frameworks. Modifikation auf eigene Gefahr.
 */
public class DrawTool {

    // Referenzen
    private static KebabGraphics kebabGraphics; //java-spezifisches Objekt zum Arbeiten mit 2D-Grafik
    private JComponent parent;

    /**
     * Zeichnet ein Objekt der Klasse BufferedImage
     * @param bI Das zu zeichnende Objekt
     * @param x Die x-Koordinate der oberen linken Ecke
     * @param y Die y-Koordinate der oberen linken Ecke
     */
    public void drawImage(BufferedImage bI, double x, double y){
        if (this.kebabGraphics != null) kebabGraphics.getGraphics2D().drawImage(bI, (int)x, (int)y, null);
    }

    public void drawImage(BufferedImage bI, double x, double y, Color color){
        if (this.kebabGraphics != null) kebabGraphics.getGraphics2D().drawImage(bI, (int)x, (int)y, color, null);
    }

    /**
     * Zeichnet ein Objekt der Klasse BufferedImage.
     * @param bI das BufferedImage, das gezeichnet wird
     * @param x x-Koordinate der oberen linken Ecke
     * @param y y-Koordinate der oberen linken Ecke
     * @param degrees Grad, die das Bild rotiert sein soll (nicht Bogemaß)
     * @param scale Faktor mit dem das Bild skaliert werden soll (für Wirkung > 0)
     */
    public void drawTransformedImage(BufferedImage bI, double x, double y, double degrees, double scale){
        if (this.kebabGraphics != null){
            AffineTransform transform = new AffineTransform();

            transform.translate(x,y);
            if(scale > 0) {
                transform.scale(scale,scale);
                if(scale < 1){
                    transform.translate(+bI.getWidth() * (1-scale), +bI.getHeight() * (1-scale));
                } else {
                    transform.translate(-bI.getWidth()*(scale-1)*0.25, -bI.getHeight()*(scale-1)*0.25);
                }
            }
            transform.rotate( Math.toRadians(degrees), bI.getWidth()/ (double) 2, bI.getHeight()/ (double) 2 );
            kebabGraphics.getGraphics2D().drawImage(bI, transform, null);
        }
    }

    /**
     * Modifiziert die Breite aller gezeichneten Linien
     * Der Standardwert ist 1
     * @param size die gewünschte Breite in Pixeln
     */
    public void setLineWidth(int size){
        if (this.kebabGraphics != null){
            kebabGraphics.getGraphics2D().setStroke(new BasicStroke(size));
        }
    }

    /**
     * Zeichnet ein Rechteck als Linie ohne Fuellung
     * @param x Die x-Koordinate der oberen linken Ecke
     * @param y Die y-Koordinate der oberen linken Ecke
     * @param width Die Breite
     * @param height Die Hoehe
     */
    public void drawRectangle(double x, double y, double width, double height){
        Rectangle2D.Double r = new Rectangle2D.Double(x,y,width,height);
        if (this.kebabGraphics != null) kebabGraphics.getGraphics2D().draw(r);
    }

    /**
     * Zeichnet ein Quadrat als Linie ohne Fuellung
     * Credit: Nils Derenthal
     * @param x Die x-Koordinate der oberen linken Ecke
     * @param y Die y-Koordinate der oberen linken Ecke
     * @param sideLength die Länge einer Seite des Quadrats
     */
    public void drawRectangle(double x, double y, double sideLength){
        drawRectangle(x,y,sideLength,sideLength);

    }

    /**
     * Zeichnet ein gefuelltes Rechteck
     * @param x Die x-Koordinate der oberen linken Ecke
     * @param y Die y-Koordinate der oberen linken Ecke
     * @param width Die Breite
     * @param height Die Hoehe
     */
    public void drawFilledRectangle(double x, double y, double width, double height){
        Rectangle2D.Double r = new Rectangle2D.Double(x,y,width,height);
        if (this.kebabGraphics != null) kebabGraphics.getGraphics2D().fill(r);
    }

    /**
     * Aendert die aktuell verwendete Farbe zum Zeichnen auf eine andere Farbe.
     * Die aenderung gilt solange, bis eine neue Farbe gesetzt wird (Zustand)
     * @param r Der Gruen-Anteil der Farbe (0 bis 255)
     * @param g Der Gelb-Anteil der Farbe (0 bis 255)
     * @param b Der Blau-Anteil der Farbe (0 bis 255)
     * @param alpha Die Transparenz der Farbe, wobei 0 nicht sichtbar und 255 voll deckend ist
     */
    public void setCurrentColor(int r, int g, int b, int alpha){
        if (this.kebabGraphics != null) {
            kebabGraphics.getGraphics2D().setColor( new Color(r,g,b,alpha) );
        }
    }

    /**
     * Aendert die aktuell verwendete Farbe zum Zeichnen auf eine andere Farbe.
     * Die Änderung gilt solange, bis eine neue Farbe gesetzt wird.
     * @param color Von außen festgelegtes Farb-Objekt
     */
    public void setCurrentColor(Color color){
        if (this.kebabGraphics != null && color != null) kebabGraphics.getGraphics2D().setColor( color );
    }

    public void setCurrentColor(Color color, int alpha){
        if (this.kebabGraphics != null && color != null) {
            var c = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
            kebabGraphics.getGraphics2D().setColor(c);
        }
    }

    /**
     * Aendert die aktuell verwendete Farbe zum Zeichnen auf eine andere Farbe.
     * Die aenderung gilt solange, bis eine neue Farbe gesetzt wird (Zustand)
     * Credit: Nils Derenthal
     * @param h Der Farbton, dargestellt in einem Farbring in Grad (0-360)
     *         -> https://crosstownarts.org/wp-content/uploads/2019/11/colour-wheel-1740381_1920-1200x1200.jpg
     * @param s Die Sättigung der Farbe in Prozent (0-100)
     * @param b Die Helligkeit der Farbe in Prozent (0-100)
     */
    public void setCurrentHSBColor(float h, float s, float b){
        if (this.kebabGraphics != null) kebabGraphics.getGraphics2D().setColor( Color.getHSBColor(h,s,b));
    }

    public void resetColor(){
        if (this.kebabGraphics != null) kebabGraphics.getGraphics2D().setColor(Color.WHITE);
    }

    /**
     * Zeichnet einen Kreis ohne Fuellung als Linie
     * @param x Die x-Koordinate des Mittelpunkts
     * @param y Die y-Koordinate des Mittelpunkts
     * @param radius Der Kreisradius
     */
    public void drawCircle(double x, double y, double radius){
        Ellipse2D.Double e = new Ellipse2D.Double(x-radius,y-radius,radius*2,radius*2);
        if (this.kebabGraphics != null) kebabGraphics.getGraphics2D().draw(e);
    }

    /**
     * Zeichnet einen gefuellten Kreis
     * @param x Die x-Koordinate des Mittelpunkts
     * @param y Die y-Koordinate des Mittelpunkts
     * @param radius Der Kreisradius
     */
    public void drawFilledCircle(double x, double y, double radius){
        Ellipse2D.Double e = new Ellipse2D.Double(x-radius,y-radius,radius*2,radius*2);
        if (this.kebabGraphics != null) kebabGraphics.getGraphics2D().fill(e);
    }

    /**
     * Zeichnet ein nicht gefuelltes Dreieck
     * @param x1 Die x-Koordinate der ersten Ecke
     * @param y1 Die y-Koordinate der ersten Ecke
     * @param x2 Die x-Koordinate der zweiten Ecke
     * @param y2 Die y-Koordinate der zweiten Ecke
     * @param x3 Die x-Koordinate der dritten Ecke
     * @param y3 Die y-Koordinate der dritten Ecke
     */
    public void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3 ){
        drawPolygon(x1,y1,x2,y2,x3,y3);
    }

    /**
     * Zeichnet ein gefuelltes Dreieck
     * @param x1 Die x-Koordinate der ersten Ecke
     * @param y1 Die y-Koordinate der ersten Ecke
     * @param x2 Die x-Koordinate der zweiten Ecke
     * @param y2 Die y-Koordinate der zweiten Ecke
     * @param x3 Die x-Koordinate der dritten Ecke
     * @param y3 Die y-Koordinate der dritten Ecke
     */
    public void drawFilledTriangle(double x1, double y1, double x2, double y2, double x3, double y3 ){
        drawFilledPolygon(x1,y1,x2,y2,x3,y3);
    }

    /**
     * Zeichnet ein Polygon mit beliebig vielen Eckpunkten (Mehr als 3).
     * @param eckpunkte eine gerade anzahl an Ecken des Polygons. Diese folgen dem Schema: [[x1], [y1], [x2], [y2], [x1]] etc.
     * @author Nils Derenthal
     */
    public void drawPolygon (double ... eckpunkte) {
        kebabGraphics.getGraphics2D().draw(getPolygon(eckpunkte));
    }

    public void drawPolygon(Vec2... eckpunkte) {
        kebabGraphics.getGraphics2D().draw(getPolygon(List.of(eckpunkte)));
    }

    public void drawPolygon(List<Vec2> eckpunkte) {
        kebabGraphics.getGraphics2D().draw(getPolygon(eckpunkte));
    }

    /**
     * Zeichnet ein gefülltes Polygon mit beliebig vielen Eckpunkten (Mehr als 3).
     * @param eckpunkte eine gerade anzahl an Ecken des Polygons. Diese folgen dem Schema: [[x1], [y1], [x2], [y2], [x1]] etc.
     * @author Nils Derenthal
     */
    public void drawFilledPolygon (double ... eckpunkte) {
        kebabGraphics.getGraphics2D().fill(getPolygon(eckpunkte));
    }

    public void drawFilledPolygon (Vec2 ... eckpunkte) {
        kebabGraphics.getGraphics2D().fill(getPolygon(List.of(eckpunkte)));
    }

    /**
     * Helper funktion für doppelten Code-block um ein Polygon aus einem Array von Ecken zu erzeugen.
     * @param eckPunkte eine gerade anzahl an Ecken des Polygons. Diese folgen dem Schema: [[x1], [y1], [x2], [y2], [x1]] etc.
     * @return das durch die Eckpunkte geschaffene Polygon
     * @author Nils Derenthal
     */
    private Polygon getPolygon(double[] eckPunkte) {
        //garantiert das eine gerade anzahl an ecken vorhanden ist und das es mehr als 3 ecken sind
        assert eckPunkte.length % 2 == 0 && eckPunkte.length >= 3 * 2;
        assert this.kebabGraphics  != null;

        Polygon p = new Polygon();

        for (int i = 0; i < eckPunkte.length - 1; i += 2) {
            p.addPoint ((int)eckPunkte[i], (int) eckPunkte[i + 1]);
        }
        return p;
    }

    private Polygon getPolygon(List<Vec2> eckPunkte) {
        //garantiert das eine gerade anzahl an ecken vorhanden ist und das es mehr als 3 ecken sind
        assert eckPunkte.size() % 2 == 0 && eckPunkte.size() >= 3;
        assert this.kebabGraphics  != null;

        Polygon p = new Polygon();

        for (int i = 0; i < eckPunkte.size(); i++) {
            p.addPoint ((int)eckPunkte.get(i).x, (int) eckPunkte.get(i).y);
        }
        return p;
    }

    /**
     * Zeichnet eine duenne Linie zwischen den beiden Punkten
     * @param x1 Die x-Koordinate des ersten Punkts
     * @param y1 Die y-Koordinate des ersten Punkts
     * @param x2 Die x-Koordinate des zweiten Punkts
     * @param y2 Die y-Koordinate des zweiten Punkts
     */
    public void drawLine(double x1, double y1, double x2, double y2){
        Line2D.Double line = new Line2D.Double(x1,y1,x2,y2);
        if (this.kebabGraphics != null) kebabGraphics.getGraphics2D().draw(line);
    }

    /**
     * Zeichnet eine Ellipse ohne Fuellung als Linie
     * Credit: Nils Derenthal
     * @param x Die x-Koordinate des Mittelpunkts in X-Richtung
     * @param y Die y-Koordinate des Mittelpunkts in Y-Richtung
     * @param radiusX Der Kreisradius in X-Richtung
     * @param radiusY Der KreisRadius in Y-Richtung
     */
    public void drawEllipse (double x, double y, double radiusX, double radiusY){
        Ellipse2D.Double e = new Ellipse2D.Double(x-radiusX,y-radiusY,radiusX*2,radiusY*2);
        if (this.kebabGraphics != null) kebabGraphics.getGraphics2D().draw(e);
    }

    /**
     * Zeichnet eine gefuellte Ellipse
     * Credit: Nils Derenthal
     * @param x Die x-Koordinate des Mittelpunkts in X-Richtung
     * @param y Die y-Koordinate des Mittelpunkts in Y-Richtung
     * @param radiusX Der Kreisradius in X-Richtung
     * @param radiusY Der KreisRadius in Y-Richtung
     */
    public void drawFilledEllipse (double x, double y, double radiusX, double radiusY){
        Ellipse2D.Double e = new Ellipse2D.Double(x-radiusX,y-radiusY,radiusX*2,radiusY*2);
        if (this.kebabGraphics != null) kebabGraphics.getGraphics2D().fill(e);
    }

    /**
     * Zeichnet einen Kreisausschnitt als Linie ohne Füllung
     * Credit: Nils Derenthal
     * @param x Die X-Koordinate des Mittelpunktes des Kreisausschnitts
     * @param y Die Y-Koordinate des Mittelpunktes des Kreisausschnitts
     * @param radius Der Radius des Kreisausschnittes
     * @param startingAngle Der Anfangswinkel des Kreisausschnitts (0° entspricht dem rechtesten Punkt des Kreises)
     * @param endingAngle Der Endwinkel des Kreisauschnitts
     * @param type Der Typ des Kreisausschnitt.
     * -> 0 entspricht offen, 1 entspricht einer Linie zwischen den beiden Winkeln, 2 entspricht Linien zwischen dem Mittelpunkt und den Winkeln
     */
    public void drawArc(double x, double y, double radius, double startingAngle, double endingAngle, int type){
        if (type > 2)  throw new IllegalArgumentException("must be in a 0 - 2 scope");
        Arc2D.Double arc = new Arc2D.Double(x,y,radius,radius,startingAngle,endingAngle,type);
        if (this.kebabGraphics != null) kebabGraphics.getGraphics2D().draw(arc);
    }

    /**
     * Zeichnet einen gefuellten Kreisausschnitt
     * Credit: Nils Derenthal
     * @param x Die X-Koordinate des Mittelpunktes des Kreisausschnitts
     * @param y Die Y-Koordinate des Mittelpunktes des Kreisausschnitts
     * @param radius Der Radius des Kreisausschnittes
     * @param startingAngle Der Anfangswinkel des Kreisausschnitts (0° entspricht dem rechtesten Punkt des Kreises)
     * @param endingAngle Der Endwinkel des Kreisauschnitts
     * @param type Der Typ des Kreisausschnitt.
     * -> 0 entspricht offen, 1 entspricht einer Linie zwischen den beiden Winkeln, 2 entspricht Linien zwischen dem Mittelpunkt und den Winkeln
     */
    public void drawFilledArc(double x, double y, double radius, double startingAngle, double endingAngle, int type){
        if (type > 2)  throw new IllegalArgumentException("must be in a 0 - 2 scope");
        Arc2D.Double arc = new Arc2D.Double(x,y,radius,radius,startingAngle,endingAngle,type);
        if (this.kebabGraphics != null) kebabGraphics.getGraphics2D().fill(arc);
    }

    /**
     * Zeichnet einen Kreisausschnitt als Linie ohne Füllung
     * Credit: Nils Derenthal
     * @param x Die X-Koordinate des Mittelpunktes des Kreisausschnitts
     * @param y Die Y-Koordinate des Mittelpunktes des Kreisausschnitts
     * @param radiusX Der Radius des Kreisausschnittes in X-Richtung
     * @param radiusY Der Radius des Kreisausschnittes in Y-Richtung
     * @param startingAngle Der Anfangswinkel des Kreisausschnitts (0° entspricht dem rechtesten Punkt des Kreises)
     * @param endingAngle Der Endwinkel des Kreisauschnitts
     * @param type Der Typ des Kreisausschnitt.
     * -> 0 entspricht offen, 1 entspricht einer Linie zwischen den beiden Winkeln, 2 entspricht Linien zwischen dem Mittelpunkt und den Winkeln
     */
    public void drawEllipticArc(double x, double y, double radiusX, double radiusY, double startingAngle, double endingAngle, int type){
        if (type > 2)  throw new IllegalArgumentException("must be in a 0 - 2 scope");
        Arc2D.Double arc = new Arc2D.Double(x,y,radiusX,radiusY,startingAngle,endingAngle,type);
        if (this.kebabGraphics != null) kebabGraphics.getGraphics2D().draw(arc);
    }

    /**
     * Zeichnet einen gefuellten Kreisausschnitt
     * Credit: Nils Derenthal
     * @param x Die X-Koordinate des Mittelpunktes des Kreisausschnitts
     * @param y Die Y-Koordinate des Mittelpunktes des Kreisausschnitts
     * @param radiusX Der Radius des Kreisausschnittes in X-Richtung
     * @param radiusY Der Radius des Kreisausschnittes in Y-Richtung
     * @param startingAngle Der Anfangswinkel des Kreisausschnitts (0° entspricht dem rechtesten Punkt des Kreises)
     * @param endingAngle Der Endwinkel des Kreisauschnitts
     * @param type Der Typ des Kreisausschnitt.
     * -> 0 entspricht offen, 1 entspricht einer Linie zwischen den beiden Winkeln, 2 entspricht Linien zwischen dem Mittelpunkt und den Winkeln
     */
    public void drawFilledEllipticArc(double x, double y, double radiusX, double radiusY, double startingAngle, double endingAngle, int type){
        if (type > 2)  throw new IllegalArgumentException("must be in a 0 - 2 scope");
        Arc2D.Double arc = new Arc2D.Double(x,y,radiusX,radiusY,startingAngle,endingAngle,type);
        if (this.kebabGraphics != null) kebabGraphics.getGraphics2D().fill(arc);
    }

    /**
     * Zeichnet einen Text an die gewuenschte Stelle
     * @param x Die x-Position des Textbeginns
     * @param y Die y-Position des Textbeginns
     * @param text Der anzuzeigende Text
     */
    public void drawText(String text, double x, double y){
        if (this.kebabGraphics !=null) kebabGraphics.getGraphics2D().drawString(text,(float) x, (float) y);
    }

    public void drawTextOutline(String text, double x, double y, Color color, double outlineWidth, Color outline) {
        this.drawTextOutline(text, x, y, color, outlineWidth, outline, true);
    }

    public void drawTextOutline(String text, double x, double y, Color color, double outlineWidth, Color outline, boolean pixelated) {
        if (this.kebabGraphics != null) {
            var font = kebabGraphics.getGraphics2D().getFont();
            AttributedString as = new AttributedString(text.replace(" ", "  "));
            as.addAttribute(TextAttribute.FONT, font);
            as.addAttribute(TextAttribute.TRACKING, outlineWidth + 2.0);
            as.addAttribute(TextAttribute.KERNING, TextAttribute.KERNING_ON);
            this.push();
            if (pixelated) {
                kebabGraphics.getGraphics2D().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            } else {
                kebabGraphics.getGraphics2D().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }

            // 2. Font und FontRenderContext holen
            kebabGraphics.getGraphics2D().setFont(font);
            FontRenderContext frc = kebabGraphics.getGraphics2D().getFontRenderContext();

            // 3. Text in ein Shape umwandeln
            GlyphVector gv = font.createGlyphVector(frc, as.getIterator());
            Shape textShape = gv.getOutline((float) x, (float) y);

            kebabGraphics.getGraphics2D().setColor(outline);
            kebabGraphics.getGraphics2D().setStroke(new BasicStroke((float) outlineWidth)); // Dicke der Kontur
            kebabGraphics.getGraphics2D().draw(textShape);

            kebabGraphics.getGraphics2D().setColor(color);
            kebabGraphics.getGraphics2D().fill(textShape);
            this.pop();
        }
    }

    public void drawCenteredTextOutline(String text, double x, double y, double width, double height, Color color, double outlineWidth, Color outline) {
        this.drawCenteredTextOutline(text, x, y, width, height, color, outlineWidth, outline, true);
    }

    public void drawCenteredTextOutline(String text, double x, double y, double width, double height, Color color, double outlineWidth, Color outline, boolean pixelated){
        if (this.kebabGraphics != null) {
            var font = kebabGraphics.getGraphics2D().getFont();
            AttributedString as = new AttributedString(text.replace(" ", "  "));
            as.addAttribute(TextAttribute.FONT, font);
            as.addAttribute(TextAttribute.TRACKING, outlineWidth + 2.0);
            as.addAttribute(TextAttribute.KERNING, TextAttribute.KERNING_ON);
            this.push();
            if (pixelated) {
                kebabGraphics.getGraphics2D().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            } else {
                kebabGraphics.getGraphics2D().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }

            // 2. Font und FontRenderContext holen
            kebabGraphics.getGraphics2D().setFont(font);
            FontRenderContext frc = kebabGraphics.getGraphics2D().getFontRenderContext();

            // 3. Text in ein Shape umwandeln
            GlyphVector gv = font.createGlyphVector(frc, as.getIterator());
            FontMetrics metrics = kebabGraphics.getGraphics2D().getFontMetrics(font);
            double textX = x + (width - metrics.stringWidth(text)) / 2;
            double textY = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
            Shape textShape = gv.getOutline((float) textX, (float) textY);

            kebabGraphics.getGraphics2D().setColor(outline);
            kebabGraphics.getGraphics2D().setStroke(new BasicStroke((float) outlineWidth)); // Dicke der Kontur
            kebabGraphics.getGraphics2D().draw(textShape);

            kebabGraphics.getGraphics2D().setColor(color);
            kebabGraphics.getGraphics2D().fill(textShape);
            this.pop();
        }
    }

    /**
     * Passt die Schriftart und Größe der gezeichneten Texte an.
     * @param fontName Der Name der Schriftart, z.B. "Arial"
     * @param style Der Style für die Schriftart, z.B. Font.BOLD
     * @param size Die Größe der Schrift
     */
    public void formatText(String fontName, int style, int size){
        if (this.kebabGraphics  != null) kebabGraphics.getGraphics2D().setFont(new Font(fontName, style, size));
    }

    public void push() {
        this.kebabGraphics.push();
    }

    public void pop() {
        this.kebabGraphics.pop();
    }

    private void resetGraphics() {
        kebabGraphics.getGraphics2D().setTransform(new AffineTransform());
        kebabGraphics.getGraphics2D().setRenderingHints(new RenderingHints(null));
        kebabGraphics.getGraphics2D().setStroke(new BasicStroke());
    }

    public void drawCenteredText(String text, double x, double y, double width, double height){
        this.drawCenteredText(kebabGraphics.getGraphics2D().getFont(), text, x, y, width, height);
    }

    public void drawCenteredText(Font font, String text, double x, double y, double width, double height){
        FontMetrics metrics = kebabGraphics.getGraphics2D().getFontMetrics(font);
        double textX = x + (width - metrics.stringWidth(text)) / 2;
        double textY = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
        kebabGraphics.getGraphics2D().setFont(font);
        this.drawText(text, textX, textY);
    }

    public int getWindowX() {
        return this.kebabGraphics.getGraphics2D().getDeviceConfiguration().getBounds().x;
    }

    public int getWindowY() {
        return this.kebabGraphics.getGraphics2D().getDeviceConfiguration().getBounds().y;
    }

    public int getWindowWidth() {
        return this.kebabGraphics.getGraphics2D().getDeviceConfiguration().getBounds().width;
    }

    public int getWindowHeight() {
        return this.kebabGraphics.getGraphics2D().getDeviceConfiguration().getBounds().height;
    }

    // Window Height without title bar -> Content height
    public int getContentHeight() {
        return DrawTool.kebabGraphics.getGraphics2D().getDeviceConfiguration().getBounds().height - 29;
    }

    public static int getX() {
        return DrawTool.kebabGraphics.getGraphics2D().getDeviceConfiguration().getBounds().x;
    }

    public int getY() {
        return DrawTool.kebabGraphics.getGraphics2D().getDeviceConfiguration().getBounds().y;
    }

    public int getWidth() {
        return DrawTool.kebabGraphics.getGraphics2D().getDeviceConfiguration().getBounds().width;
    }

    public int getHeight() {
        return DrawTool.kebabGraphics.getGraphics2D().getDeviceConfiguration().getBounds().height;
    }

    public double getFontWidth(String text) {
        return this.getFontWidth(kebabGraphics.getGraphics2D().getFont(), text);
    }

    public double getFontWidth(Font font, String text) {
        return kebabGraphics.getGraphics2D().getFontMetrics(font).stringWidth(text);
    }

    public double getFontHeight() {
        return this.getFontHeight(kebabGraphics.getGraphics2D().getFont());
    }

    public double getFontHeight(Font font) {
        return kebabGraphics.getGraphics2D().getFontMetrics(font).getDescent();
    }

    /**
     * Spezifiziert das zu verwendende Grafikobjekt von Java und
     * das Objekt in dem gezeichnet wird.
     * Bitte nicht verwenden.
     * @param g2d Das java-interne Grafikobjekt des Programmfensters
     */
    public void setGraphics2D(Graphics2D g2d, JComponent parent){
        this.kebabGraphics = new KebabGraphics(g2d);
        kebabGraphics.getGraphics2D().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        kebabGraphics.getGraphics2D().setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        this.parent = parent;
    }

    public KebabGraphics getKebabGraphics() {
        return this.kebabGraphics;
    }

    public Graphics2D getGraphics2D(){
        return this.kebabGraphics.getGraphics2D();
    }

    public JComponent getParent(){
        return parent;
    }
}
