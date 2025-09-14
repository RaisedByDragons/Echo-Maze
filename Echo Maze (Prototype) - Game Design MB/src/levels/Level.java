package levels;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import gameStart.GamePanel;
import mechanics.EchoPulse;
import utilz.Enums.Difficulty;
import utilz.Enums.TileType;

public class Level {//extends Tile{

	private final int width;
    private final int height;
    private int[][] grid;
    
	private Tile tile = new Tile();
	public Tile[] tileList = tile.buildTileInfo();
    
    GamePanel gp;
    EchoPulse echo;
    
//    public LevelManager(int width, int height) {
//        // Ensure odd dimensions for proper wall-path layout
//        this.width = (width % 2 == 0) ? width + 1 : width;
//        this.height = (height % 2 == 0) ? height + 1 : height;
//        this.grid = new int[this.height][this.width];
//        generateMaze();
//    }
    
    public Level(GamePanel gp, EchoPulse echo) {
    	this.gp = gp;
        this.width = gp.MAX_SCREEN_WIDTH;
        this.height = gp.MAX_SCREEN_HEIGHT;
        this.grid = new int[height][width];
        this.echo = echo;
    }

    public void generateMaze(Difficulty difficulty, int x, int y) {
        // Fill everything with walls
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                grid[i][j] = TileType.WALL.getValue();

        // Start recursive backtracking from (x,y)
        carvePath(x, y);
        
        addDifficultyCutouts(difficulty);
        
        addEntranceAndExit();
//        printMaze(); //Debug
    }

    private void carvePath(int x, int y) {
        int[] dx = { 0, 0, -2, 2 };
        int[] dy = { -2, 2, 0, 0 };

        // Shuffle directions for random maze
        Integer[] directions = { 0, 1, 2, 3 };
        Collections.shuffle(Arrays.asList(directions));

        for (int dir : directions) {
            int nx = x + dx[dir];
            int ny = y + dy[dir];

            // Check bounds
            if (nx > 0 && ny > 0 && nx < width - 1 && ny < height - 1) {
                if (grid[ny][nx] == TileType.WALL.getValue()) {
                    grid[ny][nx] = TileType.PATH.getValue();
                    grid[y + dy[dir] / 2][x + dx[dir] / 2] = TileType.PATH.getValue();
                    carvePath(nx, ny);
                }
            }
        }
    }

    public void printMaze() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(grid[y][x] == TileType.WALL.getValue() ? "#" : " ");
	        }
            System.out.println();
        }
    }
    
    private void addDifficultyCutouts(Difficulty difficulty) {
    	int area = width * height;
    	
        int cutouts = switch (difficulty) {
			case BABY -> (int) (area * 0.1);
        	case EASY -> (int) (area * 0.05);    //Higher means % more open
            case MEDIUM -> (int) (area * 0.03);
            case HARD -> (int) (area * 0.02);
			case EXTREME -> (int) (area * 0.01);
			case CUSTOM -> (int) (area * 0.04); //TODO should be changeable
			default -> throw new IllegalArgumentException("Unexpected value: " + difficulty);
        };

        Random rand = new Random();
        int attempts = 0;

        while (cutouts > 0 && attempts < 500) {
            int x = 1 + rand.nextInt(width - 2);
            int y = 1 + rand.nextInt(height - 2);

            // Only break walls between two paths (either horizontal or vertical)
            if (grid[y][x] == TileType.WALL.getValue()) {
                boolean horizontalCut = grid[y][x - 1] == TileType.PATH.getValue() && grid[y][x + 1] == TileType.PATH.getValue();
                boolean verticalCut = grid[y - 1][x] == TileType.PATH.getValue() && grid[y + 1][x] == TileType.PATH.getValue();

                if (horizontalCut || verticalCut) {
                    grid[y][x] = TileType.PATH.getValue();
                    cutouts--;
                }
            }

            attempts++;
        }
    }
    
    private void addEntranceAndExit() {
        // EXIT: top row, find a PATH in second row to open above it
//        for (int i = 1; i < width - 1; i++) {
//            if (grid[1][i] == TileType.PATH.getValue()) {
//                grid[0][i] = TileType.END.getValue(); // Carve entrance
//                break;
//            }
//        }
    	List<Integer> topCandidates = new ArrayList<>();
        for (int i = 1; i < width - 1; i++) {
            topCandidates.add(i);
        }
        Collections.shuffle(topCandidates);

        for (int i : topCandidates) {
            if (grid[1][i] == TileType.PATH.getValue()) {
                grid[0][i] = TileType.END.getValue(); // Carve the exit at top
                break;
            }
        }

        // ENTRANCE: bottom row, find a PATH in second-to-last row to open below it
        for (int i = width - 2; i > 0; i--) {
            if (grid[height - 1][i] == TileType.PATH.getValue()) {
                grid[height - 1][i] = TileType.START.getValue(); // Carve exit
                break;
            }
        }
    }

    
    public void draw(Graphics2D g2) {
        int[][] grid = getGrid();
//        int tileNum;
//        for (int y = 0; y < grid.length; y++) {
//            for (int x = 0; x < grid[0].length; x++) {
//            	if (echo.isTileVisible(x, y)) {
//            		g2.drawImage(tileList[grid[y][x]].getImage(), x * gp.TILE_SIZE, y * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE, null);
//            	} else {
//            	    // Optionally draw a dark overlay or skip drawing entirely
//            	}
//                
//            }
//        }    
        
        for (int row = 0; row < gp.MAX_SCREEN_HEIGHT; row++) {
            for (int col = 0; col < gp.MAX_SCREEN_WIDTH; col++) {
                if (!gp.getEcho().getVisibilityMap()[row][col]) {
                    // Fully dark tile
                    g2.setColor(Color.BLACK);
                    g2.fillRect(col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE);
                } else {
                    // Fading tile: calculate alpha
                    int timer = gp.getEcho().getVisibilityTimer()[row][col];
                    float alpha = 1.0f - ((float) timer / gp.getEcho().getVisibilityDuration());

                    // Clamp alpha just in case
//                    alpha = Math.min(1f, Math.max(0f, alpha));
                    float fadeRatio = 1.0f - ((float) timer / gp.getEcho().getVisibilityDuration());
                    fadeRatio = (float) Math.pow(fadeRatio, 2); // smooth easing
//                    Color overlay = new Color(0, 0, 0, (int) (fadeRatio * 255));
                    Color overlay = new Color(0, 0, 0, Math.max(0, Math.min(1, fadeRatio))); //In float form, keep between 0 and 255
//                    Color overlay = new Color(0, 0, 0, (int) (alpha * 255));
                    g2.setColor(overlay);
                    g2.drawImage(tileList[grid[row][col]].getImage(), col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE, null);
                    g2.fillRect(col * gp.TILE_SIZE, row * gp.TILE_SIZE, gp.TILE_SIZE, gp.TILE_SIZE);
                }
            }
        }
    }

    public int[][] getGrid() {
        return grid;
    }
    
    public int[] getExit() {
    	int[] coords = new int[2]; //2 = 1 Entrance & 1 Exit
    	
    	for (int i=0; i<grid.length; i++) {
    		for (int j=0; j<grid[0].length; j++) {
    			if (grid[i][j] == TileType.END.getValue()) {
    				coords[0] = i;
    				coords[1] = j;
    			}
    		}
    	}
    	
    	return coords;
    }

	public Tile getTile(int row, int col) {
		return tile.getTile(grid[row][col]);
	}
}