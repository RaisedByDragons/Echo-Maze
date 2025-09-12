package screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import gameStart.GamePanel;
import utilz.Enums.GameState;

public class Menu extends Screen{
	
	GamePanel gp;
	
	public Menu(GamePanel gp) {
		this.gp = gp;
	}
	
	public void draw(Graphics2D g2) {
		g2.setColor(Color.BLACK);
	    g2.fillRect(0, 0, gp.SCREEN_WIDTH, gp.SCREEN_HEIGHT);
	    g2.setColor(Color.WHITE);
	    g2.drawString("ECHO MAZE", 100, 100);
	    g2.drawString("Press ENTER to Start", 100, 120);
	    g2.drawString("Press ESC to go to Settings", 100, 140);
	}

	public void update() {
		// TODO Auto-generated method stub
		
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		switch (code) {
		case KeyEvent.VK_ESCAPE:
			gp.setGameState(GameState.SETTINGS);
			break;
		case KeyEvent.VK_ENTER:
			gp.setGameState(GameState.PLAYING);
			break;
		default:
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
