package com.syncsys.roundMessages;

import com.syncsys.ProcessNode;
import com.syncsys.roundStrategies.BellmanFordStrategy;
import com.syncsys.roundStrategies.RoundStrategy;

public class ConvergeCastMessage implements RoundMessage {
	private int senderID;
	private boolean parent;
	private boolean terminating;
	private boolean marked;
	
	public ConvergeCastMessage() {
		setSenderID(-1);
	}
	
	public ConvergeCastMessage(int senderID, boolean parent, boolean terminating,
			boolean marked) {
		this.senderID = senderID;
		this.parent = parent;
		this.terminating = terminating;
		this.marked = marked;
	}

	@Override
    public void processUsing(RoundStrategy strategy) {
		BellmanFordStrategy bfStrategy = (BellmanFordStrategy)strategy;
		ProcessNode process = bfStrategy.getProcess();

		if (parent && terminating) {
			//I should terminate
		}
    }
	
	public int getSenderID() {
	    return senderID;
    }

	public void setSenderID(int senderID) {
	    this.senderID = senderID;
    }

	public boolean isParent() {
	    return parent;
    }

	public void setParent(boolean parent) {
	    this.parent = parent;
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
