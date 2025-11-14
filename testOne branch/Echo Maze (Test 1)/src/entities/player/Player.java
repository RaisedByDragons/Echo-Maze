package entities.player;

import java.awt.Color;
import java.awt.Graphics2D;

import entities.Entity;
import gameStart.GamePanel;
import utilz.Enums.TileType;

public class Player extends Entity {
	
	private int moveDelay = 10;       // Number of frames between consecutive movement steps (controls movement speed)
	private int moveCounter = 0;      // Counter for delaying player movement
	private int heldDX = 0, heldDY = 0; // Stores currently held direction (x,y)
	private boolean isMoving = false; // Tracks if the player is currently moving

    public Player(GamePanel gp) {
		super(gp);
		
        setDefaultSpawn(); // Set player start position based on START tile
    }
    
    // Finds and sets the player's starting spawn position on the grid
    public void setDefaultSpawn() {
        int[][] grid = gp.getLevelManager().getCurrentLevel().getGrid();

        for (int row = 0; row < gp.MAX_SCREEN_HEIGHT; row++) {
            for (int col = 0; col < gp.MAX_SCREEN_WIDTH; col++) {
                // Look for the START tile type in the grid
                if (grid[row][col] == TileType.START.getValue()) {
                    spawnX = col;
                    spawnY = row;
                    this.row = row;
                    this.col = col;
                    return; // Exit once START tile is found
                }
            }
        }
    }
   
   // Placeholder method for manually spawning the player at a given tile (unused currently)
   public void spawn(int col, int row) {
		// TODO Auto-generated method stub
	}

   // Called every game tick to update the player's state
   public void update() {
	   echoPulse(); // Handle visibility and echo logic
	   move();      // Handle movement logic
   }

   // Handles player's sonar-like "echo pulse" revealing surroundings
   private void echoPulse() {
	   // Update visibility map around the player each frame
	   gp.getEchoPulse().seeAroundPlayer(col, row);
	   
	   // Check if player triggered a new echo pulse
	   if (gp.getPlaying().isPulseRequested() && gp.getEchoPulse().getNumPulseLeft() > 0) {
	        if (!gp.getEchoPulse().isActive()) {
	            int y = row;
	            int x = col;
	            gp.getEchoPulse().newEcho(x, y); // Create a new expanding echo pulse
	        }
	    }
	   gp.getPlaying().resetPulseFlag(); // Reset the pulse trigger flag
	   gp.getEchoPulse().update();       // Update pulse behavior and fading
   }
   
   // Draws the player and their echo pulse on screen
    public void draw(Graphics2D g2) {
        g2.setColor(Color.BLUE);
        g2.fillOval(col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE); // Draw player as blue circle
        gp.getEchoPulse().draw(g2); // Draw echo pulse visualization
    }
    
    // Called when progressing to the next level — resets player state and movement variables
    public void nextLevel() {
 	   setDefaultSpawn();
 	   moveCounter = 0;
 	   heldDX = 0; heldDY = 0;
 	   isMoving = false;
    }
    
    // Called when restarting the entire game — resets player state fully
    public void resetAll() {
    	setDefaultSpawn();
    	moveCounter = 0;
    	heldDX = 0; heldDY = 0;
    	isMoving = false;
    }
    
    // Called when restarting the same level — resets to original spawn
    public void resetLevel() {
    	setDefaultSpawn();
    	moveCounter = 0;
    	heldDX = 0; heldDY = 0;
    	isMoving = false;
    }
    
    // Handles smooth continuous movement with input holding logic
	private void move() {
		// Reset direction each frame
	    int dX = 0, dY = 0;
	
	    // Determine which direction key is being held
	    if (gp.getPlaying().isHeldUp()) dY = -1;
	    else if (gp.getPlaying().isHeldDown()) dY = 1;
	    else if (gp.getPlaying().isHeldLeft()) dX = -1;
	    else if (gp.getPlaying().isHeldRight()) dX = 1;
	
	    boolean directionHeld = dX != 0 || dY != 0; // True if any movement key is held
	
	    if (directionHeld) {
	        // If player starts moving or changes direction, reset move counter
	        if (!isMoving || dX != heldDX || dY != heldDY) {
	            heldDX = dX;
	            heldDY = dY;
	            moveCounter = moveDelay;  // Allow immediate first move after direction change
	            isMoving = true;
	        }
	
	        moveCounter++;
	        if (moveCounter >= moveDelay) { // Move when counter exceeds delay
	            moveCounter = 0;
	
	            // Move only if next tile is walkable
	            if (canMove(heldDX, heldDY, gp.getLevelManager())) {
	                col += heldDX;
	                row += heldDY;
	            }
	        }
	    } else {
	        // No movement input — reset counters
	        isMoving = false;
	        moveCounter = 0;
	    }
	}
}
