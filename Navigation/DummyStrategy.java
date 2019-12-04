package Navigation;

import LepinskiEngine.*;
import java.util.List;

public class DummyStrategy extends BotStrategy
{
   public DummyStrategy(){}

   public Command nextMove(Robot bot, List<Location> cur_vision, Map map) {
        return new CommandCoin(bot);
   }
}