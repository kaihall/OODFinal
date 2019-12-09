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


public class GameEngine extends Application{
    final int NUM_TURNS = 48;
    final int NUM_GOLD = 24;
    final int NUM_DIAMOND = 2;
    final int NUM_STONE = 1;
    final int NUM_SLOW = 12;
    final int NUM_DARK = 12;
	
    final String team_name = "Test Team";
    final double DELAY_TIME = 2.0;
    final String maze_file = "example2.maze";

    PlayerSearchingTeam the_team;
    PlayerHidingTeam other_team;
    List<Robot> team_bots;
    Maze the_maze;
    GameState state;
    boolean is_done;
    CommandExecution execution;
    Canvas maze_canvas;

    //You can Change TestTeam to be another class you create
    public GameEngine(){
	the_team = new TestTeam();
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
	checkBots(team_bots);

	addRobots(team_bots);

	MazeDisplayGraphics.display(the_maze, maze_canvas);
	MazeDisplayGraphics.showScore(team_name, state, maze_canvas);

    }

    //Make sure there are at most FIVE robots
    //Make sure there are at most TWO robots of any ModelType
    void checkBots(List<Robot> bots){
	if (bots.size() > 5){
	    System.err.println("!!! choose robots ERROR :: TOO MANY ROBOTS SELECTED");
	    System.exit(1);
	}

	for (ModelType model : ModelType.values()){
	    int count = 0;
	    for (Robot r : bots){
		if(r.getModel() == model){
		    count = count + 1;
		}
	    }
	    if (count > 2){
		System.err.println("!!! choose robots ERROR :: ONLY TWO ROBOTS OF EACH MODEL ALLOWED");
		System.exit(1);
	    }
	}
    }

    //NOTE: Now checks to ensure correct number of coins placed
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
	checkCoinPlace(placements);
	
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

	    checkStoneCoin(x,y,the_maze);
	    } 
    }

    //Make sure no Coin is placed inside a STONE obstacle
    void checkStoneCoin(int x, int y, Maze the_maze){
	MazeLocation loc = the_maze.getLocation(x,y);
	if (loc.getObstacles() != null){
	    for (ObstacleType ob : loc.getObstacles()){
		if(ob == ObstacleType.Stone){
		    System.err.println("!!! hide coin ERROR :: DO NOT HIDE A COIN INSIDE A STONE TRAP");
		    System.exit(1);
		}
	    }
	}
    }

    //Make sure exactly the right number of coins are placed
    void checkCoinPlace(List<PlaceCoin> placements){
	int g_count = 0;
	int d_count = 0;
	
	for (PlaceCoin pl : placements){
	    if(pl.getCoin() == CoinType.Gold){
		g_count = g_count + 1;
	    }
	    if(pl.getCoin() == CoinType.Diamond){
		d_count = d_count + 1;
	    }
	}

	if (g_count != NUM_GOLD){
	    System.err.println("!!! hide coin ERROR :: WRONG NUMBER OF GOLD COINS PLACED");
	    System.exit(1);
	}
	if (d_count !=NUM_DIAMOND){
	    System.err.println("!!! hide coin ERROR :: WRONG NUMBER OF DIAMOND COINS PLACED");
	    System.exit(1);
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
	checkObstaclePlace(placements);

	
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

    //Make sure not too many obstacles are placed
    void checkObstaclePlace(List<PlaceObstacle> placements){
	int st_count = 0;
	int d_count = 0;
	int sl_count = 0;
	
	for (PlaceObstacle pl : placements){
	    if(pl.getObstacle() == ObstacleType.Stone){
		st_count = st_count + 1;
	    }
	    if(pl.getObstacle() == ObstacleType.Dark){
		d_count = d_count + 1;
	    }
	    if(pl.getObstacle() == ObstacleType.Slow){
		sl_count = sl_count + 1;
	    }
	}

	if (st_count > NUM_STONE){
	    System.err.println("!!! place obstacle ERROR :: TOO MANY STONE OBSTACLES PLACED");
	    System.exit(1);
	}
	if (d_count > NUM_DARK){
	    System.err.println("!!! place obstacle ERROR :: TOO MANY DARK OBSTACLES PLACED"); 
	    System.exit(1);
	}
	if (sl_count > NUM_SLOW){
	    System.err.println("!!! place obstacle ERROR :: TOO MANY SLOW OBSTACLES PLACED"); 
	    System.exit(1);
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
	
