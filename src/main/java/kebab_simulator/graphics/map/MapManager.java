package kebab_simulator.graphics.map;

import KAGO_framework.view.DrawTool;
import com.google.gson.Gson;
import kebab_simulator.Config;
import kebab_simulator.control.CameraController;
import kebab_simulator.graphics.IOrder;
import kebab_simulator.model.scene.GameScene;
import kebab_simulator.physics.BodyType;
import kebab_simulator.physics.colliders.ColliderPolygon;
import kebab_simulator.physics.colliders.ColliderRectangle;
import kebab_simulator.utils.Vec2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

public class MapManager {

    private Logger logger = LoggerFactory.getLogger(MapManager.class);

    private Map map;
    private CameraController cameraController;
    private final HashMap<Map.Tileset, Batch> batches;
    private final List<String> staticLayers;
    private final List<Quad> staticQuads;

    private MapManager(String fileName, List<String> staticLayers) {
        this.cameraController = cameraController;
        this.staticLayers = staticLayers;
        InputStream fileStream = getClass().getResourceAsStream("/graphic/map/" + fileName);
        if (fileStream == null) throw new NullPointerException("The map you want to import does not exist");
        Gson gson = new Gson();
        this.map = gson.fromJson(new InputStreamReader(fileStream, StandardCharsets.UTF_8), Map.class);
        this.batches = new HashMap<>();
        this.staticQuads = new ArrayList<>();
        this.load();
    }

    public static MapManager importMap(String fileName, List<String> staticLayers) {
        return new MapManager(fileName, staticLayers);
    }

