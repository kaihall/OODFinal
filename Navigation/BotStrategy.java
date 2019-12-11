package Navigation;

import LepinskiEngine.*;
import java.util.List;
import java.util.ArrayList;

public abstract class BotStrategy
{
    public abstract Command nextMove(Robot bot, List<Location> cur_vision, Map map);
    
    public static List<BotStrategy> getValidStrategies(ModelType mod) {
        List<BotStrategy> strats = new ArrayList<BotStrategy>();
        
        switch (mod) {
            case ScoutBot:
                strats.add(new ScoutBotStrategy1());
                //strats.add(new ScoutBotStrategy2());
                break;
            case CoinBot:
                //strats.add(new CoinBotStrategy());
                strats.add(new CoinBotStrategySimple());
                break;
            case GhostBot:
            	strats.add(new GhostBotStrategy());
            	strats.add(new GhostBotScoutingStrategy());
            	break;
            default:
                strats.add(new DummyStrategy());
                break;
        }
        
        return strats;
    }
}