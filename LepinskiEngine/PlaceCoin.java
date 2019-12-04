package LepinskiEngine;

// Requests that the robot pick up any coins in its current location

public class PlaceCoin implements PlaceThing{
    private int x;
    private int y;
    private CoinType coin;

    public int getX(){
	return x;
    }

    public int getY(){
	return y;
    }

    public CoinType getCoin(){
	return coin;
    }

    public PlaceCoin(CoinType ctype, int x_val, int y_val){
	x = x_val;
        y = y_val;
        coin = ctype;
    }

}