package kebab_simulator.graphics.map.maps;

import kebab_simulator.Wrapper;
import kebab_simulator.graphics.spawner.ObjectIdResolver;
import kebab_simulator.graphics.spawner.ObjectSpawner;
import kebab_simulator.graphics.map.Map;
import kebab_simulator.graphics.spawner.table.*;
import kebab_simulator.model.entity.impl.item.EntityPlate;
import kebab_simulator.model.scene.GameScene;
import kebab_simulator.physics.Collider;

import java.util.List;

public class KitchenMap {

    private static List<String> layersWithId = List.of("table", "plate", "sensor");

    public static void loadCollider(Map.Layer layer, Map.ObjectCollider objectCollider, Collider collider) {
        if (KitchenMap.layersWithId.contains(layer.getName())) {
            ObjectIdResolver id = new ObjectIdResolver(objectCollider.getName());
            //boolean onlyOneTable = GameScene.getInstance().getRenderer().getDrawables().stream().filter(t -> t instanceof TableSpawner).collect(Collectors.toSet()).size() < 1;
            if (layer.getName().equals("table")) {
                switch (id.getSpawnerType()) {
                    case "normal", "normal-top", "normal-left-top", "normal-right-top", "normal-end", "normal-left-end", "normal-right-end": {
                        GameScene.getInstance().getRenderer().register(new TableNormalSpawner(id, collider));
                        break;
                    }
                    case "knife": {
                        GameScene.getInstance().getRenderer().register(new TableKnifeSpawner(id, collider));
                        break;
                    }
                    case "serving": {
                        GameScene.getInstance().getRenderer().register(new TableServingSpawner(id, collider));
                        break;
                    }
                    default: {
                        if (id.getSpawnerType().startsWith("storage-")) {
                            GameScene.getInstance().getRenderer().register(TableStorageSpawner.fetchStorageSpawner(id, collider));
                        }
                        break;
                    }
                }

            }

            if (layer.getName().equals("plate")) {
                EntityPlate plate = new EntityPlate(collider);
                String tableId = (String) objectCollider.getProperties().stream().filter(p -> p.getName().equals("table")).findFirst().orElse(null).getValue();
                ((TableItemIntegration) ObjectSpawner.fetchById(tableId)).addItem(plate);
                Wrapper.getEntityManager().registerEntity(plate);
            }

            if (layer.getName().equals("sensor")) {
                var spawner = ObjectSpawner.fetchById(objectCollider.getName());
                if (spawner != null) {
                    spawner.addSensorCollider(collider);

                } else {
                    ObjectSpawner.mapSensor(objectCollider.getName(), collider);
                }
                if (spawner != null) {
                    spawner.onRegisterSensor(collider);
                }
            }
        }
    }
}
