package core;
import java.awt.*;
import java.io.IOException;
import utils.FileUtils;
import edu.princeton.cs.algs4.StdDraw;
import org.antlr.v4.runtime.misc.Utils;
import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.*;
import tileengine.TERenderer;

public class World {
    private TETile[][]world;
    private int width, height;
    private Random random;
    private List<Room> rooms;
    private Character jeffrey;
    private long seed;
    private boolean colonPressed;
    private boolean lightsOn;
    private static final int LIGHT_DIST = 3;
    private static final int TILE_SIZE = 16;
    public World(int width, int height) {
        this.width = width;
        this.height = height;
        this.random = new Random();
        this.world = new TETile[width][height];
        this.rooms = new ArrayList<>();
        lightsOn = false;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    public void generate() {
        int numRooms = RandomUtils.uniform(random, 10, 15);
        int roomMaxWidth = 8;
        int roomMaxHeight = 8;

        Room largestRoom = null;
        for (int i = 0; i < numRooms; i++) {
            int attempts = 0;
            Room room;
            do {
                room = new Room(world, random, roomMaxWidth, roomMaxHeight);
                attempts++;
            } while (!isValidLocation(room.getX(), room.getY(), room.getWidth(), room.getHeight()) && attempts < 100);

            if (attempts < 100) {
                room.createRoom();
                rooms.add(room);
                if (largestRoom == null || room.getWidth() * room.getHeight() > largestRoom.getWidth() * largestRoom.getHeight()) {
                    largestRoom = room;
                }
            }
        }

        if (largestRoom != null) {
            jeffrey = new Character(this.world, largestRoom.getCenter().x, largestRoom.getCenter().y);
        }
        connectRooms();
    }

    /*private class PairRooms {
        Room room1;
        Room room2;
        double distance;

        PairRooms(Room room1, Room room2) {
            this.room1 = room1;
            this.room2 = room2;
            this.distance = distanceBetweenCenters(room1, room2);
        }
    }*/

    private void connectRooms() {
        Set<Room> connectedRooms = new HashSet<>();
        connectedRooms.add(rooms.get(0));
        while (connectedRooms.size() < rooms.size()) {
            for (Room room : rooms) {
                if (!connectedRooms.contains(room)) {
                    Room nearestRoom = findNearest(room, connectedRooms);
                    if (nearestRoom != null) {
                        connectRoomToRoom(room, nearestRoom);
                        connectedRooms.add(room);
                    }
                }
            }
        }
    }

    private Room findNearest(Room room, Set<Room> connectedRooms) {
        Room nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Room connectedRoom : connectedRooms) {
            double distance = distanceBetweenCenters(room, connectedRoom);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = connectedRoom;
            }
        }

