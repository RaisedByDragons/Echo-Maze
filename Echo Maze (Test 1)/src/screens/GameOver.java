package screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import gameStart.GamePanel;
import utilz.Enums.GameState;

public class GameOver extends Screen {

	GamePanel gp;
	
	// Constructor: initializes the GameOver screen with a reference to the GamePanel
	public GameOver(GamePanel gp) {
		this.gp = gp;
	}
	
	// Draws the Game Over screen overlay on top of the last played level
	public void draw(Graphics2D g2) {
		// Draw the current level and entities in their final state
		gp.getLevelManager().getCurrentLevel().draw(g2);
		gp.getEntityManager().draw(g2);
		
		// Draw a semi-transparent red overlay for dramatic effect
	    g2.setColor(new Color(200, 0, 0, 50));
	    g2.fillRect(0, 0, gp.SCREEN_WIDTH, gp.SCREEN_HEIGHT);
	    
	    // Draw the Game Over message box
	    g2.setColor(new Color(100, 100, 100));
	    g2.fillRect(90, 80, 200, 80);
	    
	    // Draw text prompts for user input
	    g2.setColor(Color.WHITE);
	    g2.drawString("GAME OVER", 100, 100);
	    g2.drawString("Press ESC to return to menu", 100, 120);
	    g2.drawString("Press ENTER to try again", 100, 140);
	}

	// Update method (currently unused)
	public void update() {
		// TODO Auto-generated method stub
	}

	// Handles key press events (not used here)
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	// Handles key release events for user interaction on the Game Over screen
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		switch (code) {
		case KeyEvent.VK_ESCAPE:
			// Return to main menu and reset the level
			gp.resetLevel();
        	gp.setGameState(GameState.MENU);
			break;
		case KeyEvent.VK_ENTER:
			// Restart the current level and resume playing
			gp.resetLevel();
        	gp.setGameState(GameState.PLAYING);
			break;
		default:
			break;
		}
	}

	// Handles key typed events (currently unused)
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}
}
