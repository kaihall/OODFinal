package LepinskiEngine;
import java.util.List;

//chooseTeam is called once at the very start of the game
//This function should return the five robots the player wants to use during the game

//You are allowed a team of FIVE robots.
//You can use at most two robots of a particular ModelType

//requestCommands is called each turn
//This function should return one Command for each robot awaiting command
//A robot who loses its turn due to a SLOW trap will not be including in the list

public interface PlayerSearchingTeam{
    public List<Robot> chooseRobots(GameState state); 

    public List<Command> requestCommands(List<Location> information, List<Robot> robotsAwaitingCommand, GameState state);
}
