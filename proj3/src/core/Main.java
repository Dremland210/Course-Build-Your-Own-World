package core;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        int width = 70;
        int height = 40;
        World myWorld = new World(width, height);
        TERenderer ter = new TERenderer();
        ter.initialize(width, height);
        myWorld.startTitleScreen();
        while (true) {
            myWorld.update();
            TETile[][] worldFrame = myWorld.getWorld();

            TETile[][] visibleWorld = myWorld.applyLineOfSight(worldFrame);
            ter.renderFrame(visibleWorld);
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                myWorld.moveJeffrey(key);
            }

            myWorld.displayTileDescription();
            StdDraw.show();
            StdDraw.pause(20);
        }
    }
}
