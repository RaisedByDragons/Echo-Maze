package entities;

import java.awt.Color;
import utilz.HelpMethods;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import gameStart.GamePanel;
import levels.LevelManager;
import mechanics.EchoPulse;
import utilz.Enums.Direction;
import utilz.Enums.TileType;

public class BasicEnemy extends Entity{

//	private List<Point> path; // patrol path (tile coordinates)
//    private int pathIndex = 0;
    private int moveDelay = 30; // number of updates between moves
    private int moveCounter = 0;
    
    private boolean revealed = false;

    
    private LevelManager levelM;
    private GamePanel gp;
    Direction dir = Direction.UP;
//    private int heading;


    public BasicEnemy(GamePanel gp, LevelManager levelM) {
        this.levelM = levelM;
        this.gp = gp;
        
        //default
        spawn();
//        Math.random() * (max - min) + min;
        col = spawnX;
        row = spawnY;
    }
    
    public void spawn(int defaultCol, int defaultRow) {
    	int[][] levelMap = levelM.getCurrentLevel().getGrid();
        if (levelMap[defaultRow][defaultCol] == 0) {
            spawnX = defaultCol;
            spawnY = defaultRow;
            return;
        }

        spawnNearestTile(defaultCol, defaultRow);
    }
    
    public void spawn() { //Default spawn (random)
    	spawnNearestTile((int)(Math.random() * (gp.MAX_SCREEN_WIDTH - 1) + 1),
    			(int)(Math.random() * (gp.MAX_SCREEN_HEIGHT - 1) + 1));
    }
    
    public void nextLevel() {
    	spawn();
    }

    
    public void update() {
        moveCounter++;
        
        if (moveCounter < moveDelay) return;

        moveCounter = 0;
//        heading = 0;
        followRightWall(dir);
    }
    
    private void followRightWall(Direction dire) {
        // Try to turn right
        Direction right = HelpMethods.turnRight(dire);
        if (canMove(HelpMethods.dX(right), HelpMethods.dY(right), levelM)) {
            dir = right;
            moveForward();
            return;
        }

        // Try to go straight
        if (canMove(HelpMethods.dX(dire), HelpMethods.dY(dire), levelM)) {
            dir = dire;
            moveForward();
            return;
        }

        // Try to turn left
        Direction left = HelpMethods.turnLeft(dire);
        if (canMove(HelpMethods.dX(left), HelpMethods.dY(left), levelM)) {
            dir = left;
            moveForward();
            return;
        }

        // Try to turn around (dead end)
        Direction back = HelpMethods.turnBack(dire);
        if (canMove(HelpMethods.dX(back), HelpMethods.dY(back), levelM)) {
            dir = back;
            moveForward();
            return;
        }
    }
    
    private void moveForward() {
        col += HelpMethods.dX(dir);
        row += HelpMethods.dY(dir);
    }

    public void draw(Graphics2D g2) {
    	if (!gp.getEcho().getVisibilityMap()[row][col]) {
//    		revealed = false;
    	    return; // Enemy is in the dark — skip drawing
    	}
//    	if (!revealed) {
//    		return;
//    	}
    	g2.setColor(Color.RED);
        g2.fillRect(col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE);

    }
    
    public void reset() {
    	col = spawnX;
    	row = spawnY;
    	spawn();
    	dir = Direction.UP;
    }
    
    private void spawnNearestTile(int defaultCol, int defaultRow) {
    	int[][] levelMap = levelM.getCurrentLevel().getGrid();
    	// BFS to find the nearest walkable tile
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[levelMap.length][levelMap[0].length];

        queue.add(new int[]{defaultRow, defaultCol});
        visited[defaultRow][defaultCol] = true;

        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}  // up, down, left, right
        };

        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int r = pos[0];
            int c = pos[1];

            for (int[] d : directions) {
                int newR = r + d[0];
                int newC = c + d[1];

                if (newR >= 0 && newR < levelMap.length &&
                    newC >= 0 && newC < levelMap[0].length &&
                    !visited[newR][newC]) {

                    if (levelMap[newR][newC] == TileType.PATH.getValue()) {
                        spawnY = newR;
                        spawnX = newC;
                        return;
                    }

                    visited[newR][newC] = true;
                    queue.add(new int[]{newR, newC});
                }
            }
        }

        // If we reach here, no spawnable tile was found
        System.out.println("No valid spawn location found!");
    }
    
    public void updateVisibilityFromPulse(EchoPulse echo) {
        if (echo.isActive() && echo.isTileVisible(col, row)) {
            revealed = true;
        }
    }
    
    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }
}
