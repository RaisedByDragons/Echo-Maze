package gameStart;

import java.util.ArrayList;
import java.util.List;

import entities.BasicEnemy;
import entities.EndOfMaze;
import entities.Player;
import utilz.Enums.GameState;

public class CollisionChecker {
		
	private GamePanel gp;
	private Player pl;
	private EndOfMaze end;
	private ArrayList<BasicEnemy> basEn;
	
	public CollisionChecker(GamePanel gp) { //, Player pl, ArrayList<BasicEnemy> basEn, EndOfMaze end) {
		this.gp = gp;	
	}
	
	public void checkCollision() {
		pl = gp.getPl();
		basEn = gp.getBasEnList();
		end = gp.getEndOfLevel();
		
		for (BasicEnemy enemy : basEn) {
	        if (pl.getRow() == enemy.getRow() && pl.getCol() == enemy.getCol()) {
	        	gp.setGameState(GameState.GAME_OVER);
	            return;
	        }
	    }
		
		checkPlayerHitEnd();
    }
	
	public void checkPlayerHitEnd() {
		if(pl.getRow() == end.getRow() && pl.getCol() == end.getCol()) {
			gp.nextLevel();
		}
	}
}