package utilz;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import gameStart.GamePanel;
import utilz.Enums.Direction;

// Class for computing a path from an enemy or entity to the player using BFS-like search
public class PathFinding {
	
	// Stores the directions to follow from start to player
	public List<Direction> pathDirections;
	
	// Constructor calculates the path immediately when created
	public PathFinding(GamePanel gp, int startX, int startY) {
		// Get the grid of the current level
		int[][] map = gp.getLevelManager().getCurrentLevel().getGrid();

        // Starting point for pathfinding
        Point start = new Point(startX, startY, null, null);
        // Target point is the player’s current position
        Point end = new Point(gp.getEntityManager().getPlayer().getCol(), gp.getEntityManager().getPlayer().getRow(), null, null);
        
        // Compute path as a list of directions
        pathDirections = FindPath(map, start, end);
        
        if (pathDirections == null) {
        	System.out.println("No path found"); // Path could not be found
        }
	}

	// Inner class representing a single tile point in the grid
	public static class Point {
        public int x, y; // Coordinates
        public Point previous; // Previous point in the path
        public Direction previousDirection; // Direction taken to reach this point

        public Point(int x, int y, Point previous, Direction previousDirection) {
            this.x = x;
            this.y = y;
            this.previous = previous;
            this.previousDirection = previousDirection;
        }

        // Returns a new Point offset by dx, dy, with this point as previous
        private Point offset(int dx, int dy, Direction dir) {
            return new Point(x + dx, y + dy, this, dir);
        }
        
        @Override
	    public boolean equals(Object o) {
	        if (!(o instanceof Point)) return false;
	        Point point = (Point) o;
	        return x == point.x && y == point.y;
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hash(x, y);
	    }
    }

    // Checks if a point is within bounds and not a wall
    public static boolean IsWalkable(int[][] map, Point point) {
    	return point.y >= 0 && point.y < map.length &&
                point.x >= 0 && point.x < map[0].length &&
                map[point.y][point.x] != Enums.TileType.WALL.getValue();
    }

    // Returns a list of all valid neighboring points (up, down, left, right)
    public static List<Point> FindNeighbors(int[][] map, Point point) {
    	List<Point> neighbors = new ArrayList<>();
        if (IsWalkable(map, point.offset(0, -1, Direction.UP)))    
        	neighbors.add(point.offset(0, -1, Direction.UP));
        if (IsWalkable(map, point.offset(0, 1, Direction.DOWN)))   
        	neighbors.add(point.offset(0, 1, Direction.DOWN));
        if (IsWalkable(map, point.offset(-1, 0, Direction.LEFT)))  
        	neighbors.add(point.offset(-1, 0, Direction.LEFT));
        if (IsWalkable(map, point.offset(1, 0, Direction.RIGHT)))  
        	neighbors.add(point.offset(1, 0, Direction.RIGHT));
        return neighbors;
    }

    // Main pathfinding function returning a list of directions from start to end
    public static List<Direction> FindPath(int[][] map, Point start, Point end) {
    	List<Point> open = new ArrayList<>(); // Tiles to check
        open.add(start);

        // BFS-like search loop
        while (!open.isEmpty()) {
            List<Point> newOpen = new ArrayList<>();

            for (Point current : open) {
                for (Point neighbor : FindNeighbors(map, current)) {
                    // Skip if already in open or newOpen
                    if (!containsPoint(open, neighbor) && !containsPoint(newOpen, neighbor)) {
                        if (neighbor.equals(end)) {
                            return buildDirectionList(neighbor); // Path found
                        }
                        newOpen.add(neighbor);
                    }
                }
            }

            open = newOpen; // Continue with next layer of points
        }

        return null; // No path found
    }

    // Helper: check if a point exists in a list
    private static boolean containsPoint(List<Point> list, Point p) {
        for (Point point : list) {
            if (point.x == p.x && point.y == p.y)
                return true;
        }
        return false;
    }

    // Reconstructs the list of directions from the endpoint back to start
    private static List<Direction> buildDirectionList(Point endPoint) {
        List<Direction> dirList = new ArrayList<>();
        Point current = endPoint;

        while (current.previous != null) {
            dirList.add(0, current.previousDirection); // Insert at start to reverse order
            current = current.previous;
        }

        return dirList;
    }
}
