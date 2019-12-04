package Navigation;

import java.util.List;
import java.util.ArrayList;

import LepinskiEngine.*;

public class OurHidingTeam implements PlayerHidingTeam {
	
	
	
	@Override
	public void startGame(List<ObstacleType> obs, List<CoinType> coins, RectMaze maze, GameState state) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<PlaceObstacle> setObstacles(List<ObstacleType> obs, RectMaze maze, GameState state) {
		List<PlaceObstacle> cmds = new ArrayList<PlaceObstacle>();
		
		for (ObstacleType ob : obs) {
			if (ob == ObstacleType.Dark)
				cmds.add(hideDark(maze, state));
			else if (ob == ObstacleType.Slow)
				cmds.add(hideSlow(maze, state));
			else if (ob == ObstacleType.Stone)
				cmds.add(hideStone(maze, state));
			else
				cmds.add(hideUnkObs(maze, state));
		}
		
		return cmds;
	}

	@Override
	public List<PlaceCoin> hideCoins(List<CoinType> coins, RectMaze maze, GameState state) {
		List<PlaceCoin> cmds = new ArrayList<PlaceCoin>();
		
		for (CoinType coin : coins) {
			if (coin == CoinType.Gold)
				cmds.add(hideGold(maze, state));
			else if (coin == CoinType.Diamond)
				cmds.add(hideDiamond(maze, state));
			else
				cmds.add(hideUnkCoin(maze, state));
		}
		
		return null;
	}
	
	private PlaceObstacle hideDark(RectMaze maze, GameState state) {
		// stubbed
		return null;
	}
	
	private PlaceObstacle hideSlow(RectMaze maze, GameState state) {
		// stubbed
		return null;
	}
	
	private PlaceObstacle hideStone(RectMaze maze, GameState state) {
		// stubbed
		return null;
	}
	
	private PlaceObstacle hideUnkObs(RectMaze maze, GameState state) {
		// stubbed
		return null;
	}
	
	private PlaceCoin hideGold(RectMaze maze, GameState state) {
		// stubbed
		return null;
	}
	
	private PlaceCoin hideDiamond(RectMaze maze, GameState state) {
		// stubbed
		return null;
	}
	
	private PlaceCoin hideUnkCoin(RectMaze maze, GameState state) {
		// stubbed
		return null;
	}
}
