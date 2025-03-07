package core;
import tileengine.TETile;
import tileengine.Tileset;
import java.io.IOException;

public class AutograderBuddy {

    /**
     * Simulates a game, but doesn't render anything or call any StdDraw
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public static TETile[][] getWorldFromInput(String input) {
        int width = 70;
        int height = 40;
        World world = new World(width, height);
        try {
            processString(input, world);
        } catch (IOException e) {
            e.printStackTrace();
            return new TETile[width][height];
        }
        return world.getWorld();
    }

    private static void processString(String input, World world) throws IOException {
        StringBuilder seedString = new StringBuilder();
        boolean getSeed = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (getSeed && java.lang.Character.isDigit(c)) {
                seedString.append(c);
            }

            switch (c) {
                case 'N':
                case 'n':
                    getSeed = true;
                    break;
                case 'S':
                case 's':
                    if (getSeed) {
                        long seed = Long.parseLong(seedString.toString());
                        world.setRandomSeed(seed);
                        world.generate(); // Assuming you have a method to generate the world with the seed
                        getSeed = false;
                    }
                    break;
                case ':':
                    if (i + 1 < input.length() && input.charAt(i + 1) == 'Q') {
                        world.saveAndQuit(); // Save and quit the game if ':Q' is input
                        return;
                    }
                    break;
                default:
                    if (!getSeed) {
                        world.moveJeffrey(c);
                    }
                    break;
            }
        }
    }


    /**
     * Used to tell the autograder which tiles are the floor/ground (including
     * any lights/items resting on the ground). Change this
     * method if you add additional tiles.
     */
    public static boolean isGroundTile(TETile t) {
        return t.character() == Tileset.FLOOR.character()
                || t.character() == Tileset.AVATAR.character()
                || t.character() == Tileset.FLOWER.character();
    }

    /**
     * Used to tell the autograder while tiles are the walls/boundaries. Change
     * this method if you add additional tiles.
     */
    public static boolean isBoundaryTile(TETile t) {
        return t.character() == Tileset.WALL.character()
                || t.character() == Tileset.LOCKED_DOOR.character()
                || t.character() == Tileset.UNLOCKED_DOOR.character();
    }
}
