package screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import gameStart.GamePanel;
import levels.Level;
import utilz.Enums.Difficulty;
import utilz.Enums.GameState;

public class Menu extends Screen {
	
	private GamePanel gp;
	private Level backgroundLevel;
	
	// Constructor: initializes the menu screen and generates a background level
	public Menu(GamePanel gp) {
		this.gp = gp;
		// Create a background level using an existing level generator at EXTREME difficulty
		backgroundLevel = gp.getLevelManager().createLevel(1, Difficulty.EXTREME).get(0);
	}
	
	// Draws the menu screen and its background
	public void draw(Graphics2D g2) {
		
		// Set a semi-transparent black overlay for the menu background
		g2.setColor(new Color(0, 0, 0, 200));
		drawScreenBackground(g2);
		
		// Draw title and menu text options
	    g2.setColor(Color.WHITE);
	    g2.drawString("ECHO MAZE", 100, 100);
	    g2.drawString("Press ENTER to Start", 100, 120);
	    g2.drawString("Press ESC to go to Settings", 100, 140);
	}
	
	// Draws the maze-based background behind the menu text
	private void drawScreenBackground(Graphics2D g2) {
        for (int row = 0; row < gp.MAX_SCREEN_HEIGHT; row++) {
            for (int col = 0; col < gp.MAX_SCREEN_WIDTH; col++) {

        			float fadeRatio = 0.9f;	// Controls how dark the overlay appears
                fadeRatio = (float) Math.pow(fadeRatio, 2); // Slightly darken by squaring
                Color overlay = new Color(0, 0, 0, Math.max(0, Math.min(1, fadeRatio))); // Clamp transparency between 0 and 1

                g2.setColor(overlay);
                
                // Draw each background tile image from the level grid
                g2.drawImage(
                		backgroundLevel.getTileList()[backgroundLevel.getGrid()[row][col]].getImage(),
                    col * gp.TILE_SIZE, row * gp.TILE_SIZE,
                    gp.TILE_SIZE, gp.TILE_SIZE, null
                );
                
                // Overlay a transparent rectangle for visual fading
                g2.fillRect(col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE);
            }
        }
    }

	// Update method (currently unused in the menu)
	public void update() {
		// TODO Auto-generated method stub
	}

	// Handles key press events (not used here)
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	// Handles key release actions to navigate from the menu
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		switch (code) {
		case KeyEvent.VK_ESCAPE:
			// Go to settings screen
			gp.setGameState(GameState.SETTINGS);
			break;
		case KeyEvent.VK_ENTER:
			// Start the game (switch to playing state)
			gp.setGameState(GameState.PLAYING);
			break;
		default:
			break;
		}
	}

	// Handles key typed input (currently unused)
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}
}
