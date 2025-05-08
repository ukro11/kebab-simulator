package kebab_simulator.utils;

public class MathUtils {

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
}
