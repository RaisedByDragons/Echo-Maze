package levels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import gameStart.GamePanel;
import utilz.Enums.Difficulty;
import utilz.Enums.TileType;

/**
 * Represents a single maze level in Echo Maze.
 * 
 * Responsibilities:
 * - Generate a randomized maze grid using recursive backtracking.
 * - Add difficulty-based "cutouts" to open up extra paths.
 * - Define the start (entrance) and end (exit) tiles.
 * - Draw the maze’s overlay information, including tutorial instructions.
 * - Manage tutorial progression and instructional display timing.
 */
public class Level {

    private int width;                  // Width of the maze (in tiles)
    private int height;                 // Height of the maze (in tiles)
    private int[][] grid;               // 2D grid representing walls and paths

    // Tutorial display state
    private int tutorialStage = 0;      // Current tutorial step index
    private int instructionType = 0;    // Which tutorial/instruction set is active
    private boolean showInstructions = false;
    private boolean moved = false;      // Tracks if player has moved
    private boolean pulsed = false;     // Tracks if player has used an echo pulse
    private int timer = 0;              // Controls timing between instruction updates
    private int updateInterval = 120;   // Number of frames between tutorial text updates

    private Tile tile = new Tile();     // Tile data reference (tile definitions)
    private Tile[] tileList = tile.buildTileInfo(); // Pre-built list of all tile info

    GamePanel gp;                       // Reference to the main GamePanel (for rendering & echo access)

    /**
     * Constructor — initializes the level grid with dimensions from the GamePanel.
     */
    public Level(GamePanel gp) {
        this.gp = gp;
        this.width = gp.MAX_SCREEN_WIDTH;
        this.height = gp.MAX_SCREEN_HEIGHT;
        this.grid = new int[height][width];
    }

    /**
     * Generates a random maze layout using recursive backtracking, then modifies it
     * according to difficulty settings (cutouts) and adds start/end points.
     */
    public void generateMaze(Difficulty difficulty, int x, int y) {
        // Initialize maze with walls
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                grid[i][j] = TileType.WALL.getValue();

        // Begin recursive backtracking maze generation
        carvePath(x, y);

        // Apply difficulty modifier (more cutouts = easier)
        addDifficultyCutouts(difficulty);

        // Add entry (bottom) and exit (top)
        addEntranceAndExit();

        // printMaze(); // Uncomment for console debug
    }

    /**
     * Recursive Backtracking Maze Algorithm:
     * Randomly carves paths through the grid by moving 2 cells at a time
     * and connecting the cells with open space.
     */
    private void carvePath(int x, int y) {
        int[] dx = { 0, 0, -2, 2 };  // Movement offsets (x)
        int[] dy = { -2, 2, 0, 0 };  // Movement offsets (y)

        // Randomize the order of directions
        Integer[] directions = { 0, 1, 2, 3 };
        Collections.shuffle(Arrays.asList(directions));

        // Attempt to move in each direction
        for (int dir : directions) {
            int nx = x + dx[dir];
            int ny = y + dy[dir];

            // Check bounds
            if (nx > 0 && ny > 0 && nx < width - 1 && ny < height - 1) {
                // If next cell is still a wall, carve a path
                if (grid[ny][nx] == TileType.WALL.getValue()) {
                    grid[ny][nx] = TileType.PATH.getValue();
                    grid[y + dy[dir] / 2][x + dx[dir] / 2] = TileType.PATH.getValue();
                    carvePath(nx, ny); // Recurse
                }
            }
        }
    }

    /**
     * Adds random wall removals ("cutouts") to make the maze easier.
     * The number of cutouts depends on the difficulty level.
     */
    private void addDifficultyCutouts(Difficulty difficulty) {
        int area = width * height;

        // Calculate number of walls to remove based on difficulty
        int cutouts = switch (difficulty) {
            case BABY -> (int) (area * 0.1);
            case EASY -> (int) (area * 0.05);
            case MEDIUM -> (int) (area * 0.03);
            case HARD -> (int) (area * 0.02);
            case EXTREME -> (int) (area * 0.01);
            case CUSTOM -> (int) (area * 0.04); // TODO: Make customizable later
            default -> throw new IllegalArgumentException("Unexpected value: " + difficulty);
        };

        Random rand = new Random();
        int attempts = 0;

        // Try random positions until we’ve made all cutouts or hit a limit
        while (cutouts > 0 && attempts < 500) {
            int x = 1 + rand.nextInt(width - 2);
            int y = 1 + rand.nextInt(height - 2);

            // Only remove walls that connect two paths horizontally or vertically
            if (grid[y][x] == TileType.WALL.getValue()) {
                boolean horizontalCut = grid[y][x - 1] == TileType.PATH.getValue() && grid[y][x + 1] == TileType.PATH.getValue();
                boolean verticalCut = grid[y - 1][x] == TileType.PATH.getValue() && grid[y + 1][x] == TileType.PATH.getValue();

                if (horizontalCut || verticalCut) {
                    grid[y][x] = TileType.PATH.getValue();
                    cutouts--;
                }
            }

            attempts++;
        }
    }

