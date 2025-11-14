package screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import gameStart.GamePanel;
import levels.Level;
import utilz.Enums.GameState;

// Screen shown when the player wins the game
public class Winner extends Screen {

	private GamePanel gp;          // Reference to main game panel
	private Level backgroundLevel; // Level used for background visual

	// Constructor: store reference to game panel
	public Winner(GamePanel gp) {
		this.gp = gp;
	}

	// Draw the winner screen
	public void draw(Graphics2D g2) {
		// Draw semi-transparent level background
		drawScreenBackground(g2);

		// Draw congratulatory message with a shadow effect
		g2.setColor(Color.BLACK);
	    g2.drawString("CONGRATULATIONS!! You have won the game", 101, 101);
	    g2.drawString("Press ESC or ENTER to return to menu", 101, 121);
	    
	    g2.setColor(Color.WHITE);
	    g2.drawString("CONGRATULATIONS!! You have won the game", 100, 100);
	    g2.drawString("Press ESC or ENTER to return to menu", 100, 120);
	}

	// Update method for animations or logic if needed (currently unused)
	@Override
	public void update() {
		// TODO Auto-generated method stub
	}

	// Draws a faded overlay of the current level as the background
	private void drawScreenBackground(Graphics2D g2) {
		backgroundLevel = gp.getLevelManager().getCurrentLevel();
		
        for (int row = 0; row < gp.MAX_SCREEN_HEIGHT; row++) {
            for (int col = 0; col < gp.MAX_SCREEN_WIDTH; col++) {

            	float fadeRatio = 0.7f;	// Degree of fade for overlay
                fadeRatio = (float) Math.pow(fadeRatio, 2);
                Color overlay = new Color(0, 0.7f, 0, Math.max(0, Math.min(1, fadeRatio)));

                // Draw the tile
                g2.setColor(overlay);
                g2.drawImage(
                		backgroundLevel.getTileList()[backgroundLevel.getGrid()[row][col]].getImage(),
                    col * gp.TILE_SIZE, row * gp.TILE_SIZE,
                    gp.TILE_SIZE, gp.TILE_SIZE, null
                );

                // Fill rectangle with overlay to create fade effect
                g2.fillRect(col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE);
            }
        }
    }

	// Key pressed event (currently unused)
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	// Key released event for returning to menu
	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		switch (code) {
		case KeyEvent.VK_ESCAPE: // ESC returns to main menu
			gp.resetAll();
        	gp.setGameState(GameState.MENU);
			break;
		case KeyEvent.VK_ENTER:  // ENTER also returns to main menu
			gp.resetAll();
        	gp.setGameState(GameState.MENU);
			break;
		default:
			break;
		}		
	}

	// Key typed event (currently unused)
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}
	
	// Optionally set a specific level to use as background for the winner screen
	public void setBackgroundImage(Level level) {
		backgroundLevel = level;
	}
}
