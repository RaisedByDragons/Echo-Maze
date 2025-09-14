package gameStart;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import screens.Screen;
import utilz.Enums.GameState;

public class KeyInput implements KeyListener {

	private boolean moveUp, moveDown, moveLeft, moveRight;
    private boolean upHeld, downHeld, leftHeld, rightHeld;
    private boolean pulseRequested = false;
    
    private GamePanel gp;
    
    public KeyInput(GamePanel gp) {
        this.gp = gp;
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
    	
    	switch(code) {
    	default:
    		break;
    	}
        // Delegate to the active screen
        if (gp.getGameState() != null && gp.getCurrentScreen() != null) {
            gp.getCurrentScreen().keyPressed(e);
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
    	case KeyEvent.VK_SPACE:
    		pulseRequested = true;
    		break;
		default:
			break;
    	}
        // Delegate to the active screen
        if (gp.getGameState() != null && gp.getCurrentScreen() != null) {
            gp.getCurrentScreen().keyReleased(e);
        }
    }
    
    
//    public KeyInput(GamePanel gp) {
//    	this.gp = gp;
//    }
//    
//    public void keyPressed(KeyEvent e) {
//        int code = e.getKeyCode();
//
//        if ((code == KeyEvent.VK_W || code == KeyEvent.VK_UP) && !upHeld) {
//            moveUp = true;
//            upHeld = true;
//        }
//        if ((code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) && !downHeld) {
//            moveDown = true;
//            downHeld = true;
//        }
//        if ((code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) && !leftHeld) {
//            moveLeft = true;
//            leftHeld = true;
//        }
//        if ((code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) && !rightHeld) {
//            moveRight = true;
//            rightHeld = true;
//        }
//    }
//
//    @Override
//    public void keyReleased(KeyEvent e) {
//        int code = e.getKeyCode();
//
//        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
//            upHeld = false;
//        }
//        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
//            downHeld = false;
//        }
//        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
//            leftHeld = false;
//        }
//        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
//            rightHeld = false;
//        }
//        if (code == KeyEvent.VK_ESCAPE) {
//	        switch (gp.gameState) {
//	        case MENU:
//	        	gp.gameState = GameState.SETTINGS;
//	        	break;
//	        case PLAYING:
//	        	gp.gameState = GameState.PAUSED;
//	        	break;
//	        case PAUSED:
//	        	gp.resetAll();
//	        	gp.gameState = GameState.MENU;
//	        	break;
//	        case GAME_OVER:
//	        	gp.resetAll();
//	        	gp.gameState = GameState.MENU;
//	        	break;
//	        case WINNER:
//	        	gp.resetAll();
//	        	gp.gameState = GameState.MENU;
//	        case SETTINGS:
//	        	break;
//	        default:
//	        	break;
//	        }
//        }
//        
//        if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) {
//	        switch (gp.gameState) {
//	        case MENU:
//	        	gp.gameState = GameState.PLAYING;
//	        	break;
//	        case PLAYING:
//	        	break;
//	        case PAUSED:
//	        	gp.gameState = GameState.PLAYING;
//	        	break;
//	        case GAME_OVER:
//	        	gp.resetAll();
//	        	gp.gameState = GameState.PLAYING;
//	        	break;
//	        case WINNER:
//	        	gp.resetAll();
//	        	gp.gameState = GameState.MENU;
//	        case SETTINGS:
//	        	break;
//	        default:
//	        	break;
//	        }
//        }
//    }

    @Override
    public void keyTyped(KeyEvent e) {}

    // Call this at the end of every update to reset movement flags
    
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
