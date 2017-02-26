package com.syncsys.roundMessages;

import com.syncsys.ProcessNode;
import com.syncsys.roundStrategies.BellmanFordStrategy;
import com.syncsys.roundStrategies.RoundStrategy;

public class ConvergeCastMessage implements RoundMessage {
	private int senderID;
	private boolean child;			
	private boolean terminating;
	
	public ConvergeCastMessage() {
		setSenderID(-1);
	}
	
	public ConvergeCastMessage(int senderID, boolean child, boolean terminating,
			boolean marked) {
		this.senderID = senderID;
		this.child = child;
		this.terminating = terminating;
	}

	@Override
    public void processUsing(RoundStrategy strategy) {
		BellmanFordStrategy bfStrategy = (BellmanFordStrategy)strategy;
		ProcessNode process = bfStrategy.getProcess();
		
		bfStrategy.getResponseIDs().add(senderID);
		
		if (child) {
			bfStrategy.getChildIDs().add(senderID);
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

	public void setChild(boolean child) {
	    this.child = child;
    }

	public boolean isTerminating() {
	    return terminating;
    }

	public void setTerminating(boolean terminating) {
	    this.terminating = terminating;
    }

}
