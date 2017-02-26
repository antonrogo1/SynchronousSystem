package com.syncsys.roundMessages;

import com.syncsys.ProcessNode;
import com.syncsys.roundStrategies.BellmanFordStrategy;
import com.syncsys.roundStrategies.RoundStrategy;

public class ConvergeCastMessage implements RoundMessage {
	private int senderID;
	private boolean child;			
	private boolean terminating;
	private boolean marked;
	
	public ConvergeCastMessage() {
		setSenderID(-1);
	}
	
	public ConvergeCastMessage(int senderID, boolean child, boolean terminating,
			boolean marked) {
		this.senderID = senderID;
		this.child = child;
		this.terminating = terminating;
		this.marked = marked;
	}

	@Override
    public void processUsing(RoundStrategy strategy) {
		BellmanFordStrategy bfStrategy = (BellmanFordStrategy)strategy;
		ProcessNode process = bfStrategy.getProcess();

		bfStrategy.getResponseIDs().add(senderID);
		
		if (child) {
			bfStrategy.getChildIDs().add(senderID);
		}
		
		if (child && marked) {
			bfStrategy.getMarkedChildIDs().add(senderID);
		}
    }
	
	public int getSenderID() {
	    return senderID;
    }

	public void setSenderID(int senderID) {
	    this.senderID = senderID;
    }

	public boolean isChild() {
	    return child;
    }

	public void setChild(boolean parent) {
	    this.child = parent;
    }

	public boolean isTerminating() {
	    return terminating;
    }

	public void setTerminating(boolean terminating) {
	    this.terminating = terminating;
    }

	public boolean isMarked() {
	    return marked;
    }

	public void setMarked(boolean marked) {
	    this.marked = marked;
    }

}
