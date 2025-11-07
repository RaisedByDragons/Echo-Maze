package entities.enemies;

import java.util.LinkedList;
import java.util.Queue;

import entities.Entity;
import gameStart.GamePanel;
import utilz.HelpMethods;
import utilz.Enums.Direction;
import utilz.Enums.TileType;

/**
 * Base abstract class for all enemy types in the game.
 * 
 * Handles shared movement logic, spawn positioning, and update structure.
 * Specific enemy types should extend this class and implement the update() method.
 */
public abstract class Enemy extends Entity {

    // Number of update cycles between each move (used to slow movement speed)
    protected int moveDelay; 
    
    // Counter to track how many updates have passed since the last move
    protected int moveCounter;
    
    // Current facing/movement direction of the enemy
    protected Direction direction;
    
    /**
     * Constructor for Enemy.
     * 
     * @param gp reference to the main GamePanel, used to access level data and utilities
     */
    public Enemy(GamePanel gp) {
        super(gp);
    }
    
    /**
     * Moves the enemy one tile forward in its current direction.
     * Uses helper methods to translate direction into coordinate changes.
     */
    protected void moveForward() {
        col += HelpMethods.dX(direction); // Move horizontally based on direction
        row += HelpMethods.dY(direction); // Move vertically based on direction
    }

    /**
     * Attempts to find the nearest valid (walkable) tile to spawn the enemy on.
     * 
     * Uses Breadth-First Search (BFS) starting from the default tile position.
     * If a walkable path tile is found, sets that as the spawn position.
     * 
     * @param defaultCol the default column to start searching from
     * @param defaultRow the default row to start searching from
     */
    protected void spawnNearestTile(int defaultCol, int defaultRow) {
        int[][] levelMap = gp.getLevelManager().getCurrentLevel().getGrid(); // Get current level grid layout
        
        // Queue for BFS search; stores tile coordinates to explore
        Queue<int[]> queue = new LinkedList<>();
        
        // Keeps track of visited tiles to prevent infinite loops
        boolean[][] visited = new boolean[levelMap.length][levelMap[0].length];

        // Begin search at the default tile
        queue.add(new int[]{defaultRow, defaultCol});
        visited[defaultRow][defaultCol] = true;

        // Possible movement directions: up, down, left, right
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}
        };

        // Perform BFS until a valid tile is found or all are checked
        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int r = pos[0];
            int c = pos[1];

            for (int[] d : directions) {
                int newR = r + d[0];
                int newC = c + d[1];

                // Validate tile is within bounds and not yet visited
                if (newR >= 0 && newR < levelMap.length &&
                    newC >= 0 && newC < levelMap[0].length &&
                    !visited[newR][newC]) {

                    // Check if the tile is walkable (a path)
                    if (levelMap[newR][newC] == TileType.PATH.getValue()) {
                        // Found a valid spawn position
                        spawnY = newR;
                        spawnX = newC;
                        col = spawnX;
                        row = spawnY;
                        return; // Stop once a valid tile is found
                    }

                    // Mark this tile as visited and continue searching
                    visited[newR][newC] = true;
                    queue.add(new int[]{newR, newC});
                }
            }
        }

        // If no valid tile found in entire search area
        System.out.println("No valid spawn location found!");
    }

    /**
     * Abstract update method to be implemented by each enemy type.
     * This should define per-frame or per-tick behavior (e.g., movement logic, detection, etc.)
     */
    public abstract void update();
}
