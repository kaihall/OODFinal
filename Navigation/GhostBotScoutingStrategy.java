package Navigation;

import java.util.List;
import java.util.Random;

import LepinskiEngine.Command;
import LepinskiEngine.CommandCoin;
import LepinskiEngine.CommandMove;
import LepinskiEngine.Location;
import LepinskiEngine.Robot;

public class GhostBotScoutingStrategy extends GhostBotStrategy {

	public GhostBotScoutingStrategy() {
		// TODO Auto-generated constructor stub
	}
	
	public Command nextMove(Robot bot, List<Location> cur_vision, Map map) {
		// Picking up a Diamond under the bot is the first priority
		if (map.onDiamond(bot)) {
			map.removeCoin(map.getBotLocation(bot));
			onPath = false;
			return new CommandCoin(bot);
		}
		
		// If the bot has reached its destination, it is no longer on a path
		if (map.getBotLocation(bot).getX() == dest.getX() && map.getBotLocation(bot).getY() == dest.getY())
			onPath = false;
		
		if (!onPath) {
			// Find an unexplored location
			dest = map.getBotLocation(bot);
			
			while (map.getLabel(dest.getX(),dest.getY()) != Map.UNSCANNED) {
				Random rand = new Random();
				int x = rand.nextInt(map.getMaxX()+1);
				int y = rand.nextInt(map.getMaxY()+1);
				dest = new Location(x,y);
			}
			
			onPath = true;
		}
		
		// Head directly towards it
		return new CommandMove(bot,headTowards(dest,map.getBotLocation(bot)));
	}
}
