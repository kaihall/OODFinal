package Navigation;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
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
		
		int minLength = Integer.MAX_VALUE;
		for (Location c : map.getCoinLocations()) {
			List<DirType> tmp = map.getPath(map.getBotLocation(bot),c);
			if (!tmp.isEmpty() && tmp.size() < minLength) {
				minLength = tmp.size();
				path = tmp;
			}
		}
		
		if (path.isEmpty()) {
			dir = randomDirection(map.getBotLocation(bot).getDirections());
		} else {
			dir = path.get(0);
		}
		
		lastMove = dir;
		
		return new CommandMove(bot, dir);
	}
	
	private DirType randomDirection(List<DirType> possible) {
		int i = 0;
		Collections.shuffle(possible);
		
		while (lastMove != null && possible.get(i) == reverse(lastMove) && i < possible.size()-1) {
			i++;
		}
		
		return possible.get(i);
	}
	
	private DirType reverse(DirType dir) {
		switch (dir) {
		case North:
			return DirType.South;
		case South:
			return DirType.North;
		case East:
			return DirType.East;
		case West:
			return DirType.West;
		}
		return null;
	}
}
