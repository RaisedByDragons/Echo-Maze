package screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import gameStart.GamePanel;
import utilz.Enums.GameState;

public class Paused extends Screen {

	GamePanel gp;
	
	// Constructor: initializes the pause screen and stores reference to the main game panel
	public Paused(GamePanel gp) {
		this.gp = gp;
	}
	
	// Draws the pause screen, including the current game frame behind a dark overlay
	public void draw(Graphics2D g2) {
		// Draw the current level and all entities so the paused state appears as a frozen game
		gp.getLevelManager().getCurrentLevel().draw(g2);
		gp.getEntityManager().draw(g2);
		
		// Draw a semi-transparent overlay to dim the screen
		g2.setColor(new Color(0, 0, 0, 200));
	    g2.fillRect(0, 0, gp.SCREEN_WIDTH, gp.SCREEN_HEIGHT);
	    
	    // Draw pause menu text and controls
	    g2.setColor(Color.WHITE);
	    g2.drawString("Paused - Press ENTER to Resume", 100, 100);
	    g2.drawString("Press ESC to quit the level", 100, 120);
	    g2.drawString("Press 'r' to restart the level", 100, 140);
	}

	// Update method (currently not needed for paused state)
	@Override
	public void update() {
		// TODO Auto-generated method stub
	}

	// Handles key press events (not used for paused state)
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	// Handles key releases to control pause menu navigation and actions
	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		switch (code) {
		case KeyEvent.VK_ESCAPE:
			// Quit the current level and return to main menu
			gp.resetAll();
        	gp.setGameState(GameState.MENU);
			break;
		case KeyEvent.VK_ENTER:
			// Resume the game from paused state
			gp.setGameState(GameState.PLAYING);
			break;
		case KeyEvent.VK_R:
			// Restart the current level and resume play
			gp.resetLevel();
			gp.setGameState(GameState.PLAYING);
			break;
		default:
			break;
		}		
	}

	// Handles key typing (not used here)
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}
}
