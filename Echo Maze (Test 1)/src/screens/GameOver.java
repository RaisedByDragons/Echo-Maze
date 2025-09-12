package screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import gameStart.GamePanel;
import utilz.Enums.GameState;

public class GameOver extends Screen{

	GamePanel gp;
	
	public GameOver(GamePanel gp) {
		this.gp = gp;
	}
	
	public void draw(Graphics2D g2) {
		gp.getLevelManager().getCurrentLevel().draw(g2);
		gp.getEntityManager().draw(g2);
		
		g2.setColor(new Color(200, 0, 0, 50));
	    g2.fillRect(0, 0, gp.SCREEN_WIDTH, gp.SCREEN_HEIGHT);
	    g2.setColor(Color.WHITE);
	    g2.drawString("GAME OVER", 100, 100);
	    g2.drawString("Press ESC to return to menu", 100, 120);
	    g2.drawString("Press ENTER to try again", 100, 140);
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
			gp.resetLevel();
        	gp.setGameState(GameState.MENU);
			break;
		case KeyEvent.VK_ENTER:
			gp.resetLevel();
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
