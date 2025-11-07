package entities.enemies;

import java.awt.Color;
import utilz.HelpMethods;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import gameStart.GamePanel;
import utilz.Enums.Direction;
import utilz.Enums.TileType;

/**
 * The Fast enemy type — moves more frequently than other enemies.
 * 
 * This class handles spawning logic to ensure it doesn't appear too close
 * to the player or inside a dead-end the player occupies, and defines its
 * random path-following behavior.
 */
public class Fast extends Enemy {
	
    /**
     * Constructor for Fast enemy.
     * Sets default spawn position and movement parameters.
     */
    public Fast(GamePanel gp) {
        super(gp);
        
        setDefaultSpawn();       // Find a valid initial spawn location
        moveDelay = 15;          // Determines movement speed (lower = faster)
        col = spawnX;
        row = spawnY;
        direction = Direction.UP;
    }
    
    /**
     * Attempts to spawn the enemy at a specific tile.
     * If that tile is not walkable, finds the nearest valid one.
     */
    public void spawn(int col, int row) {
        int[][] levelMap = gp.getLevelManager().getCurrentLevel().getGrid();
        
        // Check if the requested spawn tile is walkable
        if (levelMap[row][col] == 0) {
            spawnX = col;
            spawnY = row;
            return;
        }

        // Otherwise, find the nearest valid path tile
        spawnNearestTile(col, row);
    }
    
    /**
     * Randomly selects a valid spawn location far enough from the player.
     * Avoids spawning inside dead-end tunnels that the player occupies.
     */
    public void setDefaultSpawn() {
        int maxAttempts = 1000; // Prevents infinite search loops
        int attempt = 0;
        int spawnRow, spawnCol;

        do {
            // Pick random coordinates within screen bounds
            spawnCol = (int)(gp.getRandomDouble() * (gp.MAX_SCREEN_WIDTH - 1) + 1);
            spawnRow = (int)(gp.getRandomDouble() * (gp.MAX_SCREEN_HEIGHT - 1) + 1);
            attempt++;
        // Repeat until the location is valid or attempts exhausted
        } while ((!isValidSpawn(spawnRow, spawnCol) || isInPlayerDeadEnd(spawnRow, spawnCol)) && attempt < maxAttempts);

        // Once a valid region is found, pick the nearest walkable tile
        spawnNearestTile(spawnCol, spawnRow);
    }
    
    /**
     * Checks whether a given spawn tile is far enough from the player.
     * 
     * @return true if the spawn is at least 10 tiles away (Manhattan distance)
     */
    private boolean isValidSpawn(int row, int col) {
        int playerRow = gp.getEntityManager().getPlayer().getRow();
        int playerCol = gp.getEntityManager().getPlayer().getCol();

        int dist = Math.abs(playerRow - row) + Math.abs(playerCol - col); // Manhattan distance
        return dist >= 10; // Must be at least 10 tiles away
    }
    
    /**
     * Determines if a potential spawn location lies within the same tunnel
     * as the player, and whether the player is currently in a dead-end.
     * 
     * @return true if player is in a dead-end and the spawn shares that tunnel
     */
    private boolean isInPlayerDeadEnd(int row, int col) {
        int[][] grid = gp.getLevelManager().getCurrentLevel().getGrid();
        int playerRow = gp.getEntityManager().getPlayer().getRow();
        int playerCol = gp.getEntityManager().getPlayer().getCol();

        // Only check for dead-ends if spawn is in same connected tunnel as player
        if (!isSameTunnel(playerRow, playerCol, row, col, grid)) return false;

        // Count open paths (walkable adjacent tiles)
        int openPaths = 0;
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int[] d : dirs) {
            int r = playerRow + d[0];
            int c = playerCol + d[1];
            if (r >= 0 && r < grid.length && c >= 0 && c < grid[0].length && grid[r][c] == TileType.PATH.getValue()) {
                openPaths++;
            }
        }

