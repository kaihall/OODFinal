package LepinskiEngine;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RectMazeFromDisk implements RectMaze{
    private Location[][] rooms;
    private int max_x;
    private int max_y;

    public int getMaxX(){
	return max_x;
    }

    public int getMaxY(){
	return max_y;
    }

    public List<DirType> getDirections(int x, int y){
	Location loc = rooms[x][y];
	return loc.getDirections();
    }

    public RectMazeFromDisk(String filename){
	FileReader fr = null;
	BufferedReader reader = null;
	try{
	    fr = new FileReader(filename);
	    reader = new BufferedReader(fr);
	    String line;
	    Location loc;
	
	    max_x = Integer.parseInt(reader.readLine());
	    max_y = Integer.parseInt(reader.readLine());
	    
	    rooms = new Location[max_x][max_y];
	
	    for(int j = 0; j<max_y; j++){
		line = reader.readLine();
		for(int i = 0; i<max_x; i++){
		    loc = makeLocation(line.charAt(i),i, j);
		    rooms[i][j] = loc;
		}
	    }
	    reader.close();
	    fr.close();

	} catch(IOException e){
	    e.printStackTrace();
	} 
    }

    Location makeLocation(char symbol, int x, int y){
	Location loc = new Location(x,y);
	List<DirType> dirs = new ArrayList<DirType>();

	if(symbol == '+'){
	    dirs.add(DirType.North);
	    dirs.add(DirType.South);
	    dirs.add(DirType.East);
	    dirs.add(DirType.West);
	}

	if(symbol == 'T'){
	    dirs.add(DirType.South);
	    dirs.add(DirType.East);
	    dirs.add(DirType.West);
	}
	if(symbol == 'E'){
	    dirs.add(DirType.North);
	    dirs.add(DirType.South);
	    dirs.add(DirType.East);
	}
	if(symbol == '3'){
	    dirs.add(DirType.North);
	    dirs.add(DirType.South);
	    dirs.add(DirType.West);
	}
	if(symbol == '1'){
	    dirs.add(DirType.North);
	    dirs.add(DirType.East);
	    dirs.add(DirType.West);
	}

	if(symbol == '-'){
	    dirs.add(DirType.East);
	    dirs.add(DirType.West);
	}
	if(symbol == '|'){
	    dirs.add(DirType.North);
	    dirs.add(DirType.South);
	}
	if(symbol == 'L'){
	    dirs.add(DirType.North);
	    dirs.add(DirType.East);
	}
	if(symbol == 'J'){
	    dirs.add(DirType.North);
	    dirs.add(DirType.West);
	}
	if(symbol == '7'){
	    dirs.add(DirType.South);
	    dirs.add(DirType.West);
	}
	if(symbol == 'P'){
	    dirs.add(DirType.South);
	    dirs.add(DirType.East);
	}

	if(symbol == '['){
	    dirs.add(DirType.East);
	}
	if(symbol == ']'){
	    dirs.add(DirType.West);
	}
	if(symbol == 'U'){
	    dirs.add(DirType.North);
	}
	if(symbol == '^'){
	    dirs.add(DirType.South);
	}

	loc.the_directions = dirs;
	return loc;

    }


}
