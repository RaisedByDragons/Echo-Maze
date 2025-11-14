package levels;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import gameStart.GamePanel;
import mechanics.EchoPulse;
import utilz.Enums.Difficulty;
import utilz.Enums.GameState;

public class LevelManager {

	private int currentLevel, numLevels;

//	private BufferedImage image;
//	public boolean collision = false;
	ArrayList<Level> levelList = new ArrayList<>();
	
	GamePanel gp;
	EchoPulse echo;
	Difficulty diff;
	
	public LevelManager(GamePanel gp, int numLevels, Difficulty diff, EchoPulse echo) {
		currentLevel = 0;
		this.gp = gp;
		this.numLevels = numLevels;
		this.diff = diff;
		this.echo = echo;
		
		generateLevels(numLevels, diff);

		
	}
	
	public void setNumLevels(int numLevels) {
		this.numLevels = numLevels;
		
		levelList = new ArrayList<>();
		generateLevels(numLevels, diff);
	}
	
	private void generateLevels(int num, Difficulty diff) {
		int x = 1 + 2*(int)(Math.random() * (gp.MAX_SCREEN_WIDTH - 2) / 2);
//		x = x*2+1;
//		int y = (int)(Math.random() * (gp.MAX_SCREEN_HEIGHT - 1) + 1);
		int y = gp.MAX_SCREEN_HEIGHT;
		int[] coords = {x,y};
		
		for (int i=0; i<num; i++) {
			Level lv = new Level(gp, echo);
			
			lv.generateMaze(diff, x, y);
			coords = lv.getExit();
			coords[1] = coords[1]+gp.MAX_SCREEN_HEIGHT;
			
			levelList.add(lv);
			
//			i, levelList.get(i).generateMaze(diff, x, y);
		}
		
	}
	
	public boolean isSolidTile(int tileNum) {//0,1,2
//		for(Tile t : levelList.get(currentLevel).tileList) { //want to use collision variable
		boolean result;
		switch (tileNum) {
		case 0 -> result = true; //Wall
		case 1 -> result = false; //Path
		case 2 -> result = false; //End
		default -> result = false;
		}
//		}
		return result;
	}
	
	public boolean isInBounds(int row, int col) {
		return row >= 0 && row < gp.MAX_SCREEN_HEIGHT &&
				col >= 0 && col < gp.MAX_SCREEN_WIDTH;
	}
	
	public void reset() {
		currentLevel = 0;
	}
	
	public Level getCurrentLevel() {
		return levelList.get(currentLevel);
	}
	
	public int getCurrentLevelNum() {
		return currentLevel;
	}
	
	public ArrayList<Level> getLevelList() {
		return levelList;
	}
	
	public void nextLevel() {
		if(currentLevel < numLevels - 1) {
			currentLevel++;
		} else {
			gp.setGameState(GameState.WINNER);
		}
	}

	public void setDifficulty(Difficulty diff) {
		this.diff = diff;
	}
	
}
