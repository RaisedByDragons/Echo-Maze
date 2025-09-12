package entities;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import entities.endOfLevel.EndOfLevel;
import entities.enemies.Basic;
import entities.enemies.Enemy;
import entities.enemies.Fast;
import entities.enemies.Follow;
import entities.player.Player;
import gameStart.GamePanel;
import utilz.Enums.GameState;

public class EntityManager extends Entity{

	private int numEnemies;

	private double basicProportion;
	private double fastProportion;
	private double followProportion;
	
	private int numBasic;
	private int numFast;
	private int numFollow;
	
	private ArrayList<Entity> entityList = new ArrayList<>();
	private Player player;
	private EndOfLevel endOfLevel;
//	private final HashMap<String, Float> enemyTypeProportions = new HashMap<>();
	private final LinkedHashMap<String, Float> enemyTypeProportions = new LinkedHashMap<>();

	public EntityManager (GamePanel gp) {
		super(gp);
		
//		enemyTypeProportions.put("Basic", (float) basicProportion);
//	    enemyTypeProportions.put("Fast", (float) fastProportion);
//	    enemyTypeProportions.put("Follow", (float) followProportion);
//		summonEntities();
//		regenerateEnemies();
	}
	
	public void summonEntities() {
		player = new Player(gp);
		endOfLevel = new EndOfLevel(gp);
		entityList.add(player);
		entityList.add(endOfLevel);
		
		
		regenerateEnemies();
		
	}
	
	public void regenerateAll() {
		entityList = new ArrayList<>();
		summonEntities();
		regenerateEnemies();
	}
	
	public void regenerateEnemies() {
	    entityList.removeIf(e -> e instanceof Enemy);

	    if(gp.getLevelManager().isTutorial()) {
//			gp.getLevelManager().getTutorialLevel().setEnemyProportions(); //This calls itself
			switch(gp.getLevelManager().getCurrentLevelNum()) {
			case 0 -> setEnemyProportions(1,0,0);
			case 1 -> setEnemyProportions(2,0,0);
			case 2 -> setEnemyProportions(3, 1, 0);
			}
		}
	    
	    numBasic = (int) (numEnemies*basicProportion);
		numFast = (int) (numEnemies*fastProportion);
		numFollow = numEnemies - numBasic -numFast;
		
	    for (int i=0; i<numBasic; i++) {
	    	entityList.add(new Basic(gp));
	    }
	    for (int i=0; i<numFast; i++) {
	    	entityList.add(new Fast(gp));
	    }
	    for (int i=0; i<numFollow; i++) {
	    	entityList.add(new Follow(gp));
	    }
	}
	
	public void checkCollision() {

		Player player = null;
	    EndOfLevel end = null;
	    Enemy enemy = null;
	    
	    for (Entity e : entityList) {
	        if (e instanceof Player) {
	            player = (Player) e;
	        } else if (e instanceof EndOfLevel) {
	            end = (EndOfLevel) e;
	        } else if (e instanceof Enemy && player != null) {
	            enemy = (Enemy) e;
	            if (enemy.getRow() == player.getRow() && enemy.getCol() == player.getCol()) {
	                gp.setGameState(GameState.GAME_OVER);
	                return;
	            }
	        }
	    }

	    // Check for end of level
	    if (player != null && end != null &&
	        player.getRow() == end.getRow() &&
	        player.getCol() == end.getCol()) {
	        gp.nextLevel();
	    }
	}
	
	public void update() {
		for(Entity e : entityList) {
			e.update();
		}
		checkCollision();
		
//		System.out.println("Num enemies = " + numEnemies);
	}
	
	public void draw(Graphics2D g2) {
		for(Entity e : entityList) {
			e.draw(g2);
		}
	}

	public void nextLevel() {
		for(Entity e : entityList) {
			e.nextLevel();
		}
		regenerateEnemies();
	}
	
	public void resetAll() {
		for(Entity e : entityList) {
			e.resetAll();
		}
		regenerateEnemies();
	}
	
	public void resetLevel() {
		for(Entity e : entityList) {
			e.resetLevel();
		}
		regenerateEnemies();
	}
	
	public ArrayList<Entity> getEnemyList() {
		return entityList;
	}

	@Override
	public void setDefaultSpawn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void spawn(int col, int row) {
		// TODO Auto-generated method stub
		
	}

	public Player getPlayer() {
		return player;
	}

	public List<String> getEnemyTypes() {
	    return new ArrayList<>(enemyTypeProportions.keySet());
	}

	public void setNumEnemies(int num) {
	    this.numEnemies = Math.max(0, num);
	    numBasic = (int)(numEnemies * basicProportion);
	    numFast = (int)(numEnemies*fastProportion);
	    numFollow = (int)(numEnemies * followProportion);
	    int remainder = numEnemies - (numBasic + numFollow + numFast);

	    numBasic += remainder;
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
	
	public void setEnemyProportions(double basicProp, double fastProp, double followProp) {
		enemyTypeProportions.put("Basic", (float) basicProp);
        enemyTypeProportions.put("Fast", (float) fastProp);
        enemyTypeProportions.put("Follow", (float) followProp);
	}
	
	public void setEnemyProportions(int basicNum, int fastNum, int followNum) {
		numBasic = basicNum;
		numFast = fastNum;
		numFollow = followNum;
		
		numEnemies = numBasic + numFast + numFollow;

        basicProportion = ((double) (numBasic) / numEnemies) + 0.000001;
        fastProportion = ((double) (numFast) / numEnemies) + 0.000001; //Addition may not be needed,
        followProportion = ((double) (numFollow) / numEnemies) + 0.000001; // but also shouldn't break anything

        enemyTypeProportions.put("Basic", (float) basicProportion);
        enemyTypeProportions.put("Fast", (float) fastProportion);
        enemyTypeProportions.put("Follow", (float) followProportion);
		
	}

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
	
	public void adjustEnemyAmounts(String type, int dir) {
	    // Step 1: Get current counts
//	    int basic = numBasic;
//	    int fast = numFast;
//	    int follow = numFollow;

	    // Step 2: Adjust selected type
	    switch (type) {
	        case "Basic" -> numBasic = Math.max(0, numBasic + dir);
	        case "Fast" -> numFast = Math.max(0, numFast + dir);
	        case "Follow" -> numFollow = Math.max(0, numFollow + dir);
	    }

	    // Step 3: Update total enemy count
	    numEnemies = numBasic + numFast + numFollow;

	    // Step 4: Update instance counts
//	    numBasic = numBasic;
//	    numFast = numFast;
//	    numFollow = numFollow;

	    // Step 5: Update proportions
        basicProportion = ((double) (numBasic) / numEnemies) + 0.000001;
        fastProportion = ((double) (numFast) / numEnemies) + 0.000001; //Addition may not be needed,
        followProportion = ((double) (numFollow) / numEnemies) + 0.000001; // but also shouldn't break anything

        enemyTypeProportions.put("Basic", (float) basicProportion);
        enemyTypeProportions.put("Fast", (float) fastProportion);
        enemyTypeProportions.put("Follow", (float) followProportion);

//	    regenerateEnemies();
	}

	public EndOfLevel getEndOfLevel() {
		return endOfLevel;
	}
	
	
}
