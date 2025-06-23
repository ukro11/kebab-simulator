package kebab_simulator.model.entity.impl;

import kebab_simulator.physics.Collider;

import java.util.concurrent.CopyOnWriteArrayList;

public interface EntityItemLocation<T extends EntityItem> {
    Collider getCollider();
    default double getRotation() {
        return 0;
    }

    T nextToRemove();
    CopyOnWriteArrayList<T> getItems();
    void addItem(T item);
    void removeItem(T item);
    void setItems(CopyOnWriteArrayList<T> items);
}
