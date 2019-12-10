package Navigation;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

import LepinskiEngine.*;

public class RandomHidingTeam implements PlayerHidingTeam {
	
	boolean[][] hasOb;
	boolean[][] hasCoin;
	Random rand;
	int max_x, max_y;
	boolean gameStarted = false;
	
	public void startGame(List<ObstacleType> obs, List<CoinType> coins, RectMaze maze, GameState state) {
		max_x = maze.getMaxX();
		max_y = maze.getMaxY();
		hasOb = new boolean[max_x][max_y];
		hasCoin = new boolean[max_x][max_y];
		
		for (int i = 0; i < max_x; i++) {
			for (int j = 0; j < max_y; j++) {
				hasOb[i][j] = false;
				hasCoin[i][j] = false;
			}
		}
		
		rand = new Random();
	}

	@Override
	public List<PlaceObstacle> setObstacles(List<ObstacleType> obs, RectMaze maze, GameState state) {
		if (!gameStarted) {
			startGame(obs,new ArrayList<CoinType>(),maze,state);
			gameStarted = true;
		}
		
		List<PlaceObstacle> cmds = new ArrayList<PlaceObstacle>();
		
		for (ObstacleType ob : obs) {
			int x=0,y=0;
			
			while (hasOb[x][y] || hasCoin[x][y]) {
				x = rand.nextInt(max_x);
				y = rand.nextInt(max_y);
			} 
			
			hasOb[x][y] = true;
			cmds.add(new PlaceObstacle(ob,x,y));
		}
		
		return cmds;
	}

	@Override
	public List<PlaceCoin> hideCoins(List<CoinType> coins, RectMaze maze, GameState state) {
		if (!gameStarted) {
			startGame(new ArrayList<ObstacleType>(), coins, maze,state);
			gameStarted = true;
		}
		
		List<PlaceCoin> cmds = new ArrayList<PlaceCoin>();
		
		for (CoinType c : coins) {
			int x=0,y=0;
			
			while (hasOb[x][y] || hasCoin[x][y]) {
				x = rand.nextInt(max_x);
				y = rand.nextInt(max_y);
			} 
			
			hasCoin[x][y] = true;
			cmds.add(new PlaceCoin(c,x,y));
		}
		
		return cmds;
	}
}
