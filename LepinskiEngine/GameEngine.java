package LepinskiEngine;
import java.util.List;
import java.util.ArrayList;
import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import Navigation.*;


public class GameEngine extends Application{
    final int NUM_TURNS = 40;
    final int NUM_GOLD = 20;
    final int NUM_DIAMOND = 2;
    final int NUM_STONE = 1;
    final int NUM_SLOW = 8;
    final int NUM_DARK = 8;
	
    final String team_name = "Test Team";
    final double DELAY_TIME = 2.0;
    final String maze_file = "example1.maze";

    PlayerSearchingTeam the_team;
    PlayerHidingTeam other_team;
    List<Robot> team_bots;
    Maze the_maze;
    GameState state;
    boolean is_done;
    CommandExecution execution;
    Canvas maze_canvas;
    
    public static void main (String[] args) {
    	launch(args);
    }
    
    //You can Change TestTeam to be another class you create
    public GameEngine(){
	the_team = new OurSearchingTeam();
	other_team = new TestTeam();
	execution = new StandardExecution();
    }
    
    public void start(Stage primaryStage){
        primaryStage.setTitle("Coin Maze");
        Group root = new Group();
        maze_canvas = new Canvas(1200, 800);
 
        root.getChildren().add(maze_canvas);
        primaryStage.setScene(new Scene(root, 1200, 800, Color.WHITE));
        primaryStage.show();

	startGame();

	Timeline twoSecondTimer = new Timeline(new KeyFrame(Duration.seconds(DELAY_TIME), new EventHandler<ActionEvent>() {

		    public void handle(ActionEvent event) {
			nextTurn();
		    }
}));
	twoSecondTimer.setCycleCount(Timeline.INDEFINITE);
	twoSecondTimer.play();
	
    }

    void startGame(){
	RectMaze temp_maze = new RectMazeFromDisk(maze_file);
	the_maze = new Maze(temp_maze);
	is_done = false;

	int x = the_maze.getMaxX();
	int y = the_maze.getMaxY();
	state = new GameState(x, y, NUM_TURNS, 0, NUM_GOLD + NUM_DIAMOND);

	initObstacles(temp_maze);
	initCoins(temp_maze);
	
        team_bots = the_team.chooseRobots(state.clone());

	addRobots(team_bots);

	MazeDisplayGraphics.display(the_maze, maze_canvas);
	MazeDisplayGraphics.showScore(team_name, state, maze_canvas);

    }

    //NOTE: Does NOT yet check to ensure correct number of coins placed
    void initCoins(RectMaze rect_m){
	List<CoinType> the_coins = new ArrayList<CoinType>();
	List<PlaceCoin> placements;
	
	for(int i=0; i< NUM_DIAMOND; i++){
	    the_coins.add(CoinType.Diamond);
	}
	for(int i=0; i<NUM_GOLD; i++){
	    the_coins.add(CoinType.Gold);
	}

	placements = other_team.hideCoins(the_coins, rect_m, state.clone());

	for (PlaceCoin pl : placements){
	    CoinType c = pl.getCoin();
	    int x = pl.getX();
	    int y = pl.getY();
	    List<CoinType> lst_c= the_maze.getLocation(x,y).getCoins();
	    if(lst_c == null){
		lst_c = new ArrayList<CoinType>();
	    }
	    lst_c.add(c);
	    the_maze.getLocation(x,y).setTheCoins(lst_c);
	    } 
    }

    //NOTE: Does NOT yet check to ensure correct number of coins placed
    void initObstacles(RectMaze rect_m){
	List<ObstacleType> the_obs = new ArrayList<ObstacleType>();
	List<PlaceObstacle> placements;
	
	for(int i=0; i< NUM_STONE; i++){
	    the_obs.add(ObstacleType.Stone);
	}
	for(int i=0; i<NUM_DARK; i++){
	    the_obs.add(ObstacleType.Dark);
	}
	for(int i=0; i<NUM_SLOW; i++){
	    the_obs.add(ObstacleType.Slow);
	}
	
	placements = other_team.setObstacles(the_obs, rect_m, state.clone());

	for (PlaceObstacle pl : placements){
	    ObstacleType ob = pl.getObstacle();
	    int x = pl.getX();
	    int y = pl.getY();
	    List<ObstacleType> lst_ob= the_maze.getLocation(x,y).getObstacles();
	    if(lst_ob == null){
		lst_ob = new ArrayList<ObstacleType>();
	    }
	    lst_ob.add(ob);
	    the_maze.getLocation(x,y).setTheObstacles(lst_ob);

	    //Special Case to Block Neighbors of STONE Obstacle
	    if(ob == ObstacleType.Stone){
		stoneAdjustNeighbors(x,y);
	    }
	}
    }

