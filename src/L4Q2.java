import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
public class L4Q2 {
	/* =======================================================================================================================
	 * LEVEL 4, QUESTION 2
	 * You and your rescued bunny prisoners need to get out of this collapsing death trap of a space station - and fast! 
	 * Unfortunately, some of the bunnies have been weakened by their long imprisonment and can't run very fast. 
	 * Their friends are trying to help them, but this escape would go a lot faster if you also pitched in. 
	 * The defensive bulkhead doors have begun to close, and if you don't make it through in time, you'll be 
	 * trapped! You need to grab as many bunnies as you can and get through the bulkheads before they close.
	 * 
	 * The time it takes to move from your starting point to all of the bunnies and to the bulkhead will be given 
	 * to you in a square matrix of integers. Each row will tell you the time it takes to get to the start, 
	 * first bunny, second bunny, ..., last bunny, and the bulkhead in that order. The order of the rows 
	 * follows the same pattern (start, each bunny, bulkhead). The bunnies can jump into your arms, so 
	 * picking them up is instantaneous, and arriving at the bulkhead at the same time as it seals still 
	 * allows for a successful, if dramatic, escape. (Don't worry, any bunnies you don't pick up will be 
	 * able to escape with you since they no longer have to carry the ones you did pick up.) You can 
	 * revisit different spots if you wish, and moving to the bulkhead doesn't mean you have to immediately 
	 * leave - you can move to and from the bulkhead to pick up additional bunnies if time permits.
	 * 
	 * In addition to spending time traveling between bunnies, some paths interact with the space station's 
	 * security checkpoints and add time back to the clock. Adding time to the clock will delay the closing 
	 * of the bulkhead doors, and if the time goes back up to 0 or a positive number after the doors have 
	 * already closed, it triggers the bulkhead to reopen. Therefore, it might be possible to walk in a 
	 * circle and keep gaining time: that is, each time a path is traversed, the same amount of time is 
	 * used or added.
	 * 
	 * Write a function of the form answer(times, time_limit) to calculate the most bunnies you can 
	 * pick up and which bunnies they are, while still escaping through the bulkhead before the doors 
	 * close for good. If there are multiple sets of bunnies of the same size, return the set of 
	 * bunnies with the lowest prisoner IDs (as indexes) in sorted order. The bunnies are represented 
	 * as a sorted list by prisoner ID, with the first bunny being 0. There are at most 5 bunnies, 
	 * and time_limit is a non-negative integer that is at most 999.
	 * 
	 * For instance, in the case of
	 * [
	 *     [0, 2, 2, 2, -1],  # 0 = Start
	 *     [9, 0, 2, 2, -1],  # 1 = Bunny 0
	 *     [9, 3, 0, 2, -1],  # 2 = Bunny 1
	 *     [9, 3, 2, 0, -1],  # 3 = Bunny 2
	 *     [9, 3, 2, 2,  0],  # 4 = Bulkhead
	 * ]
	 * 
	 * and a time limit of 1, the five inner array rows designate the starting point, 
	 * bunny 0, bunny 1, bunny 2, and the bulkhead door exit respectively. You could take the path:
	 * 
	 * Start End Delta Time Status
	 * -   0     -    1 Bulkhead initially open
	 * 0   4    -1    2
	 * 4   2     2    0
	 * 2   4    -1    1
	 * 4   3     2   -1 Bulkhead closes
	 * 3   4    -1    0 Bulkhead reopens; you and the bunnies exit
	 * 
	 * With this solution, you would pick up bunnies 1 and 2. This is the best 
	 * combination for this space station hallway, so the answer is [1, 2].
	 * ======================================================================================================================= */
	/**
	 * Representation of a directed graph
	 */
	static class DirectedGraph {
		
		/**
		 * Inner class. Representation of a Node within the directed graph
		 */
		class Node {
			int index;
			boolean visited;
			int[] travelTimeArr;		//travelTimeArr[i] = time it takes to travel from this node to node i

			Node(int index, int[] timeArr) {
				this.index = index;
				this.visited = false;
				this.travelTimeArr = timeArr;
			}
			//end constructor Node

			@Override
			public String toString() {
				return index+"";
			}
		}
		//end class Node

		int numNodes;
		Node[] nodeArr;
		int K;				//timeLimit
		
		/**
		 * Constructor
		 * @param timeArr timeArr[i][j] = time it takes to get from Node i to Node j
		 * @param timeLimit total time limit allotted
		 */
		DirectedGraph(int[][] timeArr, int timeLimit) {
			this.K = timeLimit;
			this.numNodes = timeArr.length;
			this.nodeArr = new Node[this.numNodes];
			
			/* Create Nodes with the given index i and 
			 * timeArr[i] = array of integers indicating the time it takes to get 
			 * from Node i to any other Node */
			for (int i = 0; i < numNodes; ++i) {
				nodeArr[i] = new Node(i, timeArr[i]);
			}
		}
		//end constructor DirectedGraph

