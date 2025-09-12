package levels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import gameStart.GamePanel;
import utilz.Enums.Difficulty;
import utilz.Enums.TileType;

public class TutorialLevel extends Level{

	private boolean isTutorial = true;
	
	public TutorialLevel(GamePanel gp) {
		super(gp);
	}
	
	public ArrayList<Level> createTutorialLevel() {
	    ArrayList<Level> levelList = new ArrayList<>();

	    String[] tutorialFiles = { "tutorial0.txt", "tutorial1.txt", "tutorial2.txt" };
//	    int levelsToLoad = Math.min(numToCreate, tutorialFiles.length);

	    for (int i = 0; i < tutorialFiles.length; i++) {
	        int[][] grid = loadGridFromFile(tutorialFiles[i]);

	        if (grid != null) {
//	            System.out.println("Loaded " + tutorialFiles[i]); // ADD THIS LINE

	            Level level = new Level(gp);
	            level.setGrid(grid);
	            level.setShowInstructions(isTutorial, i);
	            levelList.add(level);
	        }
	    }

	    return levelList;
	}
	
	private int[][] loadGridFromFile(String filename) {
	    ArrayList<int[]> rows = new ArrayList<>();

	    try (BufferedReader br = new BufferedReader(new FileReader("res/levels/" + filename))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            String[] tokens = line.trim().split("\\s+");
	            int[] row = new int[tokens.length];
	            for (int i = 0; i < tokens.length; i++) {
	                row[i] = Integer.parseInt(tokens[i]);
	            }
	            rows.add(row);
	        }

	        int[][] grid = new int[rows.size()][rows.get(0).length];
	        for (int i = 0; i < rows.size(); i++) {
	            grid[i] = rows.get(i);
	        }

	        return grid;

	    } catch (IOException | NumberFormatException e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	public boolean isTutorial() {
		return isTutorial;
	}

	public void setTutorial(boolean isTutorial) {
		this.isTutorial = isTutorial;
	}
}
