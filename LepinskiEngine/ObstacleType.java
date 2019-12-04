package LepinskiEngine;

// There are three types of obstacles. 
// An Obstacle can occupy any location in the Maze.

// STONE turns a square into solid stone and blocks all movement 
// When a STONE obstacle is place, that square is disconnected from the rest of the Maze
// Placing a STONE obstacle causes the game engine to change the available directions that can be moved from neighboring locations 
// That is, the Location above (North of) the STONE obstacle is changed so that you can no longer move South.

// DARK prevents robots from seeing out of this space 
// A robot standing in a DARK location can only see the DARK location and nothing else 
// A robot standing near a DARK Location may see the DARK location, but can't see past the DARK location 
// That is, DARK makes it hard to see things on the other side of the DARK location

// SLOW causes robots entering the location to skip their next turn 
// Robots can enter the SLOW location without problem, 
// but they are then not able to execute a command on the next turn 
// This obstacle simulates sand, vegetation, or other obstacles that make it difficult to pass through the location

public enum ObstacleType {Dark, Stone, Slow}