    private void load() {
        for (Map.Layer layer : this.map.getLayers()) {
            if (layer.getType().equals("tilelayer")) {
                for (int y = 0; y < layer.getHeight() - 1; y++) {
                    for (int x = 0; x < layer.getWidth() - 1; x++) {
                        int index = y * layer.getWidth() + x + 1;
                        int gid = layer.getData().get(index);
                        if (gid > 0) {
                            Map.Tileset currentTileset = null;
                            for (Map.Tileset tileset : this.map.getTilesets()) {
                                if (tileset.getTiles() != null && tileset.getTiles().size() > 0) {
                                    Map.Tile imageInfo = null;

                                    if (gid >= tileset.getFirstGid() && gid < tileset.getFirstGid() + tileset.getTileCount()) {
                                        imageInfo = tileset.getTiles().stream().filter(t -> {
                                            int id = gid - tileset.getFirstGid();
                                            return t.getId() == id;
                                        }).findFirst().get();
                                    }

                                    if (imageInfo != null) {
                                        // TODO
                                    }
                                } else if (gid >= tileset.getFirstGid()
                                        && gid < tileset.getFirstGid() + (tileset.getImageWidth() / tileset.getTileWidth()) * (tileset.getImageHeight() / tileset.getTileHeight())) {
                                    currentTileset = tileset;
                                    break;
                                }
                            }

                            if (currentTileset != null && !this.batches.containsKey(currentTileset)) {
                                try {
                                    BufferedImage image = ImageIO.read(getClass().getResource(currentTileset.getImage()));
                                    currentTileset.setPath(currentTileset.getImage());
                                    this.batches.put(currentTileset, new Batch(
                                            image, currentTileset.getImage(), currentTileset, layer
                                    ));

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            double tilesInX = currentTileset.getImageWidth() / currentTileset.getTileWidth();
                            double offsetGid = (gid - currentTileset.getFirstGid());

                            double tileX = Math.floor(x * currentTileset.getTileWidth());
                            double tileY = Math.floor(y * currentTileset.getTileHeight());

                            Quad quad = new Quad(
                                    this.batches.get(currentTileset),
                                    tileX,
                                    tileY,
                                    (int) (offsetGid % tilesInX * currentTileset.getTileWidth()),
                                    (int) (currentTileset.getTileHeight() * Math.floor(offsetGid / tilesInX)),
                                    currentTileset.getTileWidth(),
                                    currentTileset.getTileHeight()
                            );

                            if (this.staticLayers.contains(layer.getName())) {
                                this.staticQuads.add(quad);

                            } else {
                                GameScene.getInstance().getRenderer().register(quad);
                            }
                        }
                    }
                }

            } else if (layer.getType().equals("objectgroup") && false) {
                for (Map.ObjectCollider o : layer.getObjects()) {
                    if (o.getPolygon() != null) {
                        Vec2[] vertices = new Vec2[o.getPolygon().size()];
                        for (int i = 0; i < vertices.length; i++) {
                            var v = o.getPolygon().get(i);
                            vertices[i] = new Vec2(v.getX(), v.getY());
                        }
                        var p = new ColliderPolygon(String.format("polygon-%s-%d", layer.getName(), layer.getObjects().indexOf(o)), BodyType.STATIC, o.getX() - 32, o.getY(), vertices);
                        p.setColliderClass("map");

                    } else {
                        var r = new ColliderRectangle(String.format("rectangle-%s-%d", layer.getName(), layer.getObjects().indexOf(o)), BodyType.STATIC, o.getX() - 32, o.getY(), o.getWidth(), o.getHeight());
                        r.setColliderClass("map");
                    }
                }
            }
        }
    }

    public void draw(DrawTool drawTool) {
        Graphics gr = drawTool.getGraphics2D();
        for (Quad quad : this.staticQuads) {
            gr.drawImage(quad.getQuadImage(), (int) quad.getX(), (int) quad.getY(), (int) quad.getWidth(), (int) quad.getHeight(), null);
        }
        // TODO: OrderRenderer sort render order between quads and entities
    }

    private boolean inView(Quad quad) {
        int quadSize = 32;
        if (quad.getX() >= this.cameraController.getX() / this.cameraController.getZoom() - quadSize && quad.getY() >= this.cameraController.getY() / this.cameraController.getZoom() - quadSize) {
            if (quad.getX() + quad.getWidth() <= (this.cameraController.getX() + Config.WINDOW_WIDTH) / this.cameraController.getZoom() + quadSize &&
                    quad.getY() + quad.getHeight() <= (this.cameraController.getY() + Config.WINDOW_HEIGHT) / this.cameraController.getZoom() + quadSize) {
                return true;
            }
        }
        return false;
    }

    public class Batch {
        private BufferedImage image;
        private String imagePath;
        private Map.Tileset tileset;
        private Map.Layer layer;

        public Batch(BufferedImage image, String imagePath, Map.Tileset tileset, Map.Layer layer) {
            this.image = image;
            this.imagePath = imagePath;
            this.tileset = tileset;
            this.layer = layer;
        }

        public BufferedImage getImage() {
            return this.image;
        }

        public String getImagePath() {
            return this.imagePath;
        }

        public Map.Tileset getTileset() {
            return this.tileset;
        }

        public Map.Layer getLayer() {
            return this.layer;
        }
    }

    public class Quad implements IOrder {
        private Batch batch;
        private BufferedImage quadImage;
        private double x;
        private double y;
        private double quadX;
        private double quadY;
        private double width;
        private double height;

        public Quad(Batch batch, double x, double y, int quadX, int quadY, int width, int height) {
            this.batch = batch;
            this.quadImage = batch.getImage().getSubimage(quadX, quadY, width, height);
            this.x = x;
            this.y = y;
            this.quadX = quadX;
            this.quadY = quadY;
            this.width = width;
            this.height = height;
        }

        @Override
        public double zIndex() {
            return this.getY();
        }

        public Batch getBatch() {
            return this.batch;
        }

        public BufferedImage getQuadImage() {
            return this.quadImage;
        }

        public double getX() {
            return this.x;
        }


        public double getY() {
            return this.y;
        }

        @Override
        public void draw(DrawTool drawTool) {
            drawTool.getGraphics2D().drawImage(this.getQuadImage(), (int) this.getX(), (int) this.getY(), (int) this.getWidth(), (int) this.getHeight(), null);
        }

        public double getQuadX() {
            return this.quadX;
        }

        public double getQuadY() {
            return this.quadY;
        }

        public double getWidth() {
            return this.width;
        }

        public double getHeight() {
            return this.height;
        }
    }
}
