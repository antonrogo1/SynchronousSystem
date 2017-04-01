package com.syncsys;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.syncsys.roundMessages.RoundMessage;
import com.syncsys.roundStrategies.BellmanFordStrategy;
import com.syncsys.roundStrategies.RoundStrategy;

/**
 * Created by anton on 2/9/2017.
 */
public class ProcessNode extends RunnableNode implements Runnable
{                         
    //BFS attributes
	protected int dist;
	protected boolean root;
	protected boolean done;
	protected ProcessNode parent;
	protected List<String> childIDs;
	protected List<String> doneChildIDs;   
	protected List<String> searchIDs;     
	protected List<String> responseIDs;
    
    public ProcessNode(String id)
    {
    	super(id);
        
        //set round to execute the Bellman Ford Algorithm
        this.roundStrategy = new BellmanFordStrategy(this);
        
		this.dist = Integer.MAX_VALUE;
		this.root = false;
		this.done = false;
		this.parent = null;
		this.childIDs = new LinkedList<String>();
		this.doneChildIDs = new LinkedList<String>();
		this.searchIDs = new LinkedList<String>();
		this.responseIDs = new LinkedList<String>();
    }
	
	//**************************************************************************************************//
	//                                                                                                  //
	//**************************************************************************************************//
	
	// Getters / Setters
    
	public int getDist() {
		return dist;
	}

	public void setDist(int dist) {
		this.dist = dist;
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public ProcessNode getParent() {
		return parent;
	}

	public void setParent(ProcessNode parent) {
		this.parent = parent;
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
