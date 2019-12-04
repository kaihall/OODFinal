package Navigation;

import LepinskiEngine.*;
import java.util.List;
import java.lang.Math.*;

public class CoinBotStrategy extends BotStrategy
{
   public CoinBotStrategy() {
    }

    //this returns a float to be multiplied to the pathlength. This makes the coinbot more likely to seek
    //groups of coins, as opposed to just the closest coin.
    private float weightDecision(Location loc, Map m){
        int count = 0;
        float criticalval = (m.getMaxX() + m.getMaxY())/2/5.0f;
        List<List<Location>> coins = m.coinPaths(loc);
        for(List<Location> lstloc : coins){
            if(lstloc.get(lstloc.size()-1).getX() - loc.getX() < criticalval && lstloc.get(lstloc.size()-1).getY() - loc.getY() < criticalval){
                count += 1;
            }
        }   
        return (float)Math.pow(.8, count); //.8^count
    }

   public Command nextMove(Robot bot, List<Location> cur_vision, Map map) {
       map.update(cur_vision);
       int currentmin = 50;
       List<Location> retlist = cur_vision; //initializes it to a list that will at least give you a location to go to
       DirType direction;
       if(map.onCoin(bot) != false){
           return new CommandCoin(bot); //I believe this is correct for picking up the coin?
       }
       else{
           for(List<Location> lloc : map.coinPaths(map.getBotLocation(bot))){
               float weightedlength = weightDecision(lloc.get(lloc.size()-1), map) * lloc.size();
               if(weightedlength < currentmin){
                    currentmin = lloc.size();
                    retlist = lloc;
               }
           }
           Location nextsquare = retlist.get(0);
           if(nextsquare.getX() == map.getBotLocation(bot).getX()+1)
               direction = DirType.East;
           else if(nextsquare.getX() == map.getBotLocation(bot).getX() - 1)
                direction = DirType.West;
           else if(nextsquare.getY() == map.getBotLocation(bot).getX() - 1)
                direction = DirType.North;
           else{
                direction = DirType.South;
           }
       }
       return new CommandMove(bot, direction);
   }
}