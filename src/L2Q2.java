public class L2Q2 {
	/* =======================================================================================================================
	 * LEVEL 2, QUESTION 2
	 * 
	 * Keeping track of Commander Lambda's many bunny prisoners is starting
	 * to get tricky. You've been tasked wiht writing a program to match
	 * bunny prisoner IDs to cell locations.
	 * 
	 * The LAMBCHOP doomsday device takes up much of the interior of Commander
	 * Lambda's space station, and as a result the prison blocks have an
	 * unusual layout. They are stacked in a triangular shape, and the bunny
	 * prisoners are given numerical IDs starting from the corner, as follows:
	 * | 7
	 * | 4 8
	 * | 2 5 9
	 * | 1 3 6 10
	 * 
	 * Each cell can be represented as points (x,y), with x being the
	 * distance from the vertical wall, and y being the height from the ground.
	 * 
	 * For example, the bunny prisoner at (1,1) has ID 1, the bunny prisoner at
	 * (3,2) has ID 9, and the bunny prisoner at (2,3) has ID 8. This pattern
	 * of numbering continues indefinitely (Commander Lambda has been
	 * taking a LOT of prisoners).
	 * 
	 * Write a function answer(x,y) which returns the prisoner ID of the
	 * bunny at location (x,y). Each value of x and y will be at least 1
	 * and no greater than 100,000. Since the prisoner ID can be very large,
	 * return your answer as a string representation of the number.
	 * ======================================================================================================================= */
	public static String answer(int x, int y) {
		long diagLayer = x+y-1;
		long currLayer = 1;
		long increment = 1;
		
		/* KEY INSIGHT:
		 * First realize the following:
		 * 
		 * answer(1, 3) + 2 = answer(2, 2) + 1 = answer(3, 1) = 6
		 * answer(1, 4) + 3 = answer(2, 3) + 2 = answer(3, 2) + 1 = answer(4, 1) = 10
		 * 
		 * And so on. Generally:
		 * 
		 * answer(x,y) + 1 = answer(x+1, y-1), where obviously x + y = (x+1) + (y-1).
		 * 
		 * In other words, as long as the sum of the given row and col values
		 * are equal, then the ID for a given row, col (x,y) is one less than
		 * the ID for (x+1, y-1).
		 * 
		 * So, we can derive the solution this way:
		 * Add up the x+y. Then get the answer for (1, (x+y)-1).
		 * Then increment that answer by x-1.
		 * 
		 * How do we get the answer for (1, (x+y)-1)?
		 * Easy. This is fibonacci sequence. 1, 2, 4, 7, 11, ...
		 * So just increment by 1, then by 2, then by 3, etc.
		 * */
		for (int i = 1; i < diagLayer; ++i) {
			currLayer += (increment++);
		}
		currLayer += (x-1);
		return currLayer + "";
	}
}