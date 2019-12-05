package LepinskiEngine;
import java.util.List;

//stateGame is called once at the very start of the game

//setObstacles is called once after startGame
//The player team is given a list of Obatacles to place
//You should return a PlaceObstacle object for each obstacle you wish to place

//NOTE: You will be given exactly one STONE obstacle
//      As well as an assortment of DARK and SLOW obstacles

//hideCoins is called once after hideObstacles 
//The player team is given a list of Coins to place
//You should return a PlaceCoin object for each Coin you wish to place
//Coins that you choose not to place will be place arbitrarily by the gameEngine

//NOTE: You will be given exactly two DIAMOND coins
//      As well as a number of GOLD coins


public interface PlayerHidingTeam{
    public void startGame(List<ObstacleType> obs, List<CoinType> coins, RectMaze maze, GameState state); 

    public List<PlaceObstacle> setObstacles(List<ObstacleType> obs, RectMaze maze, GameState state); 

    public List<PlaceCoin> hideCoins(List<CoinType> coins, RectMaze maze, GameState state);
}