package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;

import gameStart.GamePanel;
import gameStart.KeyInput;
import levels.LevelManager;
import mechanics.EchoPulse;
import utilz.Enums.TileType;

public class Player extends Entity {
	
	private KeyInput keyIn;
	private LevelManager levelM;
	private GamePanel gp;
	private EchoPulse echo;
	
	private int moveDelay = 10;       // adjust this to control repeat speed
	private int moveCounter = 0;
	private int heldDX = 0, heldDY = 0;
	private boolean isMoving = false;
	
	private int pulseCooldown = 0; // counts down in ticks
	private final int maxPulseCooldown = 180; // 3 seconds at 60 FPS

    public Player(GamePanel gp, KeyInput keyIn, LevelManager maze, EchoPulse echo) {
		
		this.gp = gp;
		this.keyIn = keyIn;
		this.levelM = maze;
		this.echo = echo;
		
        setDefaultSpawn();
    }
    
   private void setDefaultSpawn() {
	   for (int i=1; i<gp.MAX_SCREEN_WIDTH-1; i++) {
		   if (levelM.getCurrentLevel().getGrid()[gp.MAX_SCREEN_HEIGHT-1][i] == TileType.START.getValue()) {
			   spawnX = i;
			   spawnY = gp.MAX_SCREEN_HEIGHT-1;
		   }
	   }
//	   spawnX = 8;
//	   spawnY = 15;
	   row = spawnY;
	   col = spawnX; 
   }
   
   public void nextLevel() {
	   setDefaultSpawn();
	   moveCounter = 0;
	   heldDX = 0; heldDY = 0;
	   isMoving = false;
   }

   public void update() {
	   
	   echoPulse();
	   move();
   }
   private void echoPulse() {
	   echo.seeAroundPlayer(col, row);
	   
	   if (keyIn.isPulseRequested()) {
	        if (!echo.isActive()) {
	            int y = row;// / gp.TILE_SIZE;
	            int x = col;// / gp.TILE_SIZE;
	            echo.newEcho(x, y);
	        }
	        keyIn.resetPulseFlag();
	    }
	   
        echo.update();
        
//        if (!echo.isActive()) {
//
//        }
   }
//   private void echoPulse() {
//	   if (pulseCooldown > 0) {
//		    pulseCooldown--;
//	   }
//	   if (keyIn.isPulseRequested() && canPulse()) {
//		    echo.startPulse(col, row);
//		    usePulse();
//		}
//   }
   
//   private boolean canPulse() {
//	   return pulseCooldown <= 0;
//   }
//   
//   public void usePulse() {
//	   pulseCooldown = maxPulseCooldown;
//   }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.BLUE);
        g2.fillOval(col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE);
        echo.draw(g2);
    }
    
    public void reset() {
//    	col = spawnX; //Defaults
//    	row = spawnY;
    	setDefaultSpawn();
    	moveCounter = 0;
    	heldDX = 0; heldDY = 0;
    	isMoving = false;
    }
    
	private void move() {
		// Reset direction
	    int dX = 0, dY = 0;
	
	    // Check which keys are held
	    if (keyIn.isHeldUp()) dY = -1;
	    else if (keyIn.isHeldDown()) dY = 1;
	    else if (keyIn.isHeldLeft()) dX = -1;
	    else if (keyIn.isHeldRight()) dX = 1;
	
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
	
	            if (canMove(heldDX, heldDY, levelM)) {
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

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
