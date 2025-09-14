package screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import gameStart.GamePanel;
import utilz.Enums.GameState;

public class Winner extends Screen {

	GamePanel gp;
	
	public Winner(GamePanel gp) {
		this.gp = gp;
	}
	
	public void draw(Graphics2D g2) {
		g2.setColor(new Color(0, 200, 0, 200));
	    g2.fillRect(0, 0, gp.SCREEN_WIDTH, gp.SCREEN_HEIGHT);
	    g2.setColor(Color.WHITE);
	    g2.drawString("CONGRADULATIONS!! You have won the game", 100, 100);
	    g2.drawString("Press ESC or ENTER to return to menu", 100, 120);
//	    g2.drawString("Press ENTER to try again", 100, 140);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		switch (code) {
		case KeyEvent.VK_ESCAPE:
			gp.resetAll();
        	gp.setGameState(GameState.MENU);
			break;
		case KeyEvent.VK_ENTER:
			gp.resetAll();
        	gp.setGameState(GameState.MENU);
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
