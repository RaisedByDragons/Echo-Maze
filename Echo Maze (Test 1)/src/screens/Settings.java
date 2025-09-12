package screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gameStart.GamePanel;
import mechanics.EchoPulse;
//import gameStart.KeyInput;
import utilz.Enums.Difficulty;
import utilz.Enums.GameState;

public class Settings extends Screen {

    private GamePanel gp;
    private String[] options = {"Difficulty", "Number of Levels", "Enemy Count", "Num Echo Pulse Per Level"};
    private int selectedOption = 0;
    
//    List<OptionEntry> menuEntries;
    
    private boolean adjustingProportions = false;
    private boolean adjustingPulseSettings = false;

//    private int selectedEnemyTypeIndex = 0;

    public Settings(GamePanel gp) {
        this.gp = gp;
        setDifficultyDefaults();
        gp.getEntityManager().summonEntities();
        gp.resetAll();
    }
    
    public void draw(Graphics2D g2) {
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, 0, gp.SCREEN_WIDTH, gp.SCREEN_HEIGHT);
        g2.setColor(Color.WHITE);
        g2.drawString("Settings", 100, 60);

        List<OptionEntry> visible = getVisibleOptions();
        int y = 100;

        for (int i = 0; i < visible.size(); i++) {
            OptionEntry entry = visible.get(i);
            boolean isSelected = i == selectedOption;

            String dropdownIndicator = "  "; // default blank
            String optionText = "";

            // Build the optionText and dropdownIndicator
            if (entry.type().equals("main")) {
                int optionIndex = entry.index();
                optionText = options[optionIndex];

                switch (optionIndex) {
                    case 0 -> optionText += ": " + gp.getDiff().name();
                    case 1 -> optionText += ": " + gp.getLevelManager().getNumLevels();
                    case 2 -> {
                        dropdownIndicator = adjustingProportions ? "▾ " : "▸ ";
                        optionText += ": " + gp.getEntityManager().getNumEnemies();
                    }
                    case 3 -> {
                    	dropdownIndicator = adjustingProportions ? "▾ " : "▸ ";
                    	optionText += ": " + gp.getEchoPulse().getMaxPulse(); 
                    }
                }
            } else if (entry.type().equals("proportion")) {
                String type = gp.getEntityManager().getEnemyTypes().get(entry.index());
                int num = gp.getEntityManager().getEnemyAmounts(type);
                optionText = "  " + type + ": " + num;
            } else if (entry.type().equals("pulse")) {
                String label = switch (entry.index()) {
                    case 0 -> "Max Pulses";
                    case 1 -> "Fade Delay";
                    case 2 -> "Pulse Delay";
                    case 3 -> "Radius";
                    case 4 -> "Visibility Time";
                    default -> "";
                };

                int value = switch (entry.index()) {
                    case 0 -> gp.getEchoPulse().getMaxPulse();
                    case 1 -> gp.getEchoPulse().getFadeDelayPerTile();
                    case 2 -> gp.getEchoPulse().getPulseDelay();
                    case 3 -> gp.getEchoPulse().getRadius();
                    case 4 -> gp.getEchoPulse().getVisibilityDuration();
                    default -> 0;
                };

                optionText = "  " + label + ": " + value;
            }

            // Draw cursor at a fixed x-position
            if (isSelected) {
                g2.drawString(">", 90, y);  // Always draw cursor at x = 80
            }

            // Draw dropdown indicator and text separately, starting from x = 100
            int x = 100;
            g2.drawString(dropdownIndicator, x, y);
            x += g2.getFontMetrics().stringWidth(dropdownIndicator);
            g2.drawString(optionText, x, y);

            y += 20;
        }

