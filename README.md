# MyBYOW

This repository contains my personal implementation of the BYOW (Build Your Own World) project from CS 61B Fall 2023. Although the project is based on a course assignment, I developed and refined all of the code on my own. It showcases world generation, interactive navigation, and basic rendering—all built in Java.

## Overview

**BYOW** is a project that challenges you to build a procedurally generated world. In this implementation, the program creates a random world composed of rooms, hallways, and various terrain tiles. Users can navigate the world (with an avatar), and the game supports features such as saving/loading game state and toggling certain display elements like line-of-sight.

## Key Features

- **Random World Generation:**  
  Generates a world with multiple rooms connected by hallways. Rooms are created with random dimensions and placed without overlapping.

- **Interactive Movement:**  
  Allows movement of an avatar (represented by the player) through the generated world.

- **Rendering Engine:**  
  Uses a custom tile engine to render the world with various tile types (walls, floors, etc.).

- **Save and Load Functionality:**  
  Includes the ability to save the game state and load it back later, which is particularly useful for testing and autograding.

- **Autograder Compatibility:**  
  Contains an `AutograderBuddy` class that simulates game inputs and outputs the resulting world state for grading purposes.

## Project Structure

All of the source code is organized in the repository. Key files include:

- **Core Classes (in the `core` package):**
  - `Main.java`: Contains the main game loop and handles user input.
  - `World.java`: Responsible for generating the world, managing game state, and rendering.
  - `Room.java`, `Hallway.java`: Handle room creation and connection logic.
  - `Character.java`: Manages the player’s avatar and movement.
  - `AutograderBuddy.java`: Provides methods for simulating inputs for autograding.

- **Tile Engine (in the `tileengine` package):**
  - `TETile.java`: Represents individual tiles in the world.
  - `Tileset.java`: Provides a collection of predefined tiles (e.g., WALL, FLOOR, AVATAR).
  - `TERenderer.java`: Handles the rendering of the 2D tile array.

- **Utilities (e.g., in the `utils` package):**
  - Classes for random utilities and file handling that support the core functionality.

## How to Compile and Run

1. **Compilation:**
   - Make sure you have Java installed (Java 8 or later is recommended).
   - Compile the project from the root directory using your preferred IDE or via the command line. For example, using `javac`:
     ```bash
     javac -d bin $(find . -name "*.java")
     ```

2. **Running the Game:**
   - Once compiled, run the `Main` class from the `bin` directory. For example:
     ```bash
     java -cp bin core.Main
     ```
   - Follow the on-screen instructions to start a new game, load a game state, or interact with the world.

## Dependencies

- **Java Development Kit (JDK) 8 or higher**
- **StdDraw Library:**  
  Used for simple drawing and handling input (typically provided in the course materials or available online).

## Acknowledgments

- **CS 61B at UC Berkeley:**  
  This project was part of the course curriculum for CS 61B Fall 2023.
- **Course Materials:**  
  The assignment guidelines and starter code provided the foundation, which I then expanded upon with my own implementation.
- **Open Source Tools:**  
  Thanks to the authors of the StdDraw library and other utilities that helped simplify development.

## License

This project is released under the [MIT License](LICENSE).

---

Feel free to explore the code, run the project, and see how the world is built dynamically from scratch!
