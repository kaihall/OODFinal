package Navigation;

import LepinskiEngine.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Map {
    //These are integers instead of enums so that strategies can use math to figure out where to go next
    public static final int DNE = -1;
    public static final int UNSCANNED = 0;
    public static final int SCANNED = 1;
    public static final int VISITED = 2;
    public static final int DEAD_END = 3;
    
    private int max_x, max_y;
    private int[][] labels;
    //private 
    private ArrayList<ArrayList<Location>> map;
    private HashSet<Location> coins;
    private HashSet<Location> diamonds;
    private HashMap<Integer,Location> robots;
    
    public Map(int maze_x, int maze_y, List<Robot> bots) {
        max_x = maze_x - 1;
        max_y = maze_y - 1;
        
        labels = new int[maze_x][maze_y];
        
        map = new ArrayList<ArrayList<Location>>();
        for (int i = 0; i < maze_x; i++) {
            map.add(new ArrayList<Location>());
            for (int j = 0; j < maze_y; j++) {
                map.get(i).add(null);
                labels[i][j] = UNSCANNED;
            }
        }
        
        coins = new HashSet<Location>();
        robots = new HashMap<Integer,Location>();
    }

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
            }
                
            map.get(x).set(y,loc);
            
            if (labels[x][y] == 0)
                labels[x][y] = 1;
        }
    }
    
    public List<Location> findPath(Location here, Location there) {
        List<Location> path = new ArrayList<Location>();
        
        path.add(here);
        
        return path;
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
        //System.out.println(bot + " : " + robots.get(bot));
        return getBotLocation(bot).getCoins() != null && !getBotLocation(bot).getCoins().isEmpty();
    }
    
    public List<List<Location>> coinPaths(Location here) {
        List<List<Location>> paths = new ArrayList<List<Location>>();
        
        for (Location loc : coins)
            paths.add(findPath(here,loc));
        
        return paths;
    }
    
    public int getMaxX() {
        return max_x;
    }
    
    public int getMaxY() {
        return max_y;
    }
}
