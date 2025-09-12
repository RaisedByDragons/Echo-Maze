package utilz;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import gameStart.GamePanel;
import utilz.Enums.Direction;

public class PathFinding {
	
	public List<Direction> pathDirections;
	
	public PathFinding(GamePanel gp, int startX, int startY) {
		int[][] map = gp.getLevelManager().getCurrentLevel().getGrid();

        Point start = new Point(startX, startY, null, null);
        Point end = new Point(gp.getEntityManager().getPlayer().getCol(), gp.getEntityManager().getPlayer().getRow(), null, null);
        pathDirections = FindPath(map, start, end);
        
        if (pathDirections == null) {
        	System.out.println("No path found");
        }
	}

	public static class Point {
        public int x, y;
        public Point previous;
        public Direction previousDirection;

        public Point(int x, int y, Point previous, Direction previousDirection) {
            this.x = x;
            this.y = y;
            this.previous = previous;
            this.previousDirection = previousDirection;
        }

        private Point offset(int dx, int dy, Direction dir) {
            return new Point(x + dx, y + dy, this, dir);
        }
        
//        public String toString() { return String.format("(%d, %d)", x, y, direction); }

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

    public static boolean IsWalkable(int[][] map, Point point) {
    	return point.y >= 0 && point.y < map.length &&
                point.x >= 0 && point.x < map[0].length &&
                map[point.y][point.x] != Enums.TileType.WALL.getValue();
    }

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

    public static List<Direction> FindPath(int[][] map, Point start, Point end) {
    	List<Point> open = new ArrayList<>();
        open.add(start);

        while (!open.isEmpty()) {
            List<Point> newOpen = new ArrayList<>();

            for (Point current : open) {
                for (Point neighbor : FindNeighbors(map, current)) {
                    if (!containsPoint(open, neighbor) && !containsPoint(newOpen, neighbor)) {
                        if (neighbor.equals(end)) {
                            return buildDirectionList(neighbor);
                        }
                        newOpen.add(neighbor);
                    }
                }
            }

            open = newOpen;
        }

        return null; // No path
//        boolean finished = false;
//        List<Point> used = new ArrayList<>();
//        used.add(start);
//        while (!finished) {
//            List<Point> newOpen = new ArrayList<>();
//            for(int i = 0; i < used.size(); ++i){
//                Point point = used.get(i);
//                for (Point neighbor : FindNeighbors(map, point)) {
//                    if (!used.contains(neighbor) && !newOpen.contains(neighbor)) {
//                        newOpen.add(neighbor);
//                    }
//                }
//            }
//
//            for(Point point : newOpen) {
//                used.add(point);
//                if (end.equals(point)) {
//                    finished = true;
//                    break;
//                }
//            }
//
//            if (!finished && newOpen.isEmpty())
//                return null;
//        }
//
//        List<Point> path = new ArrayList<>();
//        Point point = used.get(used.size() - 1);
//        while(point.previous != null) {
//            path.add(0, point);
//            point = point.previous;
//        }
//        return path;
    }

    private static boolean containsPoint(List<Point> list, Point p) {
        for (Point point : list) {
            if (point.x == p.x && point.y == p.y)
                return true;
        }
        return false;
    }

    private static List<Direction> buildDirectionList(Point endPoint) {
        List<Direction> dirList = new ArrayList<>();
        Point current = endPoint;

        while (current.previous != null) {
            dirList.add(0, current.previousDirection); // Insert at start
            current = current.previous;
        }

        return dirList;
    }
}
