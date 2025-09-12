package levels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import gameStart.GamePanel;
import utilz.Enums.Difficulty;
import utilz.Enums.TileType;

public class Level {//extends Tile{

	private int width;
    private int height;
    private int[][] grid;
    
    private int tutorialStage = 0;
    private int instructionType = 0;
    private boolean showInstructions = false;
    private boolean moved = false;
    private boolean pulsed = false;
    private boolean enemySeen = false;        // whether any enemy was seen
    private boolean enemyHidden = false; // whether the seen enemy has been hidden again
    private int timer = 0;
    private int updateInterval = 120;
    
	private Tile tile = new Tile();
	private Tile[] tileList = tile.buildTileInfo();
    
    GamePanel gp;

    public Level(GamePanel gp) {//, EchoPulse echo) {
    	this.gp = gp;
        this.width = gp.MAX_SCREEN_WIDTH;
        this.height = gp.MAX_SCREEN_HEIGHT;
        this.grid = new int[height][width];
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
    
        gp.getEchoPulse().drawEchoFade(g2);
        
        g2.setColor(Color.WHITE);
		g2.drawString("Level: " + gp.getLevelManager().getCurrentLevelNum(), 3, (gp.MAX_SCREEN_HEIGHT * gp.TILE_SIZE) - 3);
		g2.drawString("Pulses Remaining: " + gp.getEchoPulse().getNumPulseLeft(), (gp.MAX_SCREEN_WIDTH * gp.TILE_SIZE) - 8*gp.TILE_SIZE, (gp.MAX_SCREEN_HEIGHT * gp.TILE_SIZE) - 3);
		
		
	    if (!showInstructions) {
	    	return;
	    }
	    
	    
    	g2.setColor(new Color(0, 0, 0, 170)); // semi-transparent black background
	    g2.fillRect(0, 0, 450, 150); // Adjust dimensions as needed
	    g2.setColor(Color.WHITE);
	    g2.setFont(new Font("Arial", Font.BOLD, 16));
	    g2.drawString("Welcome to Echo Maze!", 20, 30);
	    g2.setFont(new Font("Arial", Font.PLAIN, 14));
	    
	    if (instructionType == 0) {
	    	if(tutorialStage > 1) {
		    	timer++;
		    	
		    	if (timer >= updateInterval) {
			    	timer = 0;
			    	tutorialStage++;
			    }
		    }
		    for (int i=0; i<=tutorialStage; i++) {
		    	switch (i) {
			    	case 0 -> g2.drawString("Use W A S D or arrow keys to move.", 20, 60);
			    	case 1 -> g2.drawString("Press SPACE to send out an echo pulse.", 20, 80);
			    	case 2 -> g2.drawString("Enemies are found around the map, try to avoid them.", 20, 100);
			    	case 3 -> g2.drawString("Reach the end tile (gray) to complete the level.", 20, 120);
		    	}
		    }
	    } else if (instructionType == 1) {
	    	for (int i=0; i<=tutorialStage; i++) {
		    	switch (i) {
			    	case 0 -> g2.drawString("Different Difficulties will spawn Different Enemies", 20, 60);
//			    	case 1 -> g2.drawString("Press SPACE to send out an echo pulse.", 20, 80);
//			    	case 2 -> g2.drawString("Enemies are found around the map, try to avoid them.", 20, 100);
//			    	case 3 -> g2.drawString("Reach the end tile (gray) to complete the level.", 20, 120);
		    	}
		    }
	    } else if (instructionType == 2) {
	    	if(tutorialStage > 0) {
		    	timer++;
		    	
		    	if (timer >= updateInterval) {
			    	timer = 0;
			    	tutorialStage++;
			    }
		    }
	    	for (int i=0; i<=tutorialStage; i++) {
		    	switch (i) {
			    	case 0 -> g2.drawString("The color of the enemy denotes its type.", 20, 60);
			    	case 1 -> g2.drawString("Red are basic enemies that follow the right hand wall.", 20, 80);
			    	case 2 -> g2.drawString("Blue are fast enemies, they will follow any available path.", 20, 100);
			    	case 3 -> g2.drawString("Pink are follow enemies, make sure you keep moving or they might run into you.", 20, 120);
		    	}
		    }
	    }
    	g2.drawString("Press ENTER to hide instructions.", 20, 150);

    }

    public void printMaze() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(grid[y][x] == TileType.WALL.getValue() ? "#" : " ");
	        }
            System.out.println();
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
    
    public void advanceTutorial() {
        if (tutorialStage < 3) {
            tutorialStage++;
        }
    }
    
	public Tile[] getTileList() {
		return tileList;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	protected void setGrid(int[][] grid) {
	    this.grid = grid;
	}

	public boolean isShowInstructions() {
		return showInstructions;
	}

	public void setShowInstructions(boolean showInstructions) {
		this.showInstructions = showInstructions;
	}
	
	public void setShowInstructions(boolean showInstructions, int message) {
		this.showInstructions = showInstructions;
		instructionType = message;
	}

	public int getTutorialStage() {
		return tutorialStage;
	}

	public void setTutorialStage(int tutorialStage) {
		this.tutorialStage = tutorialStage;
	}

	public boolean isMoved() {
		return moved;
	}

	public void setMoved(boolean moved) {
		this.moved = moved;
	}

	public boolean isPulsed() {
		return pulsed;
	}

	public void setPulsed(boolean pulsed) {
		this.pulsed = pulsed;
	}

	public boolean isEnemySeen() {
		return enemySeen;
	}

	public void setEnemySeen(boolean enemySeen) {
		this.enemySeen = enemySeen;
	}

	public boolean isEnemyHidden() {
		return enemyHidden;
	}

	public void setEnemyHidden(boolean enemyHidden) {
		this.enemyHidden = enemyHidden;
	}

	public int getInstructionType() {
		return instructionType;
	}

	public void setInstructionType(int instructionType) {
		this.instructionType = instructionType;
	}
}