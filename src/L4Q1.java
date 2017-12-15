import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class L4Q1 {
	/* =======================================================================================================================
	 * LEVEL 4, QUESTION 1
	 * 
	 * You've blown up the LAMBCHOP doomsday device and broken the bunnies out of Lambda's prison - and now you 
	 * need to escape from the space station as quickly and as orderly as possible! The bunnies have all 
	 * gathered in various locations throughout the station, and need to make their way towards the seemingly 
	 * endless amount of escape pods positioned in other parts of the station. You need to get the numerous 
	 * bunnies through the various rooms to the escape pods. Unfortunately, the corridors between the rooms 
	 * can only fit so many bunnies at a time. What's more, many of the corridors were resized to accommodate 
	 * the LAMBCHOP, so they vary in how many bunnies can move through them at a time. 
	 * 
	 * Given the starting room numbers of the groups of bunnies, the room numbers of the escape pods, 
	 * and how many bunnies can fit through at a time in each direction of every corridor in between, 
	 * figure out how many bunnies can safely make it to the escape pods at a time at peak.
	 * 
	 * Write a function answer(entrances, exits, path) that takes an array of integers denoting where 
	 * the groups of gathered bunnies are, an array of integers denoting where the escape pods are 
	 * located, and an array of an array of integers of the corridors, returning the total number 
	 * of bunnies that can get through at each time step as an int. The entrances and exits are 
	 * disjoint and thus will never overlap. The path element path[A][B] = C describes that the 
	 * corridor going from A to B can fit C bunnies at each time step.  There are at most 50 rooms
	 * connected by the corridors and at most 2000000 bunnies that will fit at a time.
	 * 
	 * For example, if you have:
	 * entrances = [0, 1]
	 * exits = [4, 5]
	 * path = [
	 *   [0, 0, 4, 6, 0, 0],  # Room 0: Bunnies
	 *   [0, 0, 5, 2, 0, 0],  # Room 1: Bunnies
	 *   [0, 0, 0, 0, 4, 4],  # Room 2: Intermediate room
	 *   [0, 0, 0, 0, 6, 6],  # Room 3: Intermediate room
	 *   [0, 0, 0, 0, 0, 0],  # Room 4: Escape pods
	 *   [0, 0, 0, 0, 0, 0],  # Room 5: Escape pods
	 *  ]
	 *  
	 *  Then in each time step, the following might happen:
	 *  0 sends 4/4 bunnies to 2 and 6/6 bunnies to 3
	 *  1 sends 4/5 bunnies to 2 and 2/2 bunnies to 3
	 *  2 sends 4/4 bunnies to 4 and 4/4 bunnies to 5
	 *  3 sends 4/6 bunnies to 4 and 4/6 bunnies to 5
	 *  
	 *  So, in total, 16 bunnies could make it to the escape pods at 4 and 5 at each time step.  
	 *  (Note that in this example, room 3 could have sent any variation of 8 bunnies to 4 and 5,
	 *  such as 2/6 and 6/6, but the final answer remains the same.)
	 * ======================================================================================================================= */

	/* Try a Max Flow data structure! */
	public static int answer(int[] entrances, int[] exits, int[][] path) {

		/**
		 * Inner class indicating an edge
		 */
		class FlowEdge {
			//v is the vertex the directed edge points from. w is the vertex the edge points to. flow and capacity are self-explanatory.
			int v, w, capacity, flow;
			FlowEdge(int v, int w, int capacity) {
				this.v = v;
				this.w = w;
				this.capacity = capacity;
				this.flow = 0;
			}

//			int fromVertex() {
//				return this.v;
//			}
//
//			int toVertex() {
//				return this.w;
//			}

			int getOtherVertex(int vertex) {
				if (this.v == vertex) return w;
				else return v;	//if (this.w == vertex)
			}

			int getResidualCapacity(int vertex) {
				if (this.v == vertex) return this.flow;
				else return this.capacity - this.flow;	//if (this.w == vertex)
			}

			void addResidualFlow(int vertex, int fl) {
				if (this.v == vertex) this.flow -= fl;
				if (this.w == vertex) this.flow += fl;
			}

			@Override
			public String toString() {
				return String.format("%s->%s(%d/%d)", v,w,flow,capacity);
			}
		}
		//end class FlowEdge

		class MaxFlowNetwork {
			int numOfNodes;						//Total no. of nodes in this flow network
			LinkedList<FlowEdge>[] edgeListArr;	//Array of Lists of edges. Each vertex will have a list of edges attached to it.

			@SuppressWarnings("unchecked")
			MaxFlowNetwork(int numOfNodes) {
				this.numOfNodes = numOfNodes;
				this.edgeListArr = (LinkedList<FlowEdge>[])new LinkedList[numOfNodes]; //unavoidable cast warning, used @SuppressWarnings above
				for (int i = 0; i < numOfNodes; ++i) {
					edgeListArr[i] = new LinkedList<FlowEdge>();
				}
			}

			void addFlowEdge(FlowEdge e) {
				int v = e.v;		//vertex that the edge points away from
				int w = e.w;		//vertex that the edge points to
				this.edgeListArr[v].add(e);
				this.edgeListArr[w].add(e);
			}

//			List<FlowEdge> getList(int v){
//				return this.edgeListArr[v];
//			}

			@Override
			public String toString() {
				return Arrays.toString(edgeListArr);
			}
		}
		//end class MaxFlowNetwork

		/**
		 * Ford-fulkerson algorithm.
		 */
		class FordFulkerson {
			boolean[] reached;
			FlowEdge[] edgeTo;
			int flowValue;

			FordFulkerson(MaxFlowNetwork MF, int source, int sink) {
				this.flowValue = 0;
				while (augmentingPathExists(MF, source, sink)) {
					int bottleNeck = Integer.MAX_VALUE;
					for (int v = sink; v != source; v = edgeTo[v].getOtherVertex(v)) {
//						if (debugOn) System.out.printf("going from %s to %s...\n", v, edgeTo[v].getOtherVertex(v));
						bottleNeck = Math.min(bottleNeck, edgeTo[v].getResidualCapacity(v));
					}
//					if (debugOn) System.out.println("BottleNeck capacity: " + bottleNeck);

					for (int v = sink; v != source; v = edgeTo[v].getOtherVertex(v)) {
//						if (debugOn) System.out.printf("edgeTo[%s] = %s\n", v, edgeTo[v]);
						edgeTo[v].addResidualFlow(v, bottleNeck);
					}
					this.flowValue += bottleNeck;
				}
				//end while
			}
			//end constructor

			boolean augmentingPathExists(MaxFlowNetwork MF, int source, int sink) {
				this.edgeTo = new FlowEdge[MF.numOfNodes];
				this.reached = new boolean[MF.numOfNodes];

				//Find whether a path exists via BFS
				Queue<Integer> q = new LinkedList<>();
				reached[source] = true;
				q.add(source);
				
				while (!q.isEmpty()) {
					int v = q.remove();
					for (FlowEdge e : MF.edgeListArr[v]) {	//get list of edges attached to vertex v and go thru each edge
						int w = e.getOtherVertex(v);
						if (e.getResidualCapacity(w) > 0 && !reached[w]) {	// as long as there is a non-full forward edge or non-empty backward edge
							this.edgeTo[w] = e;
							this.reached[w] = true;
							q.add(w);
						}
					}
					//end for FlowEdge
				}
				//end while

				return reached[sink];
			}
			//end boolean augmentingPathExists

			int getFlowValue() {
				return this.flowValue;
			}
		}
		//end class FordFulkerson

		/* ======== BEGIN SOLUTION ======== */
		//We're given int[] entrances, int[] exits, int[][] path as parameters.
		//try combining entrances and exits into a single source node and sink node
		
		/* For example, the below path:
		 *  
		 * path = [
		 *   [0, 0, 4, 6, 0, 0],  # Room 0: Bunnies
		 *   [0, 0, 5, 2, 0, 0],  # Room 1: Bunnies
		 *   [0, 0, 0, 0, 4, 4],  # Room 2: Intermediate room
		 *   [0, 0, 0, 0, 6, 6],  # Room 3: Intermediate room
		 *   [0, 0, 0, 0, 0, 0],  # Room 4: Escape pods
		 *   [0, 0, 0, 0, 0, 0],  # Room 5: Escape pods
		 * 	]
		 * 
		 * can be represented as a max flow network like so:
		 *
		 *       4+5       4+6
		 *    /--------2----------\
		 * 0,1                    4,5
		 *   \---------3----------/
		 *       6+2       4+6
		 * 
		 * where rooms 0, 1 can be combined into a single source node, and rooms 4,5 can be combined into a single sink node
		 * and the capacity between (0,1) to (2) is 4+5 = 9. Capacity between (2) and (4,5) is 10. And so on.
		 * */
		HashSet<Integer> sourceSet = 	new HashSet<>();
		HashSet<Integer> sinkSet = 		new HashSet<>();
		for (int source : entrances) 	sourceSet.add(source);
		for (int sink : exits) 			sinkSet.add(sink);

		//Designate single source and sink nodes
		Arrays.sort(entrances);
		int sourceVertex = entrances[0];
		Arrays.sort(exits);
		int sinkVertex = exits[exits.length - 1];


		//Array to auto-translate each node to either itself or to the master source and/or sink node
		//translatedVertex[i] = sourceVertex if i belongs to sourceSet, sinkVertex if i belongs to sinkSet. 
		//Otherwise, translatdVertex[i] = i.
		int totalNodes = path.length;
		int[] translatedVertex = new int[totalNodes];
		for (int i = 0; i < totalNodes; ++i) {
			if (sourceSet.contains(i)) translatedVertex[i] = sourceVertex;
			else if (sinkSet.contains(i)) translatedVertex[i] = sinkVertex;
			else translatedVertex[i] = i;
		}

		/*
		 * Create max flow network and add the directed flow edges
		 */
		MaxFlowNetwork mfn = new MaxFlowNetwork(totalNodes);
		for (int i = 0; i < totalNodes; ++i) {		//totalNodes = path.length
			int currFromVertex = translatedVertex[i];
			for (int j = 0; j < path[0].length; ++j) {
				int currToVertex = translatedVertex[j];
				if (currFromVertex == currToVertex) continue;	//I'm assuming no self-loops are allowed?
				int capacity = path[i][j];
				if (capacity > 0) {
					mfn.addFlowEdge(new FlowEdge(currFromVertex, currToVertex, capacity));
				}
				//end if capacity > 0
			}
			//end for j
		}
		//end for i

		/* Run Ford-Fulkerson algorithm to get the max flow */
		FordFulkerson ff = new FordFulkerson(mfn, sourceVertex, sinkVertex);

		return ff.getFlowValue();
	}
}
