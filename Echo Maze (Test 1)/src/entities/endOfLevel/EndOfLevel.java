package entities.endOfLevel;

import java.awt.Graphics2D;

import entities.Entity;
import gameStart.GamePanel;
import utilz.Enums.TileType;

public class EndOfLevel extends Entity {
	
	public EndOfLevel(GamePanel gp) {
		super(gp);
		
		setDefaultSpawn();
	}
	
	public void setDefaultSpawn() {
		int[][] grid = gp.getLevelManager().getCurrentLevel().getGrid();

        for (int row = 0; row < gp.MAX_SCREEN_HEIGHT; row++) {
            for (int col = 0; col < gp.MAX_SCREEN_WIDTH; col++) {
                if (grid[row][col] == TileType.END.getValue()) {
                    spawnX = col;
                    spawnY = row;
                    this.row = row;
                    this.col = col;
                    return; // Exit once START is found
                }
            }
        }
	}
	public void spawn(int col, int row) {
		// TODO Auto-generated method stub
	}
	
	public void draw(Graphics2D g2) {
//		g2.drawRect(col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE); //Debug
	}

	public void resetAll() {
		setDefaultSpawn();
	}
	
	public void nextLevel() {
		setDefaultSpawn();
	}
	
	public void resetLevel() {
		setDefaultSpawn();
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}
