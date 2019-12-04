package LepinskiEngine;
import java.util.List;
import java.util.ArrayList;

public class ScanMethodVisionBot implements ScanMethod{

    public void doScan(MazeRobot bot, Maze the_maze, Maze dark_maze){
	List<MazeLocation> coin_locs = new ArrayList<MazeLocation>();
	int max_x = the_maze.getMaxX();
	int max_y = the_maze.getMaxY();

	for(int i=0; i<max_x; i++){
	    for(int j=0; j<max_y; j++){
		MazeLocation loc = the_maze.getLocation(i,j);
		if ((loc.getCoins() != null) && (loc.getCoins().size() > 0)){
		    coin_locs.add(loc);
		}
	    }
	}

	for(MazeLocation l : coin_locs){
	    addToDark(dark_maze, l);
	}
    }

    public void addToDark(Maze dark, MazeLocation loc){
	int x = loc.getX();
	int y = loc.getY();
	MazeLocation dark_loc = new MazeLocation(x,y);

	dark_loc.the_coins = loc.getCoins();
	dark_loc.the_robots = loc.getRobots();
	dark_loc.the_directions = loc.getDirections();
	dark_loc.the_obstacles = loc.getObstacles();
	dark_loc.is_start = loc.is_start;
	dark_loc.is_scanned = true;
	dark.setLocation(x,y,dark_loc);
	    
    }

}
