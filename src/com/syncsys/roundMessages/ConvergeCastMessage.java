package com.syncsys.roundMessages;

import com.syncsys.ProcessNode;
import com.syncsys.roundStrategies.BellmanFordStrategy;
import com.syncsys.roundStrategies.RoundStrategy;

public class ConvergeCastMessage implements RoundMessage {
	private String senderID;
	private boolean child;			
	
	public ConvergeCastMessage() {
		setSenderID("-1");
	}
	
	public ConvergeCastMessage(String senderID, boolean child, boolean marked) {
		this.senderID = senderID;
		this.child = child;
	}

	@Override
    public void processUsing(RoundStrategy strategy) {
		BellmanFordStrategy bfStrategy = (BellmanFordStrategy)strategy;
		
		bfStrategy.getResponseIDs().add(senderID);
		
		if (child) {
			bfStrategy.getChildIDs().add(senderID);
		}
    }

	public String getSenderID() {
		return senderID;
	}

	public void setSenderID(String senderID) {
		this.senderID = senderID;
	}

	public boolean isChild() {
	    return child;
    }

	public void setChild(boolean child) {
	    this.child = child;
    }

}
