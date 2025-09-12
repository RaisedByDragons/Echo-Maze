package mechanics;

import java.awt.Color;
import java.awt.Graphics2D;

import gameStart.GamePanel;
import levels.Level;

public class EchoPulse {
	
	private GamePanel gp;
	
    private int centerRow, centerCol, drawRadius, pulseTimer;
    private boolean active = false;
    private int radius; //Maximum radius of the echo pulse
    private int pulseDelay; //Speed of echo pulse, (0) = high to low
    private int visibilityDuration; // e.g., 60 frames = 1 second at 60 FPS
    private int fadeDelayPerTile; //Fade out per tile
    private int maxNumPulse;
    private int numPulseLeft;

	private boolean[][] visibilityMap;
    private int[][] visibilityTimer;


    public EchoPulse(GamePanel gp) {
        this.gp = gp;
        visibilityMap = new boolean[gp.MAX_SCREEN_HEIGHT][gp.MAX_SCREEN_WIDTH];
        visibilityTimer = new int[gp.MAX_SCREEN_HEIGHT][gp.MAX_SCREEN_WIDTH];
    }
    
    public void newEcho(int col, int row) {
        centerRow = row;
        centerCol = col;
        drawRadius = 0;
        pulseTimer = 0;
        if(numPulseLeft > 0) {
        	numPulseLeft--;
        }
        active = true;
    }

    public void update() {
    	for (int row = 0; row < visibilityTimer.length; row++) {
    	    for (int col = 0; col < visibilityTimer[0].length; col++) {
    	        if (visibilityTimer[row][col] > 0) {
    	        	visibilityTimer[row][col]--;
    	        }
    	    }
    	}
    	
    	for (int row = 0; row < visibilityMap.length; row++) {
    	    for (int col = 0; col < visibilityMap[0].length; col++) {
    	        visibilityMap[row][col] = visibilityTimer[row][col] > 0;
    	    }
    	}
    	
        if (!active) return;

        pulseTimer++;
        if (pulseTimer >= pulseDelay) {
            drawRadius++;
            pulseTimer = 0;
            
         // Light up newly reached tiles
            for (int y = -drawRadius; y <= drawRadius; y++) {
                for (int x = -drawRadius; x <= drawRadius; x++) {
                    double dist = Math.sqrt(x * x + y * y);
                    if (dist <= drawRadius) { // ✅ Reveal all within the current radius
                        int col = centerCol + x;
                        int row = centerRow + y;
                        if (row >= 0 && row < getVisibilityMap().length &&
                            col >= 0 && col < getVisibilityMap()[0].length) {
                        	visibilityMap[row][col] = true;
                        	int visibilityTime = Math.max(0, visibilityDuration - fadeDelayPerTile);
                        	if (visibilityTimer[row][col] < visibilityTime) {
                        	    visibilityTimer[row][col] = visibilityTime;
                        	}
                        }
                    }
                }
            }
                
        }

        if (drawRadius > radius) {
            active = false;
        }
    }

    public void draw(Graphics2D g2) {
    	    	
    	if (!active) return;
    	
        for (int y = -drawRadius; y <= drawRadius; y++) {
            for (int x = -drawRadius; x <= drawRadius; x++) {
                double dist = Math.sqrt(x * x + y * y);
                if (Math.abs(dist - drawRadius) < 0.5) {
                    int drawX = (centerCol + x) * gp.TILE_SIZE;
                    int drawY = (centerRow + y) * gp.TILE_SIZE;
                    g2.setColor(new Color(150, 0, 100, 100));
                    g2.fillRect(drawX, drawY, gp.TILE_SIZE, gp.TILE_SIZE);
                }
            }
        }
    }
    
//    public void drawEchoFade(Graphics2D g2) {
//    	for (int row = 0; row < gp.MAX_SCREEN_HEIGHT; row++) {
//            for (int col = 0; col < gp.MAX_SCREEN_WIDTH; col++) {
//                if (!getVisibilityMap()[row][col]) {
//                    // Fully dark tile
//                    g2.setColor(Color.BLACK);
//                    g2.fillRect(col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE);
//                } else {
//                    // Fading tile:
//                    int timer = getVisibilityTimer()[row][col];
//                    float fadeRatio = 1.0f - ((float) timer / getVisibilityDuration());
//                    fadeRatio = (float) Math.pow(fadeRatio, 2); // smooth easing
//                    
//                    Color overlay = new Color(0, 0, 0, Math.max(0, Math.min(1, fadeRatio))); //In float form, keep between 0 and 255
//                    
//                    g2.setColor(overlay);
//                    g2.drawImage(gp.getLevelManager().getCurrentLevel().getTileList()[gp.getLevelManager().getCurrentLevel().getGrid()[row][col]].getImage(), col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE, null);
//                    g2.fillRect(col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE);
//                }
//            }
//        }
//    }
    public void drawEchoFade(Graphics2D g2) {
        Level currentLevel = gp.getLevelManager().getCurrentLevel();
        if (currentLevel == null || currentLevel.getTileList() == null || currentLevel.getGrid() == null) {
            return; // Prevent drawing if level is not ready
        }

        for (int row = 0; row < gp.MAX_SCREEN_HEIGHT; row++) {
            for (int col = 0; col < gp.MAX_SCREEN_WIDTH; col++) {
                if (!getVisibilityMap()[row][col]) {
                    g2.setColor(Color.BLACK);
                    g2.fillRect(col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE);
                } else {
                    int timer = getVisibilityTimer()[row][col];
                    float fadeRatio = 1.0f - ((float) timer / getVisibilityDuration());
                    fadeRatio = (float) Math.pow(fadeRatio, 2);
                    Color overlay = new Color(0, 0, 0, Math.max(0, Math.min(1, fadeRatio)));

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

    
    public void setPulseDefaults(int maxNumPulse, int fadeDelayPerTile, int pulseDelay,
    		int radius, int visibilityDuration) {
    	
    	this.maxNumPulse = maxNumPulse;
    	this.fadeDelayPerTile = fadeDelayPerTile;
    	this.pulseDelay = pulseDelay;
    	this.radius = radius;
    	this.visibilityDuration = visibilityDuration;
    }
    
    public void seeAroundPlayer(int x, int y) {
     // Make surrounding 3x3 area around player always visible
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int r = y + dy;
                int c = x + dx;
                if (r >= 0 && r < gp.MAX_SCREEN_HEIGHT && c >= 0 && c < gp.MAX_SCREEN_WIDTH) {
                    visibilityMap[r][c] = true;
                    visibilityTimer[r][c] = visibilityDuration; //always visible (number corresponds to brightness of tile
                }
            }
        }
    }
    
    public void resetAll() {//resets screen visibility
    	visibilityMap = new boolean[gp.MAX_SCREEN_HEIGHT][gp.MAX_SCREEN_WIDTH];
    	numPulseLeft = maxNumPulse;
    }
    
    public void nextLevel() {
    	visibilityMap = new boolean[gp.MAX_SCREEN_HEIGHT][gp.MAX_SCREEN_WIDTH];
    	numPulseLeft = maxNumPulse;
    }
    
    public void resetLevel() {
    	visibilityMap = new boolean[gp.MAX_SCREEN_HEIGHT][gp.MAX_SCREEN_WIDTH];
    	numPulseLeft = maxNumPulse;
    }

    public boolean isActive() {
        return active;
    }
    
    public int getMaxPulse() {
    	return maxNumPulse;
    }
    
    public void setMaxPulse(int numPulse) {
    	maxNumPulse = numPulse;
    }
    
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