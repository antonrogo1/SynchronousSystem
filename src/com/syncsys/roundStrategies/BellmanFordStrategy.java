package com.syncsys.roundStrategies;

import java.util.LinkedList;
import java.util.List;

import com.syncsys.ProcessNode;
import com.syncsys.roundMessages.oldCrap.BellmanFordMessage;
import com.syncsys.roundMessages.oldCrap.ConvergeCastMessage;
import com.syncsys.roundMessages.oldCrap.DoneMessage;
import com.syncsys.roundMessages.oldCrap.RoundMessage;
import com.syncsys.roundMessages.oldCrap.TerminateMessage;


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

	private String id;                   //ID of a process that owns this Strategy
	private int dist;
	private boolean root;
	private boolean done;
	private ProcessNode process;
	private ProcessNode parent;
	private List<String> childIDs;
	private List<String> doneChildIDs;   //List of Children nodes that identified them as Complete (them and their children found Shortest Path)
	private List<String> searchIDs;      //List of Children nodes that identified them as Complete (them and their children found Shortest Path)
	private List<String> responseIDs;
	
	public BellmanFordStrategy(ProcessNode process) {
		this.id = process.getId();
		this.dist = Integer.MAX_VALUE;
		this.root = false;
		this.done = false;
		this.process = process;
		this.parent = null;
		this.childIDs = new LinkedList<String>();
		this.doneChildIDs = new LinkedList<String>();
		this.searchIDs = new LinkedList<String>();
		this.responseIDs = new LinkedList<String>();
	}
	
	@Override
    public void generateMessages() {
//		for (ProcessNode neighbor : getProcess().getNeighbors().values()) {
//
//			// Send BellmanFord message
//			BellmanFordMessage search = new BellmanFordMessage();
//			search.setSenderID(this.id);
//			search.setDistance(dist);
//			neighbor.addMessage(search);
//
//			// Send ConvergeCast message
//			if (searchIDs.contains(neighbor.getId())) {
//				ConvergeCastMessage response = new ConvergeCastMessage();
//				response.setSenderID(this.id);
//				response.setChild(null != parent && neighbor.getId() == parent.getId());
//				neighbor.addMessage(response);
//			}
//
//			// Send Done message to parent
//			if (null != parent && neighbor.getId() == parent.getId() && done) {
//				DoneMessage done = new DoneMessage();
//				done.setSenderID(this.id);
//				neighbor.addMessage(done);
//			}
//
//			// Send Terminate message to children
//			if (process.isTerminating() && childIDs.contains(neighbor.getId())) {
//				TerminateMessage terminate = new TerminateMessage();
//				terminate.setSenderID(this.id);
//				neighbor.addMessage(terminate);
//			}
//		}
    }

	@Override
    public void processMessages() {
		childIDs.clear();
		doneChildIDs.clear();
		searchIDs.clear();
		responseIDs.clear();
		
		for (RoundMessage message : process.getMessagesToProcess()) {
			
			// Only the message knows how it should be processed
			// Thus we give control to the message for processing
			
			message.processUsing(this);
		}
		
		if (null != parent && allChildrenDone()) {
			done = true;
		}
		
		if (root && allChildrenDone()) {
			process.setTerminating(true);
		}
    }

	private boolean allChildrenDone() {
	    boolean allSearchesResponded = responseIDs.size() == process.getNeighbors().size();
	    boolean allChildrenDone = childIDs.size() == doneChildIDs.size();
	    return allSearchesResponded && allChildrenDone;
    }

	@Override
    public void execute() {
		processMessages();
		generateMessages();
		
		System.out.println(
				"id: " + process.getId() + ", " +
				"dist: " + dist + ", " +
				"done: " + done + ", " +
				"terminating: " + process.isTerminating() + ", " +
				"children/done: " + childIDs.size() + "/" + doneChildIDs.size() + ", " +
				((null != parent) ? ("parent: " + parent.getId()) : ""));
	}




	//getters / setters

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

	public boolean isDone() {
	    return done;
    }

	public void setDone(boolean done) {
	    this.done = done;
    }

	public boolean isRoot() {
	    return root;
    }

	public void setRoot(boolean root) {
	    this.root = root;
	    if (root) { dist = 0; }
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getChildIDs() {
		return childIDs;
	}

	public void setChildIDs(List<String> childIDs) {
		this.childIDs = childIDs;
	}

	public List<String> getDoneChildIDs() {
		return doneChildIDs;
	}

	public void setDoneChildIDs(List<String> doneChildIDs) {
		this.doneChildIDs = doneChildIDs;
	}

	public List<String> getSearchIDs() {
		return searchIDs;
	}

	public void setSearchIDs(List<String> searchIDs) {
		this.searchIDs = searchIDs;
	}

	public List<String> getResponseIDs() {
		return responseIDs;
	}

	public void setResponseIDs(List<String> responseIDs) {
		this.responseIDs = responseIDs;
	}
}
