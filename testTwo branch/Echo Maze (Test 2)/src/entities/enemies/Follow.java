package entities.enemies;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import gameStart.GamePanel;
import utilz.Enums.Direction;
import utilz.Enums.TileType;
import utilz.PathFinding;

/**
 * The Follow enemy type — actively chases the player using pathfinding.
 * 
 * This enemy recalculates a path to the player every few frames and follows it
 * step by step, resulting in direct pursuit behavior. Spawning avoids placing it
 * too close to the player or end-of-level, and ensures it’s in a separate tunnel.
 */
public class Follow extends Enemy {

    /** Timer for how often to recalculate the path to the player */
    private int pathTimer = 20;

    /** Delay between path recalculations (in frames or ticks) */
    private int pathDeley = pathTimer; // probably intended as "pathDelay"

    /** Current index of the next direction step in the path */
    int nextIndex;

    /** Stores the most recently calculated path as a list of directions */
    private List<Direction> currentPath;
	    
    /**
     * Constructor for Follow enemy.
     * Initializes base movement properties and finds a valid spawn point.
     */
	public Follow(GamePanel gp) {
		super(gp);
		moveDelay = 45;     // Slower than Fast enemy — recalculates less frequently
		setDefaultSpawn();
		col = spawnX;
        row = spawnY;
	}

    /**
     * Main update method, called each frame.
     * Handles timing for movement and pathfinding recalculations.
     */
	public void update() {
		if (moveCounter < moveDelay) {
	        moveCounter++;
	        return;
	    }

	    moveCounter = 0;
	    pathToPlayer();
    }
	
    /**
     * Moves the enemy toward the player using a periodically recalculated path.
     * Each update either follows the current path or generates a new one.
     */
	private void pathToPlayer() {
		pathTimer++;  

        // Recalculate path after delay threshold
        if (pathTimer >= pathDeley) {
            pathTimer = 0;

            // Create a new PathFinding object centered on this enemy's position
            PathFinding pathFinding = new PathFinding(gp, col, row);

            // Retrieve calculated direction steps
            currentPath = pathFinding.pathDirections;
            nextIndex = 0;
            
            // Move along the first step immediately after recalculation
            if (currentPath != null && nextIndex < currentPath.size()) {
                Direction dir = currentPath.get(nextIndex);
                switch (dir) {
                    case UP:    row--; break;
                    case DOWN:  row++; break;
                    case LEFT:  col--; break;
                    case RIGHT: col++; break;
                }
                nextIndex++;
            }
        }

        // Continue following the current path until it’s exhausted
        if (currentPath != null && nextIndex < currentPath.size()) {
        	Direction dir = currentPath.get(nextIndex);
            nextIndex++;
            // Note: directly modifying row/col is simple but less smooth visually
            switch (dir) {
                case UP:    row--; break;
                case DOWN:  row++; break;
                case LEFT:  col--; break;
                case RIGHT: col++; break;
            }
        }
	}
	
    /**
     * Resets enemy completely when the game or enemy list is reinitialized.
     */
	public void resetAll() {
		setDefaultSpawn();
	}

    /**
     * Called when loading a new level.
     * The enemy will find a new spawn location in that map.
     */
	public void nextLevel() {
		setDefaultSpawn();
	}
	
    /**
     * Resets to the original spawn point when restarting the same level.
     */
	public void resetLevel() {
		col = spawnX;
		row = spawnY;
	}

    /**
     * Draws the Follow enemy as a pink square — visible only if inside
     * the player's active echo pulse visibility area.
     */
	@Override
	public void draw(Graphics2D g2) {
		if (!gp.getEchoPulse().getVisibilityMap()[row][col]) {
    	    return; // Enemy is hidden in the dark
    	}

    	g2.setColor(Color.PINK);
        g2.fillRect(col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE);
	}

