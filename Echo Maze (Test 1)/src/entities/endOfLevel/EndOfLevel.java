package entities.endOfLevel;

import java.awt.Graphics2D;

import entities.Entity;
import gameStart.GamePanel;
import utilz.Enums.TileType;

/**
 * Represents the end point or goal of a level in Echo Maze.
 * When the player reaches this entity, the level is completed.
 */
public class EndOfLevel extends Entity {
	
	/**
	 * Constructor for the EndOfLevel entity.
	 * Initializes the entity with a reference to the GamePanel and sets its default spawn point.
	 * 
	 * @param gp the current GamePanel instance
	 */
	public EndOfLevel(GamePanel gp) {
		super(gp);
		
		setDefaultSpawn();
	}
	
	/**
	 * Searches the current level's grid for the END tile type
	 * and sets the entity's spawn coordinates (col, row) to that location.
	 * 
	 * This ensures the EndOfLevel entity always appears where the END tile is placed.
	 */
	public void setDefaultSpawn() {
		int[][] grid = gp.getLevelManager().getCurrentLevel().getGrid();

        // Loop through every tile in the level grid
        for (int row = 0; row < gp.MAX_SCREEN_HEIGHT; row++) {
            for (int col = 0; col < gp.MAX_SCREEN_WIDTH; col++) {
                // If an END tile is found, record its location and stop searching
                if (grid[row][col] == TileType.END.getValue()) {
                    spawnX = col;
                    spawnY = row;
                    this.row = row;
                    this.col = col;
                    return; // Exit once END is found
                }
            }
        }
	}
	
	/**
	 * Placeholder spawn method (currently unused).
	 * Could be expanded to manually set the spawn position if needed.
	 */
	public void spawn(int col, int row) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Draws the EndOfLevel entity on the screen.
	 * Currently commented out; the debug line can be re-enabled to visualize the END tile's location.
	 */
	public void draw(Graphics2D g2) {
//		g2.drawRect(col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE); // Debug visualization
	}

	/**
	 * Resets the EndOfLevel entity's position to its default location.
	 * Called when restarting or resetting the game state.
	 */
	public void resetAll() {
		setDefaultSpawn();
	}
	
	/**
	 * Moves the EndOfLevel entity to its correct location
	 * when transitioning to a new level.
	 */
	public void nextLevel() {
		setDefaultSpawn();
	}
	
	/**
	 * Repositions the EndOfLevel entity to its starting location
	 * when the current level is restarted.
	 */
	public void resetLevel() {
		setDefaultSpawn();
	}

	/**
	 * Updates the EndOfLevel entity each frame.
	 * Currently not used, but exists for future expansion (e.g., animations or triggers).
	 */
	@Override
	public void update() {
		// TODO Auto-generated method stub
	}
}
