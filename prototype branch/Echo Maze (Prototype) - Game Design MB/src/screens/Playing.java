package screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import entities.BasicEnemy;
import entities.EndOfMaze;
import entities.Player;
import gameStart.GamePanel;
import utilz.Enums.GameState;

public class Playing extends Screen {

	GamePanel gp;
//	Player pl;
//	ArrayList<BasicEnemy> basEnList;
//	EndOfMaze endOfMaze;
	
	private boolean moveUp, moveDown, moveLeft, moveRight;
    private boolean upHeld, downHeld, leftHeld, rightHeld;
	
	public Playing(GamePanel gp) {//, Player pl, ArrayList<BasicEnemy> basEnList, EndOfMaze endOfMaze) {
		this.gp = gp;
//		this.pl = pl;
//		this.basEnList = basEnList;
//		this.endOfMaze = endOfMaze;
	}
	
	public void draw(Graphics2D g2) {
		g2.setColor(Color.WHITE);
		g2.drawString("Level: " + gp.getLevelM().getCurrentLevelNum(), 3, (gp.MAX_SCREEN_HEIGHT * gp.TILE_SIZE) - 3);
//		drawEchos(g2);
	}
	
//	private void drawEchos(Graphics2D g2) {
//		if (gp.getEchoPulse().isTileVisible(gp.getPl().getCol(), gp.getPl().getRow())) {
//		    drawTileNormally(g2);
//		} else {
//		    drawTileAsDark(g2);
//		}
//	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		
		        if ((code == KeyEvent.VK_W || code == KeyEvent.VK_UP) && !upHeld) {
		            moveUp = true;
		            upHeld = true;
		        }
		        if ((code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) && !downHeld) {
		            moveDown = true;
		            downHeld = true;
		        }
		        if ((code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) && !leftHeld) {
		            moveLeft = true;
		            leftHeld = true;
		        }
		        if ((code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) && !rightHeld) {
		            moveRight = true;
		            rightHeld = true;
		        }		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            upHeld = false;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downHeld = false;
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftHeld = false;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightHeld = false;
        }
		
        switch (code) {
		case KeyEvent.VK_ESCAPE:
			gp.setGameState(GameState.PAUSED);
			break;
		case KeyEvent.VK_ENTER:
			
			break;
		default:
			break;
		}		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void resetMovementFlags() {
        moveUp = moveDown = moveLeft = moveRight = false;
    }

	public boolean isMoveUp() {
		return moveUp;
	}

	public boolean isMoveDown() {
		return moveDown;
	}

	public boolean isMoveLeft() {
		return moveLeft;
	}

	public boolean isMoveRight() {
		return moveRight;
	}
}
