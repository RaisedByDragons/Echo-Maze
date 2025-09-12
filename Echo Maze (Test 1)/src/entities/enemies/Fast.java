package entities.enemies;

import java.awt.Color;
import utilz.HelpMethods;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import gameStart.GamePanel;
import utilz.Enums.Direction;
import utilz.Enums.TileType;

public class Fast extends Enemy {
	
    public Fast(GamePanel gp) {
        super(gp);
        
        setDefaultSpawn();
        moveDelay = 15;
        col = spawnX;
        row = spawnY;
        direction = Direction.UP;
    }
    
    public void spawn(int col, int row) {
    	int[][] levelMap = gp.getLevelManager().getCurrentLevel().getGrid();
        if (levelMap[row][col] == 0) {
            spawnX = col;
            spawnY = row;
            return;
            
        }

        spawnNearestTile(col, row);
    }
    
    public void setDefaultSpawn() {
        int maxAttempts = 1000;
        int attempt = 0;
        int spawnRow, spawnCol;

        do {
            spawnCol = (int)(Math.random() * (gp.MAX_SCREEN_WIDTH - 1) + 1);
            spawnRow = (int)(Math.random() * (gp.MAX_SCREEN_HEIGHT - 1) + 1);
            attempt++;
        } while ((!isValidSpawn(spawnRow, spawnCol) || isInPlayerDeadEnd(spawnRow, spawnCol)) && attempt < maxAttempts);

        spawnNearestTile(spawnCol, spawnRow);
    }
    
    private boolean isValidSpawn(int row, int col) {
        int playerRow = gp.getEntityManager().getPlayer().getRow();
        int playerCol = gp.getEntityManager().getPlayer().getCol();

        int dist = Math.abs(playerRow - row) + Math.abs(playerCol - col); // Manhattan Distance
        return dist >= 10;
    }
    
    private boolean isInPlayerDeadEnd(int row, int col) {
        int[][] grid = gp.getLevelManager().getCurrentLevel().getGrid();
        int playerRow = gp.getEntityManager().getPlayer().getRow();
        int playerCol = gp.getEntityManager().getPlayer().getCol();

        // Only check if spawn is in the same tunnel as the player
        if (!isSameTunnel(playerRow, playerCol, row, col, grid)) return false;

        int openPaths = 0;
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int[] d : dirs) {
            int r = playerRow + d[0];
            int c = playerCol + d[1];
            if (r >= 0 && r < grid.length && c >= 0 && c < grid[0].length && grid[r][c] == TileType.PATH.getValue()) {
                openPaths++;
            }
        }

        return openPaths <= 1; // Player in dead-end
    }
    
    private boolean isSameTunnel(int r1, int c1, int r2, int c2, int[][] grid) {
        boolean[][] visited = new boolean[grid.length][grid[0].length];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{r1, c1});
        visited[r1][c1] = true;

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            if (curr[0] == r2 && curr[1] == c2) return true;

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

        return false;
    }
    
    public void update() {
        moveCounter++;
        
        if (moveCounter < moveDelay) return;

        moveCounter = 0;
        followRandomWall(direction);
    }
    
    private void followRandomWall(Direction dire) {
        Direction back = HelpMethods.turnBack(dire);

        // Gather all valid directions except backwards
        List<Direction> validDirs = new ArrayList<>();
        Direction[] possibleDirs = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};

        for (Direction d : possibleDirs) {
            if (d != back && canMove(HelpMethods.dX(d), HelpMethods.dY(d), gp.getLevelManager())) {
                validDirs.add(d);
            }
        }

        // At a junction (more than one valid option besides back)
        if (validDirs.size() > 1) {
            direction = validDirs.get((int)(Math.random() * validDirs.size()));
            moveForward();
            return;
        }

        // If we can keep going forward, do so
        if (canMove(HelpMethods.dX(dire), HelpMethods.dY(dire), gp.getLevelManager())) {
            moveForward();
            return;
        }

        // Try right
        Direction right = HelpMethods.turnRight(dire);
        if (canMove(HelpMethods.dX(right), HelpMethods.dY(right), gp.getLevelManager())) {
            direction = right;
            moveForward();
            return;
        }

        // Try left
        Direction left = HelpMethods.turnLeft(dire);
        if (canMove(HelpMethods.dX(left), HelpMethods.dY(left), gp.getLevelManager())) {
            direction = left;
            moveForward();
            return;
        }

        // If all else fails, try going back (dead end)
        if (canMove(HelpMethods.dX(back), HelpMethods.dY(back), gp.getLevelManager())) {
            direction = back;
            moveForward();
        }
    }


    public void draw(Graphics2D g2) {
    	if (!gp.getEchoPulse().getVisibilityMap()[row][col]) {
    	    return; // Enemy is in the dark — skip drawing
    	}

    	g2.setColor(Color.BLUE);
        g2.fillRect(col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE);

    }
    
    public void resetAll() {
    	setDefaultSpawn();
    	direction = Direction.UP;
    }
    
    public void nextLevel() {
    	setDefaultSpawn();
    }
    
    public void resetLevel() {
    	col = spawnX;
    	row = spawnY;
    	direction = Direction.UP;
    }
}