package Navigation;

import LepinskiEngine.*;
import java.util.List;
import java.lang.Math;
import java.util.Random;


public class ScoutBotStrategy2 extends BotStrategy
{
    Random r;
    
    public ScoutBotStrategy2() {
        //Sets up random direction
        r = new Random();
    }

    public Command nextMove(Robot bot, List<Location> cur_vision, Map map) {
        //Creates done condition
        //Becomes true when there are no unexplored cells
        boolean done = false;
        Location cur_loc = map.getBotLocation(bot);
        int xLoc = map.getBotLocation(bot).getX();
        int yLoc = map.getBotLocation(bot).getY();
        int Tx = map.getBotLocation(bot).getX();
        int Ty = map.getBotLocation(bot).getY();
    
        //While the done condition remains false, a random target location is created in an unexplored cell
        /*while(done == false){
            while(map.getLabel(Tx,Ty)!= 0){
                Tx = (int)(Math.random()* map.getMaxX());
                Ty = (int)(Math.random()* map.getMaxY());
            }
    
            //The bot will then go in a random direction available to it until it reaches the target location
            while ((xLoc!=Tx)&&(yLoc!=Ty)){*/
                int randIndex = r.nextInt(cur_loc.getDirections().size());
                DirType direx = cur_loc.getDirections().get(randIndex);
                return new CommandMove(bot,direx);
            /*}
               
                //Checks if all cells have been changed to Scanned
                int doneCtr=0;
        
                for(int i=0; i <= map.getMaxX(); i++){
                for(int j=0; j <= map.getMaxY(); j++){
                    if(map.getLabel(i,j) == 0){
                        doneCtr=doneCtr+1;
                    }
                }
                }
                if(doneCtr == 0){
                    done = true;
                } else{
                    done = false;
                }
        }*/
        
        //return new CommandMove(bot,DirType.South);
    }
}