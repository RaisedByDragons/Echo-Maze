package entities;

import java.awt.Graphics2D;

import gameStart.GamePanel;
import levels.LevelManager;
import utilz.Enums.TileType;

public class EndOfMaze extends Entity{

	private GamePanel gp;
	private LevelManager levelM;
	
	public EndOfMaze(GamePanel gp, LevelManager levelM) {
		this.gp = gp;
		this.levelM = levelM;
		spawn();
	}
	
	private void spawn() {
		for (int i=1; i<gp.MAX_SCREEN_WIDTH-1; i++) {
			   if (levelM.getCurrentLevel().getGrid()[0][i] == TileType.END.getValue()) {
				   spawnX = i;
				   spawnY = 0;
			   }
		   }
//		   spawnX = 8;
//		   spawnY = 15;
		   row = spawnY;
		   col = spawnX; 
		
	}
	
	//debug
	public void draw(Graphics2D g2) {
//		g2.drawRect(col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE);
	}

	public void reset() {
		spawn();
	}
	
	public void nextLevel() {
		spawn();
	}
}
