package entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import gameStart.GamePanel;
import levels.LevelManager;
import utilz.Enums.Difficulty;

/**
 * Base abstract class for all game entities (player, enemies, end-of-level, etc.).
 * Provides shared position, movement, and interaction logic.
 */
public abstract class Entity {

    /** Current row position of the entity on the grid */
	protected int row;
    /** Current column position of the entity on the grid */
	protected int col;
    /** Default X spawn coordinate */
	protected int spawnX;
    /** Default Y spawn coordinate */
	protected int spawnY;
    /** Directional X offset for movement */
	protected int dX;
    /** Directional Y offset for movement */
	protected int dY;
	
    /** Reference to the main GamePanel controlling the game loop */
	protected GamePanel gp;
    /** Collision box for detecting solid tiles or interactions */
	protected Rectangle solidArea;
	
    /** Current game difficulty setting */
	protected Difficulty difficulty;
	
    /**
     * Constructs a new Entity and stores the GamePanel reference.
     * @param gp the main GamePanel instance managing the game state
     */
	public Entity(GamePanel gp) {
		this.gp = gp;
		difficulty = gp.getDiff();
	}
	
    /**
     * Checks whether the entity can move to the specified direction (dX, dY)
     * based on level boundaries and solid tile collisions.
     *
     * @param dX horizontal movement offset (-1, 0, or 1)
     * @param dY vertical movement offset (-1, 0, or 1)
     * @param levelM reference to the current LevelManager
     * @return true if the next tile is walkable and within bounds; false otherwise
     */
	protected boolean canMove(int dX, int dY, LevelManager levelM) {

		int newRow = row + dY;
		int newCol = col + dX;
		if (newRow >= 0 && newRow < levelM.getCurrentLevel().getGrid().length &&
		    newCol >= 0 && newCol < levelM.getCurrentLevel().getGrid()[0].length &&
		    !(levelM.isSolidTile(levelM.getCurrentLevel().getGrid()[newRow][newCol]))) {

		    return true;
		}
		
		return false;
	}
	
    /** 
     * Updates the entity's logic (movement, behavior, etc.) every game tick.
     */
	public abstract void update();

    /**
     * Resets all entity attributes, typically used when restarting the game.
     */
	public abstract void resetAll();

    /**
     * Resets the entity state for the current level only (used on level restart).
     */
	public abstract void resetLevel();

    /**
     * Prepares the entity for the next level (e.g., repositions or refreshes state).
     */
	public abstract void nextLevel();

    /**
     * Renders the entity's appearance to the screen.
     * @param g2 the Graphics2D object used for drawing
     */
	public abstract void draw(Graphics2D g2);

    /**
     * Finds and sets the entity’s default spawn position in the level grid.
     */
	public abstract void setDefaultSpawn();

    /**
     * Spawns the entity manually at a given column and row.
     * @param col target column position
     * @param row target row position
     */
	public abstract void spawn(int col, int row);
	
    /**
     * Gets the entity's current grid row position.
     * @return current row coordinate
     */
	public int getRow() {
		return row;
	}

    /**
     * Gets the entity's current grid column position.
     * @return current column coordinate
     */
	public int getCol() {
		return col;
	}
}
