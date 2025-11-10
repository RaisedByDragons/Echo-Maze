package screens;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// Abstract base class for all screens in the game (Menu, Playing, Paused, GameOver, etc.)
public abstract class Screen implements KeyListener {

    // Method to handle key presses (called when a key is initially pressed)
	public abstract void keyPressed(KeyEvent e);
    
    // Method to handle key releases (called when a key is released)
	public abstract void keyReleased(KeyEvent e);
    
    // Method to update the state of the screen (game logic, animations, etc.)
    public abstract void update();
    
    // Method to draw the screen contents (graphics) using the provided Graphics2D object
    public abstract void draw(Graphics2D g2);
}
