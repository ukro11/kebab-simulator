package kebab_simulator.utils;

import kebab_simulator.Config;
import kebab_simulator.physics.*;
import kebab_simulator.physics.colliders.ColliderCircle;
import kebab_simulator.physics.colliders.ColliderPolygon;
import kebab_simulator.physics.colliders.ColliderRectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
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

    public static boolean AABB(Collider main, Collider other) {
        if (main.isDestroyed() || other.isDestroyed()) return false;
        return main.computeAABB().overlaps(other.computeAABB());
    }

    public static boolean SAT(Collider main, Collider other) {
        if (main.isDestroyed() || other.isDestroyed()) return false;
        Vec2 normal = null;
        double overlap = Double.MAX_VALUE;
        ColliderCircle circle = null;
        ColliderPolygon polygon = null;
        if (circle == null) {
            if (main instanceof ColliderCircle) {
                circle = (ColliderCircle) main;
                polygon = (ColliderPolygon) other;

            } else if (other instanceof ColliderCircle) {
                circle = (ColliderCircle) other;
                polygon = (ColliderPolygon) main;
            }
        }
        List<Vec2> mainAxes = main.getAxes();
        List<Vec2> otherAxes = other.getAxes();

        if (mainAxes != null) {
            if (polygon != null && circle != null) mainAxes.add(MathUtils.getClosestPointAxis(polygon, circle));
            int size = mainAxes.size();
            for (int i = 0; i < size; i++) {
                Vec2 axis = mainAxes.get(i);
                // check for the zero vector
                if (!axis.isZero()) {
                    // project both shapes onto the axis
                    Interval intervalA = main.project(axis);
                    Interval intervalB = other.project(axis);
                    // if the intervals do not overlap then the two shapes
                    // cannot be intersecting
                    if (!intervalA.overlaps(intervalB)) {
                        // the shapes cannot be intersecting so immediately return null
                        main.setHitboxColor(Color.RED);
                        other.setHitboxColor(Color.RED);
                        return false;
                    } else {
                        // get the overlap
                        double o = intervalA.getOverlap(intervalB);
                        // check for containment
                        if (intervalA.containsExclusive(intervalB) || intervalB.containsExclusive(intervalA)) {
                            // if containment exists then get the overlap plus the distance
                            // to between the two end points that are the closest
                            double max = Math.abs(intervalA.getMax() - intervalB.getMax());
                            double min = Math.abs(intervalA.getMin() - intervalB.getMin());
                            if (max > min) {
                                // if the min differences is less than the max then we need
                                // to flip the penetration axis
                                axis.invert();
                                o += min;
                            } else {
                                o += max;
                            }
                        }
                        // if the intervals do overlap then get save the depth and axis
                        // get the magnitude of the overlap
                        // get the minimum penetration depth and axis
                        if (o < overlap) {
                            overlap = o;
                            normal = axis;
                        }
                    }
                }
            }
        }

        if (otherAxes != null) {
            if (polygon != null && circle != null) otherAxes.add(MathUtils.getClosestPointAxis(polygon, circle));
            int size = otherAxes.size();
            for (int i = 0; i < size; i++) {
                Vec2 axis = otherAxes.get(i);
                // check for the zero vector
                if (!axis.isZero()) {
                    // project both shapes onto the axis
                    Interval intervalA = main.project(axis);
                    Interval intervalB = other.project(axis);
                    // if the intervals do not overlap then the two shapes
                    // cannot be intersecting
                    if (!intervalA.overlaps(intervalB)) {
                        // the shapes cannot be intersecting so immediately return null
                        main.setHitboxColor(Color.RED);
                        other.setHitboxColor(Color.RED);
                        return false;
                    } else {
                        // get the overlap
                        double o = intervalA.getOverlap(intervalB);
                        // check for containment
                        if (intervalA.containsExclusive(intervalB) || intervalB.containsExclusive(intervalA)) {
                            // if containment exists then get the overlap plus the distance
                            // to between the two end points that are the closest
                            double max = Math.abs(intervalA.getMax() - intervalB.getMax());
                            double min = Math.abs(intervalA.getMin() - intervalB.getMin());
                            if (max > min) {
                                // if the min differences is less than the max then we need
                                // to flip the penetration axis
                                axis.invert();
                                o += min;
                            } else {
                                o += max;
                            }
                        }
                        // if the intervals do overlap then get save the depth and axis
                        // get the magnitude of the overlap
                        // get the minimum penetration depth and axis
                        if (o < overlap) {
                            overlap = o;
                            normal = axis;
                        }
                    }
                }
            }
        }

        main.setHitboxColor(Color.BLUE);
        other.setHitboxColor(Color.BLUE);
        if (main.isSensor() || other.isSensor()) {
            return true;
        }
        if (overlap != 0.0 && normal != null) {
            Vec2 c1 = main.getCenter();
            Vec2 c2 = other.getCenter();
            Vec2 cToc = c2.clone().sub(c1);
            if (cToc.dot(normal) < 0) {
                // negate the normal if its not
                normal.invert();
            }
            Vec2 collision = new Vec2((normal.x * overlap) / 2, (normal.y * overlap) / 2);
            if (main.getType() == BodyType.DYNAMIC) {
                if (main instanceof ColliderPolygon) {
                    Vec2[] vertices = ((ColliderPolygon) main).vertices;
                    for (int i = 0; i < vertices.length; i++) {
                        vertices[i].sub(collision);
                    }
                    main.setX(main.getX() - collision.x);
                    main.setY(main.getY() - collision.y);
                }
                main.getCenter().sub(collision.x, collision.y);
            }
            if (other.getType() == BodyType.DYNAMIC) {
                if (other instanceof ColliderPolygon) {
                    Vec2[] vertices = ((ColliderPolygon) other).vertices;
                    for (int i = 0; i < vertices.length; i++) {
                        vertices[i].add(collision);
                    }
                    other.setX(other.getX() + collision.x);
                    other.setY(other.getY() + collision.y);
                }
                other.getCenter().add(collision.x, collision.y);
            }
        }
        return true;
    }

    private static Vec2 getClosestPointAxis(ColliderPolygon polygon, ColliderCircle circle) {
        int index = MathUtils.getClosest(polygon.vertices, circle.getCenter());
        Vec2 vertex = polygon.vertices[index];
        Vec2 center = circle.getCenter();
        Vec2 distance = new Vec2(vertex.x - center.x, vertex.y - center.y);
        return distance.normalize();
    }

    private static int getClosest(Vec2[] vertices, Vec2 circleCenter) {
        int result = 0;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < vertices.length; i++) {
            Vec2 v = vertices[i];
            double distance = v.distance(circleCenter);
            if (distance < minDistance) {
                minDistance = distance;
                result = i;
            }
        }
        return result;
    }

    public static boolean fastCircleDetection(ColliderCircle main, ColliderCircle other) {
        if (main.isDestroyed() || other.isDestroyed()) return false;

        Vec2 ce1 = main.getCenter();
        Vec2 ce2 = other.getCenter();
        Vec2 normal = ce2.clone().sub(ce1);
        double radii = main.getRadius() + other.getRadius();
        double mag = normal.magnitudeSquared();
        if (mag < radii * radii) {
            double depth = radii - normal.magnitude();
            normal.normalize();

            main.setHitboxColor(Color.BLUE);
            other.setHitboxColor(Color.BLUE);

            Vec2 collision = new Vec2((normal.x * depth) / 2, (normal.y * depth) / 2);

            if (main.getType() == BodyType.DYNAMIC) {
                main.getCenter().sub(collision);
            }

            if (other.getType() == BodyType.DYNAMIC) {
                other.getCenter().add(collision);
            }
            return true;

        } else {
            main.setHitboxColor(Color.RED);
            other.setHitboxColor(Color.RED);
        }

        return false;
    }
}
