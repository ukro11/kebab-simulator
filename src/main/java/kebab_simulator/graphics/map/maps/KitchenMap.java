package kebab_simulator.graphics.map.maps;

import kebab_simulator.Wrapper;
import kebab_simulator.graphics.spawner.ObjectIdResolver;
import kebab_simulator.graphics.spawner.ObjectSpawner;
import kebab_simulator.graphics.map.Map;
import kebab_simulator.graphics.spawner.table.*;
import kebab_simulator.model.entity.impl.item.EntityPan;
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
                    case "knife": {
                        GameScene.getInstance().getRenderer().register(new TableKnifeSpawner(id, collider));
                        break;
                    }
                    case "serving": {
                        GameScene.getInstance().getRenderer().register(new TableServingSpawner(id, collider));
                        break;
                    }
                    case "cooker": {
                        GameScene.getInstance().getRenderer().register(new TableCookerSpawner(id, collider));
                        break;
                    }
                    default: {
                        if (id.getSpawnerType().startsWith("storage-")) {
                            GameScene.getInstance().getRenderer().register(TableStorageSpawner.fetchStorageSpawner(id, collider));

                        } else if (id.getSpawnerType().startsWith("normal")) {
                            GameScene.getInstance().getRenderer().register(new TableNormalSpawner(id, collider));
                        }
                        break;
                    }
                }
            }

            if (layer.getName().equals("plate")) {
                switch (id.getSpawnerType()) {
                    case "normal": {
                        EntityPlate plate = new EntityPlate(collider);
                        String tableId = (String) objectCollider.getProperties().stream().filter(p -> p.getName().equals("table")).findFirst().orElse(null).getValue();
                        ((TableItemIntegration) ObjectSpawner.fetchById(tableId)).addItem(plate);
                        Wrapper.getEntityManager().registerEntity(plate);
                        break;
                    }
                    case "pan": {
                        EntityPan pan = new EntityPan(collider);
                        String tableId = (String) objectCollider.getProperties().stream().filter(p -> p.getName().equals("table")).findFirst().orElse(null).getValue();
                        TableItemIntegration table = ((TableItemIntegration) ObjectSpawner.fetchById(tableId));
                        table.addItem(pan);
                        Wrapper.getEntityManager().registerEntity(pan);
                        break;
                    }
                }
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
