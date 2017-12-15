/* GOOGLE FOOBAR CHALLENGE */
import java.util.*;
public class L1Q1 {
	/* =======================================================================================================================
	 * LEVEL 1, QUESTION 1
	 * 
	 * Commander Lambda's minions are upset! They're given the worst jobs on the
	 * whole space station, and some of them are starting to complain that even
	 * those worst jobs are being allocated unfairly. If you can fix this problem,
	 * it'll prove your chops to Commander Lambda so you can get promoted!
	 * 
	 * Minions' tasks are assigned by putting their ID numbers into a list, one time
	 * for each day they'll work that task. As shifts are planned well in advance,
	 * the lists for each task will contain up to 99 integers. When a minion
	 * is scheduled for the same task too many times, they'll complain about it
	 * until they're taken off the task completely. Some tasks are worse than others,
	 * so the number of scheduled assignments before a minion will refuse to do
	 * a task varies depending on the task. You figure you can speed things up
	 * by automating the removal of the minions who have been assigned a task 
	 * too many times before they even get a chance to start complaining.
	 * 
	 * Write a function called answer(data, n) that takes in a list of less than
	 * 100 integers and a number n, and returns that same list but with all of the
	 * numbers that occur more than n times removed entirely. The returned list
	 * should retain the same ordering as the original list. For instance,
	 * if the data was [5, 10, 15, 10, 7] and n was 1, answer(data, n) would return
	 * [5, 15, 7] because 10 occurs twice.
	 * ======================================================================================================================= */
	public static int[] answer(int[] data, int n) {
        //base case. if n == 0, return empty list
        if (n == 0) return new int[]{};

        //keeps track of how many times each integer appears
        HashMap<Integer, Integer> countMap = new HashMap<>();
        for (int i = 0; i < data.length; ++i) {
            int num = data[i];
            Integer count = countMap.get(num);
            if (count == null) {
                count = 0;
            }
            countMap.put(num, ++count);
        }
        
        //Figure out the size of the list to be returned.
        int lenOfRetArr = 0;
        for (Integer num : countMap.keySet()) {
            int freq = countMap.get(num);
            if (freq <= n) lenOfRetArr += freq;
        }
        
        //Create the list to be returned and populate with elements that occur <= n times.
        int[] ret = new int[lenOfRetArr];
        int index = 0;
        for (int i = 0; i < data.length; ++i) {
        	if (countMap.get(data[i]) <= n) {
        		ret[index++] = data[i];
        	}
        }
        return ret;
	}
}