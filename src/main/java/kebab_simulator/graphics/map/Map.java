package kebab_simulator.graphics.map;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
"columns":16,
"firstgid":1169,
"image":"/graphic/map/pixel-top-down/TX Props.png",
"imageheight":512,
"imagewidth":512,
"margin":0,
"name":"TX Props",
"spacing":0,
"tilecount":256,
"tileheight":32,
"tilewidth":32
*/

public class Map {

    private int width;
    private int height;
    private List<Tileset> tilesets;
    private List<Layer> layers;

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public List<Tileset> getTilesets() {
        return this.tilesets;
    }

    public List<Layer> getLayers() {
        return this.layers;
    }

    public class Tile {
        private int id;

        public int getId() {
            return this.id;
        }
    }

    public class Tileset {
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
            return this.firstGid;
        }

        public String getImage() {
            return this.image;
        }

        public int getImageWidth() {
            return this.imageWidth;
        }

        public int getImageHeight() {
            return this.imageHeight;
        }

        public int getTileWidth() {
            return this.tileWidth;
        }

        public int getTileHeight() {
            return this.tileHeight;
        }

        public int getTileCount() {
            return this.tileCount;
        }

        public List<Tile> getTiles() {
            return this.tiles;
        }

        public String getPath() {
            return this.path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public class Layer {
        private String name;
        private String type;
        private int width;
        private int height;
        private List<Integer> data;
        private List<ObjectCollider> objects;

        public String getName() {
            return this.name;
        }

        public String getType() {
            return this.type;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public List<Integer> getData() {
            return this.data;
        }

        public List<ObjectCollider> getObjects() {
            return this.objects;
        }
    }

    public class ObjectCollider {
        private int x;
        private int y;
        private int width;
        private int height;
        private List<Polygon> polygon;

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public List<Polygon> getPolygon() {
            return this.polygon;
        }
    }

    class Polygon {
        private int x;
        private int y;

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }
    }
}
