package LepinskiEngine;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Maze{
    
    MazeLocation[][] rooms;
    int max_x;
    int max_y;

    public MazeLocation getLocation(int x, int y){
	MazeLocation ans = null;
	if ((x >=0) && (x < max_x)){
	    if ((y>=0) && (y < max_y)){
		ans = rooms[x][y];
	    }
	}
	return ans;
    }

    public MazeLocation getTeamStart(){
	Random rand = new Random();
	DirType dir = DirType.values()[rand.nextInt(4)];
	int x=0;
	int y=0;

	if(dir == DirType.North){
	    x = (max_x -1) /2 ;
	    y = 0;
	}
	if(dir == DirType.South){
	    x = (max_x -1) /2;
	    y = max_y - 1;
	}
	if(dir == DirType.West){
	    x = 0;
	    y = (max_y -1)/2;
	}
	if(dir == DirType.East){
	    x = max_x-1;
	    y = (max_y -1)/2;
	}
	    
	return rooms[x][y];
	
    }

    public int getMaxX(){
	return max_x;
    }

    public int getMaxY(){
	return max_y;
    }

    public int getNumCoin(){
	int count = 0;
	for(int i=0; i<max_x; i++){
	    for(int j=0; j<max_y; j++){
		if (rooms[i][j].getCoins() != null){
		    count = count + rooms[i][j].getCoins().size();
		}
	    }
	}
	return count;
    }

    public void setLocation(int x, int y, MazeLocation loc){
	rooms[x][y] = loc;
    }
	
    public MazeRobot getRobot(int id){	
	for(int i=0; i<max_x; i++){
	    for(int j=0; j<max_y; j++){
		List<MazeRobot> bots = rooms[i][j].getRobots();
		if (bots != null){
		    for(int k=0; k<bots.size(); k++){
			if(bots.get(k).getID() == id){
				return bots.get(k);
			}
		    }
		}
	    }
	}
	return null;
    }
   

    public Maze(String filename){
	FileReader fr = null;
	BufferedReader reader = null;
	try{
	    fr = new FileReader(filename);
	    reader = new BufferedReader(fr);
	    String line;
	    MazeLocation loc;
	
	    max_x = Integer.parseInt(reader.readLine());
	    max_y = Integer.parseInt(reader.readLine());
	    
	    rooms = new MazeLocation[max_x][max_y];
	
	    for(int j = 0; j<max_y; j++){
		line = reader.readLine();
		for(int i = 0; i<max_x; i++){
		    loc = makeLocation(line.charAt(i),i, j);
		    rooms[i][j] = loc;
		}
	    }
	    reader.close();
	    fr.close();

	    rooms[max_x/2][0].is_start=true;

	} catch(IOException e){
	    e.printStackTrace();
	} 
    }

    MazeLocation makeLocation(char symbol, int x, int y){
	MazeLocation loc = new MazeLocation(x,y);
	List<DirType> dirs = new ArrayList<DirType>();

	loc.is_scanned = true;

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


    public Maze(RectMaze a_maze){
	MazeLocation loc;
	
	max_x = a_maze.getMaxX();
	max_y = a_maze.getMaxY();
	    
	rooms = new MazeLocation[max_x][max_y];
	
	 for(int j = 0; j<max_y; j++){
	     for(int i = 0; i<max_x; i++){
		 loc = new MazeLocation(i, j);
		 loc.setTheDirs(a_maze.getDirections(i,j));
		 rooms[i][j] = loc;
	     }
	 }

	 rooms[max_x/2][0].is_start=true;

    }

    //Blank Maze constructor
    public Maze(int x, int y){
	max_x = x;
	max_y = y;
	rooms = new MazeLocation[max_x][max_y];
	for(int i = 0; i<max_x; i++){
	    for(int j = 0; j<max_y; j++){
		rooms[i][j] = null;
	    }
	}
    }

}
