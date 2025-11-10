package screens;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import gameStart.GamePanel;
import levels.Level;
import utilz.Enums.GameState;

public class Playing extends Screen {

	GamePanel gp;
	
	// Movement flags for triggering player movement once per keypress
	private boolean moveUp, moveDown, moveLeft, moveRight;
    
	// Hold flags to prevent continuous repeat triggering while key is held down
    private boolean upHeld, downHeld, leftHeld, rightHeld;
    
    // Flag that indicates when a pulse (echo) has been requested
    private boolean pulseRequested = false;
	
	// Constructor: stores reference to the main game panel
	public Playing(GamePanel gp) {
		this.gp = gp;
	}
	
	// Draws the current level and all entities (player, enemies, etc.)
	public void draw(Graphics2D g2) {
		gp.getLevelManager().getCurrentLevel().draw(g2);
		gp.getEntityManager().draw(g2);
	}

	// Updates all active entities in the game during the playing state
	@Override
	public void update() {
		gp.getEntityManager().update();
	}

    // Handles all key press actions (movement, tutorial progression, etc.)
    @Override
    public void keyPressed(KeyEvent e) {
    	int code = e.getKeyCode();
	    	
    	// Only respond to movement keys if the game is actively playing
    	if (gp.getGameState() == GameState.PLAYING) {
	    	if ((code == KeyEvent.VK_W || code == KeyEvent.VK_UP) && !upHeld) {
	    		moveUp = true;
		        upHeld = true;
	    	}
		    if ((code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) && !downHeld) {
		        moveDown = true;
		        downHeld = true;
		    }
		    if ((code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) && !leftHeld) {
		        moveLeft = true;
		        leftHeld = true;
		    }
		    if ((code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) && !rightHeld) {
		        moveRight = true;
		        rightHeld = true;
		    }
    	}
    	
    	// Special handling for tutorial levels (progresses based on input)
    	if (gp.getLevelManager().isTutorial()) {
    	    Level tutorialLevel = gp.getLevelManager().getCurrentLevel();

    	    // Stage 0: detects first movement input and advances the tutorial
    	    if (tutorialLevel.getTutorialStage() == 0) {
    	        if (code == KeyEvent.VK_W || code == KeyEvent.VK_A || code == KeyEvent.VK_S || code == KeyEvent.VK_D ||
    	            code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN || code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT) {
    	            tutorialLevel.setMoved(true);
    	            tutorialLevel.advanceTutorial();
    	        }
    	    }

    	    // Stage 1: waits for a pulse (spacebar) input before advancing
    	    if (tutorialLevel.getTutorialStage() == 1 && code == KeyEvent.VK_SPACE) {
    	        tutorialLevel.setPulsed(true);
    	        tutorialLevel.advanceTutorial();
    	    }

    	    // Allows player to dismiss instructions manually
    	    if (code == KeyEvent.VK_ENTER) {
    	        tutorialLevel.setShowInstructions(false);
    	    }
    	}
    	
    	// Default switch structure for future key press handling (currently empty)
    	switch(code) {
    	default:
    		break;
    	}
    }

    // Handles key releases to stop movement and detect certain actions
    @Override
    public void keyReleased(KeyEvent e) {
    	int code = e.getKeyCode();

    	// Reset the held flags when movement keys are released
    	if (gp.getGameState() == GameState.PLAYING) {
    		if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
    			upHeld = false;
    		}
    		if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
    			downHeld = false;
    		}
    		if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
    			leftHeld = false;
    		}
    		if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
    			rightHeld = false;
    		}
    	}

    	// Handle game control inputs that act on key release
    	switch (code) {
    	case KeyEvent.VK_ESCAPE:
			// Pause the game and switch to the pause screen
			gp.setGameState(GameState.PAUSED);
			break;
    	case KeyEvent.VK_SPACE:
    		// Trigger an echo pulse when the spacebar is released
    		pulseRequested = true;
    		break;
		default:
			break;
    	}
    }

	// Handles key typing (unused for this screen)
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}
	
	// Resets movement flags so the player doesn't continue moving unintentionally
	public void resetMovementFlags() {
        moveUp = moveDown = moveLeft = moveRight = false;
    }
    
    // Returns whether a pulse was requested this frame
    public boolean isPulseRequested() {
        return pulseRequested;
    }

    // Resets the pulse flag after it has been processed
    public void resetPulseFlag() {
        pulseRequested = false;
    }

	// Getters for movement direction flags
	public boolean isMoveUp() {
		return moveUp;
	}

	public boolean isMoveDown() {
		return moveDown;
	}

	public boolean isMoveLeft() {
		return moveLeft;
	}

	public boolean isMoveRight() {
		return moveRight;
	}
	
	// Getters for held key states (used to check if keys are being held continuously)
	public boolean isHeldUp() { 
		return upHeld; 
	}
	public boolean isHeldDown() { 
		return downHeld; 
	}
	public boolean isHeldLeft() { 
		return leftHeld; 
	}
	public boolean isHeldRight() { 
		return rightHeld; 
	}
}
