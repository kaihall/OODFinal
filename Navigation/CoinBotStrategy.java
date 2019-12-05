package Navigation;

import LepinskiEngine.*;
import java.util.List;
import java.lang.Math.*;

@SuppressWarnings("unused")
public class CoinBotStrategy extends BotStrategy
{
   public CoinBotStrategy() {
    }

    //this returns a float to be multiplied to the pathlength. This makes the coinbot more likely to seek
    //groups of coins, as opposed to just the closest coin.
    private float weightDecision(Location loc, Map m){
        int count = 0;
        float criticalval = (m.getMaxX() + m.getMaxY())/2/5.0f;
        List<Location> coins = m.getCoinLocations();
        for(Location c : coins){
            if(Math.abs(c.getX() - loc.getX()) < criticalval && Math.abs(c.getY() - loc.getY()) < criticalval){
                count += 1;
            }
        }   
        return (float)Math.pow(.8, count); //.8^count
    }

   public Command nextMove(Robot bot, List<Location> cur_vision, Map map) {
       map.update(cur_vision);
       int currentmin = 50;
       List<DirType> retlist = map.getBotLocation(bot).getDirections(); //initializes it to a list that will at least give you a location to go to
       DirType direction;
       if(map.onCoin(bot)){
           return new CommandCoin(bot); //I believe this is correct for picking up the coin?
       }
       else{
           for(Location c : map.getCoinLocations()){
        	   List<DirType> pathTo = map.getPath(map.getBotLocation(bot), c);
               float weightedlength = weightDecision(c, map) * pathTo.size();
               if(weightedlength < currentmin) {
            	   	currentmin = pathTo.size();
                    retlist = map.getPath(map.getBotLocation(bot), c);
               }
           }
           direction = retlist.get(0);
       }
       return new CommandMove(bot, direction);
   }
}