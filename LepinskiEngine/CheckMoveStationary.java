package LepinskiEngine;
import java.util.List;

public class CheckMoveStationary implements CheckMove{

    public boolean do_check(MazeRobot robot, CommandMove cm, Maze the_maze){
	return false;
    }

}
