package LepinskiEngine;

//CommandFastMove is only used by a FastBot
//It allows you to include two directions
//During a single turn, the FastBot will move in the first_direction
// ... and then in the same turn will also move in the second_direction

// For example, 
// CommandMove(bot, DirType.East, DirType.South)
//    ... will ask the GameEngine to first move the robot East and then South

public class CommandFastMove implements Command{
    private Robot the_robot;
    private Command first_cmd;
    private Command second_cmd;

    public Robot getRobot(){
	return the_robot;
    }

    public Command getCmd1(){
	return first_cmd;
    }

    public Command getCmd2(){
        return second_cmd;
    }

    public CommandFastMove(Robot rob, Command cmd1, Command cmd2){
	the_robot = rob;
	first_cmd = cmd1;
        second_cmd = cmd2;
    }

}
