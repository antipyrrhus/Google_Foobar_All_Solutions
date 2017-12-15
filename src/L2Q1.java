public class L2Q1 {
	/* =======================================================================================================================
	 * LEVEL 2, QUESTION 1
	 * 
	 * As Commander Lambda's personal assistant, you've been assigned the task of
	 * configuring the LAMBCHOP doomsday device's axial orientation gears. It should be
	 * pretty simple - just add gears to create the appropriate rotation ratio.
	 * But the problem is, due to the layout of the LAMBCHOP and the complicated
	 * system of beams and pipes supporting it, the pegs that will support the gears
	 * are fixed in place.
	 * 
	 * The LAMBCHOP's engineers have given you lists identifying the placement of groups
	 * of pegs along various support beams. You need to place a gear on each peg
	 * (otherwise the gears will collide with unoccupied pegs). The engineers have
	 * plenty of gears in all different sizes stocked up, so you can choose gears of
	 * any size, from a radius of 1 on up. Your goal is to build a system where the
	 * last gear rotates at twice the rate (in revolutions per minute, or rpm) of the
	 * first gear, no matter the direction. Each gear (except the last) touches
	 * and turns the gear on the next peg to the right.
	 *  
	 * Given a list of distinct positive integers named pegs representing the location
	 * of each peg along the support beam, write a function answer(pegs) which, if
	 * there is a solution, returns a list of two positive integers a and b representing
	 * the numerator and denominator of the first gear's radius in its simplest form
	 * in order to achieve the goal above, such that radius = a/b. The ratio a/b should
	 * be greater than or equal to 1. Not all support configurations will necessarily be
	 * capable of creating the proper rotation ratio so if the task is impossible, 
	 * the function should return the list [-1, -1].
	 * 
	 * For example, if the pegs are placed at [4, 30, 50], then the first gear could
	 * have a radius of 12, the second gear could have a radius of 14, and the last
	 * one a radius of 6. Thus, the last gear would rotate twice as fast as the first one.
	 * In this case, pegs would be [4, 30, 50] and answer(pegs) should return [12, 1].
	 * 
	 * The list pegs will be given sorted in ascending order and will contain at least
	 * 2 and no more than 20 distinct positive integers, all between 1 and 10000 inclusive.
	 * ======================================================================================================================= */
	public static int[] answer(int[] pegs) {
		int numPegs = pegs.length;

		//Try binary search to find the right radius size for the first peg.
		//First note the min. and max. possible radius of the first peg.
		int minOrigRadius = 1;  //as given in instructions, radius >= 1.
		int maxOrigRadius = pegs[1] - pegs[0] - 1; //Radius can't be larger than this
		if (maxOrigRadius < minOrigRadius) return new int[]{-1,-1};

		int[] origRadiusArr;  //Radius representation of first peg -- min and max.
		int[] lastRadiusArr;  //Corresponding radius representation of last peg

		origRadiusArr = new int[]{minOrigRadius, maxOrigRadius};
		lastRadiusArr = new int[2];

		/* Find out what the last peg's radius would be if we were to use
		 * the min. and max. possible radius value for the first peg */
		for (int i = 0; i < origRadiusArr.length; ++i) {
			int radius = origRadiusArr[i];
			for (int j = 0; j < numPegs-1; ++j) {
				/* Find out the radius of pegs[j+1] given the radius of
				 * the previous peg */
				int abutment = pegs[j] + radius;
				//Note this re-computed radius could be negative,
				//But we won't worry about that for now. Later one
				//we check for this.
				radius = pegs[j+1] - abutment;
				if (j+1 == numPegs - 1) { //If we got to the last peg...
					/* If the last peg's radius is twice original peg's, we found the answer */
					if (radius * 2 == origRadiusArr[i]) return new int[]{(int)Math.round(origRadiusArr[i]),1};
					lastRadiusArr[i] = radius;
				}
			}
		}

		//If the last radiuses * 2 are both less than or both greater than the corresponding orig Radiuses, impossible
		if (origRadiusArr[0] < 2*lastRadiusArr[0] && origRadiusArr[1] < 2*lastRadiusArr[1] ||
			origRadiusArr[0] > 2*lastRadiusArr[0] && origRadiusArr[1] > 2*lastRadiusArr[1])
			return new int[]{-1,-1};

		//If we get this far, there may still be an answer.
		/* KEY INSIGHTS:
		 * - The first and last radiuses are directly proportional if
		 *   lastRadiusArr[1] > lastRadiusArr[0]. Otherwise, inversely proportional.
		 *   Another (simpler) way to find this out is to realize that
		 *   if the total number of pegs is even (say there are only 2 pegs),
		 *   then it's obvious that the last peg's radius will be inversely
		 *   related to the first peg's: as the first peg's radius increases,
		 *   the last peg will decrease in radius, and vice versa.
		 *   And of course if the total no. of pegs is odd, directly proportional. 
		 * - The correct radius for the first peg, if a solution exists,
		 *   MUST be able to be represented as a fraction with 3 as the denominator.
		 *   To see this, realize that for the last peg to be twice the radius
		 *   as the first, the sum of the radii of both pegs must be able to
		 *   be divided by 3. 
		 * - So in retrospect, a simple solution to this problem would've been
		 *   to simply iterate from the min. to max. possible radius value
		 *   for the first peg, incrementing by 1/3 each time, and see if
		 *   the last peg's radius is twice the first. 
		 * - Perhaps an even easier solution would be to first find out
		 *   whether the radii are inversely or directly proportional
		 *   between the first and last peg (depending on the total no. of pegs,
		 *   as stated above), then mathematically derive a radius for the
		 *   first peg that would comprise the solution, then see if the sum
		 *   of first + last peg's radii are divisible by 3, AND if
		 *   all the intermediate pegs would have a radius of >= 1. */
		
		/* But in this (in retrospect, unnecessarily complicated) solution,
		 * I used binary search to find the radius value for the first peg. */
		if (lastRadiusArr[1] > lastRadiusArr[0]) {
			return findNewRadius(origRadiusArr, lastRadiusArr, pegs, true);
		} else {
			return findNewRadius(origRadiusArr, lastRadiusArr, pegs, false);
		}
	}
	
	/**
	 * Helper method to find the solution radius value for the first peg, if one exists.
	 * @param origRadiusArr
	 * @param lastRadiusArr
	 * @param pegs
	 * @param directProportional
	 * @return solution if one exists, or [-1, -1] otherwise.
	 */
	private static int[] findNewRadius(int[] origRadiusArr, int[] lastRadiusArr, int[] pegs, boolean directProportional) {
		int min = origRadiusArr[0];
		int max = origRadiusArr[1];
		int mid = (min+max) / 2;
		int diff = mid - origRadiusArr[0];
		int lastRadius = directProportional ? lastRadiusArr[0] + diff : lastRadiusArr[0] - diff;
		while (min + 1< max) {
			if (mid == lastRadius * 2) {
				if (isValid((double)mid, origRadiusArr, directProportional, lastRadiusArr, pegs)) return new int[]{mid,1};
				else return new int[]{-1,-1};
			}
			if (mid < lastRadius * 2 && !directProportional || mid > lastRadius * 2 && directProportional) {
				min = mid;
				mid = (min+max)/2;
			} else {
				max = mid;
				mid = (min+max)/2;
			}
			diff = mid - origRadiusArr[0];
			lastRadius = directProportional ? lastRadiusArr[0] + diff : lastRadiusArr[0] - diff;
		}
		//If we get here, it means min = max-1, and that mid = either min or max.
		double midDbl = mid + (2/3.0);
		if (isValid(midDbl, origRadiusArr, directProportional, lastRadiusArr, pegs)) return new int[]{mid*3+2,3};
		midDbl = mid + (1/3.0);
		if (isValid(midDbl, origRadiusArr, directProportional, lastRadiusArr, pegs)) return new int[]{mid*3+1,3};
		midDbl = mid - (1/3.0);
		if (isValid(midDbl, origRadiusArr, directProportional, lastRadiusArr, pegs)) return new int[]{mid*3-1,3};
		midDbl = mid - (2/3.0);
		if (isValid(midDbl, origRadiusArr, directProportional, lastRadiusArr, pegs)) return new int[]{mid*3-2,3};
		return new int[]{-1, -1};
	}

	/**
	 * Helper method to see if a radius assignment is valid.
	 * @param origRadius
	 * @param origRadiusArr
	 * @param directProportional
	 * @param lastRadiusArr
	 * @param pegs
	 * @return
	 */
	private static boolean isValid(double origRadius, int[] origRadiusArr, boolean directProportional, int[] lastRadiusArr, int[] pegs) {
		double diffDbl = origRadius - origRadiusArr[0];
		double lastRadiusDbl = directProportional ? lastRadiusArr[0] + diffDbl : lastRadiusArr[0] - diffDbl;
		if (lastRadiusDbl < 1 || Math.abs(lastRadiusDbl * 2 - origRadius) >= 0.0000000001) return false;
		double radius = origRadius;
		for (int j = 0; j < pegs.length-1; ++j) {
			double abutment = pegs[j] + radius;
			radius = pegs[j+1] - abutment;
			if (radius < 1) return false;
		}
		return true;
	}
}
