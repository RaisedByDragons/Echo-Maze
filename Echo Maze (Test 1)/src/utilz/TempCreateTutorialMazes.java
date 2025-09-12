//package utilz;
//
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Random;
//
//public class TempCreateTutorialMazes {
//
//    private static final Random rand = new Random();
//
//    public static void main(String[] args) throws IOException {
//        int[][] maze1 = generateMazeWithCutouts(17, 17, 30);
//        int[][] maze2 = generateMazeWithCutouts(17, 17, 30);
//        int[][] maze3 = generateMazeWithCutouts(37, 37, 100);
//
//        saveMazeToFile(maze1, "1.txt");
//        saveMazeToFile(maze2, "2.txt");
//        saveMazeToFile(maze3, "3.txt");
//    }
//
//    public static int[][] generateMazeWithCutouts(int width, int height, int cutouts) {
//        int[][] maze = generateMaze(width, height);
//        addCutouts(maze, cutouts);
//        return maze;
//    }
//
//    public static int[][] generateMaze(int width, int height) {
//        int[][] maze = new int[height][width];
//        for (int[] row : maze) Arrays.fill(row, 0); // Fill with walls
//
//        carvePath(maze, 1, 1);
//        return maze;
//    }
//
//    private static void carvePath(int[][] maze, int x, int y) {
//        maze[y][x] = 1;
//
//        Integer[] dirs = new Integer[]{0, 1, 2, 3};
//        Collections.shuffle(Arrays.asList(dirs), rand);
//
//        for (int dir : dirs) {
//            int dx = 0, dy = 0;
//            switch (dir) {
//                case 0 -> dy = -1;
//                case 1 -> dx = 1;
//                case 2 -> dy = 1;
//                case 3 -> dx = -1;
//            }
//
//            int nx = x + dx * 2;
//            int ny = y + dy * 2;
//
//            if (ny > 0 && ny < maze.length && nx > 0 && nx < maze[0].length && maze[ny][nx] == 0) {
//                maze[y + dy][x + dx] = 1;
//                carvePath(maze, nx, ny);
//            }
//        }
//    }
//
//    public static void addCutouts(int[][] maze, int numCutouts) {
//        int h = maze.length;
//        int w = maze[0].length;
//
//        int added = 0;
//        while (added < numCutouts) {
//            int row = rand.nextInt(h);
//            int col = rand.nextInt(w);
//
//            if (maze[row][col] == 0) {
//                maze[row][col] = 1;
//                added++;
//            }
//        }
//    }
//
//    public static void saveMazeToFile(int[][] maze, String filename) throws IOException {
//        try (FileWriter fw = new FileWriter(filename)) {
//            for (int[] row : maze) {
//                for (int cell : row) {
//                    fw.write(cell + " ");
//                }
//                fw.write("\n");
//            }
//        }
//    }
//}
//
