package levels;

import java.util.ArrayList;
import gameStart.GamePanel;
import utilz.Enums.Difficulty;
import utilz.Enums.GameState;

public class LevelManager {

	private int currentLevel, numLevels; // Tracks the current level index and total number of levels
	
	private ArrayList<Level> levelList = new ArrayList<>(); // Holds all normal (non-tutorial) levels
	private ArrayList<Level> tutorialList; // Holds tutorial levels
	
	private boolean isTutorial = true; // Determines if the game is currently in tutorial mode
	
	private GamePanel gp; // Reference to the main GamePanel for accessing shared data and state
	
	// Constructor: Initializes LevelManager with reference to GamePanel
	public LevelManager(GamePanel gp) {
		this.gp = gp;
	}
	
	// Sets how many levels the game should have, and generates them if not already created
	public void setNumLevels(int numLevels) {
		// If no levels exist yet, generate all new ones
		if (this.numLevels == 0) {
			generateLevels(numLevels, gp.getDiff());
		}
		// If new number is greater, add extra levels
		if (numLevels > this.numLevels) {
			createLevel(numLevels - this.numLevels, gp.getDiff());
		} else {
			// If new number is smaller, remove excess levels
			for (int i = numLevels; i > this.numLevels; i--) {
				levelList.remove(numLevels - 1);
			}	
		}
		this.numLevels = numLevels; // Update stored level count
	}
	
	// Generates a full list of levels, including tutorial levels
	private void generateLevels(int num, Difficulty diff) {
		// Randomly choose a starting x position, and start from bottom of screen
		int x = 1 + 2*(int)(gp.getRandomDouble() * (gp.MAX_SCREEN_WIDTH - 2) / 2);
		int y = gp.MAX_SCREEN_HEIGHT;
		int[] coords = {x, y};
		
		// Create tutorial level list
		tutorialList = new TutorialLevel(gp).createTutorialLevel();

		// Generate the specified number of normal levels
		for (int i = 0; i < num; i++) {
			Level lv = new Level(gp);
			lv.generateMaze(diff, x, y); // Generate maze based on difficulty and start position
			coords = lv.getExit(); // Store exit position for connecting levels
			coords[1] = coords[1] + gp.MAX_SCREEN_HEIGHT; // Offset for next level’s placement
			
			levelList.add(lv); // Add level to list
		}		
	}
	
	// Creates and returns a list of new levels without affecting the existing list
	public ArrayList<Level> createLevel(int num, Difficulty diff) {
		int x = 1 + 2*(int)(gp.getRandomDouble() * (gp.MAX_SCREEN_WIDTH - 2) / 2);
		int y = gp.MAX_SCREEN_HEIGHT;
		int[] coords = {x, y};
		
		ArrayList<Level> levelList = new ArrayList<Level>();
		
		// Generate specified number of levels
		for (int i = 0; i < num; i++) {
			Level lv = new Level(gp);
			lv.generateMaze(diff, x, y);
			coords = lv.getExit();
			coords[1] = coords[1] + gp.MAX_SCREEN_HEIGHT;
			levelList.add(lv);
		}
		return levelList;
	}
	
	// Determines if a tile is solid (non-walkable)
	public boolean isSolidTile(int tileNum) {
		boolean result;
		switch (tileNum) {
		case 0 -> result = true;  // Wall
		case 1 -> result = false; // Path
		case 2 -> result = false; // End/Exit tile
		default -> result = false;
		}
		return result;
	}
	
	// Resets progress to the beginning of the game or tutorial
	public void resetAll() {
		if (isTutorial) {
			tutorialList.get(currentLevel).setTutorialStage(0); // Reset tutorial progress
		}
		currentLevel = 0; // Go back to first level
	}
	
	// Resets current level’s tutorial stage without changing the level
	public void resetLevel() {
		if (isTutorial) {
			tutorialList.get(currentLevel).setTutorialStage(0);
		}
	}
	
	// Returns the currently active level (tutorial or normal)
	public Level getCurrentLevel() {
		if (isTutorial) {
			return tutorialList.get(currentLevel);
		}
		return levelList.get(currentLevel);
	}
	
	// Returns the current level number (index)
	public int getCurrentLevelNum() {
		return currentLevel;
	}
	
	// Returns the full list of levels (tutorial or normal)
	public ArrayList<Level> getLevelList() {
		if (isTutorial) {
			return tutorialList;
		}
		return levelList;
	}
	
	// Moves the game to the next level, or transitions out of tutorial mode
	public void nextLevel() {
		if (isTutorial) {
			// If more tutorial levels remain, advance
			if (currentLevel < (tutorialList.size() - 1)) {
				currentLevel++;
			} else {
				// If tutorial complete, switch to normal game
				isTutorial = false;
				gp.resetAll();
			}
		} else {
			// For normal levels, progress or end game
			if (currentLevel < numLevels) {
				currentLevel++;
			} else {
				// Player wins when all levels are completed
				gp.setGameState(GameState.WINNER);
				gp.resetAll();
			}
		}
	}
	
	// Returns the total number of normal levels
	public int getNumLevels() {
		return numLevels;
	}
	
	// Returns whether the game is in tutorial mode
	public boolean isTutorial() {
		return isTutorial;
	}

	// Enables/disables tutorial mode and resets progress
	public void setIsTutorial(boolean b) {
		isTutorial = b;
		gp.resetAll();
	}
}
