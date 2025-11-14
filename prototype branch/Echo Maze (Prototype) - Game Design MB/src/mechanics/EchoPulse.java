package mechanics;

import java.awt.Color;
import java.awt.Graphics2D;

import gameStart.GamePanel;

public class EchoPulse {
	
	private GamePanel gp;
	
    private int centerRow, centerCol;
    private int radius = 10;
    private boolean active = false;
    private int drawRadius = 0;
    private int pulseTimer = 0;
    private final int pulseDelay = 6;
    private int visibilityDuration = 90; // e.g., 60 frames = 1 second at 60 FPS
    private int fadeDelayPerTile = 5;
    
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
        active = true;
    }

    public void update() {
    	for (int row = 0; row < getVisibilityTimer().length; row++) {
    	    for (int col = 0; col < getVisibilityTimer()[0].length; col++) {
    	        if (getVisibilityTimer()[row][col] > 0) {
    	            getVisibilityTimer()[row][col]--;
    	        }
    	    }
    	}
    	
    	for (int row = 0; row < visibilityMap.length; row++) {
    	    for (int col = 0; col < visibilityMap[0].length; col++) {
    	        visibilityMap[row][col] = getVisibilityTimer()[row][col] > 0;
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
                        	}//TODO fade not quite right, needs more complex implementation probably
//                            visibilityTimer[row][col] = (int) (visibilityDuration - dist * fadeDelayPerTile);

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
    
    public void seeAroundPlayer(int x, int y) {
    	// Reset visibility
//        for (int y = 0; y < gp.MAX_SCREEN_HEIGHT; y++) {
//            for (int x = 0; x < gp.MAX_SCREEN_WIDTH; x++) {
//                visibilityMap[x][y] = false;
//            }
//        }
        
     // Make surrounding 3x3 area around player always visible
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int r = y + dy;
                int c = x + dx;
                if (r >= 0 && r < gp.MAX_SCREEN_HEIGHT && c >= 0 && c < gp.MAX_SCREEN_WIDTH) {
                    visibilityMap[r][c] = true;
                    getVisibilityTimer()[r][c] = visibilityDuration; //always visible (number corresponds to brightness of tile
                }
            }
        }
    }
    
    public void reset() {//resets screen visibility
    	visibilityMap = new boolean[gp.MAX_SCREEN_HEIGHT][gp.MAX_SCREEN_WIDTH];
    }
    
    public void nextLevel() {
    	visibilityMap = new boolean[gp.MAX_SCREEN_HEIGHT][gp.MAX_SCREEN_WIDTH];
    }

    public boolean isActive() {
        return active;
    }
    
    public boolean isTileVisible(int col, int row) {
        if (row < 0 || row >= getVisibilityMap().length || col < 0 || col >= getVisibilityMap()[0].length) {
            return false;
        }
        return getVisibilityMap()[row][col];
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
}