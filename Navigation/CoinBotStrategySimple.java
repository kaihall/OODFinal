package Navigation;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import LepinskiEngine.*;

public class CoinBotStrategySimple extends BotStrategy {
	
	private boolean onPath;
	private Location dest;
	private DirType lastMove;
	
	public CoinBotStrategySimple() {
		onPath = false;
		dest = new Location(-1,-1);
		lastMove = null;
	}

	/*
	 * Just goes towards the closest coin
	 */
	public Command nextMove(Robot bot, List<Location> cur_vision, Map map) {
		if (map.getBotLocation(bot).getX() == dest.getX() && map.getBotLocation(bot).getY() == dest.getY())
			onPath = false;
		
		if (map.onCoin(bot)){
			map.removeCoin(map.getBotLocation(bot));
	        return new CommandCoin(bot);
	    }
		
		List<DirType> path = new ArrayList<DirType>();
		DirType dir;
		
		if (onPath) {
			path = map.getPath(map.getBotLocation(bot),dest);
		} else {
			int minLength = Integer.MAX_VALUE;
			for (Location c : map.getCoinLocations()) {
				List<DirType> tmp = map.getPath(map.getBotLocation(bot),c);
				if (!tmp.isEmpty() && tmp.size() < minLength) {
					minLength = tmp.size();
					path = tmp;
					dest = c;
					onPath = true;
				}
			}
		}
		
		if (path.isEmpty()) {
			dir = randomDirection(map.getBotLocation(bot).getDirections());
			dest = new Location(-1,-1);
			onPath = false;
		} else {
			dir = path.get(0);
		}
		
		lastMove = dir;
		
		return new CommandMove(bot, dir);
	}
	
	private DirType randomDirection(List<DirType> possible) {
		int i;
		int tries = 0;
		
		do {
			Random rand = new Random();
			i = rand.nextInt(possible.size());
			tries++;
		} while (possible.get(i) == lastMove || tries >= possible.size());
		
		return possible.get(i);
	}
}
