package core;
import tileengine.TETile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import tileengine.Tileset;
import utils.RandomUtils;

public class Room {
    private int x, y;
    private int width, height;
    private TETile[][] world;
    public Room(TETile[][] world, Random random) {
        this.world = world;
        int minWidth = 4;
        int maxWidth = 8;
        int minHeight = 4;
        int maxHeight = 8;
        this.width = RandomUtils.uniform(random, minWidth, maxWidth);
        this.height = RandomUtils.uniform(random, minHeight, maxHeight);
        this.x = RandomUtils.uniform(random, 0, world.length - width);
        this.y = RandomUtils.uniform(random, 0, world[0].length - height);
    }

    public Room(TETile[][] world, Random random, int maxWidth, int maxHeight) {
        this.world = world;
        int minWidth = 7;
        int minHeight = 7;
        maxWidth = Math.min(maxWidth, world.length - 3);
        maxHeight = Math.min(maxHeight, world[0].length - 3);

        this.width = RandomUtils.uniform(random, minWidth, maxWidth + 1);
        this.height = RandomUtils.uniform(random, minHeight, maxHeight + 1);

        this.x = RandomUtils.uniform(random,  0 + 1, world.length - this.width - 1);
        this.y = RandomUtils.uniform(random, 0 + 1, world[0].length - this.height - 1);

        //System.out.println("Creating room at (" + x + ", " + y + ") with dimensions " + width + "x" + height);
    }

    public void createRoom() {
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                world[i][j] = Tileset.FLOOR;
            }
        }
        for (int i = x - 1; i <= x + width; i++) {
            if (i >= 0 && i < world.length) {
                if (y - 1 >= 0) world[i][y - 1] = Tileset.WALL;
                if (y + height < world[0].length) world[i][y + height] = Tileset.WALL;
            }
        }
        for (int j = y; j < y + height; j++) {
            if (x - 1 >= 0) world[x - 1][j] = Tileset.WALL;
            if (x + width < world.length) world[x + width][j] = Tileset.WALL;
        }
    }
    private List<Room> connectedRooms = new ArrayList<>();

    public void connectTo(Room other) {
        connectedRooms.add(other);
        other.connectedRooms.add(this);
    }

    public boolean isConnected(Room other) {
        return connectedRooms.contains(other);
    }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public Point getCenter() {
        return new Point(x + width / 2, y + height / 2);
    }
    public double distanceTo(Room other) {
        Point centerOther = other.getCenter();
        return Math.sqrt(Math.pow(this.getCenter().x - centerOther.x, 2) + Math.pow(this.getCenter().y - centerOther.y, 2));
    }
    public Point getEntrancePoint(Random random) {
        int side = random.nextInt(4);
        switch (side) {
            case 0:
                return new Point(x + random.nextInt(width), y + height - 1);
            case 1:
                return new Point(x + random.nextInt(width), y);
            case 2:
                return new Point(x, y + random.nextInt(height));
            case 3:
                return new Point(x + width - 1, y + random.nextInt(height));
            default:
                return new Point(x + width / 2, y + height / 2); // Fallback to center
        }
    }
}

