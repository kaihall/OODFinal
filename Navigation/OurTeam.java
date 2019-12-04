package Navigation;

import LepinskiEngine.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class OurTeam implements PlayerTeam
{
    GameState cur_state;
    HashMap<Integer,BotStrategy> strats;
    Map map;
    
    public void startGame(List<Robot> bots, GameState state) {
        //System.out.println("Starting game...");
        cur_state = state;
        strats = new HashMap<Integer,BotStrategy>();
        for (Robot rob : bots)
            strats.put(rob.getID(), assignStrategy(rob));
        map = new Map(cur_state.maze_size_x, cur_state.maze_size_y, bots);
    }
    
    public List<Command> requestCommands(List<Location> information, List<Robot> robotsAwaitingCommand, GameState state) {
        cur_state = state;
        map.update(information);
        
        List<Command> commands = new ArrayList<Command>();
        
        for (Robot bot : robotsAwaitingCommand) {
            if (!strats.containsKey(bot.getID()))
                strats.put(bot.getID(), assignStrategy(bot));
            
            commands.add(strats.get(bot.getID()).nextMove(bot,information,map));
        }
        
        //System.out.println("Commands get returned.");
        return commands;
    }
    
    public static BotStrategy assignStrategy(Robot bot) {
        List<BotStrategy> strategies = BotStrategy.getValidStrategies(bot.getModel());
        if (strategies.isEmpty()) return new DummyStrategy();
        return strategies.get(bot.getID()%strategies.size());
    }
}