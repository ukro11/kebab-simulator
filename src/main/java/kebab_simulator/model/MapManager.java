package kebab_simulator.model;

import KAGO_framework.view.DrawTool;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class Tile {
    private int id;

    public int getId() {
        return id;
    }
}

/*
"columns":16,
"firstgid":1169,
"image":"..\/..\/..\/..\/..\/..\/..\/..\/Klasse 10\/Projektarbeit\/love2d-project\/src\/assets\/sprites\/gamemap\/pixel-top-down\/TX Props.png",
"imageheight":512,
"imagewidth":512,
"margin":0,
"name":"TX Props",
"spacing":0,
"tilecount":256,
"tileheight":32,
"tilewidth":32
*/

class Tileset {
    @SerializedName("firstgid")
    private int firstGid;
    private String image;
    @SerializedName("imagewidth")
    private int imageWidth;
    @SerializedName("imageheight")
    private int imageHeight;
    @SerializedName("tilewidth")
    private int tileWidth;
    @SerializedName("tileheight")
    private int tileHeight;
    @SerializedName("tilecount")
    private int tileCount;
    private List<Tile> tiles;

    private String path;

    public int getFirstGid() {
        return firstGid;
    }

    public String getImage() {
        return image;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getTileCount() {
        return tileCount;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

class Layer {
    private String name;
    private String type;
    private int width;
    private int height;
    private List<Integer> data;
    private List<Object> objects;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Integer> getData() {
        return data;
    }

    public List<Object> getObjects() {
        return objects;
    }
}

class Map {
    private int width;
    private int height;
    private List<Tileset> tilesets;
    private List<Layer> layers;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Tileset> getTilesets() {
        return tilesets;
    }

    public List<Layer> getLayers() {
        return layers;
    }
}

public class MapManager {

    private Logger logger = LoggerFactory.getLogger(MapManager.class);

    private Map map;
    private final HashMap<Tileset, Batch> batches;
    private final List<Quad> quads;

    private MapManager(String fileName) {
        InputStream fileStream = getClass().getResourceAsStream("/graphic/map/" + fileName);
        if (fileStream == null) throw new NullPointerException("The map you want to import does not exist");
        Gson gson = new Gson();
        this.map = gson.fromJson(new InputStreamReader(fileStream, StandardCharsets.UTF_8), Map.class);
        this.batches = new HashMap<>();
        this.quads = new ArrayList<>();
        this.load();
    }

    private void load() {
        for (Layer layer : this.map.getLayers()) {
            if (layer.getType().equals("tilelayer")) {
                for (int y = 0; y < layer.getHeight() - 1; y++) {
                    for (int x = 0; x < layer.getWidth() - 1; x++) {
                        int index = y * layer.getWidth() + x + 1;
                        int gid = layer.getData().get(index);
                        if (gid > 0) {
                            Tileset currentTileset = null;
                            for (Tileset tileset : this.map.getTilesets()) {
                                if (tileset.getTiles() != null && tileset.getTiles().size() > 0) {
                                    Tile imageInfo = null;

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
                            this.quads.add(quad);
                        }
                    }
                }

            } else if (layer.getType().equals("objectgroup")) {
                // TODO: objectgroup
            }
        }
    }

    public static MapManager importMap(String fileName) {
        return new MapManager(fileName);
    }

    public void draw(DrawTool drawTool) {
        for (Quad quad : this.quads) {
            drawTool.drawImage(quad.getQuadImage(), quad.getX(), quad.getY());
        }
    }

    public class Batch {
        private BufferedImage image;
        private String imagePath;
        private Tileset tileset;
        private Layer layer;

        public Batch(BufferedImage image, String imagePath, Tileset tileset, Layer layer) {
            this.image = image;
            this.imagePath = imagePath;
            this.tileset = tileset;
            this.layer = layer;
        }

        public BufferedImage getImage() {
            return image;
        }

        public String getImagePath() {
            return imagePath;
        }

        public Tileset getTileset() {
            return tileset;
        }

        public Layer getLayer() {
            return layer;
        }
    }

    public class Quad {
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

        public Batch getBatch() {
            return batch;
        }

        public BufferedImage getQuadImage() {
            return quadImage;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getQuadX() {
            return quadX;
        }

        public double getQuadY() {
            return quadY;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }
    }
}