    /**
     * Determines a suitable spawn point for the Follow enemy.
     * 
     * Logic:
     *  - Identifies all connected tunnels in the maze (via flood fill).
     *  - Avoids tunnels containing the player or level exit.
     *  - Requires the spawn to be far from the player and moderately far from the end.
     *  - Picks a random valid point from the remaining candidates.
     */
	public void setDefaultSpawn() {
	    int[][] levelMap = gp.getLevelManager().getCurrentLevel().getGrid();
	    int rows = levelMap.length;
	    int cols = levelMap[0].length;

	    int[][] tunnelIdMap = new int[rows][cols];
	    int nextTunnelId = 1;

	    // Assign each connected tunnel a unique ID via flood fill
	    for (int r = 0; r < rows; r++) {
	        for (int c = 0; c < cols; c++) {
	            if (levelMap[r][c] == TileType.PATH.getValue() && tunnelIdMap[r][c] == 0) {
	                floodFillTunnelId(r, c, nextTunnelId++, levelMap, tunnelIdMap);
	            }
	        }
	    }

	    // Identify the tunnels the player and level end are in
	    int playerRow = gp.getEntityManager().getPlayer().getRow();
	    int playerCol = gp.getEntityManager().getPlayer().getCol();
	    int playerTunnelId = tunnelIdMap[playerRow][playerCol];

	    int endRow = gp.getEntityManager().getEndOfLevel().getRow();
	    int endCol = gp.getEntityManager().getEndOfLevel().getCol();
	    int endTunnelId = tunnelIdMap[endRow][endCol];

	    List<Point> validSpawnPoints = new ArrayList<>();

	    // Search for candidate path tiles that satisfy constraints
	    for (int r = 0; r < rows; r++) {
	        for (int c = 0; c < cols; c++) {
	            if (levelMap[r][c] == TileType.PATH.getValue()) {
	                Point p = new Point(c, r);

	                int distFromPlayer = Math.abs(r - playerRow) + Math.abs(c - playerCol);
	                int distFromEnd = Math.abs(r - endRow) + Math.abs(c - endCol);

	                boolean farFromPlayer = distFromPlayer >= 15; // Keep distance from player
	                boolean farFromEnd = distFromEnd >= 3;        // Avoid spawning right at exit

	                int tileTunnelId = tunnelIdMap[r][c];
	                boolean notInPlayerTunnel = tileTunnelId != playerTunnelId;
	                boolean notInEndTunnel = tileTunnelId != endTunnelId;

	                // Only accept if outside player/end tunnels and properly distanced
	                if (farFromPlayer && farFromEnd && notInPlayerTunnel && notInEndTunnel) {
	                    validSpawnPoints.add(p);
	                }
	            }
	        }
	    }

	    // Choose a random valid point from the filtered list
	    if (!validSpawnPoints.isEmpty()) {
	        Point spawn = validSpawnPoints.get((int)(gp.getRandomDouble() * validSpawnPoints.size()));
	        spawnX = spawn.x;
	        spawnY = spawn.y;
	        col = spawnX;
	        row = spawnY;
	    } else {
	        System.out.println("No valid spawn location found for Follow Enemy!");
	    }
	}

    /**
     * Flood fill helper used to assign tunnel IDs to connected path regions.
     * Each distinct open area of the maze gets a unique identifier.
     */
	private void floodFillTunnelId(int startRow, int startCol, int tunnelId, int[][] map, int[][] idMap) {
	    Queue<Point> queue = new LinkedList<>();
	    queue.add(new Point(startCol, startRow));
	    idMap[startRow][startCol] = tunnelId;

	    while (!queue.isEmpty()) {
	        Point p = queue.poll();
	        for (int[] d : new int[][]{{-1,0}, {1,0}, {0,-1}, {0,1}}) {
	            int newR = p.y + d[0];
	            int newC = p.x + d[1];

	            if (newR >= 0 && newR < map.length && newC >= 0 && newC < map[0].length &&
	                map[newR][newC] == TileType.PATH.getValue() &&
	                idMap[newR][newC] == 0) {
	                idMap[newR][newC] = tunnelId;
	                queue.add(new Point(newC, newR));
	            }
	        }
	    }
	}
	
    /**
     * Spawn override — intentionally empty since Follow enemies use
     * procedural spawn logic from setDefaultSpawn().
     */
	@Override
	public void spawn(int col, int row) {
		// Not used: spawn logic handled automatically
	}
}
