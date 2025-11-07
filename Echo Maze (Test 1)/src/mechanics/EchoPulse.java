package mechanics;

import java.awt.Color;
import java.awt.Graphics2D;

import gameStart.GamePanel;
import levels.Level;

public class EchoPulse {
	
	private GamePanel gp; // Reference to the main game panel (used for constants and level access)
	
    // Core echo pulse variables
    private int centerRow, centerCol; // The tile position where the pulse starts
    private int drawRadius;           // Current radius of the expanding pulse
    private int pulseTimer;           // Timer for pulse expansion speed control
    private boolean active = false;   // Whether an echo pulse is currently active
    
    // Pulse configuration parameters
    private int radius;               // Maximum distance (in tiles) the pulse will expand
    private int pulseDelay;           // Delay between each radius expansion (smaller = faster pulse)
    private int visibilityDuration;   // How long tiles remain visible before fading
    private int fadeDelayPerTile;     // Delay between fade timing per tile (affects ripple fading)
    private int maxNumPulse;          // Maximum number of echoes the player can send
    private int numPulseLeft;         // Number of echoes currently available to the player

    // Visibility tracking arrays
	private boolean[][] visibilityMap; // Tracks which tiles are currently visible
    private int[][] visibilityTimer;   // Tracks how long each tile should remain visible

    // Constructor initializes arrays based on screen size from GamePanel
    public EchoPulse(GamePanel gp) {
        this.gp = gp;
        visibilityMap = new boolean[gp.MAX_SCREEN_HEIGHT][gp.MAX_SCREEN_WIDTH];
        visibilityTimer = new int[gp.MAX_SCREEN_HEIGHT][gp.MAX_SCREEN_WIDTH];
    }
    
    // Starts a new echo pulse centered on a given tile (col, row)
    public void newEcho(int col, int row) {
        centerRow = row;
        centerCol = col;
        drawRadius = 0;  // Reset pulse radius
        pulseTimer = 0;  // Reset pulse speed timer
        if(numPulseLeft > 0) {
        	numPulseLeft--; // Consume one available pulse
        }
        active = true; // Activate pulse
    }

    // Updates the echo pulse and visibility maps each frame
    public void update() {
    	// --- Update visibility timers ---
    	for (int row = 0; row < visibilityTimer.length; row++) {
    	    for (int col = 0; col < visibilityTimer[0].length; col++) {
    	        if (visibilityTimer[row][col] > 0) {
    	        	visibilityTimer[row][col]--; // Countdown visibility time for each tile
    	        }
    	    }
    	}
    	
    	// --- Update visibility states based on timers ---
    	for (int row = 0; row < visibilityMap.length; row++) {
    	    for (int col = 0; col < visibilityMap[0].length; col++) {
    	        visibilityMap[row][col] = visibilityTimer[row][col] > 0; // Visible if timer > 0
    	    }
    	}
    	
        // If pulse is not active, nothing else to update
        if (!active) return;

        pulseTimer++;
        if (pulseTimer >= pulseDelay) { // Expand pulse after enough frames
            drawRadius++;
            pulseTimer = 0;
            
            // Light up tiles newly reached by this radius
            for (int y = -drawRadius; y <= drawRadius; y++) {
                for (int x = -drawRadius; x <= drawRadius; x++) {
                    double dist = Math.sqrt(x * x + y * y);
                    if (dist <= drawRadius) { // Reveal all tiles within the circular pulse radius
                        int col = centerCol + x;
                        int row = centerRow + y;
                        // Ensure indices are within bounds
                        if (row >= 0 && row < getVisibilityMap().length &&
                            col >= 0 && col < getVisibilityMap()[0].length) {
                        	visibilityMap[row][col] = true;
                        	int visibilityTime = Math.max(0, visibilityDuration - fadeDelayPerTile);
                        	// Refresh visibility timer if it's lower than new time
                        	if (visibilityTimer[row][col] < visibilityTime) {
                        	    visibilityTimer[row][col] = visibilityTime;
                        	}
                        }
                    }
                }
            }
        }

        // When pulse exceeds max radius, deactivate it
        if (drawRadius > radius) {
            active = false;
        }
    }

    // Draws the expanding edge of the echo pulse ring
    public void draw(Graphics2D g2) {
    	if (!active) return;
    	
        for (int y = -drawRadius; y <= drawRadius; y++) {
            for (int x = -drawRadius; x <= drawRadius; x++) {
                double dist = Math.sqrt(x * x + y * y);
                // Draw only the tiles close to the pulse edge
                if (Math.abs(dist - drawRadius) < 0.5) {
                    int drawX = (centerCol + x) * gp.TILE_SIZE;
                    int drawY = (centerRow + y) * gp.TILE_SIZE;
                    g2.setColor(new Color(150, 0, 100, 100)); // Purple translucent ring
                    g2.fillRect(drawX, drawY, gp.TILE_SIZE, gp.TILE_SIZE);
                }
            }
        }
    }
    
