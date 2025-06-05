package kebab_simulator.graphics.map;

import kebab_simulator.graphics.map.spawner.table.TableKnifeSpawner;
import kebab_simulator.graphics.map.spawner.table.TableNormalSpawner;
import kebab_simulator.graphics.map.spawner.table.TableServingSpawner;
import kebab_simulator.model.scene.GameScene;
import kebab_simulator.physics.Collider;

public class KitchenMap {

    public static void loadCollider(Map.Layer layer, Map.ObjectCollider objectCollider, Collider collider) {
        //boolean onlyOne = GameScene.getInstance().getRenderer().getDrawables().stream().filter(t -> t instanceof TableSpawner).collect(Collectors.toSet()).size() < 1;

        if (layer.getName().equals("table")) {
            String[] args = objectCollider.getName().split("_");
            switch (args[1]) {
                case "normal", "normal-top", "normal-left-top", "normal-right-top", "normal-end", "normal-left-end", "normal-right-end": {
                    GameScene.getInstance().getRenderer().register(new TableNormalSpawner(objectCollider.getName(), collider));
                    break;
                }
                case "knife": {
                    GameScene.getInstance().getRenderer().register(new TableKnifeSpawner(objectCollider.getName(), collider));
                    break;
                }
                case "serving": {
                    GameScene.getInstance().getRenderer().register(new TableServingSpawner(objectCollider.getName(), collider));
                    break;
                }
                case "oven": {
                    //GameScene.getInstance().getRenderer().register(new OvenSpawner(o.getName(), collider));
                    break;
                }
            }

        } else if (layer.getName().equals("sensor")) {
            var spawner = ObjectSpawner.fetchById(objectCollider.getName());
            if (spawner != null) {
                spawner.addSensorCollider(collider);

            } else {
                ObjectSpawner.mapSensor(objectCollider.getName(), collider);
            }
            if (spawner != null && spawner.onRegisterSensor != null) {
                spawner.onRegisterSensor.run();
            }
        }
    }
}
