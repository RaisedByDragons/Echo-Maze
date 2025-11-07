package entities.enemies;

import java.awt.Color;
import utilz.HelpMethods;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.Queue;
import gameStart.GamePanel;
import utilz.Enums.Direction;
import utilz.Enums.TileType;

/**
 * Basic enemy type that patrols the maze using the "follow right wall" rule.
 * This enemy moves at a fixed speed and only becomes visible when revealed by an echo pulse.
 */
public class Basic extends Enemy {
	
    /**
     * Constructs a Basic enemy.
     * Sets default spawn position, movement delay, and starting direction.
     */
    public Basic(GamePanel gp) {
        super(gp);
        
        setDefaultSpawn();       // Find a valid initial spawn tile
        moveDelay = 30;          // Controls how many frames pass between movements
        direction = Direction.UP; // Start facing upward
    }
    
    /**
     * Attempts to spawn the enemy at the specified grid coordinates.
     * If the tile is invalid (e.g., wall), finds the nearest path tile instead.
     */
    public void spawn(int col, int row) {
    	int[][] levelMap = gp.getLevelManager().getCurrentLevel().getGrid();
        
        // If the specified location is a walkable path, set it as spawn point
        if (levelMap[row][col] == 0) {
            spawnX = col;
            spawnY = row;
            return;
        }

        // Otherwise, find the nearest available path tile
        spawnNearestTile(col, row);
    }
    
    /**
     * Chooses a random valid spawn position for the enemy.
     * Ensures it's not too close to the player and avoids spawning
     * in the same dead-end tunnel the player is trapped in.
     */
    public void setDefaultSpawn() {
        int maxAttempts = 1000; // Prevents infinite loops
        int attempt = 0;
        int spawnRow, spawnCol;

        // Keep picking random tiles until a valid spawn is found
        do {
            spawnCol = (int)(gp.getRandomDouble() * (gp.MAX_SCREEN_WIDTH - 1) + 1);
            spawnRow = (int)(gp.getRandomDouble() * (gp.MAX_SCREEN_HEIGHT - 1) + 1);
            attempt++;
        } while (
            (!isValidSpawn(spawnRow, spawnCol) || isInPlayerDeadEnd(spawnRow, spawnCol)) 
            && attempt < maxAttempts
        );

        // Move enemy to a nearby valid path tile if needed
        spawnNearestTile(spawnCol, spawnRow);
    }
    
    /**
     * Returns true if the tile is at least 10 tiles away (Manhattan distance)
     * from the player's current position.
     */
    private boolean isValidSpawn(int row, int col) {
        int playerRow = gp.getEntityManager().getPlayer().getRow();
        int playerCol = gp.getEntityManager().getPlayer().getCol();

        // Manhattan distance: no diagonals
        int dist = Math.abs(playerRow - row) + Math.abs(playerCol - col);
        return dist >= 10;
    }
    