		Node getNode(int index) {
			return this.nodeArr[index];
		}

		@Override
		public String toString() {
			return Arrays.toString(nodeArr);
		}
	}
	//end class DirectedGraph

	public static int[] answer(int[][] timeArr, int timeLimit) {
		boolean debugOn = true;

		/* Try directed graph representation! Remember that there are at most 5 bunnies, so the total no. of nodes
		 * will be 5 + 1 (starting node) + 1 (exit node) = 7.
		 * Also, every node is connected to every other node. So this is a directed Clique, which has at most
		 * (7 Choose 2) = 7(6) = 42 different pairs of nodes. Since each pair (a,b)of nodes has
		 * two directed edges linking them (a->b and a<-b), that means we have at most 42 * 2 = 84 edges.
		 * This is doable...
		 * ====================================================================================================
		 * FURTHER INSIGHT: You want to get from the starting node to exit node within the given timeLimit, AND
		 * you want to pick up as many bunnies (AKA visit as many nodes) as possible!
		 *
		 * Each directed edge linking a pair of nodes has a weight w indicating the amount of time that will be spent to
		 * traverse that edge. Recall that w could be negative -- in this case you EARN time by traversing
		 * thru that edge! It's also possible that the graph could contain negative cycles -- if so, you can earn INFINITE time!
		 * This means you can rescue all the bunnies automatically. My solution will want to look for this base case maybe?
		 *
		 * OK so how to solve this? Well you can't just use shortest-path algorithm like Bellman-Ford because the point isn't
		 * to minimize time, the point is to maximize number of visited nodes as long as the total time spent <= timeLimit.
		 *
		 * Also remember this graph isn't acyclic (since it's a clique). So you could reach the end node and still visit other nodes.
		 * Also, it's OK to visit nodes more than once. My solution will have to keep these in mind.....
		 * 
		 * Also it's OK to "run out of time" during the traversal as long as it's possible to "gain time" by traversing negative-weight edges,
		 * provided you can ultimately reach the end node in timeLimit or less. (for this to be true, the graph would need to contain
		 * negative cycles, which means you can earn infinite time and you can automatically rescue all bunnies)
		 *
		 * Maybe run Floyd-Warshall once to get all-pairs shortest paths? Might be helpful...
		 * */
		//Directed graph representation of the problem
		DirectedGraph g = new DirectedGraph(timeArr, timeLimit);
		
		if (debugOn) {
//			System.out.println("Directed Graph consists of the following nodes: " + g);
//			System.out.println("Each node has the following travel time:");
//			for (DirectedGraph.Node n : g.nodeArr) {
//				System.out.println(Arrays.toString(n.travelTimeArr));
//			}
		}

		//Try pre-processing with all-pair shortest paths algorithm
		//SP[i][j][k] = minimum time to travel from Node i to j while traveling through any
		//(or none, or all) of the first k Nodes as an intermediate stop.
		int[][][] SP = new int[g.numNodes][g.numNodes][g.numNodes+1];

		//P[i][j][k] = the actual minimum-cost path (represented as an ArrayList of Nodes)
		//from i to j while traveling thru any of the first k nodes as intermediate stop.
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[][][] P = (ArrayList<Integer>[][][])new ArrayList[g.numNodes][g.numNodes][g.numNodes+1];

		//base case: SP[i][i][k] = 0 for all i and all k. Travel time from a node i to itself takes 0 time no matter what.
		//base case: SP[i][j][0] = edgeCost(i to j). This needs to be a direct path from i to j since we're not travelling
		//           THRU any node as an intermediate stop.
		//base case: SP[i][j][k] = initialized to infinity for all k > 0 and all i != j
		//Also set P[i][i][k] etc. accordingly to the base cases above
		for (int i = 0; i < g.numNodes; ++i) {
			for (int j = 0; j < g.numNodes; ++j) {
				for (int k = 0; k <= g.numNodes; ++k) {
					SP[i][j][k] = (i == j ? 0 : k == 0 ? g.nodeArr[i].travelTimeArr[j] : Integer.MAX_VALUE);
					ArrayList<Integer> list = new ArrayList<>();
					if (i == j) {
						list.add(i);
					} else if (k == 0) {
						list.add(i);
						list.add(j);
					} else {
						//do nothing since no path from i to j during this initialization step
					}
					P[i][j][k] = list;  //Set the initial path from Node i to j in the form of ArrayList of Nodes 
//					if (debugOn) System.out.printf("SP[%d][%d][%d] = %d\nP[%d][%d][%d] = %s\n\n",
//							i,j,k, SP[i][j][k],
//							i,j,k, P[i][j][k]);
				}
			}
		}
		//end for i,j,k


		//All-pair shortest path algorithm (with the actual solution paths as bonus)
		for (int k = 1; k <= g.numNodes; ++k) {
			for (int i = 0; i < g.numNodes; ++i) {
				for (int j = 0; j < g.numNodes; ++j) {
					//SP[i][j][k] = SP[i][j][k-1] OR SP[i][k][k-1] + SP[k][j][k-1], whichever is smaller
					//Got to watch out for overflow, due to Integer.MAX_VALUE
					if (SP[i][k-1][k-1] == Integer.MAX_VALUE || SP[k-1][j][k-1] == Integer.MAX_VALUE) {
						SP[i][j][k] = SP[i][j][k-1];
						P[i][j][k] = P[i][j][k-1];
					} else {
						int pathLength = SP[i][k-1][k-1] + SP[k-1][j][k-1];
						//NOTE: if the currently saved minimum path length SP[i][j][k-1] EQUALS the new (longer) path length,
						//We will prefer the longer path (this way we have chance of saving more bunnies)!
						if (SP[i][j][k-1] >= pathLength) {
							SP[i][j][k] = pathLength;

							//Concatenate the lists together, except we leave out the first element of the rightHalf list (to avoid listing the same node twice)
							ArrayList<Integer> leftHalf = P[i][k-1][k-1];
							ArrayList<Integer> rightHalf = P[k-1][j][k-1];
							ArrayList<Integer> concatenatedList = new ArrayList<>();
							concatenatedList.addAll(leftHalf);
							concatenatedList.addAll(rightHalf);
							concatenatedList.remove(leftHalf.size());	//remove the first element of rightHalf to avoid duplicate
							P[i][j][k] = concatenatedList;
						//Otherwise, just set SP[i][j][k] = SP[i][j][k-1] (the previously saved value).
						//Ditto for the actual path P[i][j][k].
						} else {
							SP[i][j][k] = SP[i][j][k-1];
							P[i][j][k] = P[i][j][k-1];
						}
						//end if/else
					}
					//end if/else
				}
				//end for j
			}
			//end for i
		}
		//end for k


		//Testing
		if (debugOn) {
//			System.out.println("Printing all-pair shortest paths...");
//			for (int i = 0; i < g.numNodes; ++i) {
//				for (int j = 0; j < g.numNodes; ++j) {
//					System.out.printf("SP[%d][%d][%d] = %d\nP[%d][%d][%d] = %s\n\n",
//							i, j, g.numNodes, SP[i][j][g.numNodes],
//							i, j, g.numNodes, P[i][j][g.numNodes]);
//				}
//			}
		}

		//Now check to see if negative cycle exists, and if so we know we can save all the bunnies
		for (int i = 0; i < g.numNodes; ++i) {
//			if (debugOn) System.out.printf("SP[%d][%d][%d] = %d\n", i,i,g.numNodes,SP[i][i][g.numNodes]);
			if (SP[i][i][g.numNodes] < 0) {
//				if (debugOn) System.out.println("Graph contains a negative cycle!");
				//in this case, we can earn infinite time, meaning we can pick up all the bunnies!
				//return a list of all bunnies
				int[] bunnies = new int[g.numNodes - 2];	//All the nodes except for starting and ending node are bunny nodes
				for (int b = 0; b < bunnies.length; ++b) bunnies[b] = b;
				return bunnies;
			}
		}


		//If we get this far, there are no negative cycles. Try to save as many bunnies as possible
		//As per problem instructions, all things being equal, try to save the lowest indexed bunnies
		ArrayList<Integer> al = new ArrayList<>();
		al.add(0);  //add the starting node. (there is no bunny at the starting node)
		return solveRecur(g, SP, P, g.numNodes, 0, g.numNodes - 1, al, g.K);
	}

