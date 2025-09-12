package levels;

import java.util.ArrayList;
import gameStart.GamePanel;
import utilz.Enums.Difficulty;
import utilz.Enums.GameState;

public class LevelManager {

	private int currentLevel, numLevels;
	
	private ArrayList<Level> levelList = new ArrayList<>();
	private ArrayList<Level> tutorialList;
	
	private boolean isTutorial = true;
	
	private GamePanel gp;
	
	public LevelManager(GamePanel gp) {
//		currentLevel = 0;
//		numLevels = 2;
		this.gp = gp;
//		generateLevels(numLevels, gp.getDiff());	
	}
	
	public void setNumLevels(int numLevels) {
		this.numLevels = numLevels;
		levelList = new ArrayList<>();
		generateLevels(numLevels, gp.getDiff());
	}
	
	private void generateLevels(int num, Difficulty diff) {
		int x = 1 + 2*(int)(Math.random() * (gp.MAX_SCREEN_WIDTH - 2) / 2);
		int y = gp.MAX_SCREEN_HEIGHT;
		int[] coords = {x,y};
		
		tutorialList = new TutorialLevel(gp).createTutorialLevel();

		for (int i=0; i<num; i++) {
			Level lv = new Level(gp);
			
			lv.generateMaze(diff, x, y);
			coords = lv.getExit();
			coords[1] = coords[1]+gp.MAX_SCREEN_HEIGHT;
			
			levelList.add(lv);
			
		}		
	}
	
	public boolean isSolidTile(int tileNum) {//0,1,2
		boolean result;
		switch (tileNum) {
		case 0 -> result = true; //Wall
		case 1 -> result = false; //Path
		case 2 -> result = false; //End
		default -> result = false;
		}
		return result;
	}
	
	public void resetAll() {
		currentLevel = 0;
	}
	
	public void resetLevel() {
		
	}
	
	public Level getCurrentLevel() {
		if (isTutorial) {
			
			return tutorialList.get(currentLevel);
		}
//		if(currentLevel >= levelList.size()) {
//			return null;
//		}
		
		return levelList.get(currentLevel);
	}
	
	public int getCurrentLevelNum() {
		return currentLevel;
	}
	
	public ArrayList<Level> getLevelList() {
		if (isTutorial) {
			return tutorialList;
		}
		return levelList;
	}
	
	public void nextLevel() {
		if (isTutorial) {
			if (currentLevel < (tutorialList.size() - 1)) {
				currentLevel++;
			} else {
				for (int i=0; i<(tutorialList.size() - 1); i++) {
					levelList.remove(i);
				}
				isTutorial = false;
				gp.resetAll();
			}
		} else {
			if(currentLevel < numLevels) {
				currentLevel++;
			} else {
				gp.setGameState(GameState.WINNER);
			}
		}
	}
	
	public int getNumLevels() {
		return numLevels;
	}
	
	public boolean isTutorial() {
		return isTutorial;
	}

	public void setIsTutorial(boolean b) {
		isTutorial = b;		
	}
}
