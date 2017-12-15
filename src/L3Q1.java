import java.util.*;
public class L3Q1 {
	/* =======================================================================================================================
	 * LEVEL 3, QUESTION 1
	 * 
	 * With her LAMBCHOP doomsday device finished, Commander Lambda is preparing for her debut on the galactic stage - but in order
	 * to make a grand entrance, she needs a grand staircase! As her personal assistant, you've been tasked with figuring out how
	 * to build the best staircase EVER.
	 * 
	 * Lambda has given you an overview of the types of bricks available, plus a budget. You can buy different amounts of the
	 * different types of bricks (for example, 3 little pink bricks, or 5 blue lace bricks). Commander Lambda wants to know
	 * how many different types of staircases can be built with each amount of bricks, so she can pick the one with the most options.
	 * Each type of staircase should consist of 2 or more steps.  No two steps are allowed to be at the same height - each step
	 * must be lower than the previous one. All steps must contain at least one brick. A step's height is classified as the total
	 * amount of bricks that make up that step.
	 * 
	 * For example, when N = 3, you have only 1 choice of how to build the staircase, with the first step having a height of 2 and
	 * the second step having a height of 1: (# indicates a brick)
	 * #
	 * ##
	 * 21
	 * 
	 * When N = 4, you still only have 1 staircase choice:
	 * #
	 * #
	 * ##
	 * 31
	 * 
	 * But when N = 5, there are two ways you can build a staircase from the given bricks.
	 * 
	 * The two staircases can have heights (4, 1) or (3, 2), as shown below:
	 * #
	 * #
	 * #
	 * ##
	 * 41
	 * 
	 * #
	 * ##
	 * ##
	 * 32
	 * 
	 * Write a function called answer(n) that takes a positive integer n and returns the number of different staircases that can
	 * be built from exactly n bricks. n will always be at least 3 (so you can have a staircase at all), but no more than 200,
	 * because Commander Lambda's not made of money!
	 * ======================================================================================================================= */
	public static int answer(int n) {
		//Assume that 3 <= n <= 200

		/* Let's try recursion. First find out the max. WIDTH (no. of horizontal bricks) that the BOTTOM layer can have:
		 * For example if the no. of bricks = n = 6, 3 is the most no. of bricks for the bottom layer.
		 * (the upper layer will then have 2, the layer above that will have 1 brick)
		 * #
		 * ##
		 * ###
		 * 
		 * Another example: If n = 10, 4 is the most no. of bricks.
		 * #
		 * ##
		 * ###
		 * ####
		 * 
		 * There is a formula to figure this out:
		 * x * (x+1) / 2 = n (imagine taking the area of a rectangle with bottom length x and height x+1,
		 * then dividing it diagonally by half. This results in a triangular staircase consisting of n steps.
		 * Thus x is the most no. of bricks for the bottom layer.)
		 *
		 * Solving for x...
		 * (x^2 + x)/2 = n
		 * x^2 + x = 2n
		 * x^2+x-2n = 0
		 *
		 * -1 +- sqrt(1 - 4*1*(-2n))
		 * ------------------------- = x
		 *            2
		 * -1 +- sqrt(1 + 8n)
		 * ------------------ = x
		 *          2
		 *
		 * Discarding negative answer....
		 * -1 + sqrt(1+8n)
		 * --------------- = x (round down if x turns out not to be an integer)
		 *        2
		 *
		 * Let's check if our formula works. For example, if n = 9:
		 * -1 + sqrt(1+8*9)
		 * --------------- = 3.77 => round down => x = 3.
		 *        2
		 *
		 * Let's check one more time. For n = 3:
		 * [-1 + sqrt(1 + 8*3)]/2 = 2. Thus x = 2. Makes sense.
		 *
		 *  */
		//Max. no. of bricks making up the bottom layer, pursuant to above formula.
		int bottomBricks = (int)Math.floor((-1 + Math.sqrt(1 + 8*n))/2.0);

		/* Let cache[n][l] = The total no. of possible different ways to build the upper portion (upper layers) of a staircase
		 * with n bricks remaining, given that the immediate lower layer used l bricks. */
		int[][] cache = new int[n+1][bottomBricks+1];
		for (int i = 0; i < cache.length; ++i) {
			Arrays.fill(cache[i], -1); //initialize as -1
		}
		
		int sum = 0;
		
		/* From the instructions we know the bottom step must have at least 2 bricks. So begin at bottomSteps
		 * (max. no. of bricks for bottom layer) and decrement until 2 */
		while (bottomBricks >= 2) {
			sum += numOfPossibleStairCases(n-bottomBricks, bottomBricks, cache); //recursive helper function
			bottomBricks--;
		}
		return sum;
	}

	/**
	 * Recursive function to figure out number of possible staircases (pertaining to the upper layers) that can be built
	 * with n remaining bricks, given that the immediate lower layer used exactly lastUsedNumBricks bricks.
	 * @param n remaining no. of bricks
	 * @param lastUsedNumBricks how many bricks were used for the lower layer
	 * @param cache a 2D array for caching cache[n][l] = The total no. of possible different ways to
	 *        build the upper portion (upper layers) of a staircase with n bricks remaining, given that
	 *        the immediate lower layer used l bricks.
	 * @return cache[n][l], where l = lastUsedNumBricks.
	 */
	private static int numOfPossibleStairCases(int n, int lastUsedNumBricks, int[][] cache) {
		if (n < 0) return 0;	//base case. This means we used more bricks than we have available. Impossible.
		if (cache[n][lastUsedNumBricks] != -1) return cache[n][lastUsedNumBricks];	//base case. If this value was previously cached, return value
		if (n == 0) {	//base case. no more bricks. in this case the last no. of bricks used MUST equal 1. Otherwise not valid staircase
			cache[n][lastUsedNumBricks] = lastUsedNumBricks == 1 ? 1 : 0;
			return cache[n][lastUsedNumBricks];
		}

		int bricksUsedForThisLevel = lastUsedNumBricks;
		int sum = 0;
		//every layer can use either bricksUsedForThisLevel or bricksUsedForThisLevel - 1, 
		//with the caveat that each level must use at least 1 brick. So go thru both cases and recurse.
		sum += numOfPossibleStairCases(n - bricksUsedForThisLevel, bricksUsedForThisLevel, cache);
		bricksUsedForThisLevel--;
		if (bricksUsedForThisLevel >= 1)
			sum += numOfPossibleStairCases(n - bricksUsedForThisLevel, bricksUsedForThisLevel, cache);

		cache[n][lastUsedNumBricks] = sum;
		return sum;
	}
}
