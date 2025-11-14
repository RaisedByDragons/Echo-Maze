//package utilz;
//
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Random;
//
// This class is used to generate random maze layouts with additional "cutouts" for tutorials
// and save them as text files for later use in the game.

/*
 * TODO 
 * 
 * FILE AND CLASS NOT USED
 * 
 * TODO
 */

//public class TempCreateTutorialMazes {
//
//    private static final Random rand = new Random(); // Random number generator
//
//    public static void main(String[] args) throws IOException {
//        // Generate three different mazes with varying sizes and number of cutouts
//        int[][] maze1 = generateMazeWithCutouts(17, 17, 30);
//        int[][] maze2 = generateMazeWithCutouts(17, 17, 30);
//        int[][] maze3 = generateMazeWithCutouts(37, 37, 100);
//
//        // Save the generated mazes to files
//        saveMazeToFile(maze1, "1.txt");
//        saveMazeToFile(maze2, "2.txt");
//        saveMazeToFile(maze3, "3.txt");
//    }
//
//    // Generates a maze with extra random cutouts to create more paths
//    public static int[][] generateMazeWithCutouts(int width, int height, int cutouts) {
//        int[][] maze = generateMaze(width, height); // Generate a basic maze
//        addCutouts(maze, cutouts); // Add extra open spaces
//        return maze;
//    }
//
//    // Generates a basic maze using recursive backtracking
//    public static int[][] generateMaze(int width, int height) {
//        int[][] maze = new int[height][width];
//        for (int[] row : maze) Arrays.fill(row, 0); // Fill the entire grid with walls
//
//        carvePath(maze, 1, 1); // Start carving a path from (1,1)
//        return maze;
//    }
//
//    // Recursive method to carve a path in the maze
//    private static void carvePath(int[][] maze, int x, int y) {
//        maze[y][x] = 1; // Mark the current cell as a path
//
//        // Randomize directions: 0 = UP, 1 = RIGHT, 2 = DOWN, 3 = LEFT
//        Integer[] dirs = new Integer[]{0, 1, 2, 3};
//        Collections.shuffle(Arrays.asList(dirs), rand); // Shuffle directions
//
//        // Attempt to carve paths in each direction
//        for (int dir : dirs) {
//            int dx = 0, dy = 0;
//            switch (dir) {
//                case 0 -> dy = -1;
//                case 1 -> dx = 1;
//                case 2 -> dy = 1;
//                case 3 -> dx = -1;
//            }
//
//            int nx = x + dx * 2; // Next x coordinate, skip one cell for walls
//            int ny = y + dy * 2; // Next y coordinate
//
//            // Check boundaries and if the target cell is still a wall
//            if (ny > 0 && ny < maze.length && nx > 0 && nx < maze[0].length && maze[ny][nx] == 0) {
//                maze[y + dy][x + dx] = 1; // Carve the wall between current and next
//                carvePath(maze, nx, ny); // Recurse from next cell
//            }
//        }
//    }
//
//    // Adds random open spaces ("cutouts") to a maze
//    public static void addCutouts(int[][] maze, int numCutouts) {
//        int h = maze.length;
//        int w = maze[0].length;
//
//        int added = 0;
//        while (added < numCutouts) {
//            int row = rand.nextInt(h);
//            int col = rand.nextInt(w);
//
//            // Only add cutout if cell is currently a wall
//            if (maze[row][col] == 0) {
//                maze[row][col] = 1;
//                added++;
//            }
//        }
//    }
//
//    // Saves the maze to a text file with rows and columns of integers
//    public static void saveMazeToFile(int[][] maze, String filename) throws IOException {
//        try (FileWriter fw = new FileWriter(filename)) {
//            for (int[] row : maze) {
//                for (int cell : row) {
//                    fw.write(cell + " "); // Separate cells with a space
//                }
//                fw.write("\n"); // New line at the end of each row
//            }
//        }
//    }
//}
