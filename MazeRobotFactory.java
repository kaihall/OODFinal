package LepinskiEngine;
import java.util.List;
import java.util.ArrayList;

public class MazeRobotFactory{
    
    public static MazeRobot makeMazeRobot(ModelType model, int id, MazeLocation loc){
	
	switch (model)
	    {
	    case CoinBot: return makeCoinBot(id, loc);
	    case ScoutBot:  return makeScoutBot(id, loc);
	    case VisionBot:  return makeVisionBot(id, loc);
	    case GhostBot:  return makeGhostBot(id, loc);
	    }
	return null;
    }

    public static MazeRobot makeCoinBot(int id, MazeLocation loc){
	MazeRobot bot = new MazeRobot(ModelType.CoinBot, id, loc);
	List<CoinType> coins = new ArrayList<CoinType>();
	coins.add(CoinType.Gold);
	coins.add(CoinType.Diamond);
  
	bot.setCheckMove(new CheckMoveNormal());
	bot.setCheckCoin(new CheckCoin(coins));
	bot.setScanMethod(new ScanMethodNormal(1));
	return bot;
    }

    public static MazeRobot makeScoutBot(int id, MazeLocation loc){
	MazeRobot bot = new MazeRobot(ModelType.ScoutBot, id, loc);
	List<CoinType> coins = new ArrayList<CoinType>();
     
	bot.setCheckMove(new CheckMoveNormal());
	bot.setCheckCoin(new CheckCoin(coins));
	bot.setScanMethod(new ScanMethodNormal(3));
	return bot;
    }

    public static MazeRobot makeVisionBot(int id, MazeLocation loc){
	MazeRobot bot = new MazeRobot(ModelType.VisionBot, id, loc);
	List<CoinType> coins = new ArrayList<CoinType>();
     
	bot.setCheckMove(new CheckMoveStationary());
	bot.setCheckCoin(new CheckCoin(coins));
	bot.setScanMethod(new ScanMethodVisionBot());
	return bot;
    }

    public static MazeRobot makeGhostBot(int id, MazeLocation loc){
	MazeRobot bot = new MazeRobot(ModelType.GhostBot, id, loc);
	List<CoinType> coins = new ArrayList<CoinType>();
	coins.add(CoinType.Diamond);
  
	bot.setCheckMove(new CheckMoveGhost());
	bot.setCheckCoin(new CheckCoin(coins));
	bot.setScanMethod(new ScanMethodNormal(1));
	return bot;
    }
    
}
		    
	    
	
