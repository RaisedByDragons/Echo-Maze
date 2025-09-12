package entities.player;

import java.awt.Color;
import java.awt.Graphics2D;

import entities.Entity;
import gameStart.GamePanel;
import utilz.Enums.TileType;

public class Player extends Entity {
	
	private int moveDelay = 10;       // adjust this to control repeat speed
	private int moveCounter = 0;
	private int heldDX = 0, heldDY = 0;
	private boolean isMoving = false;

    public Player(GamePanel gp) {
		super(gp);
		
        setDefaultSpawn();
    }
    
    public void setDefaultSpawn() {
        int[][] grid = gp.getLevelManager().getCurrentLevel().getGrid();

        for (int row = 0; row < gp.MAX_SCREEN_HEIGHT; row++) {
            for (int col = 0; col < gp.MAX_SCREEN_WIDTH; col++) {
                if (grid[row][col] == TileType.START.getValue()) {
                    spawnX = col;
                    spawnY = row;
                    this.row = row;
                    this.col = col;
                    return; // Exit once START is found
                }
            }
        }
    }
   
   public void spawn(int col, int row) {
		// TODO Auto-generated method stub
	}

   public void update() {
	   
	   echoPulse();
	   move();
   }
   private void echoPulse() {
	   gp.getEchoPulse().seeAroundPlayer(col, row);
	   
	   if (gp.getPlaying().isPulseRequested() && gp.getEchoPulse().getNumPulseLeft() > 0) {
	        if (!gp.getEchoPulse().isActive()) {
	            int y = row;// / gp.TILE_SIZE;
	            int x = col;// / gp.TILE_SIZE;
	            gp.getEchoPulse().newEcho(x, y);
	        }
	    }
	   gp.getPlaying().resetPulseFlag();
	   gp.getEchoPulse().update();
   }
   
    public void draw(Graphics2D g2) {
        g2.setColor(Color.BLUE);
        g2.fillOval(col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE);
        gp.getEchoPulse().draw(g2);
    }
    
    public void nextLevel() {
 	   setDefaultSpawn();
// 	   gp.getEchoPulse().setNumPulseLeft(gp.getEchoPulse().defaultPulseLeft());
 	   moveCounter = 0;
 	   heldDX = 0; heldDY = 0;
 	   isMoving = false;
    }
    
    public void resetAll() {
    	setDefaultSpawn();
    	moveCounter = 0;
    	heldDX = 0; heldDY = 0;
    	isMoving = false;
    }
    
    public void resetLevel() {
    	setDefaultSpawn();
    	moveCounter = 0;
    	heldDX = 0; heldDY = 0;
    	isMoving = false;
    }
    
	private void move() {
		// Reset direction
	    int dX = 0, dY = 0;
	
	    // Check which keys are held
	    if (gp.getPlaying().isHeldUp()) dY = -1;
	    else if (gp.getPlaying().isHeldDown()) dY = 1;
	    else if (gp.getPlaying().isHeldLeft()) dX = -1;
	    else if (gp.getPlaying().isHeldRight()) dX = 1;
	
	    boolean directionHeld = dX != 0 || dY != 0;
	
	    if (directionHeld) {
	        if (!isMoving || dX != heldDX || dY != heldDY) {
	            // Start fresh if it's a new direction or first press
	            heldDX = dX;
	            heldDY = dY;
	            moveCounter = moveDelay;  // allow immediate move
	            isMoving = true;
	        }
	
	        moveCounter++;
	        if (moveCounter >= moveDelay) {
	            moveCounter = 0;
	
	            if (canMove(heldDX, heldDY, gp.getLevelManager())) {
	                col += heldDX;
	                row += heldDY;
	            }
	        }
	    } else {
	        // No key held — reset movement state
	        isMoving = false;
	        moveCounter = 0;
	    }
	}
}