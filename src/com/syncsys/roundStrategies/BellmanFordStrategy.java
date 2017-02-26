package com.syncsys.roundStrategies;

import java.util.LinkedList;
import java.util.List;

import com.syncsys.ProcessNode;
import com.syncsys.roundMessages.BellmanFordMessage;
import com.syncsys.roundMessages.ConvergeCastMessage;
import com.syncsys.roundMessages.RoundMessage;


public class BellmanFordStrategy implements RoundStrategy {
	
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
	private boolean marked;
	private ProcessNode process;
	private ProcessNode parent;
	private List<Integer> childIDs;
	private List<Integer> markedChildIDs;
	private List<Integer> searchIDs;
	private List<Integer> responseIDs;
	
	public BellmanFordStrategy(ProcessNode process) {
		this.ID = process.getID();
		this.dist = Integer.MAX_VALUE;
		this.marked = false;
		this.process = process;
		this.parent = null;
		this.childIDs = new LinkedList<Integer>();
		this.markedChildIDs = new LinkedList<Integer>();
		this.searchIDs = new LinkedList<Integer>();
		this.responseIDs = new LinkedList<Integer>();
	}
	
	@Override
    public void generateMessages() {
		for (ProcessNode neighbor : getProcess().getNeighbors().values()) {
			
			// Send BellmanFord message
			BellmanFordMessage search = new BellmanFordMessage();
			search.setSenderID(ID);
			search.setDistance(dist);
			neighbor.addMessage(search);
			
			// Send ConvergeCast message
			if (searchIDs.contains(neighbor.getID())) {
				ConvergeCastMessage response = new ConvergeCastMessage();
				response.setSenderID(ID);
				response.setMarked(marked);
				response.setChild(neighbor == parent);
				response.setTerminating(process.isTerminating());
				neighbor.addMessage(response);
			}
		}
    }

	@Override
    public void processMessages() {
		childIDs.clear();
		markedChildIDs.clear();
		searchIDs.clear();
		responseIDs.clear();
		
		for (RoundMessage message : process.getMessagesToProcess()) {
			
			// Only the message knows how it should be processed
			// Thus we give control to the message for processing
			// Note: a better approach involves messenger services
			
			message.processUsing(this);
		}
		
		if (null != parent) {
			boolean allSearchesResponded = responseIDs.size() == process.getNeighbors().size();
			boolean allChildrenMarked = childIDs.size() == markedChildIDs.size();
			
			marked = allSearchesResponded && allChildrenMarked;
			process.setTerminating(parent.isTerminating());
			
		}
    }

	@Override
    public void execute() {
		processMessages();
		generateMessages();
		
		System.out.println(
				"id: " + process.getID() + ", " + 
				"dist: " + dist + ", " +
				"CID: " + childIDs.size()+", "+markedChildIDs.size() + ", " +
				((marked) ? ("parent: " + parent.getID()) : ""));
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

	public boolean isMarked() {
	    return marked;
    }

	public void setMarked(boolean marked) {
	    this.marked = marked;
    }

	public List<Integer> getMarkedChildIDs() {
	    return markedChildIDs;
    }

	public void setMarkedChildIDs(List<Integer> markedChildIDs) {
	    this.markedChildIDs = markedChildIDs;
    }

	public List<Integer> getChildIDs() {
	    return childIDs;
    }

	public void setChildIDs(List<Integer> childIDs) {
	    this.childIDs = childIDs;
    }

	public List<Integer> getSearchIDs() {
	    return searchIDs;
    }

	public void setSearchIDs(List<Integer> searchIDs) {
	    this.searchIDs = searchIDs;
    }

	public List<Integer> getResponseIDs() {
	    return responseIDs;
    }

	public void setResponseIDs(List<Integer> responseIDs) {
	    this.responseIDs = responseIDs;
    }
}
