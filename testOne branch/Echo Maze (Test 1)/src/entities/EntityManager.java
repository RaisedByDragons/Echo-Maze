package entities;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import entities.endOfLevel.EndOfLevel;
import entities.enemies.Basic;
import entities.enemies.Enemy;
import entities.enemies.Fast;
import entities.enemies.Follow;
import entities.player.Player;
import gameStart.GamePanel;
import utilz.Enums.GameState;

/**
 * Manages all entities in the game world including the player, enemies, and end-of-level marker.
 * Responsible for spawning, updating, drawing, and resetting entities between levels.
 */
public class EntityManager extends Entity {

	// === ENEMY COUNTS AND PROPORTIONS ===
	private int numEnemies;                   // Total number of enemies in the level
	private double basicProportion;           // Fraction of enemies that are "Basic"
	private double fastProportion;            // Fraction of enemies that are "Fast"
	private double followProportion;          // Fraction of enemies that are "Follow"

	private int numBasic;                     // Actual number of Basic enemies
	private int numFast;                      // Actual number of Fast enemies
	private int numFollow;                    // Actual number of Follow enemies
	
	// === ENTITY COLLECTIONS AND REFERENCES ===
	private ArrayList<Entity> entityList = new ArrayList<>();   // Master list of all entities (player, enemies, end marker)
	private Player player;                                      // Reference to player entity
	private EndOfLevel endOfLevel;                              // Reference to end-of-level marker
	private final LinkedHashMap<String, Float> enemyTypeProportions = new LinkedHashMap<>(); // Maps enemy types to their proportions

	/**
	 * Constructs an EntityManager and associates it with the main GamePanel.
	 */
	public EntityManager(GamePanel gp) {
		super(gp);
	}
	
	/**
	 * Creates and adds the player and end-of-level marker to the entity list.
	 * Also spawns enemies based on the current game state.
	 */
	public void summonEntities() {
		player = new Player(gp);
		endOfLevel = new EndOfLevel(gp);
		entityList.add(player);
		entityList.add(endOfLevel);
		regenerateEnemies();
	}
	
	/**
	 * Clears all entities and regenerates the player, end-of-level marker, and enemies.
	 */
	public void regenerateAll() {
		entityList = new ArrayList<>();
		summonEntities();
		regenerateEnemies();
	}
	
	/**
	 * Removes all enemies and repopulates them based on proportions or tutorial logic.
	 * Tutorial levels have specific enemy setups.
	 */
	public void regenerateEnemies() {
	    entityList.removeIf(e -> e instanceof Enemy); // Remove all existing enemies

	    // Tutorial-specific setups
	    if (gp.getLevelManager().isTutorial()) {
			switch (gp.getLevelManager().getCurrentLevelNum()) {
				case 0 -> setEnemyProportions(1,0,0);
				case 1 -> setEnemyProportions(2,0,0);
				case 2 -> setEnemyProportions(3,1,0);
			}
		}
	    
	    // Compute enemy counts from proportions
	    numBasic = (int) (numEnemies * basicProportion);
		numFast = (int) (numEnemies * fastProportion);
		numFollow = numEnemies - numBasic - numFast;
		
	    // Spawn Basic enemies
	    for (int i = 0; i < numBasic; i++) {
	    	entityList.add(new Basic(gp));
	    }
	    // Spawn Fast enemies
	    for (int i = 0; i < numFast; i++) {
	    	entityList.add(new Fast(gp));
	    }
	    // Spawn Follow enemies
	    for (int i = 0; i < numFollow; i++) {
	    	entityList.add(new Follow(gp));
	    }
	}
	
	/**
	 * Checks collisions between the player, enemies, and end-of-level tile.
	 * - If the player reaches the end tile, proceeds to the next level or triggers win condition.
	 * - If the player collides with any enemy, triggers Game Over.
	 */
	public void checkCollision() {

		Player player = null;
	    EndOfLevel end = null;
	    Enemy enemy = null;
	    
	    for (Entity e : entityList) {
	        if (e instanceof Player) {
	            player = (Player) e;
	        } else if (e instanceof EndOfLevel) {
	            end = (EndOfLevel) e;
	            // Check if player reached end of level
	            if (player.getRow() == e.getRow() && player.getCol() == e.getCol()) {
	            		if (gp.getLevelManager().getCurrentLevelNum() < gp.getLevelManager().getNumLevels() - 1) {
	            			gp.nextLevel(); // Advance to next level
	            		} else {
	            			gp.setGameState(GameState.WINNER); // Final level reached
	            		}
	            		return;
	            }
	        } else if (e instanceof Enemy && player != null) {
	            enemy = (Enemy) e;
	            // Player collision with enemy
	            if (enemy.getRow() == player.getRow() && enemy.getCol() == player.getCol()) {
	                gp.setGameState(GameState.GAME_OVER);
	                return;
	            }
	        }
	    }

	    // Redundant safety check for reaching end tile
	    if (player != null && end != null &&
	        player.getRow() == end.getRow() &&
	        player.getCol() == end.getCol()) {
	        gp.nextLevel();
	    }
	}
	
	/**
	 * Updates all entities each frame and performs collision checks.
	 */
	public void update() {
		for (Entity e : entityList) {
			e.update();
		}
		checkCollision();
	}
	
