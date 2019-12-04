package LepinskiEngine;

// There are Seven models of Robot
// It is up to the coin hunting team what 

// CoinBot can pick up any type of Coin and can see one square in every direction 

// ScoutBot cannot pick up any Coin
// ScoutBot can see all squares within a distance of three from the ScoutBot 

// FastBot can only pick up Gold Coins. 
// FastBot can see one square in every direction.
// FastBot can move two squares every turn. (Only FastBot can use CommandFastMove)

// GhostBot can pick up only Diamond Coins (NOT Gold coins). 
// GhostBot can see one square in every direction 
// GhostBot can move through walls and STONE obstacles. 
// GhostBot is not limited by the available directions at a location

// VisionBot cannot pick up coins. 
// VisionBot cannot move. 
// VisionBot can see every Location in the Maze that contains a Coin 
//    ... (walls and distance do not affect VisionBot's ability to see Coin Locations)

// EscortBot cannot pick up coins. 
// EscortBot can see two squares in every direction
// SLOW obstacles in the same Location as EscortBot have no affect on any robot
// SLOW obstacles in a Location adjacent to EscortBot have no affect on any robot

// BasicBot can pick up Gold Coins. 
// BasicBot can see three squares, but cannot see through walls. 
// BasicBot is the only robot whose vision is limited by walls

public enum ModelType {CoinBot, ScoutBot, FastBot, BasicBot, GhostBot, VisionBot, EscortBot}