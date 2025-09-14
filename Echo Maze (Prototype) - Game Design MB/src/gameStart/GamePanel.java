package gameStart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JPanel;

import entities.BasicEnemy;
import entities.EndOfMaze;
import entities.Player;
import levels.Level;
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

public class GamePanel extends JPanel implements Runnable {

	// Screen Settings
	
	public EndOfMaze getEndOfLevel() {
		return endOfLevel;
	}

	final static int ORIGINAL_TILE_SIZE = 16;
	public final static double SCALE = 1;
	
	public final int TILE_SIZE = (int)(ORIGINAL_TILE_SIZE * SCALE);
	
	public final int MAX_SCREEN_WIDTH = 35; //Must be odd
	public final int MAX_SCREEN_HEIGHT = 35;
	
	public final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_WIDTH;
	public final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_HEIGHT;
	
	int fps = 60;
	
	private GameState gameState = GameState.MENU;
	
	private Difficulty diff = Difficulty.MEDIUM;
	
//	private int playerSpawnX = 1;
//	private int playerSpawnY = 1;
	private int numLevels = 2;
	
	private int numEnemy = 2;
			
			
	private EchoPulse echo = new EchoPulse(this);
	private LevelManager levelM = new LevelManager(this, getNumLevels(), getDiff(), getEcho());
	private KeyInput keyIn = new KeyInput(this);
	private Player pl = new Player(this, keyIn, getLevelM(), getEcho());
	private EndOfMaze endOfLevel = new EndOfMaze(this, getLevelM());

	Thread gameThread;
	
	private GameOver gameOver = new GameOver(this);
	private Menu menu = new Menu(this);
	private Paused paused = new Paused(this);
	private Winner winScreen = new Winner(this);
	private Settings settings = new Settings(this);
	private Playing playing = new Playing(this);//, pl, basEnList, endOfLevel);
	private Screen currentScreen = getCurrentScreen();
	
	private ArrayList<BasicEnemy> basEnList = new ArrayList<>();
	private ArrayList<Level> levelList = getLevelM().getLevelList();
	
	private CollisionChecker coll = new CollisionChecker(this);
		
	public GamePanel() {
		
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyIn);
		this.setFocusable(true);
		
		
		generateEnemies();
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
		
		setCurrentScreen(getCurrentScreen());
		if (getCurrentScreen() != null) {
			if (getGameState() == GameState.PLAYING) {
				getPl().update();
				
				for(BasicEnemy en : getBasEnList()) {
					en.update();
					en.updateVisibilityFromPulse(echo);
				}
	
				coll.checkCollision();
			}
	        getCurrentScreen().update();
	    }
	}
	
	public void paintComponent (Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		setCurrentScreen(getCurrentScreen());
		if (getCurrentScreen() != null) {
			if (getGameState() == GameState.PLAYING || 
					getGameState() == GameState.PAUSED ||
					getGameState() == GameState.GAME_OVER) {
				getLevelM().getCurrentLevel().draw(g2);
				endOfLevel.draw(g2);
				getPl().draw(g2);
				
				for(BasicEnemy en : getBasEnList()) {
//					if (!en.isRevealed()) return;
					en.draw(g2);
				}
			}
	        getCurrentScreen().draw(g2);
	    }
		
		g2.dispose();
	}
	
	public void nextLevel() {
		getLevelM().nextLevel();
		getPl().nextLevel();
		endOfLevel.nextLevel();
		getEcho().nextLevel();
		for (BasicEnemy en : getBasEnList()) {
			en.nextLevel();
		}
	}
	
	public void generateEnemies() {
		basEnList = new ArrayList<>();
		for(int i=0; i<getNumEnemy(); i++) {
			getBasEnList().add(new BasicEnemy(this, getLevelM()));
		}
	}
	
	public void resetAll() {
		getLevelM().reset();
		getPl().reset();
		endOfLevel.reset();
		getEcho().reset();
		
		for (BasicEnemy en : getBasEnList()) {
			en.reset();
		}
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
	            return settings;
	        default:
	            return null; // Return null if no valid game state exists
	    }
	}

	public int getNumEnemy() {
		return numEnemy;
	}

	public void setNumEnemy(int numEnemy) {
		this.numEnemy = numEnemy;
	}

	public ArrayList<BasicEnemy> getBasEnList() {
		return basEnList;
	}

	public void setBasEnList(ArrayList<BasicEnemy> basEnList) {
		this.basEnList = basEnList;
	}

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
		return getEcho();
	}

	public int getNumLevels() {
		return numLevels;
	}

	public void setNumLevels(int numLevels) {
		this.numLevels = numLevels;
		getLevelM().setNumLevels(numLevels);
		resetAll();
	}

	public Player getPl() {
		return pl;
	}

	public LevelManager getLevelM() {
		return levelM;
	}

	public EchoPulse getEcho() {
		return echo;
	}
}
