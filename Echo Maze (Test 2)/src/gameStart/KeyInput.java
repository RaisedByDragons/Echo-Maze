package gameStart;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Handles keyboard input for the game. 
 * This class listens for key events and delegates them to the currently active screen.
 */
public class KeyInput implements KeyListener {
 
    // Reference to the main game panel, used to access the current screen and game state
    private GamePanel gp;

    /**
     * Constructor for KeyInput.
     * @param gp The GamePanel instance that manages the active screen and game state.
     */
    public KeyInput(GamePanel gp) {
        this.gp = gp;
    }

    /**
     * Called automatically when a key is pressed down.
     * Forwards the event to the currently active screen (e.g., Menu, Playing, etc.)
     */
    public void keyPressed(KeyEvent e) {
        // Check that the game state and current screen exist before delegating
        if (gp.getGameState() != null && gp.getCurrentScreen() != null) {
            gp.getCurrentScreen().keyPressed(e);
        }
    }

    /**
     * Called automatically when a key is released.
     * Forwards the event to the currently active screen.
     */
    public void keyReleased(KeyEvent e) {
        // Check that the game state and current screen exist before delegating
        if (gp.getGameState() != null && gp.getCurrentScreen() != null) {
            gp.getCurrentScreen().keyReleased(e);
        }
    }

    /**
     * Called automatically when a key is typed (pressed and released, generating a character).
     * Forwards the event to the currently active screen.
     */
    public void keyTyped(KeyEvent e) {
        // Check that the game state and current screen exist before delegating
        if (gp.getGameState() != null && gp.getCurrentScreen() != null) {
            gp.getCurrentScreen().keyTyped(e);
        }
    }
}
