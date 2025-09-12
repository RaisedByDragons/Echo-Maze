package utilz;

import utilz.Enums.Direction;

public class HelpMethods {

	
	public static Direction turnRight(Direction dir) {
	    return switch (dir) {
	        case UP -> Direction.RIGHT;
	        case RIGHT -> Direction.DOWN;
	        case DOWN -> Direction.LEFT;
	        case LEFT -> Direction.UP;
	    };
	}

	public static Direction turnLeft(Direction dir) {
	    return switch (dir) {
	        case UP -> Direction.LEFT;
	        case LEFT -> Direction.DOWN;
	        case DOWN -> Direction.RIGHT;
	        case RIGHT -> Direction.UP;
	    };
	}

	public static Direction turnBack(Direction dir) {
	    return switch (dir) {
	        case UP -> Direction.DOWN;
	        case DOWN -> Direction.UP;
	        case LEFT -> Direction.RIGHT;
	        case RIGHT -> Direction.LEFT;
	    };
	}

	public static int dX(Direction dir) {
	    return switch (dir) {
	        case LEFT -> -1;
	        case RIGHT -> 1;
	        default -> 0;
	    };
	}

	public static int dY(Direction dir) {
	    return switch (dir) {
	        case UP -> -1;
	        case DOWN -> 1;
	        default -> 0;
	    };
	}

}
