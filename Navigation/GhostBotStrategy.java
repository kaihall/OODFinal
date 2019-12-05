package Navigation;

import java.util.List;

import LepinskiEngine.*;

public class GhostBotStrategy extends BotStrategy {
	
	//private List<Location> pickedUp;
	
	public GhostBotStrategy() {
		
	}
	
	@Override
	public Command nextMove(Robot bot, List<Location> cur_vision, Map map) {
		// Picking up a Diamond under the bot is the first priority
		if (map.onDiamond(bot)) {
			map.removeCoin(map.getBotLocation(bot));
			return new CommandCoin(bot);
		}
		
		// If there is no Diamond under the bot, find the closest Diamond
		int minDist = Integer.MAX_VALUE;
		Location destination = map.getBotLocation(bot);
		Location botLocation = map.getBotLocation(bot);
		for (Location diamond : map.getDiamondLocations()) {
			// Find the distance between the Robot and this Diamond
			int dist = Math.abs(diamond.getX()-botLocation.getX()) + 
					   Math.abs(diamond.getY()-botLocation.getY());
			// If this Diamond is the closest, store its Location and the distance to it
			if (dist < minDist) {
				minDist = dist;
				destination = diamond;
			}
		}
		
		// Start heading towards the closest Diamond
		if (destination.getX() > botLocation.getX())
			return new CommandMove(bot,DirType.East);
		if (destination.getX() < botLocation.getX())
			return new CommandMove(bot,DirType.West);
		if (destination.getY() > botLocation.getY())
			return new CommandMove(bot,DirType.South);
		
		return new CommandMove(bot,DirType.North);
	}

}