        return nearest;
    }
    private void connectRoomToRoom(Room room1, Room room2) {
        Point entrance1 = room1.getEntrancePoint(random);
        Point entrance2 = room2.getEntrancePoint(random);
        boolean horizontalFirst = random.nextBoolean();
        Point midPoint = horizontalFirst ? new Point(entrance2.x, entrance1.y) : new Point(entrance1.x, entrance2.y);
        drawLine(entrance1, midPoint);
        drawLine(midPoint, entrance2);
    }
    private double distanceBetweenCenters(Room a, Room b) {
        Point centerA = a.getCenter();
        Point centerB = b.getCenter();
        return Math.sqrt(Math.pow(centerA.x - centerB.x, 2) + Math.pow(centerA.y - centerB.y, 2));
    }
    private void createHallway(Point start, Point end) {
        boolean horizontalFirst = random.nextBoolean();

        Point midPoint;
        if (horizontalFirst) {
            midPoint = new Point(end.x, start.y);
        } else {
            midPoint = new Point(start.x, end.y);
        }
        drawLine(start, midPoint);
        drawLine(midPoint, end);
        HallwayWalls(start, midPoint);
        HallwayWalls(midPoint, end);
    }

    private void drawLine(Point start, Point end) {
        if (start.x == end.x) {
            for (int y = Math.min(start.y, end.y); y <= Math.max(start.y, end.y); y++) {
                if (y >= 0 && y < height && start.x >= 0 && start.x < width) {
                    world[start.x][y] = Tileset.FLOOR;
                    if (start.x - 1 >= 0 && world[start.x - 1][y] == Tileset.NOTHING) {
                        world[start.x - 1][y] = Tileset.WALL;
                    }
                    if (start.x + 1 < width && world[start.x + 1][y] == Tileset.NOTHING) {
                        world[start.x + 1][y] = Tileset.WALL;
                    }
                }
            }
        } else {
            for (int x = Math.min(start.x, end.x); x <= Math.max(start.x, end.x); x++) {
                if (x >= 0 && x < width && start.y >= 0 && start.y < height) {
                    world[x][start.y] = Tileset.FLOOR;
                    if (start.y - 1 >= 0 && world[x][start.y - 1] == Tileset.NOTHING) {
                        world[x][start.y - 1] = Tileset.WALL;
                    }
                    if (start.y + 1 < height && world[x][start.y + 1] == Tileset.NOTHING) {
                        world[x][start.y + 1] = Tileset.WALL;
                    }
                }
            }
        }
    }

    private boolean isValidLocation(int x, int y, int width, int height) {
        int buffer = 1;
        for (Room room : rooms) {
            int roomMinX = room.getX() - buffer;
            int roomMaxX = room.getX() + room.getWidth() + buffer;
            int roomMinY = room.getY() - buffer;
            int roomMaxY = room.getY() + room.getHeight() + buffer;
            if (x < roomMaxX && x + width > roomMinX &&
                    y < roomMaxY && y + height > roomMinY) {
                return false;
            }
        }
        return true;
    }
    private void HallwayWalls(Point start, Point end) {
        if (start.x == end.x) {
            for (int y = Math.min(start.y, end.y); y <= Math.max(start.y, end.y); y++) {
                addWallIfEmpty(start.x - 1, y);
                addWallIfEmpty(start.x + 1, y);
            }
        } else {
            for (int x = Math.min(start.x, end.x); x <= Math.max(start.x, end.x); x++) {
                addWallIfEmpty(x, start.y - 1);
                addWallIfEmpty(x, start.y + 1);
            }
        }
    }

    private void addWallIfEmpty(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height && world[x][y] == Tileset.NOTHING) {
            world[x][y] = Tileset.WALL;
        }
    }
    private void setupGameWindow() {
        int canvasWidth = width * TILE_SIZE;
        int canvasHeight = height * TILE_SIZE;
        StdDraw.setCanvasSize(canvasWidth, canvasHeight);
        StdDraw.setXscale(0, canvasWidth);
        StdDraw.setYscale(0, canvasHeight);
    }
    public void startGame() throws IOException {
        setupGameWindow();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                moveJeffrey(key);
            }
            update();
            render();
        }
    }
    private void render() {
        StdDraw.clear();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (lightsOn && !canSee(x, y)) {
                    Tileset.NOTHING.draw(x * TILE_SIZE, y * TILE_SIZE);
                } else {
                    world[x][y].draw(x * TILE_SIZE, y * TILE_SIZE);
                }
            }
        }
        displayTileDescription();
        StdDraw.show();
    }
    public void displayTileDescription() {
        int mouseX = (int) (StdDraw.mouseX());
        int mouseY = (int) (StdDraw.mouseY());

        if (mouseX >= 0 && mouseX < width && mouseY >= 0 && mouseY < height) {
            TETile tile = world[mouseX][mouseY];
            String tileDescription = tile.description();
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.setFont(new Font("Arial", Font.BOLD, 16));
            StdDraw.text(width - width + 2, height - 1, tileDescription);
        }
    }
    public void update() {
    }
    public void moveJeffrey(char key) throws IOException {
        if (key == 'O' || key == 'o') {
            lightsOn = !lightsOn;
            System.out.println("Line of Sight Toggled: " + lightsOn);
        }
        if (colonPressed) {
            if (key == 'Q' || key == 'q') {
                saveAndQuit();
                colonPressed = false;
            } else {
                colonPressed = false;
            }
        } else if (key == ':') {
            colonPressed = true;
        } else {
            switch (key) {
                case 'w':
                case 'W':
                    jeffrey.move(0, 1);
                    break;
                case 'a':
                case 'A':
                    jeffrey.move(-1, 0);
                    break;
                case 's':
                case 'S':
                    jeffrey.move(0, -1);
                    break;
                case 'd':
                case 'D':
                    jeffrey.move(1, 0);
                    break;
                case ':':
                    if (StdDraw.isKeyPressed('Q') || StdDraw.isKeyPressed('q')) {
                        saveAndQuit();
                    }
                    break;
            }
        }
    }
    private boolean canSee(int x, int y) {
        int playerX = jeffrey.getX();
        int playerY = jeffrey.getY();
        return Math.sqrt(Math.pow(x - playerX, 2) + Math.pow(y - playerY, 2)) <= LIGHT_DIST;
    }
    public TETile[][] applyLineOfSight(TETile[][] originalWorld) {
        TETile[][] tempWorld = new TETile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (lightsOn && !canSee(x, y)) {
                    tempWorld[x][y] = Tileset.NOTHING;
                } else {
                    tempWorld[x][y] = originalWorld[x][y];
                }
            }
        }
        return tempWorld;
    }
    private char tileToChar(TETile tile) {
        if (tile.equals(Tileset.AVATAR)) {
            return '@';
        } else if (tile.equals(Tileset.WALL)) {
            return '#';
        } else if (tile.equals(Tileset.FLOOR)) {
            return '.';
        }
        return ' ';
    }
    public void saveAndQuit() throws IOException {
        StringBuilder gameState = new StringBuilder();
        gameState.append(seed).append("\n");
        gameState.append(jeffrey.getX()).append(",").append(jeffrey.getY()).append("\n");

        // Iterate through each row in the world in reverse order
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                gameState.append(tileToChar(world[x][y]));
            }
            gameState.append("\n");
        }

            Utils.writeFile("game_save.txt", gameState.toString());
            System.exit(0);
    }
    public void loadGameState() throws IOException {
        if (!FileUtils.fileExists("game_save.txt")) {
            System.out.println("No saved game to load.");
            System.exit(0);
        }

        String savedData = FileUtils.readFile("game_save.txt");
        String[] lines = savedData.split("\n");

        if (lines.length < 2) {
            System.out.println("Saved game data is corrupted or in an unexpected format.");
            System.exit(0);
        }
        this.seed = Long.parseLong(lines[0]);
        this.random = new Random(this.seed);

        String[] avatarPos = lines[1].split(",");
        if (avatarPos.length < 2) {
            System.out.println("Saved game data is corrupted or in an unexpected format.");
            System.exit(0);
        }
        int avatarX = Integer.parseInt(avatarPos[0]);
        int avatarY = Integer.parseInt(avatarPos[1]);

        for (int y = 0; y < height; y++) {
            if (y + 2 >= lines.length) {
                System.out.println("Insufficient data for world tiles.");
                System.exit(0);
            }
            String line = lines[lines.length - 1 - y];

            if (line.length() != width) {
                System.out.println("Incorrect number of tiles in row " + y);
                System.exit(0);
            }

            for (int x = 0; x < width; x++) {
                char tileChar = line.charAt(x);
                world[x][y] = charToTETile(tileChar);
            }
        }

        jeffrey = new Character(this.world, avatarX, avatarY);
    }
    private TETile charToTETile(char ch) {
        switch (ch) {
            case '@': return Tileset.AVATAR;
            case '#': return Tileset.WALL;
            case '.': return Tileset.FLOOR;
            default: return Tileset.NOTHING;
        }
    }
    public void startGame(TERenderer ter) throws IOException {
        setupGameWindow();
        while (true) {
            //System.out.println("Game loop running");
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                moveJeffrey(key);
            }
            update();
            TETile[][] finalWorldFrame = getWorld();
            ter.renderFrame(finalWorldFrame);
            displayTileDescription();
            StdDraw.show();
            StdDraw.pause(20);
        }
    }
    public void startTitleScreen() throws IOException {
        StringBuilder seedInput = new StringBuilder();
        boolean startSeedInput = false;

        while (true) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.setFont(new Font("Decotura", Font.ITALIC, 24));
            StdDraw.text(width / 2.0, height / 2.0 + 8, "Pac-Man! (But Not Really)");
            StdDraw.text(width / 2.0, height / 2.0 + 4, "Enter a Seed (N)");
            StdDraw.text(width / 2.0, height / 2.0, "Save and Quit (:Q)");
            StdDraw.text(width / 2.0, height / 2.0 - 4, "Load State (L)");

            if (startSeedInput) {
                StdDraw.text(width / 2.0, height / 2.0 - 8, "Enter Seed: " + seedInput);
                StdDraw.text(width / 2.0, height / 2.0 - 12, "Start Game (S)");
            }

            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();

                if (key == 'L' || key == 'l') {
                    loadGameState();
                    break;
                } else if (key == 'N' || key == 'n') {
                    startSeedInput = true;
                } else if (startSeedInput && (key == 'S' || key == 's')) {
                    if (seedInput.length() > 0) {
                        this.seed = Long.parseLong(seedInput.toString());
                        this.random = new Random(this.seed);
                        generate();
                    }
                    break;
                } else if (startSeedInput && java.lang.Character.isDigit(key)) {
                    seedInput.append(key);
                }
            }
            if (StdDraw.isMousePressed()) {
                double mouseX = StdDraw.mouseX();
                double mouseY = StdDraw.mouseY();
                if (mouseY > height / 2.0 + 3 && mouseY < height / 2.0 + 5) {
                    startSeedInput = true;
                } else if (mouseY > height / 2.0 - 1 && mouseY < height / 2.0 + 1) {
                } else if (mouseY > height / 2.0 - 5 && mouseY < height / 2.0 - 3) {
                    loadGameState();
                    break;
                } else if (startSeedInput && mouseY > height / 2.0 - 13 && mouseY < height / 2.0 - 11) {
                    if (seedInput.length() > 0) {
                        this.seed = Long.parseLong(seedInput.toString());
                        this.random = new Random(this.seed);
                        generate();
                        break;
                    }
                    StdDraw.pause(200);
                }
            }
        }

    }
    private boolean isInMenuBounds(double mouseX, double mouseY, double menuY) {
        double menuX = width / 2.0;
        double menuWidth = 100;
        double menuHeight = 30;

        return (mouseX >= menuX - menuWidth / 2 && mouseX <= menuX + menuWidth / 2 &&
                mouseY >= menuY - menuHeight / 2 && mouseY <= menuY + menuHeight / 2);
    }

    private void renderHUD() {
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        if (mouseX >= 0 && mouseX < width && mouseY >= 0 && mouseY < height) {
            String tileDescription = world[mouseX][mouseY].description();
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.textLeft(1, height - 1, "Tile: " + tileDescription);
        }
        StdDraw.show();
    }
    public void setRandomSeed(long seed) {
        random.setSeed(seed);
    }
    public TETile[][] getWorld() {
        return world;
    }
}