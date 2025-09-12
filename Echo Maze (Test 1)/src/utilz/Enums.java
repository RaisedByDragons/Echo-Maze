package utilz;

public class Enums {
	public enum Difficulty {
	    BABY, EASY, MEDIUM, HARD, EXTREME, CUSTOM
	}
	
	public enum Direction {
	    UP, RIGHT, DOWN, LEFT;
	}
	
	public enum GameState {
		MENU, PLAYING, PAUSED, GAME_OVER, WINNER, SETTINGS;
	}
	
	public enum TileType {
		WALL(0), PATH(1), END(2), START(3);
		private final int value;
		
		private TileType(int value) {
			this.value = value;
		}
		public int getValue() {
	        return value;
	    }
	}
}