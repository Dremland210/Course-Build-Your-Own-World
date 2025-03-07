package core;
import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;
import java.util.Random;

public class Hallway {
    private int startX, startY;
    private int endX, endY;
    private TETile[][] world;
    int worldWidth, worldHeight;
    private boolean startHorizontal;

    public Hallway(TETile[][] world, int worldWidth, int worldHeight, Random random, Point start, Point end) {
        this.world = world;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.startX = start.x;
        this.startY = start.y;
        this.endX = end.x;
        this.endY = end.y;

        this.startHorizontal = RandomUtils.bernoulli(random);
        createHallway(startHorizontal);
    }
    /*public Hallway(TETile[][] world, Random random, core.Point centerA, core.Point centerB) {
    }*/

    private void createHallway(boolean startHorizontal) {
        int midX, midY;

        if (startHorizontal) {
            midX = endX;
            midY = startY;
        } else {
            midX = startX;
            midY = endY;
        }
        for (int x = Math.min(startX, midX); x <= Math.max(startX, midX); x++) {
            if (x >= 0 && x < world.length && startY >= 0 && startY < world[x].length) {
                world[x][startY] = Tileset.FLOOR;
            }
        }
        for (int y = Math.min(startY, midY); y <= Math.max(startY, midY); y++) {
            if (endX >= 0 && endX < world.length && y >= 0 && y < world[endX].length) {
                world[endX][y] = Tileset.FLOOR;
            }
        }
        addWallsAroundHallway();
    }
    public int getStartX() {
        return startX; }
    public int getStartY() {
        return startY; }
    public int getEndX() {
        return endX; }
    public int getEndY() {
        return endY; }
    public boolean isWithin(int x, int y) {
        if (y == startY && x >= Math.min(startX, endX) && x <= Math.max(startX, endX)) {
            return true;
        }
        if (x == endX && y >= Math.min(startY, endY) && y <= Math.max(startY, endY)) {
            return true;
        }

        return false;
    }
    private static class Point {
        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    private void addWallsAroundHallway() {
        if (startX != endX) {
            int minY = Math.min(startY, endY);
            int maxY = Math.max(startY, endY);
            for (int x = Math.min(startX, endX); x <= Math.max(startX, endX); x++) {
                if (minY - 1 >= 0 && world[x][minY - 1] == Tileset.NOTHING) {
                    world[x][minY - 1] = Tileset.WALL;
                }
                if (maxY + 1 < worldHeight && world[x][maxY + 1] == Tileset.NOTHING) {
                    world[x][maxY + 1] = Tileset.WALL;
                }
            }
        }

        if (startY != endY) {
            int minX = Math.min(startX, endX);
            int maxX = Math.max(startX, endX);
            for (int y = Math.min(startY, endY); y <= Math.max(startY, endY); y++) {
                if (minX - 1 >= 0 && world[minX - 1][y] == Tileset.NOTHING) {
                    world[minX - 1][y] = Tileset.WALL;
                }
                if (maxX + 1 < worldWidth && world[maxX + 1][y] == Tileset.NOTHING) {
                    world[maxX + 1][y] = Tileset.WALL;
                }
            }
        }
        if (startX != endX && startY != endY) {
            addCornerWalls(startX, startY);
            addCornerWalls(endX, endY);
        }
    }

    private void addCornerWalls(int x, int y) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < worldWidth && ny >= 0 && ny < worldHeight && world[nx][ny] == Tileset.NOTHING) {
                    world[nx][ny] = Tileset.WALL;
                }
            }
        }
    }
    private void addWallIfEmpty(int x, int y) {
        if (x >= 0 && x < worldWidth && y >= 0 && y < worldHeight && world[x][y] == Tileset.NOTHING) {
            world[x][y] = Tileset.WALL; // Replace with your wall tile
        }
    }
}
