package LepinskiEngine;
import java.util.List;

public class CheckMoveGhost implements CheckMove{

    public boolean do_check(MazeRobot robot, CommandMove cm, Maze the_maze){
	MazeLocation loc = robot.getCurrentLoc();
	int x = loc.getX();
	int y = loc.getY();
	int max_x = the_maze.getMaxX();
	int max_y = the_maze.getMaxY();
	DirType dir = cm.getDir();

	if ((x == 0) && (dir == DirType.West)){
	    return false;
	}
	if ((x + 1 == max_x) && (dir == DirType.East)){
	    return false;
	}
	if ((y == 0) && (dir == DirType.North)){
	    return false;
	}
	if ((y + 1 == max_y) && (dir == DirType.South)){
	    return false;
	}
	return true;
	
    }

}
