package screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import gameStart.GamePanel;
import utilz.Enums.Difficulty;
import utilz.Enums.GameState;

// Screen for changing game settings like difficulty, enemy count, and echo pulse parameters
public class Settings extends Screen {

    private GamePanel gp; // Reference to main game panel
    private String[] options = {"Difficulty", "Number of Levels", "Enemy Count", "Num Echo Pulse Per Level", "Tutorial Enabled"};
    private int selectedOption = 0; // Currently selected option in menu
    private boolean adjustingProportions = false; // Whether we are editing enemy proportions
    private boolean adjustingPulseSettings = false; // Whether we are editing echo pulse settings

    // Constructor: sets default difficulty, spawns entities, resets game state
    public Settings(GamePanel gp) {
        this.gp = gp;
        setDifficultyDefaults(); // Apply default values for chosen difficulty
        gp.getEntityManager().summonEntities(); // Initialize enemies
        gp.resetAll(); // Reset visibility, pulses, etc.
    }
    
    // Draws the settings screen
    public void draw(Graphics2D g2) {
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, 0, gp.SCREEN_WIDTH, gp.SCREEN_HEIGHT); // Background color
        g2.setColor(Color.WHITE);
        g2.drawString("Settings", 100, 60); // Title

        // Get currently visible options, including sub-options if editing proportions or pulses
        List<OptionEntry> visible = getVisibleOptions();
        int y = 100;

        for (int i = 0; i < visible.size(); i++) {
            OptionEntry entry = visible.get(i);
            boolean isSelected = i == selectedOption; // Highlight selected option

            String dropdownIndicator = "  "; // Arrow symbol for expandable options
            String optionText = "";

            // Build the option text based on the type of entry
            if (entry.type().equals("main")) {
                int optionIndex = entry.index();
                optionText = options[optionIndex];

                // Display current value for main options
                switch (optionIndex) {
                    case 0 -> optionText += ": " + gp.getDiff().name(); // Difficulty
                    case 1 -> optionText += ": " + gp.getLevelManager().getNumLevels(); // Number of levels
                    case 2 -> { // Enemy count with dropdown arrow
                        dropdownIndicator = adjustingProportions ? "▾ " : "▸ ";
                        optionText += ": " + gp.getEntityManager().getNumEnemies();
                    }
                    case 3 -> { // Echo pulse settings with dropdown arrow
                        dropdownIndicator = adjustingProportions ? "▾ " : "▸ ";
                        optionText += ": " + gp.getEchoPulse().getMaxPulse(); 
                    }
                    case 4 -> { // Tutorial enabled
                        optionText += ": " + gp.getLevelManager().isTutorial();
                    }
                }
            } else if (entry.type().equals("proportion")) { // Enemy proportion sub-options
                String type = gp.getEntityManager().getEnemyTypes().get(entry.index());
                int num = gp.getEntityManager().getEnemyAmounts(type);
                optionText = "  " + type + ": " + num;
            } else if (entry.type().equals("pulse")) { // Pulse sub-options
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

            // Draw cursor for currently selected option
            if (isSelected) {
                g2.drawString(">", 90, y);  // Cursor symbol
            }

            // Draw the option text and any dropdown arrow
            int x = 100;
            g2.drawString(dropdownIndicator, x, y);
            x += g2.getFontMetrics().stringWidth(dropdownIndicator);
            g2.drawString(optionText, x, y);

            y += 20; // Move down for next option
        }

        // Instructions at bottom
        g2.drawString("Press ENTER to save settings", 100, y + 20);
        g2.drawString("Press ESC to return", 100, y + 40);
    }

    // Handles key release events for menu navigation and adjustments
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        List<OptionEntry> visible = getVisibleOptions();
        int size = visible.size();