	private static int[] solveRecur(DirectedGraph g, int[][][] SP, ArrayList<Integer>[][][] P, int totalNodes,
									int currNode, int endNode, ArrayList<Integer> al, int timeLimit) {

		boolean reachedEndNode = false;

		//Base case. If currNode isn't the endNode and currNode has been visited, return immediately
		//There should be no scenario where we must visit a non-endNode again; if there is,
		//it means that it's impossible to save any bunny in time.
		if (currNode != endNode && g.getNode(currNode).visited) return new int[]{};
		g.getNode(currNode).visited = true; //set current node to visited
		
		//Base case. If we're currently at the endNode...
		if (currNode == endNode) {
			if (timeLimit >= 0) {
				//If time is currently at 0 and there is no other path to traverse elsewhere and come back to endNode...
				//(the "== 1" part of the code below means that the only path from endNode to endNode
				//that takes minimium time (0 time) is to stay in place.)
				//In this case, we're done. Whatever bunnies we managed to save thus far will be the best we can do.
				if (timeLimit == 0 && P[endNode][endNode][totalNodes].size() == 1) {
					g.getNode(currNode).visited = false;
					return process(al, endNode); //helper method to post-process the array of nodes to include only the bunny nodes
				}
				//Else, there MAY BE a way to traverse elsewhere and return here within time limit. We'll move on to the main part of this algorithm.
				//Do nothing for now
				else {
					reachedEndNode = true;
				}
			} else {	//Else, if we're at the endNode but we're NOT within time limit...
				//Since there are no negative cycles in the graph, there is no way to traverse elsewhere and return to the endNode
				//while EARNING time. return empty list because we cannot succeed this mission from the current position
				g.getNode(currNode).visited = false;
				return new int[]{};
			}
		}

		//Another base case.
		//See if there is a way to get from the current Node to the end Node such that the time is >= 0 when you do.
		//If there's no way, it's impossible to save bunnies and get out in time from the current Node within the time limit.
		//So return empty array.
		if (timeLimit - SP[currNode][endNode][totalNodes] < 0) {
			g.getNode(currNode).visited = false;
			return new int[]{};
		}

		//If we get this far, we MAY or MAY NOT currently be at the end node, AND it may be possible to
		//explore elsewhere and/or get to the endNode within the time limit.
		//Try every neighboring node recursively and ultimately choose the one that results in the most bunnies saved.
		//In the event that there are two or more ways to save equal no. of bunnies, we choose the one
		//that will save smaller-index bunnies.

		//Note: if we have reached the endNode (see base cases above), then we already have a valid (if perhaps suboptimal) solution.
		//So in this case, take the existing array of bunnies as the baseline. Maybe we can improve upon this baseline, maybe not.
		int [] mostBunniesSavedSoFarArr = (reachedEndNode ? process(al, endNode) : new int[]{});
		int numMostBunniesSavedSoFar = (reachedEndNode ? mostBunniesSavedSoFarArr.length : Integer.MIN_VALUE);

		for (int i = 0; i < totalNodes; ++i) {
			if (i == currNode) continue;	//no sense in staying in the same node
			if (i != endNode && g.getNode(i).visited) continue;
			al.add(i);  //add the current node to the arraylist
			//Recurse!
			int[] bunniesSavedArr = solveRecur(g, SP, P, totalNodes, i, endNode, al, timeLimit - SP[currNode][i][totalNodes]);
			if (numMostBunniesSavedSoFar < bunniesSavedArr.length ||
				numMostBunniesSavedSoFar == bunniesSavedArr.length && hasSmallerIndices(bunniesSavedArr, mostBunniesSavedSoFarArr)) 
			{
				numMostBunniesSavedSoFar = bunniesSavedArr.length;
				mostBunniesSavedSoFarArr = bunniesSavedArr;
			}
			al.remove(al.size() - 1);   //remove the last node added from the arraylist, before starting next for loop iteration
		}
		//end for i
		g.getNode(currNode).visited = false; //we must reset the visited variable to false before returning
		return mostBunniesSavedSoFarArr;
	}