        g2.drawString("Press ENTER to save settings", 100, y + 20);
        g2.drawString("Press ESC to return", 100, y + 40);
    }



    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        List<OptionEntry> visible = getVisibleOptions();
        int size = visible.size();

        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
            selectedOption = (selectedOption - 1 + size) % size;
        } else if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
            selectedOption = (selectedOption + 1) % size;
        } else if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
            handleEnter();
        } else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            handleLeftRight(-1);
        } else if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            handleLeftRight(1);
        } else if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_BACK_SPACE) {
            gp.resetAll();
            gp.setGameState(GameState.MENU);
            gp.setCurrentScreen(gp.getCurrentScreen());
        }
    }

    private void handleEnter() {
        OptionEntry entry = getVisibleOptions().get(selectedOption);

        if (entry.type().equals("main")) {
            int index = entry.index();
            if (index == 2) adjustingProportions = !adjustingProportions;
            else if (index == 3) adjustingPulseSettings = !adjustingPulseSettings;
        }
    }

    
    private void handleLeftRight(int dir) {
        OptionEntry entry = getVisibleOptions().get(selectedOption);

        if (entry.type().equals("main")) {
            switch (entry.index()) {
                case 0 -> changeDifficulty(dir);
                case 1 -> setNumLevels(dir);
                case 2 -> setEnemyCount(dir);
                case 3 -> setEchoAmount(dir);
            }
        } else if (entry.type().equals("proportion")) {
            String type = gp.getEntityManager().getEnemyTypes().get(entry.index());
            gp.getEntityManager().adjustEnemyAmounts(type, dir);
        } else if (entry.type().equals("pulse")) {
            switch (entry.index()) {
            case 0 -> gp.getEchoPulse().adjustMaxPulse(dir);
            case 1 -> gp.getEchoPulse().adjustFadeDelay(dir);
            case 2 -> gp.getEchoPulse().adjustPulseDelay(dir);
            case 3 -> gp.getEchoPulse().adjustRadius(dir);
            case 4 -> gp.getEchoPulse().adjustVisibilityDuration(dir);
            }
        }
    }
    
    private void changeDifficulty(int dir) {
    	int ordinal = gp.getDiff().ordinal() + dir;
        if (ordinal < 0) ordinal = Difficulty.values().length - 1;
        if (ordinal >= Difficulty.values().length) ordinal = 0;
        gp.setDiff(Difficulty.values()[ordinal]);
        
        setDifficultyDefaults();
    }
    
    
    
    private void setNumLevels(int dir) {
    	gp.setDiff(Difficulty.CUSTOM);
    	
    	int newNum = gp.getLevelManager().getNumLevels() + dir;
        if (newNum < 1) {
            newNum = 1;
        }
        gp.getLevelManager().setNumLevels(newNum);
//        gp.getLevelManager().resetAll();
    }
    
    private void setEnemyCount(int dir) {
    	gp.setDiff(Difficulty.CUSTOM);
    	gp.getEntityManager().setNumEnemies(gp.getEntityManager().getNumEnemies() + dir);
        if (gp.getEntityManager().getNumEnemies() < 0) {
        	gp.getEntityManager().setNumEnemies(0);
        }
//        gp.getEntityManager().setNumBasics(gp.getEntityManager().getNumEnemies());;
    }
    
//    private void adjustEnemyProportion(float delta) {
//        String[] types = gp.getEntityManager().getEnemyTypes().toArray(new String[0]);
//        String selectedType = types[selectedEnemyTypeIndex];
//
//        float current = gp.getEntityManager().getEnemyTypeProportion(selectedType);
//        float newVal = Math.max(0f, current + delta);
//        gp.getEntityManager().setEnemyTypeProportion(selectedType, newVal);
//    }
    
    private void setEchoAmount(int dir) {
    	gp.setDiff(Difficulty.CUSTOM);
    	gp.getEchoPulse().setMaxPulse(gp.getEchoPulse().getMaxPulse() + dir);
    	if (gp.getEchoPulse().getMaxPulse() < 0) {
    		gp.getEchoPulse().setMaxPulse(0);
    	}
    	
    }
    @Override
    public void update() {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
    }
    
    private int getTotalOptions() {
        int base = options.length;
        return base + (adjustingProportions ? gp.getEntityManager().getEnemyTypes().size() : 0);
    }
    
    private List<OptionEntry> getVisibleOptions() {
        List<OptionEntry> entries = new ArrayList<>();

        for (int i = 0; i < options.length; i++) {
            entries.add(new OptionEntry("main", i));

            if (i == 2 && adjustingProportions) {
                for (int j = 0; j < gp.getEntityManager().getEnemyTypes().size(); j++) {
                    entries.add(new OptionEntry("proportion", j));
                }
            }

            if (i == 3 && adjustingPulseSettings) {
                for (int j = 0; j < 5; j++) {
                    entries.add(new OptionEntry("pulse", j));
                }
            }
        }

        return entries;
    }
    
    private void setDifficultyDefaults() {
    	switch (gp.getDiff()) {
        case BABY:
        	gp.getLevelManager().setNumLevels(1);
        	gp.getEntityManager().setNumEnemies(1);
        	gp.getEntityManager().setEnemyProportions(1, 0, 0);
        	gp.getEchoPulse().setPulseDefaults(20, 5, 6, 20, 90);
        	break;
        case EASY:
        	gp.getLevelManager().setNumLevels(3);
        	gp.getEntityManager().setNumEnemies(3);
        	gp.getEntityManager().setEnemyProportions(3, 0, 0);
        	gp.getEchoPulse().setPulseDefaults(15, 5, 6, 15, 90);
        	break;
        case MEDIUM:
        	gp.getLevelManager().setNumLevels(4);
        	gp.getEntityManager().setNumEnemies(4);
        	gp.getEntityManager().setEnemyProportions(3, 1, 0);
//        	gp.getEchoPulse().setPulseDefaults(10, 5, 6, 10, 75);
        	gp.getEchoPulse().setPulseDefaults(10, 0, 0, 50, 300);
//        	gp.getEntityManager().setEnemyProportions(0, 0, 4);

        	break;
        case HARD:
        	gp.getLevelManager().setNumLevels(5);
        	gp.getEntityManager().setNumEnemies(6);
        	gp.getEntityManager().setEnemyProportions(2, 3, 1);
        	gp.getEchoPulse().setPulseDefaults(5, 5, 5, 9, 60);
        	break;
        case EXTREME:
        	gp.getLevelManager().setNumLevels(9);
        	gp.getEntityManager().setNumEnemies(10);
        	gp.getEntityManager().setEnemyProportions(1, 7, 2);
        	gp.getEchoPulse().setPulseDefaults(2, 5, 3, 7, 30);

        	break;
        case CUSTOM:
        	break;
        }
    }

    
    private record OptionEntry(String type, int index) {}
    

}