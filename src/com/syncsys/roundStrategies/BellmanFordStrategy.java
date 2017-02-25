package com.syncsys.roundStrategies;

import com.syncsys.ProcessNode;
import com.syncsys.roundMessages.BellmanFordMessage;
import com.syncsys.roundMessages.RoundMessage;


public class BellmanFordStrategy implements RoundStrategy {
	
	public static final int ROOT_ID = 1;
	
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
	private int numConvergeCastMessages;
	private boolean sendingConvergeCast;
	private ProcessNode process;
	private ProcessNode parent;
	
	public BellmanFordStrategy(ProcessNode process) {
		ID = process.getID();
		dist = (ROOT_ID == ID ? 0 : Integer.MAX_VALUE);
		
		this.setProcess(process);
		this.setNumConvergeCastMessages(0);
		this.setSendingConvergeCast(false);
		this.setParent(ROOT_ID == process.getID() ? process : null);
	}
	
	@Override
    public void generateMessages() {
		for (ProcessNode neighbor : getProcess().getNeighbors().values()) {
			
			BellmanFordMessage message = new BellmanFordMessage();
			message.setSenderID(ID);
			message.setDistance(dist);
			
			//TODO: Send convergecast message
			
			neighbor.addMessage(message);
		}
    }

	@Override
    public void processMessages() throws InterruptedException {
		numConvergeCastMessages = 0;
		
		for (int i=0; i<getProcess().getMessages().size(); i++) {
			RoundMessage message = getProcess().getMessages().take();
			message.processUsing(this);
		}
		
		if (numConvergeCastMessages == getProcess().getNeighbors().size()) {
			
		}
    }

	@Override
    public void execute() throws InterruptedException {
		processMessages();
		generateMessages();
		
		System.out.println("id: " + getProcess().getID() + ", dist: " + getDist());
		//System.out.println("Messages: " + process.getMessages().toString());
    }

	public int getDist() {
	    return dist;
    }

	public void setDist(int dist) {
	    this.dist = dist;
    }

	public ProcessNode getParent() {
	    return parent;
    }

	public void setParent(ProcessNode parent) {
	    this.parent = parent;
    }

	public ProcessNode getProcess() {
	    return process;
    }

	public void setProcess(ProcessNode process) {
	    this.process = process;
    }

	public int getNumConvergeCastMessages() {
	    return numConvergeCastMessages;
    }

	public void setNumConvergeCastMessages(int numConvergeCastMessages) {
	    this.numConvergeCastMessages = numConvergeCastMessages;
    }

	public boolean isSendingConvergeCast() {
	    return sendingConvergeCast;
    }

	public void setSendingConvergeCast(boolean sendingConvergeCast) {
	    this.sendingConvergeCast = sendingConvergeCast;
    }
}
