package gameStart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import entities.EntityManager;
import levels.LevelManager;
import levels.TutorialLevel;
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

public class GamePanel extends JPanel implements Runnable {
	
	private static final long serialVersionUID = 963838067364679961L;

	final static int ORIGINAL_TILE_SIZE = 16;
	public final static double SCALE = 1.0;
	
	public final int TILE_SIZE = (int)(ORIGINAL_TILE_SIZE * SCALE);
	
	public final int MAX_SCREEN_WIDTH = 37; //Must be odd
	public final int MAX_SCREEN_HEIGHT = 37;
	
	public final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_WIDTH;
	public final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_HEIGHT;
	
	int fps = 60;
	
	private GameState gameState = GameState.MENU;
	private Difficulty diff = Difficulty.MEDIUM;
	
//	private int numLevels = 2;
//	private int numEnemy = 2;
	
	private LevelManager levelManager = new LevelManager(this);
	private KeyInput keyIn = new KeyInput(this);

	private EntityManager entityManager = new EntityManager(this);
	
	private EchoPulse echo = new EchoPulse(this);
	
	private GameOver gameOver = new GameOver(this);
	private Menu menu = new Menu(this);
	private Paused paused = new Paused(this);
	private Winner winScreen = new Winner(this);
	private Settings settings = new Settings(this);
	private Playing playing = new Playing(this);
	private Screen currentScreen = menu;


	Thread gameThread;
			
	public GamePanel() {
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyIn);
		this.setFocusable(true);
	}

	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}


	@Override
	public void run() {

		double drawInterval = 1000000000/fps;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		
		int drawCount = 0;
		long timer = 0;
		
		while(gameThread != null) {
			
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			timer += (currentTime - lastTime);
			lastTime = currentTime;
			
			if (delta >= 1) {
				update();
				repaint();
				delta--;
				drawCount++;
			}
			
			if (timer >= 1000000000) {
				System.out.println("FPS: " + drawCount);
				drawCount = 0;
				timer = 0;
			}
		}
	}
	
	public void update() {
		
		currentScreen = getCurrentScreen();
		if (currentScreen != null) {
			currentScreen.update();
	    }
	}
	
	public void paintComponent (Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		currentScreen = getCurrentScreen();
		if (currentScreen != null) {
			currentScreen.draw(g2);
	    }
		
		g2.dispose();
	}
	
	public void nextLevel() {
		levelManager.nextLevel();
		entityManager.nextLevel();
		echo.nextLevel();
	}
	
	public void resetAll() {
		levelManager.resetAll();
		entityManager.resetAll();
		echo.resetAll();	
	}
	
	public void resetLevel() {
		levelManager.resetLevel();
		entityManager.resetLevel();
		echo.resetLevel();
	}
	
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
	            return null; // Return null if no valid game state exists
	    }
	}

//	public int getNumEnemy() {
//		return numEnemy;
//	}
//
//	public void setNumEnemy(int numEnemy) {
//		this.numEnemy = numEnemy;
//	}

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
}
