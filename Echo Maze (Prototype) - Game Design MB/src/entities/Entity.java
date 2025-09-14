package entities;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import gameStart.GamePanel;
import levels.LevelManager;

abstract class Entity {

//	public int worldX, worldY;
	protected int row, col, spawnX, spawnY;
	protected int dX, dY;
//	private int speed;
	
//	private BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
//	private String direction;
	
//	private int spriteCounter = 0;
//	public int spriteNum = 1;
	
	protected Rectangle solidArea;
//	public boolean collisionOn = false;
	
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
	
	abstract void reset();
	abstract void nextLevel();
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int getSpawnX() {
		return spawnX;
	}

	public void setSpawnX(int spawnX) {
		this.spawnX = spawnX;
	}

	public int getSpawnY() {
		return spawnY;
	}

	public void setSpawnY(int spawnY) {
		this.spawnY = spawnY;
	}
	
	
}
