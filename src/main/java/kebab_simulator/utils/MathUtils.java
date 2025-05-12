package kebab_simulator.utils;

import kebab_simulator.physics.BodyType;
import kebab_simulator.physics.Collider;
import kebab_simulator.physics.ColliderForm;
import kebab_simulator.physics.colliders.ColliderCircle;
import kebab_simulator.physics.colliders.ColliderPolygon;
import kebab_simulator.physics.colliders.ColliderRectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class MathUtils {

    private static Logger logger = LoggerFactory.getLogger(MathUtils.class);

    /**
     * Begrenzen (clamp) eines Wertes auf ein bestimmtes Intervall.
     * Wenn der Wert kleiner als der untere Grenzwert ist, wird der untere Grenzwert zurückgegeben.
     * Wenn der Wert größer als der obere Grenzwert ist, wird der obere Grenzwert zurückgegeben.
     * Andernfalls wird der Wert selbst zurückgegeben, wenn er innerhalb des Intervalls liegt.
     *
     * @param value Der Wert, der begrenzt werden soll.
     * @param min Der minimale Grenzwert des Intervalls.
     * @param max Der maximale Grenzwert des Intervalls.
     * @return Der begrenzte Wert, der innerhalb des Intervalls [min, max] liegt.
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    public static boolean isCollided(Collider main, Collider other) {
        switch (main.getForm()) {
            case RECTANGLE -> {
                return MathUtils.isRectangleCollided((ColliderRectangle) main, other);
            }
            case CIRCLE -> {
                return MathUtils.isCircleCollided((ColliderCircle) main, other);
            }
            //case POLYGON -> MathUtils.is
            default -> {
                return false;
            }
        }
    }

    /**
     * Berechnet, ob eine Kollision vorliegt.<br>
     * <strong>Die Methode wird nur aufgerufen, wenn der Body (this) ein Rectangle ist.</strong>
     * @return {@code true}, wenn eine Kollision vorliegt.
     */
    public static boolean isRectangleCollided(ColliderRectangle main, Collider other) {
        // ! WICHTIG: Diese Methode sollte nur aufgerufen werden, wenn body (this) ein rectangle ist
        if (main.getForm() != ColliderForm.RECTANGLE) {
            throw new InvalidParameterException("To call checkCollisionRectangle(...) you need to make sure that your collider is a rectangle!");
        }
        switch (other.getForm()) {
            case RECTANGLE -> {
                boolean result = main.getX() < other.getX() + other.getWidth() &&
                        main.getX() + main.getWidth() > other.getX() &&
                        main.getY() < other.getY() + other.getHeight() &&
                        main.getY() + main.getHeight() > other.getY();
                if (result) {
                    double overlapX = Math.min(main.getX() + main.getWidth() - other.getX(), other.getX() + other.getWidth() - main.getX());
                    double overlapY = Math.min(main.getY() + main.getHeight() - other.getY(), other.getY() + other.getHeight() - main.getY());

                    if (overlapX < overlapY) {
                        if (main.getX() < other.getX()) {
                            main.setX(main.getX() - overlapX);
                        } else {
                            main.setX(main.getX() + overlapX);
                        }
                    } else {
                        if (main.getY() < other.getY()) {
                            main.setY(main.getY() - overlapY);
                        } else {
                            main.setY(main.getY() + overlapY);
                        }
                    }
                }
                return result;
            }
            case CIRCLE -> {
                return MathUtils.isRectangleCollidedWithCircle(main, (ColliderCircle) other);
            }
            case POLYGON -> {
                return MathUtils.isPolygonCollidedWithRectangle((ColliderPolygon) other, main);
            }
        }
        return false;
    }

    /**
     * Berechnet, ob eine Kollision vorliegt.<br>
     * <strong>Die Methode wird nur aufgerufen, wenn der Body (this) ein Kreis ist.</strong>
     * @return {@code true}, wenn eine Kollision vorliegt.
     */
    public static boolean isCircleCollided(ColliderCircle main, Collider other) {
        // ! WICHTIG: Diese Methode sollte nur aufgerufen werden, wenn body (this) ein rectangle ist
        if (main.getForm() != ColliderForm.CIRCLE) {
            throw new InvalidParameterException("To call checkCollisionRectangle(...) you need to make sure that your collider is a circle!");
        }
        switch (other.getForm()) {
            case CIRCLE -> {
                double dx = main.getX() - other.getX();
                double dy = main.getY() - other.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                boolean res = distance < (main.getRadius() + other.getRadius());
                if (res) {
                    double overlap = main.getRadius() + other.getRadius() - distance;
                    double offsetX = dx / distance * overlap / 2;
                    double offsetY = dy / distance * overlap / 2;
                    if (main.getType() == BodyType.DYNAMIC) {
                        main.setX(main.getX() + offsetX);
                        main.setY(main.getY() + offsetY);
                    }
                    if (other.getType() == BodyType.DYNAMIC) {
                        other.setX(other.getX() - offsetX);
                        other.setY(other.getY() - offsetY);
                    }
                }
                return res;
            }
            case RECTANGLE -> {
                return MathUtils.isRectangleCollidedWithCircle((ColliderRectangle) other, main);
            }
            case POLYGON -> {
                return MathUtils.isPolygonCollidedWithCircle((ColliderPolygon) other, main);
            }
        }
        return false;
    }

    private static List<Line2D> getEdges(Polygon polygon) {
        List<Line2D> edges = new ArrayList<>();
        for (int i = 0; i < polygon.npoints; i++) {
            int x1 = polygon.xpoints[i];
            int y1 = polygon.ypoints[i];
            int x2 = polygon.xpoints[(i + 1) % polygon.npoints];
            int y2 = polygon.ypoints[(i + 1) % polygon.npoints];
            edges.add(new Line2D.Double(x1, y1, x2, y2));
        }
        return edges;
    }

    private static boolean checkProjectionOverlap(Line2D edge, Polygon p1, Polygon p2) {
        // Berechne den Normalenvektor der Kante
        double dx = edge.getX2() - edge.getX1();
        double dy = edge.getY2() - edge.getY1();
        double nx = -dy;
        double ny = dx;

        // Normalisiere den Vektor
        double length = Math.sqrt(nx * nx + ny * ny);
        nx /= length;
        ny /= length;

        // Berechne die Projektion des ersten Polygons
        double min1 = Double.MAX_VALUE;
        double max1 = Double.MIN_VALUE;
        for (int i = 0; i < p1.npoints; i++) {
            double projection = p1.xpoints[i] * nx + p1.ypoints[i] * ny;
            min1 = Math.min(min1, projection);
            max1 = Math.max(max1, projection);
        }

        // Berechne die Projektion des zweiten Polygons
        double min2 = Double.MAX_VALUE;
        double max2 = Double.MIN_VALUE;
        for (int i = 0; i < p2.npoints; i++) {
            double projection = p2.xpoints[i] * nx + p2.ypoints[i] * ny;
            min2 = Math.min(min2, projection);
            max2 = Math.max(max2, projection);
        }

        // Überprüfe, ob die Projektionen sich überschneiden
        return max1 >= min2 && max2 >= min1;
    }

    public static boolean isPolygonCollided(ColliderPolygon main, Collider other) {
        if (main.getForm() != ColliderForm.POLYGON) {
            throw new InvalidParameterException("To call checkCollisionRectangle(...) you need to make sure that your collider is a circle!");
        }
        switch (other.getForm()) {
            case POLYGON -> {
                ColliderPolygon pOther = (ColliderPolygon) other;
                List<Line2D> edges1 = MathUtils.getEdges(main.getPolygonShape());
                List<Line2D> edges2 = MathUtils.getEdges(pOther.getPolygonShape());
                for (Line2D edge : edges1) {
                    if (!MathUtils.checkProjectionOverlap(edge, main.getPolygonShape(), pOther.getPolygonShape())) {
                        return false;
                    }
                }
                for (Line2D edge : edges2) {
                    if (!MathUtils.checkProjectionOverlap(edge, pOther.getPolygonShape(), main.getPolygonShape())) {
                        return false;
                    }
                }
                MathUtils.movePolygon(main, pOther);
                return true;
            }
            case RECTANGLE -> {
                return MathUtils.isPolygonCollidedWithRectangle(main, (ColliderRectangle) other);
            }
            case CIRCLE -> {
                return MathUtils.isPolygonCollidedWithCircle(main, (ColliderCircle) other);
            }
        }
        return false;
    }

    private static void movePolygon(ColliderPolygon cp1, Collider cp2) {
        Polygon p1 = cp1.getPolygonShape();
        Polygon p2;

        if (cp2 instanceof ColliderRectangle) {
            p2 = ((ColliderRectangle) cp2).getRectangleShape();

        } else if (cp2 instanceof ColliderPolygon) {
            p2 = ((ColliderPolygon) cp2).getPolygonShape();

        } else {
            throw new InvalidParameterException("cp2 needs to be a ColliderRectangle or a ColliderPolygon");
        }

        double minOverlapX = Double.MAX_VALUE;
        double minOverlapY = Double.MAX_VALUE;

        // Berechne die Kanten des ersten Polygons
        for (int i = 0; i < p1.npoints; i++) {
            int x1 = p1.xpoints[i];
            int y1 = p1.ypoints[i];
            int x2 = p1.xpoints[(i + 1) % p1.npoints]; // Nächster Punkt im Polygon
            int y2 = p1.ypoints[(i + 1) % p1.npoints];

            // Berechne den Abstand des aktuellen Punkts zum nächsten Punkt des zweiten Polygons
            for (int j = 0; j < p2.npoints; j++) {
                int px = p2.xpoints[j];
                int py = p2.ypoints[j];

                // Berechne den Abstand und die Überlappung in X und Y
                double dx = px - x1;
                double dy = py - y1;
                double overlapX = Math.abs(dx);
                double overlapY = Math.abs(dy);

                // Bestimme die minimalen Überlappungswerte
                minOverlapX = Math.min(minOverlapX, overlapX);
                minOverlapY = Math.min(minOverlapY, overlapY);
            }
        }

        // Berechne den Verschiebungsvektor (offset)
        double offsetX = minOverlapX;  // Verwende den minimalen Überlappungsbereich für X
        double offsetY = minOverlapY;

        if (cp1.getType() == BodyType.DYNAMIC) {
            double finalOffsetX = offsetX;
            double finalOffsetY = offsetY;
            cp1.getPolygonVertices().forEach(vert -> vert.add(finalOffsetX, finalOffsetY));
        }
        if (cp2.getType() == BodyType.DYNAMIC) {
            double finalOffsetX = offsetX;
            double finalOffsetY = offsetY;
            if (cp2 instanceof ColliderRectangle) {
                cp2.setX(cp2.getX() + offsetX);
                cp2.setY(cp2.getY() + offsetY);

            } else {
                /*MathUtils.logger.info("{} {}", offsetX, offsetY);
                for (int i = 0; i < ((ColliderPolygon) cp2).getPolygonVertices().size(); i++) {
                    var vert = ((ColliderPolygon) cp2).getPolygonVertices().get(i).sub(finalOffsetX, finalOffsetY);
                    ((ColliderPolygon) cp2).getPolygonShape().xpoints[i] = (int) vert.x;
                    ((ColliderPolygon) cp2).getPolygonShape().ypoints[i] = (int) vert.y;
                }*/
            }
        }
    }

    public static boolean isPolygonCollidedWithRectangle(ColliderPolygon polygon, ColliderRectangle rectangle) {
        List<Line2D> edges1 = MathUtils.getEdges(polygon.getPolygonShape());
        List<Line2D> edges2 = MathUtils.getEdges(rectangle.getRectangleShape());
        for (Line2D edge : edges1) {
            if (!MathUtils.checkProjectionOverlap(edge, polygon.getPolygonShape(), rectangle.getRectangleShape())) {
                return false;
            }
        }
        for (Line2D edge : edges2) {
            if (!MathUtils.checkProjectionOverlap(edge, rectangle.getRectangleShape(), polygon.getPolygonShape())) {
                return false;
            }
        }
        MathUtils.movePolygon(polygon, rectangle);
        return true;
    }

    public static boolean isPolygonCollidedWithCircle(ColliderPolygon polygon, ColliderCircle circle) {
        for (int i = 0; i < polygon.getPolygonShape().npoints; i++) {
            int px = polygon.getPolygonShape().xpoints[i];
            int py = polygon.getPolygonShape().ypoints[i];
            double distance = Point2D.distance(px, py, circle.getX(), circle.getY());
            if (distance < circle.getRadius()) {
                double overlap = circle.getRadius() - distance;
                if (polygon.getType() == BodyType.DYNAMIC) {
                    polygon.getPolygonShape().xpoints[i] += overlap;
                    polygon.getPolygonShape().ypoints[i] += overlap;
                }
                if (circle.getType() == BodyType.DYNAMIC) {
                    circle.setX(circle.getX() - overlap);
                    circle.setY(circle.getY() - overlap);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Berechnet, ob eine Kollision vorliegt zwischen einem {@code Body rectangle} und einem {@code Body circle}.
     * @return {@code true}, wenn eine Kollision vorliegt.
     */
    public static boolean isRectangleCollidedWithCircle(ColliderRectangle rectangle, ColliderCircle circle) {
        // Finde den nächstgelegenen Punkt auf dem Rechteck zum Mittelpunkt des Kreises
        double closestX = MathUtils.clamp(circle.getX(), rectangle.getX(), rectangle.getX() + rectangle.getWidth());
        double closestY = MathUtils.clamp(circle.getY(), rectangle.getY(), rectangle.getY() + rectangle.getHeight());

        // Berechne den Abstand vom Mittelpunkt des Kreises zum nächsten Punkt auf dem Rechteck
        double dx = circle.getX() - closestX;
        double dy = circle.getY() - closestY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        boolean collide = distance < circle.getRadius();

        if (collide) {
            // Berechne den Überlappungsbereich
            double overlap = circle.getRadius() - distance;

            // Verschiebe die Körper entlang der Linie zwischen dem Mittelpunkt des Kreises und dem Punkt
            double offsetX = dx / distance * overlap;
            double offsetY = dy / distance * overlap;

            if (circle.getType() == BodyType.DYNAMIC) {
                circle.setX(circle.getX() + offsetX);
                circle.setY(circle.getY() + offsetY);

            } else if (circle.getType() == BodyType.STATIC) {
                rectangle.setX(rectangle.getX() - offsetX);
                rectangle.setY(rectangle.getY() - offsetY);
            }
        }
        return collide;
    }
}
