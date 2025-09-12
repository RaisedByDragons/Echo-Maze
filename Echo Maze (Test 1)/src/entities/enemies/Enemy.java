package entities.enemies;

import java.util.LinkedList;
import java.util.Queue;

import entities.Entity;
import gameStart.GamePanel;
import utilz.HelpMethods;
import utilz.Enums.Direction;
import utilz.Enums.TileType;

public abstract class Enemy extends Entity {

	protected int moveDelay; // number of updates between moves
    protected int moveCounter;
    
    protected Direction direction;// = Direction.UP;
    
    public Enemy (GamePanel gp) {
    	super(gp);
    }
    
    protected void moveForward() {
        col += HelpMethods.dX(direction);
        row += HelpMethods.dY(direction);
    }
    protected void spawnNearestTile(int defaultCol, int defaultRow) {
    	int[][] levelMap = gp.getLevelManager().getCurrentLevel().getGrid();
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
                        col = spawnX;
                		row = spawnY;
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

	public abstract void update();
}
