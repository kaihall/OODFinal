package Navigation;

import LepinskiEngine.*;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

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
    	
    	// Used for path finding
    	public boolean visited;
    	public Node parent;
    	public List<Node> adj;
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
                addLocation(loc, true);
            } else {
            	addLocation(loc, false);
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
    
    public int getLabel(Location loc) {
        if (loc == null) return DNE;
        return labels[loc.getX()][loc.getY()];
    }
    
    public int getLabel(int x, int y) {
        if (x > max_x || y > max_y || x < 0 || y < 0) return DNE;
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
     * Gets the stored path or attempts to find one if there is none.
     */
    public List<DirType> getPath(Location here, Location there) {
    	Node h = toNode(here);
    	Node t = toNode(there);
    	
    	/*
    	if (paths.get(h).containsKey(t)) {
    		return paths.get(h).get(t);
    	} else if (paths.get(t).containsKey(h)) {
    		List<DirType> tmp = reversePath(paths.get(t).get(h));
    		paths.get(h).put(t,tmp);
    		return tmp;
    	} else {
    		//return findPath(h, t, new ArrayList<Node>());
    		return findPath(h, t);
    	}
    	*/
    	
    	return findPath(h,t);
    }
    
    /*
     * Uses recursion to find, store, and return the shortest path between two Locations.
     */
    private List<DirType> findPathRecursion(Node here, Node there, List<Node> pathList) {
        pathList.add(here);
        
        if (!paths.containsKey(here))
        	paths.put(pathList.get(0), new HashMap<Node,List<DirType>>());	
        paths.get(pathList.get(0)).put(here,toDirPath(pathList));
        
        List<DirType> n = new ArrayList<DirType>();
        List<DirType> s = new ArrayList<DirType>();
        List<DirType> e = new ArrayList<DirType>();
        List<DirType> w = new ArrayList<DirType>();
        
        if (here.north != null) {
	        if (here.north == there) {
	        	pathList.add(here.north);
	        	return toDirPath(pathList);
	        } else {
	        	n = findPathRecursion(here.north,there,pathList);
	        }
        }
        
        if (here.south != null) {
	        if (here.south == there) {
	        	pathList.add(here.south);
	        	return toDirPath(pathList);
	        } else {
	        	s = findPathRecursion(here.south,there,pathList);
	        }
        }
        
        if (here.east != null) {
        	if (here.east == there) {
            	pathList.add(here.east);
            	return toDirPath(pathList);
            } else {
            	e = findPathRecursion(here.east,there,pathList);
            }
        }
        
        if (here.west != null) {
	        if (here.west == there) {
	        	pathList.add(here.west);
	        	return toDirPath(pathList);
	        } else {
	            w = findPathRecursion(here.west,there,pathList);
	        }
        }
        
        List<DirType> shortest = new ArrayList<DirType>();
        
        if (shortest.size() == 0 || (n.size() != 0 && n.size() < shortest.size()))
        	shortest = n;
        if (shortest.size() == 0 || (s.size() != 0 && s.size() < shortest.size()))
        	shortest = s;
        if (shortest.size() == 0 || (e.size() != 0 && e.size() < shortest.size()))
        	shortest = e;
        if (shortest.size() == 0 || (w.size() != 0 && w.size() < shortest.size()))
        	shortest = w;
        
        //Adds a type 1000 times to discourage going in random directions
        if (shortest.isEmpty()) {
        	DirType dir = null;
        	if (there.x > here.x && here.east != nil)
        		dir = DirType.East;
        	else if (there.x < here.x && here.west != nil)
        		dir = DirType.West;
        	else if (there.y > here.y && here.north != nil)
        		dir = DirType.North;
        	else if (there.y < here.y && here.south != nil)
        		dir = DirType.South;
        	
        	if (dir == null) {
        		List<DirType> dirxns = new ArrayList<DirType>();
        		Random rand = new Random();
        		
        		if (here.north != nil) dirxns.add(DirType.North);
        		if (here.south != nil) dirxns.add(DirType.South);
        		if (here.east != nil) dirxns.add(DirType.East);
        		if (here.west != nil) dirxns.add(DirType.West);
        		
        		if (dirxns.isEmpty()) {
        			dir = DirType.East;
        		} else {
	        		int i = rand.nextInt(dirxns.size());
	        		dir = dirxns.get(i);
        		}
        	}
        	
        	for (int i = 0; i < 1000; i++)
        		shortest.add(dir);
        }
        
        return shortest;
    }
    
    /*
     * Uses breadth-first search to find, store, and return the shortest path between two Locations.
     */
    private List<DirType> findPath(Node here, Node there) {
    	Node origin = here;
        Node dest = there;
        List<DirType> path = new ArrayList<DirType>();
        
        if(origin == null || dest == null)
          return null;
        
        removeVisited();
       
        Queue<Node> bfsQueue = new LinkedList<Node>();   
        bfsQueue.add(origin);
        
        origin.visited = true;
        
        while(!bfsQueue.isEmpty()){
          Node s = bfsQueue.poll();
          
          if(s == dest) {
            path = rollBack(here, s);
            paths.get(here).put(dest, path);
            break;
          }
          
          for(Node u : s.adj) {
            if(!u.visited){
              bfsQueue.add(u);
              u.visited = true;
              u.parent = s;
            }
          }
        }  
        
        if (path.isEmpty()) {
        	return crappyPath(here,there);
        }
        
        return path;
    }
    
    private void removeVisited() {
    	for (int i = 0; i < max_x; i++) {
    		for (int j = 0; j < max_y; j++) {
    			Node cur = map.get(i).get(j);
    			if (cur != null)
    				cur.visited = false;
    		}
    	}
    }
    
    private List<DirType> rollBack(Node start, Node end){
        List<Node> path = new ArrayList<Node>();
        
        while(end.parent != start){
          end = end.parent;
          path.add(0, end);
        }
        
        if(path.size() == 1)
          path.add(end); //special case for path to self.
        
        return toDirPath(path);
    }
    
    /*
     * Takes a path of Nodes and turns it into a path of DirTypes.
     */
    private List<DirType> toDirPath(List<Node> path) {
    	
    	List<DirType> dirPath = new ArrayList<DirType>();
    	
    	if (path.isEmpty()) return dirPath;
    	
    	for (int i = 0; i < path.size()-1; i++) {
    		Node cur = path.get(i);
    		Node next = path.get(i+1);
    		if (next == cur.north) {
    			dirPath.add(DirType.North);
    			continue;
    		} else if (next == cur.south) {
    			dirPath.add(DirType.South);
    			continue;
    		} else if (next == cur.east) {
    			dirPath.add(DirType.East);
    			continue;
    		} else {
    			dirPath.add(DirType.West);
    			continue;
    		}
    	}
    	
    	return dirPath;
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
    
    /*
     * Turns a Location into a Node and adds it to the Map.
     */
    private void addLocation(Location loc, boolean newLoc) {
		Node u;
		int x = loc.getX();
		int y = loc.getY();
		
		if (newLoc) {
			u = new Node();
			// Create a new HashMap to store paths to/from the Node
	    	paths.put(u, new HashMap<Node,List<DirType>>());
		} else {
			u = map.get(x).get(y);
		}
		
    	u.loc = loc;
    	u.x = x;
    	u.y = y;
    	u.adj = new ArrayList<Node>();
    	
    	List<DirType> dirxns = loc.getDirections();
    	
    	// Set the Node's north neighbor
    	Node n = (u.y > 0) ? map.get(u.x).get(u.y-1) : null;
    	if (dirxns.contains(DirType.North) && n != null) {
    		u.north = n;
    		n.south = u;
    		u.adj.add(n);
    	} else
    		u.north = nil;
    	
    	// Set the Node's south neighbor
    	Node s = (u.y < max_y) ? map.get(u.x).get(u.y+1) : null;
    	if (dirxns.contains(DirType.South) && s != null) {
    		u.south = s;
    		s.north = u;
    		u.adj.add(s);
    	} else
    		u.south = nil;
    	
    	// Set the Node's east neighbor
    	Node e = (u.x < max_x) ? map.get(u.x+1).get(u.y) : null;
    	if (dirxns.contains(DirType.East) && e != null) {
    		u.east = e;
    		e.west = u;
    		u.adj.add(e);
    	} else
    		u.east = nil;
    	
    	// Set the Node's west neighbor
    	Node w = (u.x > 0) ? map.get(u.x-1).get(u.y) : null;
    	if (dirxns.contains(DirType.West) && w != null) {
    		u.west = w;
    		w.east = u;
    		u.adj.add(w);
    	} else
    		u.west = nil;
    	
    	
    	// Add the new Node to the map.
    	map.get(u.x).set(u.y,u);
    }
    
    private Node toNode(Location loc) {
    	if (map.get(loc.getX()).get(loc.getY()) == null)
    		addLocation(loc, true);
    	
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

    private List<DirType> crappyPath(Node here, Node there) {
    	List<DirType> path = new ArrayList<DirType>();
    	
    	DirType dir = null;
    	if (there.x > here.x && here.east != nil)
    		dir = DirType.East;
    	else if (there.x < here.x && here.west != nil)
    		dir = DirType.West;
    	else if (there.y > here.y && here.north != nil)
    		dir = DirType.North;
    	else
    		dir = DirType.South;
    	/*
    	if (dir == null) {
    		List<DirType> dirxns = new ArrayList<DirType>();
    		Random rand = new Random();
    		
    		if (here.north != nil) dirxns.add(DirType.North);
    		if (here.south != nil) dirxns.add(DirType.South);
    		if (here.east != nil) dirxns.add(DirType.East);
    		if (here.west != nil) dirxns.add(DirType.West);
    		
    		if (dirxns.isEmpty()) {
    			dir = DirType.East;
    		} else {
        		int i = rand.nextInt(dirxns.size());
        		dir = dirxns.get(i);
    		}
    	}
    	*/
    	path.add(dir);
    	
    	return path;
    }
}
