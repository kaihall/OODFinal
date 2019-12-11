package Navigation;

import java.util.List;
import java.util.Random;

import LepinskiEngine.*;

public class GhostBotStrategy extends BotStrategy {
	
	protected boolean onPath;
	protected Location dest;
	
	public GhostBotStrategy() {
		onPath = false;
		dest = new Location(0,0);
	}
	
	@Override
	/*
	 * If there are Diamonds, the GhostBot heads towards the Diamonds. Otherwise, it starts exploring locations.
	 */
	public Command nextMove(Robot bot, List<Location> cur_vision, Map map) {
		if (!map.getDiamondLocations().isEmpty()) {
			// Picking up a Diamond under the bot is the first priority
			if (map.onDiamond(bot)) {
				map.removeCoin(map.getBotLocation(bot));
				onPath = false;
				return new CommandCoin(bot);
			}
			
			// If there is no Diamond under the bot and the bot is not on a path, find the closest Diamond
			if (!onPath) {	
				int minDist = Integer.MAX_VALUE;
				dest = map.getBotLocation(bot);
				Location botLocation = map.getBotLocation(bot);
				for (Location diamond : map.getDiamondLocations()) {
					// Find the distance between the Robot and this Diamond
					int dist = Math.abs(diamond.getX()-botLocation.getX()) + 
							   Math.abs(diamond.getY()-botLocation.getY());
					// If this Diamond is the closest, store its Location and the distance to it
					if (dist < minDist) {
						minDist = dist;
						dest = diamond;
					}
				}
				onPath = true;
			}
			
			// Start heading towards the closest Diamond
			return new CommandMove(bot,headTowards(dest,map.getBotLocation(bot)));
			
		} else {
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
	
	protected DirType headTowards(Location destination, Location botLocation) {
		if (destination.getX() > botLocation.getX())
			return DirType.East;
		if (destination.getX() < botLocation.getX())
			return DirType.West;
		if (destination.getY() > botLocation.getY())
			return DirType.South;
		
		return DirType.North;
	}

}