    /**
     * Creates an entrance and exit by modifying the top and bottom edges of the maze.
     */
    private void addEntranceAndExit() {
        // Find a valid top path tile for the exit
        List<Integer> topCandidates = new ArrayList<>();
        for (int i = 1; i < width - 1; i++) {
            topCandidates.add(i);
        }
        Collections.shuffle(topCandidates);

        // Assign the first valid candidate as the END tile
        for (int i : topCandidates) {
            if (grid[1][i] == TileType.PATH.getValue()) {
                grid[0][i] = TileType.END.getValue();
                break;
            }
        }

        // Entrance: look at bottom row, open path if a nearby path exists
        for (int i = width - 2; i > 0; i--) {
            if (grid[height - 1][i] == TileType.PATH.getValue()) {
                grid[height - 1][i] = TileType.START.getValue();
                break;
            }
        }
    }

    /**
     * Draws level UI overlays (echo fade, level info, and tutorial/instruction messages).
     */
    public void draw(Graphics2D g2) {

        // Draw echo pulse fading effect
        gp.getEchoPulse().drawEchoFade(g2);

        // HUD: display level number and remaining pulses
        g2.setColor(Color.WHITE);
        g2.drawString("Level: " + gp.getLevelManager().getCurrentLevelNum(), 3, (gp.MAX_SCREEN_HEIGHT * gp.TILE_SIZE) - 3);
        g2.drawString("Pulses Remaining: " + gp.getEchoPulse().getNumPulseLeft(), (gp.MAX_SCREEN_WIDTH * gp.TILE_SIZE) - 8 * gp.TILE_SIZE, (gp.MAX_SCREEN_HEIGHT * gp.TILE_SIZE) - 3);

        // If not showing instructions, skip further drawing
        if (!showInstructions) {
            return;
        }

        // Draw translucent black box for instructions
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRect(0, 0, 450, 150);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Welcome to Echo Maze!", 20, 30);
        g2.setFont(new Font("Arial", Font.PLAIN, 14));

        // Instructional messages depend on the current type
        if (instructionType == 0) {
            // Base tutorial messages shown progressively
            if (tutorialStage > 1) {
                timer++;
                if (timer >= updateInterval) {
                    timer = 0;
                    tutorialStage++;
                }
            }
            for (int i = 0; i <= tutorialStage; i++) {
                switch (i) {
                    case 0 -> g2.drawString("Use W A S D or arrow keys to move.", 20, 60);
                    case 1 -> g2.drawString("Press SPACE to send out an echo pulse.", 20, 80);
                    case 2 -> g2.drawString("Enemies are found around the map, try to avoid them.", 20, 100);
                    case 3 -> g2.drawString("Reach the end tile (gray) to complete the level.", 20, 120);
                }
            }
        } else if (instructionType == 1) {
            // Difficulty-specific tips
            for (int i = 0; i <= tutorialStage; i++) {
                switch (i) {
                    case 0 -> g2.drawString("Different Difficulties will spawn Different Enemies", 20, 60);
                }
            }
        } else if (instructionType == 2) {
            // Enemy-type tutorial
            if (tutorialStage > 0) {
                timer++;
                if (timer >= updateInterval) {
                    timer = 0;
                    tutorialStage++;
                }
            }
            for (int i = 0; i <= tutorialStage; i++) {
                switch (i) {
                    case 0 -> g2.drawString("The color of the enemy denotes its type.", 20, 60);
                    case 1 -> g2.drawString("Red are basic enemies that follow the right hand wall.", 20, 80);
                    case 2 -> g2.drawString("Blue are fast enemies, they will follow any available path.", 20, 100);
                    case 3 -> g2.drawString("Pink are follow enemies, make sure you keep moving or they may find you.", 20, 120);
                }
            }
        }

        g2.drawString("Press ENTER to hide instructions.", 20, 150);
    }

    /**
     * Prints the maze to the console as ASCII (# = wall, space = path).
     * Useful for debugging.
     */
    public void printMaze() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(grid[y][x] == TileType.WALL.getValue() ? "#" : " ");
            }
            System.out.println();
        }
    }

    // ------------------------------
    // Getters and Setters
    // ------------------------------

    public int[][] getGrid() {
        return grid;
    }

    /**
     * Finds and returns coordinates of the exit tile.
     * @return [row, column] of the END tile.
     */
    public int[] getExit() {
        int[] coords = new int[2];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == TileType.END.getValue()) {
                    coords[0] = i;
                    coords[1] = j;
                }
            }
        }
        return coords;
    }

    public void advanceTutorial() {
        if (tutorialStage < 3) {
            tutorialStage++;
        }
    }

    public Tile[] getTileList() {
        return tileList;
    }

    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    protected void setGrid(int[][] grid) { this.grid = grid; }

    public boolean isShowInstructions() { return showInstructions; }
    public void setShowInstructions(boolean showInstructions) { this.showInstructions = showInstructions; }

    /**
     * Overloaded setter that also selects which tutorial type to display.
     */
    public void setShowInstructions(boolean showInstructions, int message) {
        this.showInstructions = showInstructions;
        instructionType = message;
    }

    public int getTutorialStage() { return tutorialStage; }
    public void setTutorialStage(int tutorialStage) { this.tutorialStage = tutorialStage; }

    public boolean isMoved() { return moved; }
    public void setMoved(boolean moved) { this.moved = moved; }

    public boolean isPulsed() { return pulsed; }
    public void setPulsed(boolean pulsed) { this.pulsed = pulsed; }

    public int getInstructionType() { return instructionType; }
    public void setInstructionType(int instructionType) { this.instructionType = instructionType; }
}
