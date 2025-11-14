package screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import gameStart.GamePanel;
import gameStart.KeyInput;
import utilz.Enums.Difficulty;
import utilz.Enums.GameState;

public class Settings extends Screen {

    private GamePanel gp;
    private String[] options = {"Difficulty", "Number of Levels", "Enemy Count"};
    private int selectedOption = 0;

    public Settings(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, 0, gp.SCREEN_WIDTH, gp.SCREEN_HEIGHT);
        g2.setColor(Color.WHITE);
        g2.drawString("Settings", 100, 60);

        for (int i = 0; i < options.length; i++) {
            String text = options[i];
            if (i == selectedOption) {
                text = "> " + text; // highlight selection
            }

            switch (i) {
                case 0:
                    text += ": " + gp.getDiff().name();
                    break;
                case 1:
                	text += ": " + gp.getNumLevels();
                	break;
                case 2:
                    text += ": " + gp.getNumEnemy();
                    break;
            }

            g2.drawString(text, 100, 100 + i * 20);
        }

        g2.drawString("Press ESC to return", 100, 200);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Nothing for now
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        switch (code) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                selectedOption--;
                if (selectedOption < 0) selectedOption = options.length - 1;
                break;

            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                selectedOption++;
                if (selectedOption >= options.length) selectedOption = 0;
                break;

            case KeyEvent.VK_ENTER:
                handleEnter();
                break;

            case KeyEvent.VK_LEFT:
                handleLeftRight(-1);
                break;

            case KeyEvent.VK_RIGHT:
                handleLeftRight(1);
                break;

            case KeyEvent.VK_ESCAPE:
                gp.setGameState(GameState.MENU);
                gp.setCurrentScreen(gp.getCurrentScreen());
                break;
            default:
            	break;
        }
    }

    private void handleEnter() {
        switch (selectedOption) {
            case 1: // Generate New Levels
                gp.resetAll();
                break;
        }
    }

    private void handleLeftRight(int dir) {
        switch (selectedOption) {
            case 0: // Difficulty
                changeDifficulty(dir);
                break;
            case 1: //Create New Levels
            	setNumLevels(dir);
            	break;
            case 2: // Enemy Count
                setEnemyCount(dir);
                break;
        }
    }
    
    private void changeDifficulty(int dir) {
    	int ordinal = gp.getDiff().ordinal() + dir;
        if (ordinal < 0) ordinal = Difficulty.values().length - 1;
        if (ordinal >= Difficulty.values().length) ordinal = 0;
        gp.setDiff(Difficulty.values()[ordinal]);
        gp.getLevelM().setDifficulty(gp.getDiff());
        
        switch (gp.getDiff()) {
        case BABY:
        	gp.setNumLevels(1);
        	gp.setNumEnemy(1);
        	break;
        case EASY:
        	gp.setNumLevels(3);
        	gp.setNumEnemy(3);
        	break;
        case MEDIUM:
        	gp.setNumLevels(4);
        	gp.setNumEnemy(4);
        	break;
        case HARD:
        	gp.setNumLevels(5);
        	gp.setNumEnemy(6);
        	break;
        case EXTREME:
        	gp.setNumLevels(9);
        	gp.setNumEnemy(10);
        	break;
        case CUSTOM:
        	break;
        }
    }
    
    private void setNumLevels(int dir) {
    	gp.setDiff(Difficulty.CUSTOM);
    	gp.setNumLevels(gp.getNumLevels() + dir);
    	if (gp.getNumLevels() < 1) {
    		gp.setNumLevels(0);
    	}
    	gp.getLevelM().reset();
    }
    
//    private void setNumLevels(int dir) {
//    	gp.setDiff(Difficulty.CUSTOM);
//    	gp.setNumLevels(gp.getNumLevels() + dir);
//    	if (gp.getNumLevels() < 1) {
//    		gp.setNumLevels(0);
//    	}
//    	gp.levelM.reset();
//    }
    
    private void setEnemyCount(int dir) {
    	gp.setDiff(Difficulty.CUSTOM);
    	gp.setNumEnemy(gp.getNumEnemy() + dir);
        if (gp.getNumEnemy() < 0) {
        	gp.setNumEnemy(0);
        }
        gp.setBasEnList(null);
        gp.generateEnemies();
    }

    @Override
    public void update() {
        // Nothing needed here yet
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Unused
    }
}