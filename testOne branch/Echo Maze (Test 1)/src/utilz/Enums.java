package utilz;

// Container class for all enums used in the game
public class Enums {

    // Difficulty levels for the game
	public enum Difficulty {
	    BABY, EASY, MEDIUM, HARD, EXTREME, CUSTOM
	}
	
	// Directions for movement or orientation
	public enum Direction {
	    UP, RIGHT, DOWN, LEFT;
	}
	
	// Represents the different states/screens of the game
	public enum GameState {
		MENU,       // Main menu screen
        PLAYING,    // Game in progress
        PAUSED,     // Game paused
        GAME_OVER,  // Player lost
        WINNER,     // Player won
        SETTINGS;   // Settings screen
	}
	
	// Types of tiles in a level
	public enum TileType {
		WALL(0),  // Impassable tile
        PATH(1),  // Walkable tile
        END(2),   // End-of-level tile
        START(3); // Player start tile

		private final int value; // Numeric value for the tile type
		
		// Constructor to associate an int value with the tile type
		private TileType(int value) {
			this.value = value;
		}

        // Getter for the numeric value
		public int getValue() {
	        return value;
	    }
	}
}
