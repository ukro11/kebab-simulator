package kebab_simulator.test;

import KAGO_framework.control.Drawable;
import KAGO_framework.view.DrawTool;
import kebab_simulator.model.scene.GameScene;
import kebab_simulator.physics.BodyType;
import kebab_simulator.physics.Collider;
import kebab_simulator.physics.colliders.ColliderCircle;
import kebab_simulator.physics.colliders.ColliderPolygon;
import kebab_simulator.utils.misc.Vec2;

public class ColliderTest extends Test {

    private static ColliderTest instance = new ColliderTest();

    private ColliderTest() {
        super("Collider Test");
    }

    public static ColliderTest getInstance() {
        return ColliderTest.instance;
    }

    public void test1(double firstPolygonY, double secondPolygonY, Vec2 velocity) {
        this.startTest("Testing collision between 2 polygon colliders");

        Collider polygonFirst = new ColliderPolygon(
                "test1",
                BodyType.STATIC,
                145,
                firstPolygonY,
                new Vec2[] {
                    new Vec2(-60, 0),
                    new Vec2(-60, 10),
                    new Vec2(0, 10),
                    new Vec2(0, -30),
                    new Vec2(-10,-30),
                    new Vec2(-10,0),
                }
        );
        polygonFirst.onCollision((event) -> logger.info("SECOND: {}", event.getState()));

        ColliderPolygon polygonSecond = new ColliderPolygon(
                "test2",
                BodyType.DYNAMIC,
                120,
                secondPolygonY,
                new Vec2[] {
                    new Vec2(-60, 20),
                    new Vec2(-20, 30),
                    new Vec2(0, 0),
                    new Vec2(-20, 0),
                    new Vec2(-20,-10),
                });
        polygonSecond.setLinearVelocity(velocity.x, velocity.y);

        ColliderPolygon polygonThird = new ColliderPolygon(
                "test3",
                BodyType.DYNAMIC,
                120,
                secondPolygonY,
                new Vec2[] {
                        new Vec2(0, 0),
                        new Vec2(20, 0),
                        new Vec2(20, 20),
                        new Vec2(0, 20)
                });
        polygonThird.setLinearVelocity(velocity.x, velocity.y);

        GameScene.getInstance().getDrawables().add(new Drawable() {
            @Override
            public void draw(DrawTool drawTool) {
                polygonFirst.drawHitbox(drawTool);
                polygonSecond.drawHitbox(drawTool);
            }
            @Override
            public void update(double dt) {}
        });
    }

    public void test2(double firstPolygonY, double secondCircleY, Vec2 polygonVelocity, Vec2 circleVelocity) {
        this.startTest("Testing collision between a polygon and a circle colliders");

        Collider polygon = new ColliderPolygon(
                "polygon",
                BodyType.STATIC,
                85,
                firstPolygonY,
                new Vec2[] {
                        new Vec2(0, 20),
                        new Vec2(35, 30),
                        new Vec2(55, 0),
                }
        );
        polygon.onCollision((e) -> logger.info("COLLISION: {}", e));
        polygon.setLinearVelocity(polygonVelocity.x, polygonVelocity.y);

        ColliderCircle circle = new ColliderCircle(
                "circle",
                BodyType.DYNAMIC,
                110,
                secondCircleY,
                10
        );
        circle.setLinearVelocity(circleVelocity.x, circleVelocity.y);

        GameScene.getInstance().getDrawables().add(new Drawable() {
            @Override
            public void draw(DrawTool drawTool) {
                polygon.drawHitbox(drawTool);
                circle.drawHitbox(drawTool);
            }
            @Override
            public void update(double dt) {}
        });
    }

    public void test3(double firstCircleY, double secondCircleY, Vec2 velocity) {
        this.startTest("Testing collision between two circle colliders");

        ColliderCircle circleFirst = new ColliderCircle(
                "circle1",
                BodyType.STATIC,
                100,
                firstCircleY,
                20
        );

        ColliderCircle circleSecond = new ColliderCircle(
                "circle2",
                BodyType.DYNAMIC,
                95,
                secondCircleY,
                10
        );
        circleSecond.setLinearVelocity(velocity.x, velocity.y);

        GameScene.getInstance().getDrawables().add(new Drawable() {
            @Override
            public void draw(DrawTool drawTool) {
                circleFirst.drawHitbox(drawTool);
                circleSecond.drawHitbox(drawTool);
            }
            @Override
            public void update(double dt) {}
        });
    }

    public void test4(double circleY, double polygonY, Vec2 circleVelocity, Vec2 polygonVelocity) {
        this.startTest("Testing collision between a circle and a polygon colliders");

        ColliderCircle circle = new ColliderCircle(
                "circle",
                BodyType.DYNAMIC,
                100,
                circleY,
                20
        );
        circle.setLinearVelocity(circleVelocity.x, circleVelocity.y);

        ColliderPolygon polygon = new ColliderPolygon(
            "polygon",
            BodyType.STATIC,
            100,
            100,
            new Vec2[] {
                new Vec2(0, 0),
                new Vec2(10, 20),
                new Vec2(20, 10)
            });
        /*ColliderPolygon polygon = new ColliderRectangle(
                "polygon",
                BodyType.STATIC,
                90,
                polygonY,
                20,
                20
                );*/
        polygon.setLinearVelocity(polygonVelocity.x, polygonVelocity.y);

        GameScene.getInstance().getDrawables().add(new Drawable() {
            @Override
            public void draw(DrawTool drawTool) {
                circle.drawHitbox(drawTool);
                polygon.drawHitbox(drawTool);
            }
            @Override
            public void update(double dt) {}
        });
    }

    public void test5() {
        this.startTest("Deleting the dynamic collider when its colliding with the static one");
    }
}