    //This blocks all directions into and out of a STONE obstacle at x,y
    void stoneAdjustNeighbors(int x,int y){
	MazeLocation stone = the_maze.getLocation(x,y);
	MazeLocation north = the_maze.getLocation(x,y-1);
	MazeLocation south = the_maze.getLocation(x,y+1);
	MazeLocation west = the_maze.getLocation(x-1, y);
	MazeLocation east = the_maze.getLocation(x+1, y);

	stone.setTheDirs(new ArrayList<DirType>());
	
	if (north != null){
	    if (north.getDirections() != null){
		north.getDirections().remove(DirType.South);
	    }
	}

	if (south != null){
	    if (south.getDirections() != null){
		south.getDirections().remove(DirType.North);
	    }
	}
	
	if (west != null){
	    if (west.getDirections() != null){
		west.getDirections().remove(DirType.East);
	    }
	}

	if (east != null){
	    if (east.getDirections() != null){
		east.getDirections().remove(DirType.West);
	    }
	}

    }

    
	
    //This transforms MazeLocations into Locations
    //This produces the list that is passed to the players
    List<Location> mazeToLocations(Maze scan_maze){
	List<Location> loc_list = new ArrayList<Location>();
	int max_x = scan_maze.getMaxX();
	int max_y = scan_maze.getMaxY();

	for (int i=0; i<max_x; i++){
	    for(int j=0; j<max_y; j++){
		MazeLocation cur_loc = scan_maze.getLocation(i,j);
		if (cur_loc != null){
		    loc_list.add(new Location(cur_loc));
		}
	    }
	}
	return loc_list;
    }

    void nextTurn(){
	List<Command> team_cmds;
	List<Location> scan_locations;

	if(!is_done){	  
	    scan_locations = mazeToLocations(doTeamScan());
	    MazeDisplayGraphics.display(doTeamScan(), maze_canvas);
	    MazeDisplayGraphics.showScore(team_name, state, maze_canvas);

	    List<Robot> ready_bots = getReadyRobots();
		
	    team_cmds = the_team.requestCommands(scan_locations,ready_bots, state.clone());

	    execution.executeCommands(the_maze, team_cmds, state);
	    
	    if (state.turns_remaining == 0){
		is_done = true;
	    }
	    state.turns_remaining -= 1;
	}
    }

    void addRobots(List<Robot> robots){
	MazeLocation startLoc = the_maze.getTeamStart();
	
	for (Robot bot : robots){
	    MazeRobot new_bot = MazeRobotFactory.makeMazeRobot(bot.getModel(), bot.getID(), startLoc);

	    if (startLoc.getRobots() == null){
		startLoc.setTheRobots(new ArrayList<MazeRobot>());
	    }
	    startLoc.getRobots().add(new_bot);
	}

    }

    Maze doTeamScan(){
	List<MazeRobot> bots = getMazeRobots();
	Maze scan_info = new Maze(the_maze.getMaxX(), the_maze.getMaxY());

	for(MazeRobot r : bots){
	    r.doScan(the_maze, scan_info);
	}
	return scan_info;
    }

    List<MazeRobot> getMazeRobots(){
	int max_x = the_maze.getMaxX();
	int max_y = the_maze.getMaxY();
	List<MazeRobot> team_bots = new ArrayList<MazeRobot>();

	for(int i=0; i<max_x; i++){
	    for(int j=0; j<max_y; j++){
		List<MazeRobot> bots = the_maze.getLocation(i,j).getRobots();
		if(bots != null){
		    for(MazeRobot the_bot: bots){
			team_bots.add(the_bot);
		    }
		}
	    }
	}
	return team_bots;
    }

    List<Robot> getReadyRobots(){
	List<Robot> ready_bots = new ArrayList<Robot>();
	MazeRobot maz_bot;
	ModelType the_model;
	int the_id;

	for(Robot bot : team_bots){
	    the_model = bot.getModel();
	    the_id = bot.getID();
	    maz_bot = the_maze.getRobot(the_id);
	    if(maz_bot.ready == true && maz_bot.getModel()!=ModelType.VisionBot){
		ready_bots.add(new Robot(the_model, the_id));
	    }
	    else{
		maz_bot.ready = true;
	    }
	}
	    
	return ready_bots;
    }

}
	
