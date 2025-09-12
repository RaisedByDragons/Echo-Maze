package entities.enemies;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import entities.player.Player;
import gameStart.GamePanel;
import levels.Level;
import utilz.Enums.Direction;
import utilz.Enums.TileType;
import utilz.PathFinding;

public class Follow extends Enemy{

 	private int pathTimer = 20;
    private int pathDeley = pathTimer; // frames between path recalculations = pathDelay*moveDelay or something
    int nextIndex;
    
    private List<Direction> currentPath;
	    
	public Follow(GamePanel gp) {
		super(gp);
		moveDelay = 45;
		
		setDefaultSpawn();
		col = spawnX;
        row = spawnY;
	}

	public void update() {
		
		if (moveCounter < moveDelay) {
	        moveCounter++;
	        return;
	    }
	    moveCounter = 0;
	    pathToPlayer();
		
    }
	
	private void pathToPlayer() {
		pathTimer++;  

        // Recalculate path periodically
        if (pathTimer >= pathDeley) {
//        	System.out.println("Calculating Path!");
            pathTimer = 0;
            
            PathFinding pathFinding = new PathFinding(gp, col, row);
            currentPath = pathFinding.pathDirections;
            nextIndex = 0;
            
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

        // Follow the path
        if (currentPath != null && nextIndex < currentPath.size()) {
        	Direction dir = currentPath.get(nextIndex);
            nextIndex++;
            switch (dir) { //direct change to row/col not recommended, but should be fine
                case UP:    row--; break;
                case DOWN:  row++; break;
                case LEFT:  col--; break;
                case RIGHT: col++; break;
            }
        }
        
	}
	
	
	public void resetAll() {
		setDefaultSpawn();
	}

	public void nextLevel() {
		setDefaultSpawn();
	}
	
	public void resetLevel() {
		col = spawnX;
		row = spawnY;
	}

	@Override
	public void draw(Graphics2D g2) {
		if (!gp.getEchoPulse().getVisibilityMap()[row][col]) {
    	    return; // Enemy is in the dark — skip drawing
    	}

    	g2.setColor(Color.PINK);
        g2.fillRect(col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE);
	}

	public void setDefaultSpawn() {
	    int[][] levelMap = gp.getLevelManager().getCurrentLevel().getGrid();
	    int rows = levelMap.length;
	    int cols = levelMap[0].length;

	    int[][] tunnelIdMap = new int[rows][cols];
	    int nextTunnelId = 1;

	    // Fill all tunnels with unique IDs
	    for (int r = 0; r < rows; r++) {
	        for (int c = 0; c < cols; c++) {
	            if (levelMap[r][c] == TileType.PATH.getValue() && tunnelIdMap[r][c] == 0) {
	                floodFillTunnelId(r, c, nextTunnelId++, levelMap, tunnelIdMap);
	            }
	        }
	    }

	    int playerRow = gp.getEntityManager().getPlayer().getRow();
	    int playerCol = gp.getEntityManager().getPlayer().getCol();
	    int playerTunnelId = tunnelIdMap[playerRow][playerCol];

	    int endRow = gp.getEntityManager().getEndOfLevel().getRow();
	    int endCol = gp.getEntityManager().getEndOfLevel().getCol();
	    int endTunnelId = tunnelIdMap[endRow][endCol];

	    List<Point> validSpawnPoints = new ArrayList<>();

	    for (int r = 0; r < rows; r++) {
	        for (int c = 0; c < cols; c++) {
	            if (levelMap[r][c] == TileType.PATH.getValue()) {
	                Point p = new Point(c, r);

	                int distFromPlayer = Math.abs(r - playerRow) + Math.abs(c - playerCol);
	                int distFromEnd = Math.abs(r - endRow) + Math.abs(c - endCol);

	                boolean farFromPlayer = distFromPlayer >= 15;
	                boolean farFromEnd = distFromEnd >= 3;

	                int tileTunnelId = tunnelIdMap[r][c];
	                boolean notInPlayerTunnel = tileTunnelId != playerTunnelId;
	                boolean notInEndTunnel = tileTunnelId != endTunnelId;

	                if (farFromPlayer && farFromEnd && notInPlayerTunnel && notInEndTunnel) {
	                    validSpawnPoints.add(p);
	                }
	            }
	        }
	    }

	    if (!validSpawnPoints.isEmpty()) {
	        Point spawn = validSpawnPoints.get((int)(Math.random() * validSpawnPoints.size()));
	        spawnX = spawn.x;
	        spawnY = spawn.y;
	        col = spawnX;
	        row = spawnY;
	    } else {
	        System.out.println("No valid spawn location found for Follow Enemy!");
	    }
	}

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
	
	
	@Override
	public void spawn(int col, int row) {
		
	}

}
