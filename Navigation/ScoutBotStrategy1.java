package Navigation;

import LepinskiEngine.*;
import java.util.List;
import java.util.Random;

public class ScoutBotStrategy1 extends BotStrategy
{
    Random rand;
    
    public ScoutBotStrategy1() {
        // creates a new instance of random
        rand = new Random();
    }
    
    public Command nextMove(Robot bot, List<Location> cur_vision, Map map) {
        //gets the bot's current location
        Location cur_loc = map.getBotLocation(bot);
        
        //gets a list of directions
        List<DirType> possibleDirections = cur_loc.getDirections();
        
        // gets the bots current location in the maze
        int xLoc = cur_loc.getX();
        int yLoc = cur_loc.getY();
        
        // checks to see if theres only one possible direction to move in
    // if there is it'll mark that location as a dead end
    if (deadEnd(map,cur_loc,xLoc,yLoc)){
        map.setLabel(xLoc, yLoc, Map.DEAD_END);
    }
        
        // if the direction is both possible AND unexplored, then the bot will go that way
        // the bot checks three away from the current posistion in each direction
        if (possibleDirections.contains(DirType.North) && shouldCheck(DirType.North,map,xLoc,yLoc)) {
            return new CommandMove(bot,DirType.North);
        }
        else if (possibleDirections.contains(DirType.East) && shouldCheck(DirType.East,map,xLoc,yLoc)) {
            return new CommandMove(bot,DirType.East);
        }
        else if (possibleDirections.contains(DirType.South) && shouldCheck(DirType.South,map,xLoc,yLoc)) {
            return new CommandMove(bot,DirType.South);
        }
        else if (possibleDirections.contains(DirType.West) && shouldCheck(DirType.West,map,xLoc,yLoc)) {
            return new CommandMove(bot,DirType.West);
        }
        // if all of the directions are explored then the bot picks a random direction and moves that way
        else{
            // direction is based on the list of possible directions
            int randomIndex = rand.nextInt(possibleDirections.size());
            DirType direction = possibleDirections.get(randomIndex);
            return new CommandMove(bot,direction);
        }
    }
    
    private boolean deadEnd(Map map, Location curLoc, int xLoc, int yLoc) {
        List<DirType> dirxns = curLoc.getDirections();
        
        if (dirxns.size() == 1) return true;
        
        else {
            int deadEnds = 0;
            for (DirType check : dirxns) {
                if (check == DirType.North && yLoc > 0 && map.getLabel(xLoc,yLoc-1) == Map.DEAD_END)
                    deadEnds++;
                if (check == DirType.South && yLoc < map.getMaxY() && map.getLabel(xLoc,yLoc+1) == Map.DEAD_END)
                    deadEnds++;
                if (check == DirType.East && xLoc > 0 && map.getLabel(xLoc-1,yLoc) == Map.DEAD_END)
                    deadEnds++;
                if (check == DirType.West && xLoc < map.getMaxX() && map.getLabel(xLoc+1,yLoc) == Map.DEAD_END)
                    deadEnds++;
            }
            
            //if all directions are dead ends or all but one are, this location is also a dead end
            return deadEnds >= dirxns.size()-1;
        }
    }
    
    private boolean shouldCheck(DirType dirxn, Map m, int xLoc, int yLoc) {
        int xCheck = 0;
        int yCheck = 0;
        
        switch (dirxn) {
            case North:
                yCheck = -3;
                break;
            case South:
                yCheck = 3;
                break;
            case East:
                xCheck = -3;
                break;
            case West:
                xCheck = 3;
                break;
        }        
        
        if (xLoc+xCheck >= 0 && xLoc+xCheck <= m.getMaxX() && 
            yLoc+yCheck >= 0 && yLoc+yCheck <= m.getMaxY() &&
            m.getLabel(xLoc+xCheck,yLoc+yCheck) == Map.UNSCANNED)
                return true;
        
        return false;
    }
}