        // Navigate options
        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
            selectedOption = (selectedOption - 1 + size) % size;
        } else if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
            selectedOption = (selectedOption + 1) % size;
        } else if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
            handleEnter(); // Open dropdowns for sub-options
        } else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            handleLeftRight(-1); // Decrease values
        } else if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            handleLeftRight(1); // Increase values
        } else if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_BACK_SPACE) {
            gp.resetAll(); // Reset level state and return to menu
            gp.setGameState(GameState.MENU);
            gp.setCurrentScreen(gp.getCurrentScreen());
        }
    }

    // Handles ENTER key to toggle dropdowns
    private void handleEnter() {
        OptionEntry entry = getVisibleOptions().get(selectedOption);
        if (entry.type().equals("main")) {
            int index = entry.index();
            if (index == 2)
                adjustingProportions = !adjustingProportions;
            else if (index == 3)
                adjustingPulseSettings = !adjustingPulseSettings;
        }
    }

    // Adjust values based on LEFT/RIGHT input or analog
    private void handleLeftRight(int dir) {
        OptionEntry entry = getVisibleOptions().get(selectedOption);

        if (entry.type().equals("main")) {
            switch (entry.index()) {
                case 0 -> changeDifficulty(dir); // Cycle difficulty
                case 1 -> setNumLevels(dir);     // Change number of levels
                case 2 -> setEnemyCount(dir);    // Change enemy count
                case 3 -> setEchoAmount(dir);    // Change echo pulse count
                case 4 -> setTutorialEnabled();  // Toggle tutorial
            }
        } else if (entry.type().equals("proportion")) { // Adjust individual enemy type counts
            String type = gp.getEntityManager().getEnemyTypes().get(entry.index());
            gp.getEntityManager().adjustEnemyAmounts(type, dir);
        } else if (entry.type().equals("pulse")) { // Adjust echo pulse parameters
            switch (entry.index()) {
            case 0 -> gp.getEchoPulse().adjustMaxPulse(dir);
            case 1 -> gp.getEchoPulse().adjustFadeDelay(dir);
            case 2 -> gp.getEchoPulse().adjustPulseDelay(dir);
            case 3 -> gp.getEchoPulse().adjustRadius(dir);
            case 4 -> gp.getEchoPulse().adjustVisibilityDuration(dir);
            }
        }
    }

    // Changes difficulty and resets defaults for the selected difficulty
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
        if (newNum < 1) newNum = 1;
        gp.getLevelManager().setNumLevels(newNum);
    }

    private void setEnemyCount(int dir) {
        gp.setDiff(Difficulty.CUSTOM);
        gp.getEntityManager().setNumEnemies(gp.getEntityManager().getNumEnemies() + dir);
        if (gp.getEntityManager().getNumEnemies() < 0)
            gp.getEntityManager().setNumEnemies(0);
    }

    private void setEchoAmount(int dir) {
        gp.setDiff(Difficulty.CUSTOM);
        gp.getEchoPulse().setMaxPulse(gp.getEchoPulse().getMaxPulse() + dir);
        if (gp.getEchoPulse().getMaxPulse() < 0)
            gp.getEchoPulse().setMaxPulse(0);
    }

    private void setTutorialEnabled() {
        gp.getLevelManager().setIsTutorial(!gp.getLevelManager().isTutorial());
    }

    // Unused update method for the screen
    @Override
    public void update() {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    // Builds a list of visible options for the menu, including sub-options
    private List<OptionEntry> getVisibleOptions() {
        List<OptionEntry> entries = new ArrayList<>();

        for (int i = 0; i < options.length; i++) {
            entries.add(new OptionEntry("main", i));

            // If enemy proportions dropdown is open, add sub-options
            if (i == 2 && adjustingProportions) {
                for (int j = 0; j < gp.getEntityManager().getEnemyTypes().size(); j++) {
                    entries.add(new OptionEntry("proportion", j));
                }
            }

            // If pulse settings dropdown is open, add sub-options
            if (i == 3 && adjustingPulseSettings) {
                for (int j = 0; j < 5; j++) {
                    entries.add(new OptionEntry("pulse", j));
                }
            }
        }

        return entries;
    }

    // Sets default values for each difficulty level
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
            gp.getEchoPulse().setPulseDefaults(10, 5, 6, 10, 75);
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

    // Helper record to represent menu entries (type and index)
    private record OptionEntry(String type, int index) {}

}
