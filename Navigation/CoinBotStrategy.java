package Navigation;

import LepinskiEngine.*;
import java.util.List;
import java.lang.Math.*;

@SuppressWarnings("unused")
public class CoinBotStrategy extends BotStrategy
{
    
    private int favorUnknown;
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
    	   map.removeCoin(map.getBotLocation(bot));
           return new CommandCoin(bot); //I believe this is correct for picking up the coin?
       }
       else{
           for(Location c : map.getCoinLocations()){
              List<DirType> pathTo = map.getPath(map.getBotLocation(bot), c);
               boolean unknownPreference = false; //CHANGE THIS TO CHANGE UNKNOWN PREFERENCE
               setUnknownPref(unknownPreference);
               int finalsize = unknownWeighting(bot, map, pathTo, c, unknownPreference);
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

   //sets the coinbot's logic-- is it trying to seek unknown paths?
   public void setUnknownPref(boolean b){
    if(b == true) 
        favorUnknown = 0;
    else 
        favorUnknown = 1;
   }

   public int getUnknownPref(){
       return favorUnknown;
   }

    private int unknownWeighting(Robot bot, Map map, List<DirType> dir, Location coinloc, boolean unknownBehav){
        setUnknownPref(unknownBehav);
        int unknownVal = getUnknownPref();
        int focusX = map.getBotLocation(bot).getX();
        int focusY = map.getBotLocation(bot).getY();
        for(DirType d : dir){
        if(d == DirType.East)
            focusX += 1;
        if(d == DirType.West)
            focusX -= 1;
        if(d == DirType.South)
            focusY += 1;
        if(d == DirType.North)
            focusY -= 1;
        }
        int xdiff = coinloc.getX() - focusX;
        int ydiff = coinloc.getY() - focusY;
        if(xdiff == 0 && ydiff == 0)
            return dir.size();
        else{
            boolean furtherInX;
            while(xdiff != 0 && ydiff != 0){
                if(Math.abs(xdiff) > Math.abs(ydiff))
                    furtherInX = true;
                else
                    furtherInX = false;
                if(furtherInX){
                    if(xdiff > 0){
                        focusX += 1;
                        xdiff -= 1;
                    }
                    else{
                        focusX -= 1;
                        xdiff += 1;
                    }
                    
                }
                else{
                    if(ydiff > 0){
                        focusY += 1;
                        ydiff -= 1;
                    }
                    else{
                        focusY -= 1;
                        ydiff += 1;
                    }
                }
                if(map.getLabel(focusX, focusY) == 0)
                    unknownVal += unknownVal;
            }
            return dir.size() + unknownVal;
        }
}
}