package Navigation;

import java.util.ArrayList;
import java.util.List;
import LepinskiEngine.*;

public class LongPathHidingTeam implements PlayerHidingTeam {
	private boolean gameStarted = false;
	
	public LongPathHidingTeam() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void startGame(List<ObstacleType> obs, List<CoinType> coins, RectMaze maze, GameState state) {
		
	}

	@Override
	public List<PlaceObstacle> setObstacles(List<ObstacleType> obs, RectMaze maze, GameState state) {
		if (!gameStarted) {
			startGame(obs,new ArrayList<CoinType>(),maze,state);
			gameStarted = true;
		}
		
		return null;
	}

	@Override
	public List<PlaceCoin> hideCoins(List<CoinType> coins, RectMaze maze, GameState state) {
		if (!gameStarted) {
			startGame(new ArrayList<ObstacleType>(), coins, maze,state);
			gameStarted = true;
		}
		
		return null;
	}
}
