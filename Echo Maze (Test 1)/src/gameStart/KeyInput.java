package gameStart;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInput implements KeyListener {
 
    private GamePanel gp;

    public KeyInput(GamePanel gp) {
        this.gp = gp;
    }

    public void keyPressed(KeyEvent e) {
        // Delegate to the active screen
        if (gp.getGameState() != null && gp.getCurrentScreen() != null) {
            gp.getCurrentScreen().keyPressed(e);
        }
    }

    public void keyReleased(KeyEvent e) {
        // Delegate to the active screen
        if (gp.getGameState() != null && gp.getCurrentScreen() != null) {
            gp.getCurrentScreen().keyReleased(e);
        }
    }

    public void keyTyped(KeyEvent e) {
	    if (gp.getGameState() != null && gp.getCurrentScreen() != null) {
	        gp.getCurrentScreen().keyReleased(e);
	    }
    }
}
