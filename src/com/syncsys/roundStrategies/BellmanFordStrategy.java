package com.syncsys.roundStrategies;

import com.syncsys.ProcessNode;
import com.syncsys.roundMessages.BellmanFordMessage;
import com.syncsys.roundMessages.RoundMessage;


public class BellmanFordStrategy implements RoundStrategy {
	
	public static final int ROOT_ID = 1;
	public static final int POS_INF = 9999999;
	
	/*** 
	 * As defined in Distributed Algorithms by Nancy A. Lynch (p. 62). 
	 * The Bellman Ford Algorithm for Shortest Path finding is as follows:
	 * 
	 * 		Each Process 'i' keeps track of 'dist', the shortest distance
	 * 		from 'i0' it knows so far, together with 'parent' the incoming
	 * 		neighbor that precedes 'i' in a path whose weight is 'dist'.
	 * 
	 * 		Initially, i0.dist = 0, i.dist = +INF for i != i0, and the
	 * 		'parent' components are undefined.
	 * 
	 * 		At each round, each process sends its 'dist' to all its outgoing
	 * 		neighbors. 
	 * 
	 * 		Then each process 'i' updates its 'dist' by a "relaxation step,"
	 * 		in which it takes the minimum of its previous 'dist' value and
	 * 		all the values j.dist + i.weight(j), where 'j' is an incoming 
	 * 		neighbor. 
	 * 
	 * 		If 'dist' is changed, the 'parent' components is also updated
	 * 		accordingly. After n-1 rounds, 'dist' contains the shortest
	 * 		distance, and 'parent' the parent in the shortest paths tree.
	 * 
	 */

	private int ID;
	private int dist;
	private ProcessNode process;
	private ProcessNode parent;
	
	public BellmanFordStrategy(ProcessNode process) {
		ID = process.getID();
		dist = ROOT_ID == process.getID() ? 0 : POS_INF;
		
		this.process = process;
		this.parent = ROOT_ID == process.getID() ? process : null;
	}
	
	@Override
    public void generateMessages() {
		for (ProcessNode neighbor : process.getNeighbors().values()) {
			
			BellmanFordMessage message = new BellmanFordMessage();
			message.setSenderID(ID);
			message.setDistance(dist);
			
			neighbor.addMessage((RoundMessage)message);
		}
    }

	@Override
    public void processMessages() {
		boolean hasDistanceChanged = false;
		
		for (RoundMessage roundMessage : process.getMessages()) {
			BellmanFordMessage message = (BellmanFordMessage) roundMessage;
			
			int edgeWeight = process.getWeights().get(message.getSenderID());
			if (message.getDistance() + edgeWeight < dist) {
				dist = message.getDistance() + edgeWeight;
				parent = process.getNeighbors().get(message.getSenderID());
			}
		}
		
		if (hasDistanceChanged) {
			//TODO: Probably converagecast?
		}
    }

	@Override
    public void execute() {
		generateMessages();
		processMessages();
		System.out.println("id: " + process.getID() + ", dist: " + dist);
    }
}
