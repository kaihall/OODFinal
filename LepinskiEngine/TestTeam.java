package LepinskiEngine;
import java.util.List;
import java.util.ArrayList;

//startGame is called once at the very start of the game
//This function should return the robots the player wants to use during the game

//requestCommands is called each turn
//This function should return one Command for each robot awaiting command

public class TestTeam implements PlayerSearchingTeam, PlayerHidingTeam{
    GameState cur_state;

    //Should Return FIVE Robots, this is a small test
    public List<Robot> chooseRobots(GameState state){
	cur_state = state;
	ArrayList<Robot> bots = new ArrayList<Robot>();
	bots.add(new Robot(ModelType.ScoutBot, 1));
	bots.add(new Robot(ModelType.FastBot, 2));
       
	return bots;
    }

    public List<Command> requestCommands(List<Location> information, List<Robot> robotsAwaitingCommand, GameState state){

	List<Command> the_coms = new ArrayList<Command>();

	for(Robot bot : robotsAwaitingCommand){
	    if(bot.getModel() == ModelType.ScoutBot){
		Command com = new CommandMove(bot, DirType.West);
		the_coms.add(com);
	    }
	    if(bot.getModel() == ModelType.FastBot){
		Command com1 = new CommandMove(bot, DirType.East);
	        Command com2 = new CommandMove(bot, DirType.South);
		the_coms.add(new CommandFastMove(bot, com1, com2));
	    }
	}

	return the_coms;
    }

    public void startGame(List<ObstacleType> obs, List<CoinType> coins, RectMaze maze, GameState state)
    {
	cur_state = state;
    }

    public List<PlaceObstacle> setObstacles(List<ObstacleType> list_obs, RectMaze maze, GameState state)
    {
	int x = 4;
	int y = 4;
	
	List<PlaceObstacle> placements = new ArrayList<PlaceObstacle>();
	
	for (ObstacleType ob : list_obs){
	    PlaceObstacle place = new PlaceObstacle(ob, x, y);
	    placements.add(place);
	    if (x == 8){
		x = 4;
		y = y + 1;
	    }
	    else{
		x = x + 1;
	    }
	}
	
	return placements;
    }

    public List<PlaceCoin> hideCoins(List<CoinType> coins, RectMaze maze, GameState state)
    {
	int x = 1;
	int y = 1;
	
	List<PlaceCoin> placements = new ArrayList<PlaceCoin>();
	
	for (CoinType c : coins){
	    PlaceCoin place = new PlaceCoin(c, x, y);
	    placements.add(place);
	    if (x == 10){
		x = 1;
		y = y + 1;
	    }
	    else{
		x = x + 1;
	    }
	}
	
	return placements;
    }	
    
}
