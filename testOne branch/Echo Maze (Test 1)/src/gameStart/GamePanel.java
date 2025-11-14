package gameStart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import entities.EntityManager;
import levels.LevelManager;
import mechanics.EchoPulse;
import screens.GameOver;
import screens.Menu;
import screens.Paused;
import screens.Playing;
import screens.Screen;
import screens.Settings;
import screens.Winner;
import utilz.Enums.Difficulty;
import utilz.Enums.GameState;
import java.util.Random;

/**
 * GamePanel is the main canvas and control hub for the Echo Maze game.
 * It manages rendering, updating, and switching between different game screens.
 * This class also handles the main game loop thread and maintains references
 * to all key subsystems (levels, entities, echo pulse, etc.).
 */
public class GamePanel extends JPanel implements Runnable {
	
	private static final long serialVersionUID = 963838067364679961L;

	// ==== TILE & SCREEN SETTINGS ====
	public final static int ORIGINAL_TILE_SIZE = 16;  // Base tile size (pixels)
	public final static double SCALE = 1.0;           // Global scaling factor
	public final int TILE_SIZE = (int)(ORIGINAL_TILE_SIZE * SCALE); // Scaled tile size
	
	public final int MAX_SCREEN_WIDTH = 37;  // Must be odd for centered maze symmetry
	public final int MAX_SCREEN_HEIGHT = 37;
	
	public final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_WIDTH;   // Final pixel width
	public final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_HEIGHT; // Final pixel height
	
	// ==== GAME LOOP SETTINGS ====
	private int fps = 60;  // Target frames per second
	
	// ==== RANDOM SEED (STATIC FOR REPRODUCIBLE LEVEL GENERATION) ====
	private static final long SEED = serialVersionUID;
	private Random srand = new java.util.Random(SEED);
	
	// ==== CORE GAME STATE VARIABLES ====
	private GameState gameState = GameState.MENU; // Current state (menu, playing, etc.)
	private Difficulty diff = Difficulty.MEDIUM;  // Current game difficulty
	
	// ==== MAIN GAME COMPONENTS ====
	private LevelManager levelManager = new LevelManager(this);  // Handles maze generation & level data
	private KeyInput keyIn = new KeyInput(this);                  // Handles player input
	private EntityManager entityManager = new EntityManager(this); // Manages all entities
	private EchoPulse echo = new EchoPulse(this);                 // Manages sound-wave visibility mechanic
	
	// ==== SCREEN INSTANCES (UI STATES) ====
	private GameOver gameOver = new GameOver(this);
	private Menu menu = new Menu(this);
	private Paused paused = new Paused(this);
	private Winner winScreen = new Winner(this);
	private Settings settings = new Settings(this);
	private Playing playing = new Playing(this);
	private Screen currentScreen = menu; // Starts at the main menu
	
	Thread gameThread; // The main game loop thread
			
	// ==== CONSTRUCTOR ====
	public GamePanel() {
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);  // Enables smoother rendering
		this.addKeyListener(keyIn);    // Attaches keyboard input
		this.setFocusable(true);       // Allows panel to receive focus for key events
	}

	// ==== START GAME LOOP THREAD ====
	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	/**
	 * Main game loop — runs continuously while the game is active.
	 * It regulates update frequency and rendering based on target FPS.
	 */
	@Override
	public void run() {

		double drawInterval = 1000000000 / fps; // Time per frame (nanoseconds)
		double delta = 0;                        // Tracks when to trigger updates
		long lastTime = System.nanoTime();
		long currentTime;
		
		int drawCount = 0;   // Frame counter
		long timer = 0;      // Time accumulator for FPS output
		
		// === MAIN GAME LOOP ===
		while (gameThread != null) {
			
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			timer += (currentTime - lastTime);
			lastTime = currentTime;
			
			// Only update/draw when a full frame interval has passed
			if (delta >= 1) {
				update();
				repaint();
				delta--;
				drawCount++;
			}
			
			// Output FPS every second (for debugging)
			if (timer >= 1000000000) {
				System.out.println("FPS: " + drawCount);
				drawCount = 0;
				timer = 0;
			}
		}
	}
	
	/**
	 * Update method — delegates logic updates to the current active screen.
	 */
	public void update() {
		currentScreen = getCurrentScreen();
		if (currentScreen != null) {
			currentScreen.update(); // Update screen logic (e.g., menu navigation or gameplay)
		} else {
			System.out.println("Error on screen update");
		}
	}
	
	/**
	 * Handles all rendering — delegates drawing to the current active screen.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		currentScreen = getCurrentScreen();
		if (currentScreen != null) {
			currentScreen.draw(g2); // Draws the screen (menu, gameplay, etc.)
	    }
		
		g2.dispose(); // Clean up graphics context
	}
	
	/**
	 * Called when player reaches the level exit.
	 * Advances level state for all subsystems.
	 */
	public void nextLevel() {
		levelManager.nextLevel();
		entityManager.nextLevel();
		echo.nextLevel();
	}
	
	/**
	 * Resets all game data (used when starting a new game or changing difficulty).
	 */
	public void resetAll() {
		levelManager.resetAll();
		entityManager.resetAll();
		echo.resetAll();	
	}
	
	/**
	 * Resets only the current level (used for retries or deaths).
	 */
	public void resetLevel() {
		levelManager.resetLevel();
		entityManager.resetLevel();
		echo.resetLevel();
	}
	
	/**
	 * Determines which screen should currently be displayed based on the game state.
	 */
	public Screen getCurrentScreen() {
	    switch (getGameState()) {
	        case MENU:
	            return menu;
	        case PLAYING:
	            return playing;
	        case PAUSED:
	            return paused;
	        case GAME_OVER:
	            return gameOver;
	        case WINNER:
	            return winScreen;
	        case SETTINGS:
	            return getSettings();
	        default:
	            return null; // No valid state found (should not happen)
	    }
	}

	// ==== SCREEN AND STATE ACCESSORS ====
	public void setCurrentScreen(Screen currentScreen) {
		this.currentScreen = currentScreen;
	}

	public Difficulty getDiff() {
		return diff;
	}

	public void setDiff(Difficulty diff) {
		this.diff = diff;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	// ==== SUBSYSTEM ACCESSORS ====
	public EchoPulse getEchoPulse() {
		return echo;
	}

	public LevelManager getLevelManager() {
		return levelManager;
	}

	public Playing getPlaying() {
		return playing;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Settings getSettings() {
		return settings;
	}
	
	/**
	 * Returns a pseudo-random double between 0 and 1 using the fixed SEED.
	 * This ensures repeatable randomness for procedural generation.
	 */
	public Double getRandomDouble() {
		return srand.nextDouble();
	}
}