	/**
	 * Method: hasSmallerIndices.
	 * @param arr1 sorted array (ascending order). Assumed that arr1.length = arr2.length
	 * @param arr2 sorted array (ascending order). Assumed that arr1.length = arr2.length
	 * @return true IFF arr1 has smaller values than arr2.
	 */
	private static boolean hasSmallerIndices(int[] arr1, int[] arr2) {
		for (int i = 0; i < arr1.length; ++i) {
			if (arr1[i] < arr2[i]) return true;
			if (arr1[i] > arr2[i]) return false;
		}

		//If we get this far, the two arrays are exactly equal
		return false;
	}

	/**
	 * Process the arraylist of Nodes so that it contains only the nodes containing bunnies
	 * (i.e. not the starting nor ending Node) in sorted order. The bunnies themselves have ID numbers starting with 0.
	 * @param al
	 * @param endNode
	 * @return
	 */
	private static int[] process(ArrayList<Integer> al, int endNode) {

		//We want only the bunny indices, not the source and end nodes. so remove them.
		HashSet<Integer> set = new HashSet<>();
		set.addAll(al);
		set.remove(0);
		set.remove(endNode);

		int[] arr = new int[set.size()];
		int index = 0;
		for (Integer i : set) {
			arr[index++] = i - 1;	//Bunnies index starts at 0, but in the original given timeArr, index 0 actually refers to the source Node.
									//So decrement by 1 to get the bunnies index right.
		}
		Arrays.sort(arr);		//Pursuant to instructions, we must sort the array of bunnies.
		return arr;
	}
}
