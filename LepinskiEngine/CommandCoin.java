package LepinskiEngine;

// Requests that the robot pick up any coins in its current location

public class CommandCoin implements Command{
    private Robot the_robot;

    public Robot getRobot(){
	return the_robot;
    }

    public CommandCoin(Robot rob){
	the_robot = rob;
    }

}