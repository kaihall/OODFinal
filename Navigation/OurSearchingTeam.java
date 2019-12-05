package Navigation;

import LepinskiEngine.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class OurSearchingTeam implements PlayerSearchingTeam
{
    GameState cur_state;
    HashMap<Integer,BotStrategy> strats;
    Map map;
    
    public List<Robot> chooseRobots(GameState state) {
        cur_state = state;
        
        List<Robot> bots = new ArrayList<Robot>();
        
        bots.add(new Robot(ModelType.ScoutBot, 1));
        bots.add(new Robot(ModelType.ScoutBot, 2));
        bots.add(new Robot(ModelType.CoinBot, 3));
        bots.add(new Robot(ModelType.VisionBot, 4));
        bots.add(new Robot(ModelType.GhostBot, 5));
        
        strats = new HashMap<Integer,BotStrategy>();
        for (Robot rob : bots)
            strats.put(rob.getID(), assignStrategy(rob));
        
        map = new Map(cur_state.maze_size_x, cur_state.maze_size_y, bots);
        
        return bots;
    }
    
    public List<Command> requestCommands(List<Location> information, List<Robot> robotsAwaitingCommand, GameState state) {
        cur_state = state;
        map.update(information);
        
        List<Command> commands = new ArrayList<Command>();
        
        for (Robot bot : robotsAwaitingCommand) 
        	commands.add(strats.get(bot.getID()).nextMove(bot,information,map));
        
        //System.out.println("Commands get returned.");
        return commands;
    }
    
    public static BotStrategy assignStrategy(Robot bot) {
        List<BotStrategy> strategies = BotStrategy.getValidStrategies(bot.getModel());
        if (strategies.isEmpty()) return new DummyStrategy();
        return strategies.get(bot.getID()%strategies.size());
    }
}