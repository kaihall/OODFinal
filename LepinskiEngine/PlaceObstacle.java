package LepinskiEngine;

// Requests that the robot pick up any coins in its current location

public class PlaceObstacle implements PlaceThing{
    private int x;
    private int y;
    private ObstacleType obstacle;

    public int getX(){
	return x;
    }

    public int getY(){
	return y;
    }

    public ObstacleType getObstacle(){
	return obstacle;
    }

    public PlaceObstacle(ObstacleType ob, int x_val, int y_val){
	x = x_val;
        y = y_val;
        obstacle = ob;
    }

}