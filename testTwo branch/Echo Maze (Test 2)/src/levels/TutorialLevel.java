package levels;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import gameStart.GamePanel;

public class TutorialLevel extends Level {

    // Flag that identifies this level as a tutorial
    private boolean isTutorial = true;
    
    // Constructor: passes the GamePanel reference to the superclass (Level)
    public TutorialLevel(GamePanel gp) {
        super(gp);
    }
    
    // Creates a list of tutorial levels by reading multiple tutorial grid files
    public ArrayList<Level> createTutorialLevel() {
        ArrayList<Level> levelList = new ArrayList<>();

        // List of tutorial text files that define level layouts
        String[] tutorialFiles = { "tutorial0.txt", "tutorial1.txt", "tutorial2.txt" };

        // Loop through each tutorial file and build a Level from it
        for (int i = 0; i < tutorialFiles.length; i++) {
            // Load the level's tile grid data from the file
            int[][] grid = loadGridFromFile(tutorialFiles[i]);

            // If the grid loaded successfully, create and configure a Level
            if (grid != null) {
                Level level = new Level(gp); // Create a new level instance
                level.setGrid(grid);         // Apply the grid layout
                level.setShowInstructions(isTutorial, i); // Show tutorial instructions
                levelList.add(level);        // Add to list of tutorial levels
            }
        }

        // Return all generated tutorial levels
        return levelList;
    }
    
    // Loads a level grid from a text file and converts it to a 2D int array
    private int[][] loadGridFromFile(String filename) {
        ArrayList<int[]> rows = new ArrayList<>();

        // Try-with-resources automatically closes the file reader
        try (BufferedReader br = new BufferedReader(new FileReader("res/levels/" + filename))) {
            String line;
            // Read each line of the file
            while ((line = br.readLine()) != null) {
                // Split the line by spaces to get tile values
                String[] tokens = line.trim().split("\\s+");
                int[] row = new int[tokens.length];

                // Convert each value from String to int
                for (int i = 0; i < tokens.length; i++) {
                    row[i] = Integer.parseInt(tokens[i]);
                }

                // Add the row to the list of all rows
                rows.add(row);
            }

            // Convert ArrayList of rows into a 2D array
            int[][] grid = new int[rows.size()][rows.get(0).length];
            for (int i = 0; i < rows.size(); i++) {
                grid[i] = rows.get(i);
            }

            // Return the completed 2D grid
            return grid;

        } catch (IOException | NumberFormatException e) {
            // Print any file or parsing errors
            e.printStackTrace();
            return null;
        }
    }

    // Returns whether this is a tutorial level
    public boolean isTutorial() {
        return isTutorial;
    }

    // Sets whether this level should behave as a tutorial
    public void setTutorial(boolean isTutorial) {
        this.isTutorial = isTutorial;
    }
}