    /**
     * Returns true if the player is currently in a dead-end tunnel
     * and the spawn position is within that same tunnel.
     */
    private boolean isInPlayerDeadEnd(int row, int col) {
        int[][] grid = gp.getLevelManager().getCurrentLevel().getGrid();
        int playerRow = gp.getEntityManager().getPlayer().getRow();
        int playerCol = gp.getEntityManager().getPlayer().getCol();

        // Skip check if spawn isn't in the same connected path region as player
        if (!isSameTunnel(playerRow, playerCol, row, col, grid)) return false;

        int openPaths = 0;
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}}; // Up, down, left, right

        // Count how many open path tiles surround the player
        for (int[] d : dirs) {
            int r = playerRow + d[0];
            int c = playerCol + d[1];
            if (r >= 0 && r < grid.length && c >= 0 && c < grid[0].length && grid[r][c] == TileType.PATH.getValue()) {
                openPaths++;
            }
        }

        // If only one exit, it's a dead-end
        return openPaths <= 1;
    }
    
    /**
     * Checks whether two coordinates (r1, c1) and (r2, c2) belong to the same tunnel.
     * Uses a breadth-first search (BFS) through connected PATH tiles.
     */
    private boolean isSameTunnel(int r1, int c1, int r2, int c2, int[][] grid) {
        boolean[][] visited = new boolean[grid.length][grid[0].length];
        Queue<int[]> queue = new LinkedList<>();
        
        // Start BFS from the player's position
        queue.add(new int[]{r1, c1});
        visited[r1][c1] = true;

        // Explore all reachable tiles connected to (r1, c1)
        while (!queue.isEmpty()) {
            int[] curr = queue.poll();

            // If we reached the target tile, they’re in the same tunnel
            if (curr[0] == r2 && curr[1] == c2) return true;

            // Check all four neighboring tiles
            for (int[] d : new int[][]{{-1,0},{1,0},{0,-1},{0,1}}) {
                int nr = curr[0] + d[0]; // Next row
                int nc = curr[1] + d[1]; // Next column

                // Skip invalid or already visited tiles
                if (nr >= 0 && nr < grid.length && nc >= 0 && nc < grid[0].length &&
                    !visited[nr][nc] && grid[nr][nc] == TileType.PATH.getValue()) {
                    visited[nr][nc] = true;
                    queue.add(new int[]{nr, nc}); // Add neighbor to queue
                }
            }
        }

        // If we never reached (r2, c2), they are in different tunnels
        return false;
    }
    
    /**
     * Called when transitioning to the next level.
     * Finds a new valid spawn position.
     */
    public void nextLevel() {
    	setDefaultSpawn();
    }

    /**
     * Called every frame to update the enemy’s logic.
     * Moves the enemy only after 'moveDelay' ticks, then follows the right wall.
     */
    public void update() {
        moveCounter++;
        
        // Wait until enough frames have passed before moving again
        if (moveCounter < moveDelay) return;

        moveCounter = 0;
        followRightWall(direction);
    }
    
    /**
     * Executes the "follow right wall" navigation algorithm:
     * 1. Try turning right.
     * 2. If blocked, go straight.
     * 3. If blocked, turn left.
     * 4. If blocked, turn around (dead end).
     */
    private void followRightWall(Direction dire) {
        // Step 1: Try to turn right relative to current direction
        Direction right = HelpMethods.turnRight(dire);
        if (canMove(HelpMethods.dX(right), HelpMethods.dY(right), gp.getLevelManager())) {
            direction = right;
            moveForward(); // Move one tile in that direction
            return;
        }

        // Step 2: Try to continue straight ahead
        if (canMove(HelpMethods.dX(dire), HelpMethods.dY(dire), gp.getLevelManager())) {
            direction = dire;
            moveForward();
            return;
        }

        // Step 3: Try turning left
        Direction left = HelpMethods.turnLeft(dire);
        if (canMove(HelpMethods.dX(left), HelpMethods.dY(left), gp.getLevelManager())) {
            direction = left;
            moveForward();
            return;
        }

        // Step 4: Dead end — turn around
        Direction back = HelpMethods.turnBack(dire);
        if (canMove(HelpMethods.dX(back), HelpMethods.dY(back), gp.getLevelManager())) {
            direction = back;
            moveForward();
            return;
        }
    }

    /**
     * Draws the Basic enemy if it is visible within the current echo pulse range.
     * Appears as a red square on the map.
     */
    public void draw(Graphics2D g2) {
    	// Skip drawing if enemy is outside the revealed area
    	if (!gp.getEchoPulse().getVisibilityMap()[row][col]) {
    	    return;
    	}

    	// Draw enemy as a red tile
    	g2.setColor(Color.RED);
        g2.fillRect(col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE);
    }
    
    /**
     * Fully resets the enemy — used when restarting or resetting the entire game.
     */
    public void resetAll() {
    	setDefaultSpawn();
    	direction = Direction.UP;
    }
    
    /**
     * Resets only the current level’s state.
     * Returns the enemy to its initial position for that level.
     */
    public void resetLevel() {
    	col = spawnX;
		row = spawnY;
    }
}