        // If the player is in a tunnel with 1 or fewer exits, it's a dead-end
        return openPaths <= 1;
    }
    
    /**
     * Uses BFS to determine if two positions are part of the same tunnel system.
     * This prevents enemies from spawning in isolated sections separated by walls.
     */
    private boolean isSameTunnel(int r1, int c1, int r2, int c2, int[][] grid) {
        boolean[][] visited = new boolean[grid.length][grid[0].length];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{r1, c1});
        visited[r1][c1] = true;

        // Breadth-First Search to traverse all connected path tiles
        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            if (curr[0] == r2 && curr[1] == c2) return true; // Found the target tile

            for (int[] d : new int[][]{{-1,0},{1,0},{0,-1},{0,1}}) {
                int nr = curr[0] + d[0];
                int nc = curr[1] + d[1];
                if (nr >= 0 && nr < grid.length && nc >= 0 && nc < grid[0].length &&
                    !visited[nr][nc] && grid[nr][nc] == TileType.PATH.getValue()) {
                    visited[nr][nc] = true;
                    queue.add(new int[]{nr, nc});
                }
            }
        }

        return false; // No path connects the two tiles
    }
    
    /**
     * Called each game tick to update the enemy’s behavior.
     * Handles timing for movement and direction logic.
     */
    public void update() {
        moveCounter++;
        
        // Move only when enough time (moveDelay) has passed
        if (moveCounter < moveDelay) return;

        moveCounter = 0;
        followRandomWall(direction); // Execute movement behavior
    }
    
    /**
     * Moves the enemy through the maze by following walls with randomized turns.
     * Prioritizes continuing forward, then right, then left, then back if necessary.
     */
    private void followRandomWall(Direction dire) {
        Direction back = HelpMethods.turnBack(dire);

        // Gather all valid movement directions except the backward one
        List<Direction> validDirs = new ArrayList<>();
        Direction[] possibleDirs = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};

        for (Direction d : possibleDirs) {
            if (d != back && canMove(HelpMethods.dX(d), HelpMethods.dY(d), gp.getLevelManager())) {
                validDirs.add(d);
            }
        }

        // If multiple paths are available (junction), pick one randomly
        if (validDirs.size() > 1) {
            direction = validDirs.get((int)(gp.getRandomDouble() * validDirs.size()));
            moveForward();
            return;
        }

        // Continue straight if possible
        if (canMove(HelpMethods.dX(dire), HelpMethods.dY(dire), gp.getLevelManager())) {
            moveForward();
            return;
        }

        // Try turning right
        Direction right = HelpMethods.turnRight(dire);
        if (canMove(HelpMethods.dX(right), HelpMethods.dY(right), gp.getLevelManager())) {
            direction = right;
            moveForward();
            return;
        }

        // Try turning left
        Direction left = HelpMethods.turnLeft(dire);
        if (canMove(HelpMethods.dX(left), HelpMethods.dY(left), gp.getLevelManager())) {
            direction = left;
            moveForward();
            return;
        }

        // Dead end: turn back if no other options
        if (canMove(HelpMethods.dX(back), HelpMethods.dY(back), gp.getLevelManager())) {
            direction = back;
            moveForward();
        }
    }

    /**
     * Draws the enemy on the screen.
     * Only visible when revealed by an active echo pulse.
     */
    public void draw(Graphics2D g2) {
        // Skip rendering if enemy is outside the visible echo range
        if (!gp.getEchoPulse().getVisibilityMap()[row][col]) {
            return;
        }

        g2.setColor(Color.BLUE);
        g2.fillRect(col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE);
    }
    
    /**
     * Fully resets the enemy for a new game or major reset event.
     */
    public void resetAll() {
        setDefaultSpawn();
        direction = Direction.UP;
    }
    
    /**
     * Called when advancing to the next level.
     * Repositions the enemy at a new valid spawn.
     */
    public void nextLevel() {
        setDefaultSpawn();
    }
    
    /**
     * Resets position and direction when restarting the same level.
     */
    public void resetLevel() {
        col = spawnX;
        row = spawnY;
        direction = Direction.UP;
    }
}
