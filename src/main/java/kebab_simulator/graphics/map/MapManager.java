package kebab_simulator.graphics.map;

import KAGO_framework.view.DrawTool;
import com.google.gson.Gson;
import kebab_simulator.Config;
import kebab_simulator.graphics.IOrder;
import kebab_simulator.graphics.map.spawner.FridgeSpawner;
import kebab_simulator.graphics.map.spawner.OvenSpawner;
import kebab_simulator.model.scene.GameScene;
import kebab_simulator.physics.BodyType;
import kebab_simulator.physics.Collider;
import kebab_simulator.physics.colliders.ColliderCircle;
import kebab_simulator.physics.colliders.ColliderPolygon;
import kebab_simulator.physics.colliders.ColliderRectangle;
import kebab_simulator.utils.misc.Vec2;
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
    private final HashMap<Map.Tileset, Batch> batches;
    private final List<String> staticLayers;
    private final List<Quad> staticQuads;

    private MapManager(String fileName, List<String> staticLayers) {
        this.staticLayers = staticLayers;
        InputStream fileStream = getClass().getResourceAsStream("/graphic" + fileName);
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
                if (layer.getChunks() != null && !layer.getChunks().isEmpty()) {
                    for (Map.Chunk chunk : layer.getChunks()) {
                        this.loadChunk(layer, chunk);
                    }
                }
            } else if (layer.getType().equals("objectgroup")) {
                for (Map.ObjectCollider o : layer.getObjects()) {
                    if (!o.isVisible()) continue;
                    Collider collider = null;

                    if (o.getPolygon() != null) {
                        Vec2[] vertices = new Vec2[o.getPolygon().size()];
                        for (int i = 0; i < vertices.length; i++) {
                            var v = o.getPolygon().get(i);
                            vertices[i] = new Vec2(v.getX(), v.getY());
                        }
                        collider = new ColliderPolygon(String.format("polygon-%s-%d", layer.getName(), layer.getObjects().indexOf(o)), BodyType.STATIC, o.getX(), o.getY(), vertices);
                        if (layer.getName().equals("sensors")) collider.setSensor(true);
                        collider.setColliderClass("map");

                    } else if (o.isEllipse()) {
                        var radius = o.getWidth() / 2;
                        collider = new ColliderCircle(
                            String.format("circle-%s-%d", layer.getName(), layer.getObjects().indexOf(o)),
                            BodyType.STATIC, o.getX(), o.getY(), radius
                        );
                        if (layer.getName().equals("sensors")) collider.setSensor(true);
                        collider.setColliderClass("map");

                    } else {
                        collider = new ColliderRectangle(String.format("rectangle-%s-%d", layer.getName(), layer.getObjects().indexOf(o)), BodyType.STATIC, o.getX(), o.getY(), o.getWidth(), o.getHeight());
                        if (layer.getName().equals("sensors")) collider.setSensor(true);
                        collider.setColliderClass("map");
                    }

                    if (layer.getName().equals("spawner")) {
                        String[] args = o.getName().split("_");
                        switch (args[0]) {
                            case "oven": {
                                GameScene.getInstance().getRenderer().register(new OvenSpawner(o.getName(), collider));
                                break;
                            }
                            case "fridge": {
                                var type = args[1].equals("meat") ? FridgeSpawner.FridgeType.MEAT_FRIDGE : FridgeSpawner.FridgeType.VEGETABLES_FRIDGE;
                                GameScene.getInstance().getRenderer().register(new FridgeSpawner(type, o.getName(), collider));
                                break;
                            }
                        }

                    } else if (layer.getName().equals("sensors")) {
                        var spawner = ObjectSpawner.fetchById(o.getName());
                        if (spawner != null) {
                            spawner.setSensorCollider(collider);

                        } else {
                            ObjectSpawner.mapSensor(o.getName(), collider);
                        }
                    }
                }
            }
        }
    }

    private void loadChunk(Map.Layer layer, Map.Chunk chunk) {
        int chunkX = chunk.getX();
        int chunkY = chunk.getY();

        for (int y = 0; y < chunk.getHeight(); y++) {
            for (int x = 0; x < chunk.getWidth(); x++) {
                int index = y * chunk.getWidth() + x;
                int gid = chunk.getData().get(index);
                if (gid > 0) {
                    Map.Tileset currentTileset = this.findTilesetForGid(gid);
                    if (currentTileset == null) continue;
                    if (!this.batches.containsKey(currentTileset)) {
                        try {
                            String path = currentTileset.getImage().replace("sprites", "/graphic/map/sprites").toString();
                            BufferedImage image = ImageIO.read(getClass().getResource(path));
                            currentTileset.setPath(path);
                            this.batches.put(currentTileset, new Batch(
                                    image, currentTileset.getImage(), currentTileset, layer
                            ));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    double tilesInX = currentTileset.getImageWidth() / currentTileset.getTileWidth();
                    double offsetGid = (gid - currentTileset.getFirstGid());

                    double tileX = Math.floor((chunkX + x) * currentTileset.getTileWidth());
                    double tileY = Math.floor((chunkY + y) * currentTileset.getTileHeight());

                    int localTileId = gid - currentTileset.getFirstGid();
                    //BufferedImage tileImage = getAnimatedTileImage(currentTileset, image, localTileId, (int) System.currentTimeMillis());


                    Quad quad = new Quad(
                            this.batches.get(currentTileset),
                            gid,
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
    }

    protected Map.Tile getTile(Map.Tileset tileset, int tileId) {
        if (tileset.getTiles() == null) return null;

        return tileset.getTiles().stream().filter(t -> t.getId() == tileId).findFirst().orElse(null);
    }

    protected boolean isTileAnimated(Map.Tile tile) {
        return tile.getAnimation() != null;
    }

    private BufferedImage getAnimatedTileImage(Map.Tileset tileset, BufferedImage image, int finalTileId, int elapsedTime) {
        if (tileset.getTiles() == null) return null;

        Map.Tile tile = tileset.getTiles().stream()
                .filter(t -> finalTileId == t.getId())
                .findFirst()
                .orElse(null);

        int tileId = finalTileId;

        if (!this.isTileAnimated(tile)) return null;

        if (tile != null && tile.getAnimation() != null) {
            List<Map.TileAnimationFrame> frames = tile.getAnimation();
            int totalDuration = frames.stream().mapToInt(f -> f.getDuration()).sum();
            int currentTime = elapsedTime % totalDuration;
            int accumulatedTime = 0;

            for (Map.TileAnimationFrame frame : frames) {
                accumulatedTime += frame.getDuration();
                if (currentTime < accumulatedTime) {
                    tileId = frame.getTileId();
                    break;
                }
            }
        } else {
            return null;
        }

        int tilesPerRow = tileset.getImageWidth() / tileset.getTileWidth();
        int tileX = (tileId % tilesPerRow) * tileset.getTileWidth();
        int tileY = (tileId / tilesPerRow) * tileset.getTileHeight();

        return image.getSubimage(tileX, tileY, tileset.getTileWidth(), tileset.getTileHeight());
    }

    private Map.Tileset findTilesetForGid(int gid) {
        for (Map.Tileset tileset : this.map.getTilesets()) {
            if (gid >= tileset.getFirstGid() &&
                    gid < tileset.getFirstGid() + tileset.getTileCount()) {
                return tileset;
            }
        }
        return null;
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
        if (quad.getX() >= GameScene.getInstance().getCameraController().getX() / GameScene.getInstance().getCameraController().getZoom() - quadSize && quad.getY() >= GameScene.getInstance().getCameraController().getY() / GameScene.getInstance().getCameraController().getZoom() - quadSize) {
            if (quad.getX() + quad.getWidth() <= (GameScene.getInstance().getCameraController().getX() + Config.WINDOW_WIDTH) / GameScene.getInstance().getCameraController().getZoom() + quadSize &&
                    quad.getY() + quad.getHeight() <= (GameScene.getInstance().getCameraController().getY() + Config.WINDOW_HEIGHT) / GameScene.getInstance().getCameraController().getZoom() + quadSize) {
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
        private int tileId;
        private Map.Tile tile;
        private int gid;
        private double x;
        private double y;
        private double quadX;
        private double quadY;
        private double width;
        private double height;

        public Quad(Batch batch, int gid, double x, double y, int quadX, int quadY, int width, int height) {
            this.batch = batch;
            this.quadImage = batch.getImage().getSubimage(quadX, quadY, width, height);
            this.gid = gid;
            this.tileId = gid - batch.getTileset().getFirstGid();
            this.tile = getTile(batch.getTileset(), gid - batch.getTileset().getFirstGid());
            this.x = x;
            this.y = y;
            this.quadX = quadX;
            this.quadY = quadY;
            this.width = width;
            this.height = height;
        }

        public Quad(Batch batch, int gid, double x, double y, BufferedImage tileImage, int width, int height) {
            this.batch = batch;
            this.quadImage = tileImage;
            this.gid = gid;
            this.tileId = gid - batch.getTileset().getFirstGid();
            this.tile = getTile(batch.getTileset(), gid - batch.getTileset().getFirstGid());
            this.x = x;
            this.y = y;
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
            if (this.tile != null && isTileAnimated(this.tile)) {
                return getAnimatedTileImage(this.batch.getTileset(), this.batch.getImage(), this.tileId, (int) System.currentTimeMillis());
            }

            return this.quadImage;
        }

        public int getGid() {
            return this.gid;
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
