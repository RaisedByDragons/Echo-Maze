package screens;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import gameStart.GamePanel;
import levels.Level;
import utilz.Enums.GameState;

public class Playing extends Screen {

	GamePanel gp;
	
	private boolean moveUp, moveDown, moveLeft, moveRight;
    private boolean upHeld, downHeld, leftHeld, rightHeld;
    private boolean pulseRequested = false;
	
	public Playing(GamePanel gp) {
		this.gp = gp;
	}
	
	public void draw(Graphics2D g2) {
		gp.getLevelManager().getCurrentLevel().draw(g2);
		gp.getEntityManager().draw(g2);
	}

	@Override
	public void update() {
		gp.getEntityManager().update();
	}

    @Override
    public void keyPressed(KeyEvent e) {
    	int code = e.getKeyCode();
	    	
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
    	
    	if (gp.getLevelManager().isTutorial()) {
    	    Level tutorialLevel = gp.getLevelManager().getCurrentLevel();

    	    if (tutorialLevel.getTutorialStage() == 0) {
    	        if (code == KeyEvent.VK_W || code == KeyEvent.VK_A || code == KeyEvent.VK_S || code == KeyEvent.VK_D ||
    	            code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN || code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT) {
    	            tutorialLevel.setMoved(true);
    	            tutorialLevel.advanceTutorial();
    	        }
    	    }

    	    if (tutorialLevel.getTutorialStage() == 1 && code == KeyEvent.VK_SPACE) {
    	        tutorialLevel.setPulsed(true);
    	        tutorialLevel.advanceTutorial();
    	    }

    	    if (code == KeyEvent.VK_ENTER) {
    	        tutorialLevel.setShowInstructions(false);
    	    }
    	}
    	
    	switch(code) {
    	default:
    		break;
    	}
    }

    @Override
    public void keyReleased(KeyEvent e) {
    	int code = e.getKeyCode();
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
    	switch (code) {
    	case KeyEvent.VK_ESCAPE:
			gp.setGameState(GameState.PAUSED);
			break;
    	case KeyEvent.VK_SPACE:
    		pulseRequested = true;
    		break;
		default:
			break;
    	}
    }

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void resetMovementFlags() {
        moveUp = moveDown = moveLeft = moveRight = false;
    }
    
    public boolean isPulseRequested() {
        return pulseRequested;
    }

    public void resetPulseFlag() {
        pulseRequested = false;
    }

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
