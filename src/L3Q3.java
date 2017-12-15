import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
public class L3Q3 {
	/* =======================================================================================================================
	 * LEVEL 3, QUESTION 3. 
	 * 
	 * You're awfully close to destroying the LAMBCHOP doomsday device and freeing Commander Lambda's bunny prisoners, 
	 * but once they're free of the prison blocks, the bunnies are going to need to escape Lambda's space station 
	 * via the escape pods as quickly as possible. Unfortunately, the halls of the space station are a maze 
	 * of corridors and dead ends that will be a deathtrap for the escaping bunnies. Fortunately, Commander 
	 * Lambda has put you in charge of a remodeling project that will give you the opportunity to make things 
	 * a little easier for the bunnies. Unfortunately(again), you can't just remove all obstacles between 
	 * the bunnies and the escape pods - at most you can remove one wall per escape pod path, both to 
	 * maintain structural integrity of the station and to avoid arousing Commander Lambda's suspicions.
	 * 
	 * You have maps of parts of the space station, each starting at a prison exit and ending at the door to 
	 * an escape pod. The map is represented as a matrix of 0sand 1s, where 0s are passable space and 1s are 
	 * impassable walls. The door out of the prison is at the top left (0,0) and the door into an escape 
	 * pod is at the bottom right (w-1,h-1).
	 * 
	 * Write a function answer(map) that generates the length of the shortest path from the prison door to 
	 * the escape pod, where you are allowed to remove one wall as part of your remodeling plans. The path 
	 * length is the total number of nodes you pass through, counting both the entrance and exit nodes. 
	 * The starting and ending positions are always passable (0). The map will always be solvable, 
	 * though you may or may not need to remove a wall. The height and width of the map can be from 
	 * 2 to 20. Moves can only be made in cardinal directions; no diagonal moves are allowed.
	 * ======================================================================================================================= */
	
	/**
	 * Private helper class representing a Cell, its (i,j) location, whether it's been visited, and whether it is an impassable wall.
	 */
	private static class Cell {
		boolean isWall, visited;
		int row, col;
		Cell(int i, int j, boolean isWall) {
			row = i;
			col = j;
			this.isWall = isWall;
			this.visited = false;
		}
	}

	/**
	 * @param maze int[][] array of size w x h, where each Cell has value of 0 or 1 (passable or impassable)
	 * @return shortest path from (0,0) to (w-1, h-1), where w, h = width and height of array,
	 *         where you're allowed to change up to one Cell's value from 1 to 0 (remove an impassable wall)
	 */
	public static int answer(int[][] maze) {
		int ROWS = maze.length;
		int COLS = maze[0].length;
		Cell[][] cellMaze = new Cell[ROWS][COLS];  

		//Representation of the given maze as a 2-D array of Cell objects
		for (int i = 0; i < ROWS; ++i)
			for (int j = 0; j < COLS; ++j)
				cellMaze[i][j] = new Cell(i,j, maze[i][j]==1);


		//Get the distance from the origin (0,0) to every other cell. Uses Dijkstra's algorithm.
		int[][] dFromOrig = new int[ROWS][COLS];
		dFromOrig[0][0] = 1;		//Distance from the origin to itself is 1.
		ArrayDeque<Cell> q = new ArrayDeque<Cell>();  //Initialize a queue consisting of Cell objects.
		q.addLast(cellMaze[0][0]); //begin by adding the origin Cell

		Cell currCell = null;
		
		//Dijkstra's
		while (!q.isEmpty()) {
			currCell = q.removeFirst();
			currCell.visited = true;
			if (currCell.row == ROWS - 1 && currCell.col == COLS - 1) break;	//reached exit.
			if (currCell.isWall) continue;  //If current cell is a wall, cannot get neighbors. Continue to next iteration.
			
			List<Cell> neighbors = getUnvisitedNeighbors(cellMaze, currCell);	//this also grabs unvisited walls.
			for (Cell neighbor : neighbors) {
				dFromOrig[neighbor.row][neighbor.col] = dFromOrig[currCell.row][currCell.col] + 1;
				q.add(neighbor);
			}
		}
		//end while
		
		/* The min. distance from origin to the exit, assuming no change to any of the Cells. */
		int minMovesSoFar = dFromOrig[ROWS-1][COLS-1];

		//Now Get the distance from the exit (bottom right corner) to every other cell
		for (int i = 0; i < ROWS; ++i)
			for (int j = 0; j < COLS; ++j)
				cellMaze[i][j].visited = false;	//re-initialize visited to false

		int[][] dFromExit = new int[ROWS][COLS];	//dFromExit[i] = shortest possible distance from exit to i
		dFromExit[ROWS-1][COLS-1] = 1;				//Distance from the exit to itself is 1.
		q.clear();	//Clear the queue (ArrayDeque)
		q.add(cellMaze[ROWS-1][COLS-1]); //Add the exit Cell to begin.
		currCell = null;
		
		//Dijkstra's (same logic as above)
		while (!q.isEmpty()) {
			currCell = q.removeFirst();
			currCell.visited = true;
			if (currCell.row == 0 && currCell.col == 0) break;
			if (currCell.isWall) continue;

			List<Cell> neighbors = getUnvisitedNeighbors(cellMaze, currCell);
			for (Cell neighbor : neighbors) {
				dFromExit[neighbor.row][neighbor.col] = dFromExit[currCell.row][currCell.col] + 1;
				q.add(neighbor);
			}
		}
		//end while

		//Now iterate thru every wall (with cell value == 1) and see if removing it will lead to fewer moves
		for (int i = 0; i < ROWS; ++i) {
			for (int j = 0; j < COLS; ++j) {
				if (cellMaze[i][j].isWall) {
					int distFromOriginToIJ = dFromOrig[i][j];  //This has been saved via 1st Dijkstra's above
					int distFromExitToIJ = dFromExit[i][j];    //This has been saved via 2nd Dijkstra's above
					
					//Below means it's impossible to get from origin to (i,j), or get from exit to (i,j), so removing
					//this wall won't make a difference. So continue to next iteration
					if (distFromOriginToIJ == 0 || distFromExitToIJ == 0) continue;
					
					//If we get this far, get new distance from origin to exit after removing the wall
					int distFromOriginToExit = distFromOriginToIJ + distFromExitToIJ - 1;
					
					//Update minimum moves from origin to exit if we found a new minimum.
					if (minMovesSoFar == 0) minMovesSoFar = distFromOriginToExit;
					else if (minMovesSoFar > distFromOriginToExit) minMovesSoFar = distFromOriginToExit;
				}
			}
			//end for j
		}
		//end for i
		return minMovesSoFar;
	}

	private static List<Cell> getUnvisitedNeighbors(Cell[][] maze, Cell currCell) {
		int ROWS = maze.length;
		int COLS = maze[0].length;

		LinkedList<Cell> neighbors = new LinkedList<>();

		if (currCell.row > 0) {
			Cell neighbor = maze[currCell.row-1][currCell.col];
			if (!neighbor.visited) neighbors.add(neighbor);
		}
		if (currCell.row < ROWS-1) {
			Cell neighbor = maze[currCell.row+1][currCell.col];
			if (!neighbor.visited) neighbors.add(neighbor);
		}

		if (currCell.col > 0) {
			Cell neighbor = maze[currCell.row][currCell.col-1];
			if (!neighbor.visited) neighbors.add(neighbor);
		}
		if (currCell.col < COLS-1) {
			Cell neighbor = maze[currCell.row][currCell.col+1];
			if (!neighbor.visited) neighbors.add(neighbor);
		}

		return neighbors;
	}
}
