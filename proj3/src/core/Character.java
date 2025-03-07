package core;
import tileengine.TETile;
import tileengine.Tileset;
public class Character {
    private int x, y;
    private TETile[][] world;

    public Character(TETile[][] world, int startX, int startY) {
        this.world = world;
        this.x = startX;
        this.y = startY;
        world[x][y] = Tileset.AVATAR;
    }
    public void move(int dx, int dy) {
        int newX = x + dx;
        int newY = y + dy;

        if (isValidMove(newX, newY)) {
            world[x][y] = Tileset.FLOOR;
            x = newX;
            y = newY;
            world[x][y] = Tileset.AVATAR;
        }
    }

    private boolean isValidMove(int newX, int newY) {
        return newX >= 0 && newX < world.length && newY >= 0 && newY < world[0].length
                && !world[newX][newY].equals(Tileset.WALL);
    }
    /*public void draw(double tileSize) {
        StdDraw.setScale();
        double screenX = x * tileSize + tileSize / 2.0;
        double screenY = y * tileSize + tileSize / 2.0;
        double characterRadius = tileSize * 0.4;

        Font font = new Font("Monospaced", Font.BOLD, (int) characterRadius);
        StdDraw.setFont();

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(screenX, screenY, "@");
    }*/

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
}
