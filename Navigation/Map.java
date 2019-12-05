package Navigation;

import LepinskiEngine.*;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Map {
    //These are integers instead of enums so that strategies can use math to figure out where to go next.
	//		Numbers represent label priorities (e.x., don't care if a spot has been visited if it's a dead end)
    public static final int DNE = -1;
    public static final int UNSCANNED = 0;
    public static final int SCANNED = 1;
    public static final int VISITED = 2;
    public static final int DEAD_END = 3;
    
    private int max_x, max_y;
    private int[][] labels;
    private ArrayList<ArrayList<Node>> map;
    private HashMap<Location,Node> nodes;
    private HashMap<Location,HashMap<Location,List<DirType>>> paths;	// we use findPath() a lot so this is an effort to reduce complexity.
    
    private HashSet<Location> coins;
    private HashSet<Location> diamonds;
    private HashMap<Integer,Location> robots;
    
    private final Node nil = new Node();
    
    // Node class that contains a Location and all of the Locations it's connected to
    private class Node {
    	public Location loc;
    	public Node north;
    	public Node south;
    	public Node east;
    	public Node west;
    	public int x;
    	public int y;
    }
    
    public Map(int maze_x, int maze_y, List<Robot> bots) {
        max_x = maze_x - 1;
        max_y = maze_y - 1;
        
        labels = new int[maze_x][maze_y];
        
        map = new ArrayList<ArrayList<Node>>();
        for (int i = 0; i < maze_x; i++) {
            map.add(new ArrayList<Node>());
            for (int j = 0; j < maze_y; j++) {
                map.get(i).add(null);
                labels[i][j] = UNSCANNED;
            }
        }
        
        nodes = new HashMap<Location,Node>();
        paths = new HashMap<Location,HashMap<Location,List<DirType>>>();
        
        coins = new HashSet<Location>();
        diamonds = new HashSet<Location>();
        robots = new HashMap<Integer,Location>();
    }
    
    /*
     * Updates the map to include all of the Locations the Robots can see.
     */
    public void update(List<Location> info) {
        for (Location loc : info) {
            int x = loc.getX();
            int y = loc.getY();
            
            if (loc.getCoins() != null && !loc.getCoins().isEmpty()) {
                coins.add(loc);
                if (loc.getCoins().contains(CoinType.Diamond))
                	diamonds.add(loc);
            }
            
            if (loc.getRobots() != null) {
                for (Robot bot : loc.getRobots())
                    robots.put(bot.getID(),loc);
                if (labels[x][y] < VISITED) labels[x][y] = VISITED;
            }
                
            addLocation(loc);
            
            if (labels[x][y] == UNSCANNED)
                labels[x][y] = SCANNED;
            
            if (deadEnd(loc))
            	labels[x][y] = DEAD_END;
        }
    }
    
    /*
     * Gets the stored path or attempts to find one if there is none.
     */
    public List<DirType> getPath(Location here, Location there) {
    	if (paths.get(here).containsKey(there)) {
    		return paths.get(here).get(there);
    	} else if (paths.get(there).containsKey(here)) {
    		List<DirType> tmp = reversePath(paths.get(there).get(here));
    		paths.get(here).put(there,tmp);
    		return tmp;
    	} else {
    		return findPath(here, there);
    	}
    }
    
    /*
     * Uses BFS to find, store, and return the shortest path between two Locations.
     */
    private List<DirType> findPath(Location here, Location there) {
        List<DirType> path = new ArrayList<DirType>();
        
        Node start = nodes.get(here);
        
        // TODO: stubbed
        path.add(DirType.South);
        
        return path;
    }
    
    /*
     * Takes a path from point A to point B and turns it into a path from point B to point A.
     */
    private List<DirType> reversePath(List<DirType> oldPath) {
    	// Start a new path.
    	List<DirType> newPath = new ArrayList<DirType>();
    	
    	// Starting at the end of the path, work backwards and add the opposite direction to the new path at each step.
    	for (int i = oldPath.size()-1; i >= 0; i--) {
    		switch (oldPath.get(i)) {
    		case North:
    			newPath.add(DirType.South);
    			break;
    		case South:
    			newPath.add(DirType.North);
    			break;
    		case East:
    			newPath.add(DirType.West);
    			break;
    		case West:
    			newPath.add(DirType.East);
    			break;
    		}
    	}
    	
    	//Return the new path.
    	return newPath;
    }
    
    public int getLabel(Location loc) {
        if (loc == null) return DNE;
        return labels[loc.getX()][loc.getY()];
    }
    
    public int getLabel(int x, int y) {
        if (x > max_x || y > max_y) return DNE;
        return labels[x][y];
    }
    
    public void setLabel(Location loc, int label) {
        if (loc != null) 
            labels[loc.getX()][loc.getY()] = label;
    }
    
    public void setLabel(int x, int y, int label) {
        if (x <= max_x && y <= max_y) {
            labels[x][y] = label;
        }
    }
    
    public Location getBotLocation(Robot bot) {
        return robots.get(bot.getID());
    }
    
    public boolean onCoin(Robot bot) {
        return coins.contains(getBotLocation(bot));
    }
    
    public boolean onDiamond(Robot bot) {
    	return diamonds.contains(getBotLocation(bot));
    }
    
    public void removeCoin(Location loc) {
    	coins.remove(loc);
    	diamonds.remove(loc);
    }
    
    public List<List<DirType>> coinPaths(Location here) {
        List<List<DirType>> paths = new ArrayList<List<DirType>>();
        
        for (Location loc : coins)
            paths.add(findPath(here,loc));
        
        return paths;
    }
    
    public List<Location> getCoinLocations() {
    	return new ArrayList<Location>(coins);
    }
    
    public List<Location> getDiamondLocations() {
    	return new ArrayList<Location>(diamonds);
    }
    
    public int getMaxX() {
        return max_x;
    }
    
    public int getMaxY() {
        return max_y;
    }
    
    
    /*
     * Turns a Location into a Node and adds it to the Map.
     */
    private void addLocation(Location loc) {
    	if (!nodes.containsKey(loc)) {
    		Node u = new Node();
	    	u.loc = loc;
	    	u.x = loc.getX();
	    	u.y = loc.getY();
	    	
	    	List<DirType> dirxns = loc.getDirections();
	    	
	    	// Set the Node's north neighbor
	    	if (dirxns.contains(DirType.North) && u.y > 0)
	    		u.north = map.get(u.x).get(u.y-1);
	    	else
	    		u.north = nil;
	    	
	    	// Set the Node's south neighbor
	    	if (dirxns.contains(DirType.South) && u.y < max_y)
	    		u.south = map.get(u.x).get(u.y+1);
	    	else
	    		u.south = nil;
	    	
	    	// Set the Node's east neighbor
	    	if (dirxns.contains(DirType.East) && u.x < max_x)
	    		u.east = map.get(u.x+1).get(u.y);
	    	else
	    		u.east = nil;
	    	
	    	// Set the Node's west neighbor
	    	if (dirxns.contains(DirType.West) && u.x > 0)
	    		u.west = map.get(u.x-1).get(u.y);
	    	else
	    		u.east = nil;
	    	
	    	
	    	// Add the new Node to the map.
	    	map.get(u.x).set(u.y,u);
	    	
	    	// Store the Location and its Node so that the Location can be used to access the Node.
	    	nodes.put(loc, u);
	    	
	    	// Create a new HashMap to store paths to/from the location
	    	paths.put(loc, new HashMap<Location,List<DirType>>());
    	}
    }
    
    
    /*
     * Determines if a Location is a dead end.
     */
    private boolean deadEnd(Location curLoc) {
        List<DirType> dirxns = curLoc.getDirections();
        int xLoc = curLoc.getX();
        int yLoc = curLoc.getY();
        
        if (dirxns.size() == 1) return true;
        
        else {
            int deadEnds = 0;
            for (DirType check : dirxns) {
                if (check == DirType.North && yLoc > 0 && labels[xLoc][yLoc-1] == Map.DEAD_END)
                    deadEnds++;
                if (check == DirType.South && yLoc < max_y && labels[xLoc][yLoc+1] == Map.DEAD_END)
                    deadEnds++;
                if (check == DirType.East && xLoc < max_x && labels[xLoc+1][yLoc] == Map.DEAD_END)
                    deadEnds++;
                if (check == DirType.West && xLoc > 0 && labels[xLoc-1][yLoc] == Map.DEAD_END)
                    deadEnds++;
            }
            
            //if all directions are dead ends or all but one are, this location is also a dead end
            return deadEnds >= dirxns.size()-1;
        }
    }
}
