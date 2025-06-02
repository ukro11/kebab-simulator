package kebab_simulator.animation;

import com.google.common.collect.Range;

import java.util.concurrent.CopyOnWriteArrayList;

public interface IAnimationState {
    int getRowIndex();
    Range<Integer> getColumnRange();
    int getFrames();
    double getDuration();
    boolean isLoop();
    boolean isReverse();

    static <T extends Enum<T> & IAnimationState> CopyOnWriteArrayList<T> fetch(Class<T> enumClass, int row, int column) {
        if (!enumClass.isEnum()) return new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<T> found = new CopyOnWriteArrayList<>();
        for (var state : enumClass.getEnumConstants()) {
            if (state.getRowIndex() == row && state.getColumnRange().contains(column)) {
                found.add(state);
            }
        }
        return found;
    }
}