    // Draws visible and fading tiles based on visibility timers
    public void drawEchoFade(Graphics2D g2) {
        Level currentLevel = gp.getLevelManager().getCurrentLevel();
        if (currentLevel == null || currentLevel.getTileList() == null || currentLevel.getGrid() == null) {
            return; // Skip drawing if level not ready
        }

        // Loop over entire screen grid
        for (int row = 0; row < gp.MAX_SCREEN_HEIGHT; row++) {
            for (int col = 0; col < gp.MAX_SCREEN_WIDTH; col++) {
                if (!getVisibilityMap()[row][col]) {
                    // Completely hidden tile — draw as black
                    g2.setColor(Color.BLACK);
                    g2.fillRect(col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE);
                } else {
                    // Tile is visible but fading
                    int timer = getVisibilityTimer()[row][col];
                    float fadeRatio = 1.0f - ((float) timer / getVisibilityDuration());
                    fadeRatio = (float) Math.pow(fadeRatio, 2); // Smooth fade curve
                    Color overlay = new Color(0, 0, 0, Math.max(0, Math.min(1, fadeRatio)));

                    // Draw original tile image with fading overlay
                    g2.setColor(overlay);
                    g2.drawImage(
                        currentLevel.getTileList()[currentLevel.getGrid()[row][col]].getImage(),
                        col * gp.TILE_SIZE, row * gp.TILE_SIZE,
                        gp.TILE_SIZE, gp.TILE_SIZE, null
                    );
                    g2.fillRect(col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE);
                }
            }
        }
    }

    // Sets all configurable echo pulse parameters (used when loading settings)
    public void setPulseDefaults(int maxNumPulse, int fadeDelayPerTile, int pulseDelay,
    		int radius, int visibilityDuration) {
    	
    	this.maxNumPulse = maxNumPulse;
    	this.fadeDelayPerTile = fadeDelayPerTile;
    	this.pulseDelay = pulseDelay;
    	this.radius = radius;
    	this.visibilityDuration = visibilityDuration;
    }
    
    // Keeps the 3x3 area around the player always visible
    public void seeAroundPlayer(int x, int y) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int r = y + dy;
                int c = x + dx;
                if (r >= 0 && r < gp.MAX_SCREEN_HEIGHT && c >= 0 && c < gp.MAX_SCREEN_WIDTH) {
                    visibilityMap[r][c] = true;
                    visibilityTimer[r][c] = visibilityDuration; // Prevents these tiles from fading
                }
            }
        }
    }
    
    // --- Reset and level transition handling ---

    // Resets all visibility and pulse counters (e.g., after player death)
    public void resetAll() {
    	visibilityMap = new boolean[gp.MAX_SCREEN_HEIGHT][gp.MAX_SCREEN_WIDTH];
    	visibilityTimer = new int[gp.MAX_SCREEN_HEIGHT][gp.MAX_SCREEN_WIDTH];
    	active = false;
    	numPulseLeft = maxNumPulse;
    }
    
    // Called when moving to the next level
    public void nextLevel() {
    	visibilityMap = new boolean[gp.MAX_SCREEN_HEIGHT][gp.MAX_SCREEN_WIDTH];
    	visibilityTimer = new int[gp.MAX_SCREEN_HEIGHT][gp.MAX_SCREEN_WIDTH];
    	active = false;
    	numPulseLeft = maxNumPulse;
    }
    
    // Called when restarting the current level
    public void resetLevel() {
    	visibilityMap = new boolean[gp.MAX_SCREEN_HEIGHT][gp.MAX_SCREEN_WIDTH];
    	visibilityTimer = new int[gp.MAX_SCREEN_HEIGHT][gp.MAX_SCREEN_WIDTH];
    	active = false;
    	numPulseLeft = maxNumPulse;
    }

    // --- Getter and Setter methods ---
    public boolean isActive() {
        return active;
    }
    
    public int getMaxPulse() {
    	return maxNumPulse;
    }
    
    public void setMaxPulse(int numPulse) {
    	maxNumPulse = numPulse;
    }
    
    // Returns whether a given tile is visible
    public boolean isTileVisible(int col, int row) {
        return visibilityMap[row][col];
    }

	public boolean[][] getVisibilityMap() {
		return visibilityMap;
	}

	public int[][] getVisibilityTimer() {
		return visibilityTimer;
	}

	public int getVisibilityDuration() {
		return visibilityDuration;
	}

	public int getNumPulseLeft() {
		return numPulseLeft;
	}

	public void setNumPulseLeft(int numPulseLeft) {
		this.numPulseLeft = numPulseLeft;
	}

	public int defaultPulseLeft() {
		return maxNumPulse;
	}
	
	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getPulseDelay() {
		return pulseDelay;
	}

	public void setPulseDelay(int pulseDelay) {
		this.pulseDelay = pulseDelay;
	}

	public int getFadeDelayPerTile() {
		return fadeDelayPerTile;
	}

	public void setFadeDelayPerTile(int fadeDelayPerTile) {
		this.fadeDelayPerTile = fadeDelayPerTile;
	}

	public void setVisibilityDuration(int visibilityDuration) {
		this.visibilityDuration = visibilityDuration;
	}
	
    // --- Adjustment methods for settings screen (increase/decrease values) ---
	public void adjustMaxPulse(int delta) {
	    maxNumPulse = Math.max(0, maxNumPulse + delta);
	}
	public void adjustFadeDelay(int delta) {
	    fadeDelayPerTile = Math.max(1, fadeDelayPerTile + delta);
	}
	public void adjustPulseDelay(int delta) {
	    pulseDelay = Math.max(0, pulseDelay + delta);
	}
	public void adjustRadius(int delta) {
	    radius = Math.max(1, radius + delta);
	}
	public void adjustVisibilityDuration(int delta) {
	    visibilityDuration = Math.max(0, visibilityDuration + delta);
	}
}
