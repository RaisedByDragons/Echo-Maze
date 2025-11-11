package utilz;

import utilz.Enums.Direction;

// Helper class containing methods for direction-related calculations
public class HelpMethods {

    // Returns the direction when turning 90 degrees to the right
	public static Direction turnRight(Direction dir) {
	    return switch (dir) {
	        case UP -> Direction.RIGHT;
	        case RIGHT -> Direction.DOWN;
	        case DOWN -> Direction.LEFT;
	        case LEFT -> Direction.UP;
	    };
	}

    // Returns the direction when turning 90 degrees to the left
	public static Direction turnLeft(Direction dir) {
	    return switch (dir) {
	        case UP -> Direction.LEFT;
	        case LEFT -> Direction.DOWN;
	        case DOWN -> Direction.RIGHT;
	        case RIGHT -> Direction.UP;
	    };
	}

    // Returns the opposite direction (180-degree turn)
	public static Direction turnBack(Direction dir) {
	    return switch (dir) {
	        case UP -> Direction.DOWN;
	        case DOWN -> Direction.UP;
	        case LEFT -> Direction.RIGHT;
	        case RIGHT -> Direction.LEFT;
	    };
	}

    // Returns the change in X-coordinate for a given direction
    // LEFT decreases X by 1, RIGHT increases X by 1, UP/DOWN does not change X
	public static int dX(Direction dir) {
	    return switch (dir) {
	        case LEFT -> -1;
	        case RIGHT -> 1;
	        default -> 0;
	    };
	}

    // Returns the change in Y-coordinate for a given direction
    // UP decreases Y by 1, DOWN increases Y by 1, LEFT/RIGHT does not change Y
	public static int dY(Direction dir) {
	    return switch (dir) {
	        case UP -> -1;
	        case DOWN -> 1;
	        default -> 0;
	    };
	}

}
