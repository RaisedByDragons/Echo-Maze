package screens;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public abstract class Screen implements KeyListener {

	public abstract void keyPressed(KeyEvent e);
	public abstract void keyReleased(KeyEvent e);
    public abstract void update(); // For any screen-specific updates (e.g., animations, timers)
    public abstract void draw(Graphics2D g2); // For rendering the screen contents
}
