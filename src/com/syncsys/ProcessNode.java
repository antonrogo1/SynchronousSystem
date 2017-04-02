package com.syncsys;

import java.util.Set;
import java.util.HashSet;

import com.syncsys.roundStrategies.AsyncBFSStrategy;

/**
 * Created by anton on 2/9/2017.
 */
public class ProcessNode extends RunnableNode implements Runnable
{                         
    //BFS attributes
	protected int dist;
	protected boolean root;
	protected boolean done;
	protected boolean gotNewParent;
	protected boolean needsToSendMessages;
	protected boolean needsToSendDoneToParent;
	protected boolean needsToSendInitialSearch;
	protected ProcessNode parent;
	protected Set<String> childIDs;
	protected Set<String> doneChildIDs;   
	protected Set<String> searchIDs;     
	protected Set<String> responseIDs;
    
    public ProcessNode(String id)
    {
    	super(id);
        
        //set round to execute the Bellman Ford Algorithm
        this.roundStrategy = new AsyncBFSStrategy(this);
        
		this.dist = Integer.MAX_VALUE;
		this.root = false;
		this.done = false;
		this.gotNewParent = false;
		this.needsToSendDoneToParent = false;
		this.needsToSendInitialSearch = false;
		this.parent = null;
		this.childIDs = new HashSet<String>();
		this.doneChildIDs = new HashSet<String>();
		this.searchIDs = new HashSet<String>();
		this.responseIDs = new HashSet<String>();
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

	public boolean isGotNewParent() {
		return gotNewParent;
	}

	public void setGotNewParent(boolean gotNewParent) {
		this.gotNewParent = gotNewParent;
	}

	public boolean isNeedsToSendMessages() {
		return needsToSendMessages;
	}

	public void setNeedsToSendMessages(boolean needsToSendMessages) {
		this.needsToSendMessages = needsToSendMessages;
	}

	public boolean isNeedsToSendDoneToParent() {
		return needsToSendDoneToParent;
	}

	public void setNeedsToSendDoneToParent(boolean needsToSendDoneToParent) {
		this.needsToSendDoneToParent = needsToSendDoneToParent;
	}

	public boolean isNeedsToSendInitialSearch() {
		return needsToSendInitialSearch;
	}

	public void setNeedsToSendInitialSearch(boolean needsToSendInitialSearch) {
		this.needsToSendInitialSearch = needsToSendInitialSearch;
	}

	public ProcessNode getParent() {
		return parent;
	}

	public void setParent(ProcessNode parent) {
		this.parent = parent;
	}

	public Set<String> getChildIDs() {
		return childIDs;
	}

	public void setChildIDs(Set<String> childIDs) {
		this.childIDs = childIDs;
	}

	public Set<String> getDoneChildIDs() {
		return doneChildIDs;
	}

	public void setDoneChildIDs(Set<String> doneChildIDs) {
		this.doneChildIDs = doneChildIDs;
	}

	public Set<String> getSearchIDs() {
		return searchIDs;
	}

	public void setSearchIDs(Set<String> searchIDs) {
		this.searchIDs = searchIDs;
	}

	public Set<String> getResponseIDs() {
		return responseIDs;
	}

	public void setResponseIDs(Set<String> responseIDs) {
		this.responseIDs = responseIDs;
	}

}