	/**
	 * Draws all entities to the screen.
	 * @param g2 graphics context
	 */
	public void draw(Graphics2D g2) {
		for (Entity e : entityList) {
			e.draw(g2);
		}
	}

	/**
	 * Called when progressing to a new level — resets entity states and regenerates enemies.
	 */
	public void nextLevel() {
		for (Entity e : entityList) {
			e.nextLevel();
		}
		regenerateEnemies();
	}
	
	/**
	 * Fully resets all entities (used on full game reset or replay).
	 */
	public void resetAll() {
		for (Entity e : entityList) {
			e.resetAll();
		}
		regenerateEnemies();
	}
	
	/**
	 * Resets entities for the current level only.
	 */
	public void resetLevel() {
		for (Entity e : entityList) {
			e.resetLevel();
		}
		regenerateEnemies();
	}
	
	/**
	 * Returns the complete entity list (includes player, enemies, end tile).
	 */
	public ArrayList<Entity> getEnemyList() {
		return entityList;
	}

	@Override
	public void setDefaultSpawn() {
		// Not used for EntityManager
	}

	@Override
	public void spawn(int col, int row) {
		// Not used for EntityManager
	}

	/**
	 * Returns the player entity instance.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Returns a list of enemy type names currently tracked.
	 */
	public List<String> getEnemyTypes() {
	    return new ArrayList<>(enemyTypeProportions.keySet());
	}

	/**
	 * Sets the total number of enemies and recalculates their distribution.
	 */
	public void setNumEnemies(int num) {
	    this.numEnemies = Math.max(0, num);
	    numBasic = (int)(numEnemies * basicProportion);
	    numFast = (int)(numEnemies * fastProportion);
	    numFollow = (int)(numEnemies * followProportion);
	    int remainder = numEnemies - (numBasic + numFollow + numFast);

	    numBasic += remainder; // Assign any leftover count to Basic type
	}
	
	public int getNumEnemies() {
	    return numEnemies;
	}

	public int getNumBasic() {
		return numBasic;
	}

	public void setNumBasic(int numBasic) {
		this.numBasic = numBasic;
	}

	public int getNumFast() {
		return numFast;
	}

	public void setNumFast(int numFast) {
		this.numFast = numFast;
	}

	public int getNumFollow() {
		return numFollow;
	}

	public void setNumFollow(int numFollow) {
		this.numFollow = numFollow;
	}
	
	/**
	 * Sets proportions directly as percentages (not counts) for each enemy type.
	 */
	public void setEnemyProportions(double basicProp, double fastProp, double followProp) {
		enemyTypeProportions.put("Basic", (float) basicProp);
        enemyTypeProportions.put("Fast", (float) fastProp);
        enemyTypeProportions.put("Follow", (float) followProp);
	}
	
	/**
	 * Sets enemy amounts by specific counts, then calculates proportions from them.
	 */
	public void setEnemyProportions(int basicNum, int fastNum, int followNum) {
		numBasic = basicNum;
		numFast = fastNum;
		numFollow = followNum;
		
		numEnemies = numBasic + numFast + numFollow;

        // Add a small value to avoid divide-by-zero issues
        basicProportion = ((double) numBasic / numEnemies) + 0.000001;
        fastProportion = ((double) numFast / numEnemies) + 0.000001;
        followProportion = ((double) numFollow / numEnemies) + 0.000001;

        enemyTypeProportions.put("Basic", (float) basicProportion);
        enemyTypeProportions.put("Fast", (float) fastProportion);
        enemyTypeProportions.put("Follow", (float) followProportion);
	}

	/**
	 * Gets the number of enemies of a given type ("Basic", "Fast", or "Follow").
	 */
	public int getEnemyAmounts(String type) {
	    if (!enemyTypeProportions.containsKey(type)) {
	        System.out.println("Warning: enemy type '" + type + "' not found.");
	        return 0;
	    }

	    int count;
	    switch (type) {
	        case "Basic" -> count = numBasic;
	        case "Fast" -> count = numFast;
	        case "Follow" -> count = numFollow;
	        default -> count = 0;
	    }
	    return count;
	}
	
	/**
	 * Adjusts enemy counts dynamically (e.g. +1 or -1) and recalculates proportions.
	 */
	public void adjustEnemyAmounts(String type, int dir) {
	    // Step 1: Adjust selected type
	    switch (type) {
	        case "Basic" -> numBasic = Math.max(0, numBasic + dir);
	        case "Fast" -> numFast = Math.max(0, numFast + dir);
	        case "Follow" -> numFollow = Math.max(0, numFollow + dir);
	    }

	    // Step 2: Update total enemy count
	    numEnemies = numBasic + numFast + numFollow;

	    // Step 3: Update proportions
        basicProportion = ((double) numBasic / numEnemies) + 0.000001;
        fastProportion = ((double) numFast / numEnemies) + 0.000001;
        followProportion = ((double) numFollow / numEnemies) + 0.000001;

        enemyTypeProportions.put("Basic", (float) basicProportion);
        enemyTypeProportions.put("Fast", (float) fastProportion);
        enemyTypeProportions.put("Follow", (float) followProportion);
	}

	/**
	 * Returns the EndOfLevel entity instance.
	 */
	public EndOfLevel getEndOfLevel() {
		return endOfLevel;
	}
}
