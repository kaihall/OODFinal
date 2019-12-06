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
    private HashMap<Node,HashMap<Node,List<DirType>>> paths;	// we use findPath() a lot so this is an effort to reduce complexity.
    
    private HashSet<Node> coins;
    private HashSet<Node> diamonds;
    private HashMap<Integer,Node> robots;
    
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
        
        paths = new HashMap<Node,HashMap<Node,List<DirType>>>();
        
        coins = new HashSet<Node>();
        diamonds = new HashSet<Node>();
        robots = new HashMap<Integer,Node>();
    }
    
    /*
     * Updates the map to include all of the Locations the Robots can see.
     */
    public void update(List<Location> info) {
        for (Location loc : info) {
            int x = loc.getX();
            int y = loc.getY();
            
            if (labels[x][y] == UNSCANNED) {
                labels[x][y] = SCANNED;
                addLocation(loc);
            }
            
            if (deadEnd(loc))
            	labels[x][y] = DEAD_END;
            
            if (loc.getCoins() != null && !loc.getCoins().isEmpty()) {
                coins.add(toNode(loc));
                if (loc.getCoins().contains(CoinType.Diamond))
                	diamonds.add(toNode(loc));
            }
            
            if (loc.getRobots() != null) {
                for (Robot bot : loc.getRobots())
                    robots.put(bot.getID(),toNode(loc));
                if (labels[x][y] < VISITED) labels[x][y] = VISITED;
            }
               
        }
    }
    
    /*
     * Gets the stored path or attempts to find one if there is none.
     */
    public List<DirType> getPath(Location here, Location there) {
    	Node h = toNode(here);
    	Node t = toNode(there);
    	
    	if (paths.get(h).containsKey(t)) {
    		return paths.get(h).get(t);
    	} else if (paths.get(t).containsKey(h)) {
    		List<DirType> tmp = reversePath(paths.get(t).get(h));
    		paths.get(h).put(t,tmp);
    		return tmp;
    	} else {
    		return findPath(h, t);
    	}
    }
    
    /*
     * Uses BFS to find, store, and return the shortest path between two Locations.
     */
    private List<DirType> findPath(Node here, Node there) {
        List<DirType> path = new ArrayList<DirType>();
        
        Node start = here;
        
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
        return robots.get(bot.getID()).loc;
    }
    
    public boolean onCoin(Robot bot) {
        return coins.contains(robots.get(bot.getID()));
    }
    
    public boolean onDiamond(Robot bot) {
    	return diamonds.contains(robots.get(bot.getID()));
    }
    
    public void removeCoin(Location loc) {
    	coins.remove(toNode(loc));
    	diamonds.remove(toNode(loc));
    }
    
    public void removeCoin(Robot bot) {
    	coins.remove(robots.get(bot.getID()));
    	diamonds.remove(robots.get(bot.getID()));
    }
    
    public List<List<DirType>> coinPaths(Location here) {
        List<List<DirType>> paths = new ArrayList<List<DirType>>();
        
        for (Node u : coins)
            paths.add(findPath(toNode(here),u));
        
        return paths;
    }
    
    public List<Location> getCoinLocations() {
    	ArrayList<Location> cL = new ArrayList<Location>();
    	
    	for (Node u : coins)
    		cL.add(u.loc);
    	
    	return cL;
    }
    
    public List<Location> getDiamondLocations() {
    	ArrayList<Location> dL = new ArrayList<Location>();
    	
    	for (Node u : diamonds)
    		dL.add(u.loc);
    	
    	return dL;
    }
    
    public int getMaxX() {
        return max_x;
    }
    
    public int getMaxY() {
        return max_y;
    }
    
    public Location get(int x, int y) {
    	return map.get(x).get(y).loc;
    }
    
    /*
     * Turns a Location into a Node and adds it to the Map.
     */
    private void addLocation(Location loc) {
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
    	
    	// Create a new HashMap to store paths to/from the Node
    	paths.put(u, new HashMap<Node,List<DirType>>());
    }
    
    private Node toNode(Location loc) {
    	Node u = map.get(loc.getX()).get(loc.getY());
    	return u;
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
