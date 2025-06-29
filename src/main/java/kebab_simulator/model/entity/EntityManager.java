package kebab_simulator.model.entity;

import kebab_simulator.Wrapper;
import kebab_simulator.graphics.spawner.ObjectSpawner;
import kebab_simulator.graphics.spawner.table.TableItemIntegration;
import kebab_simulator.model.entity.impl.food.*;
import kebab_simulator.model.entity.impl.item.EntityPan;
import kebab_simulator.model.entity.impl.item.EntityPlate;
import kebab_simulator.model.entity.impl.player.EntityPlayer;
import kebab_simulator.physics.BodyType;
import kebab_simulator.physics.colliders.ColliderPolygon;
import kebab_simulator.physics.colliders.ColliderRectangle;

import java.util.HashMap;
import java.util.Map;

public class EntityManager {

    private final Map<String, Entity> entities = new HashMap<>();

    public EntityPlayer spawnPlayer(String id, double x, double y) {
        ColliderPolygon collider = ColliderPolygon.createCIPolygon(id, BodyType.DYNAMIC, x, y, 20, 14, 4.5);
        collider.setColliderClass("entity_player");
        EntityPlayer player = new EntityPlayer(collider, -16, -52, 32, 64);
        collider.setEntity(player);
        ObjectSpawner.objects.forEach(obj -> obj.onRegisterPlayer(player));
        this.registerEntity(player);
        return player;
    }

    public EntityMeat spawnMeat() {
        EntityMeat meat = new EntityMeat();
        this.registerEntity(meat);
        return meat;
    }

    public EntityCabbage spawnCabbage() {
        EntityCabbage cabbage = new EntityCabbage();
        this.registerEntity(cabbage);
        return cabbage;
    }

    public EntityBread spawnBread() {
        EntityBread bread = new EntityBread();
        this.registerEntity(bread);
        return bread;
    }

    public EntityTomato spawnTomato() {
        EntityTomato tomato = new EntityTomato();
        this.registerEntity(tomato);
        return tomato;
    }

    public EntityOnion spawnOnion() {
        EntityOnion onion = new EntityOnion();
        this.registerEntity(onion);
        return onion;
    }

    public void spawnPlate() {
        EntityPlate plate = new EntityPlate(new ColliderRectangle(BodyType.DYNAMIC, 0, 0, 32, 32));
        var tablePlate1 = ((TableItemIntegration) ObjectSpawner.fetchById("table_normal_9"));
        var tablePlate2 = ((TableItemIntegration) ObjectSpawner.fetchById("table_normal_10"));

        if (((TableItemIntegration) tablePlate1).getItems().isEmpty()) {
            plate.getBody().setX(tablePlate1.getCollider().getX());
            plate.getBody().setY(tablePlate1.getCollider().getY());
            tablePlate1.addItem(plate);
            Wrapper.getEntityManager().registerEntity(plate);

        } else if (((TableItemIntegration) tablePlate2).getItems().isEmpty()) {
            plate.getBody().setX(tablePlate2.getCollider().getX());
            plate.getBody().setY(tablePlate2.getCollider().getY());
            tablePlate2.addItem(plate);
            Wrapper.getEntityManager().registerEntity(plate);
        }
    }

    public void spawnPan() {
        EntityPan plate = new EntityPan(new ColliderRectangle(BodyType.DYNAMIC, 0, 0, 32, 32));
        var tablePlate1 = ((TableItemIntegration) ObjectSpawner.fetchById("table_cooker_1"));
        var tablePlate2 = ((TableItemIntegration) ObjectSpawner.fetchById("table_cooker_2"));

        if (((TableItemIntegration) tablePlate1).getItems().isEmpty()) {
            plate.getBody().setX(tablePlate1.getCollider().getX());
            plate.getBody().setY(tablePlate1.getCollider().getY());
            tablePlate1.addItem(plate);
            Wrapper.getEntityManager().registerEntity(plate);

        } else if (((TableItemIntegration) tablePlate2).getItems().isEmpty()) {
            plate.getBody().setX(tablePlate2.getCollider().getX());
            plate.getBody().setY(tablePlate2.getCollider().getY());
            tablePlate2.addItem(plate);
            Wrapper.getEntityManager().registerEntity(plate);
        }
    }

    public void registerEntity(Entity entity) {
        if (entity != null) {
            this.entities.put(entity.getId(), entity);
        }
    }

    public EntityFood unregister(Entity entity) {
        if (entity != null) {
            this.entities.remove(entity.getId());
        }
        return null;
    }


    public Map<String, Entity> getEntities() {
        return this.entities;
    }
}
