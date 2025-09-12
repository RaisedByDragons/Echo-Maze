package entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import gameStart.GamePanel;
import levels.LevelManager;
import utilz.Enums.Difficulty;

public abstract class Entity {

	protected int row, col, spawnX, spawnY;
	protected int dX, dY;
	
	protected GamePanel gp;
	protected Rectangle solidArea;
	
	protected Difficulty difficulty;
	
	public Entity(GamePanel gp) {
		this.gp = gp;
		difficulty = gp.getDiff();
	}
	
	protected boolean canMove(int dX, int dY, LevelManager levelM) {

		int newRow = row+dY;
		int newCol = col+dX;
		if (newRow >= 0 && newRow < levelM.getCurrentLevel().getGrid().length &&
			       newCol >= 0 && newCol < levelM.getCurrentLevel().getGrid()[0].length &&
			       !(levelM.isSolidTile(levelM.getCurrentLevel().getGrid()[newRow][newCol]))) {

			       return true;
			   }
		return false;
	}
	
	public abstract void update();
	public abstract void resetAll();
	public abstract void resetLevel();
	public abstract void nextLevel();
	public abstract void draw(Graphics2D g2);
	public abstract void setDefaultSpawn();
	public abstract void spawn(int col, int row);
	
	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
}