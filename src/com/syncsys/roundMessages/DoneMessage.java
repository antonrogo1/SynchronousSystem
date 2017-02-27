package com.syncsys.roundMessages;

import com.syncsys.ProcessNode;
import com.syncsys.roundStrategies.BellmanFordStrategy;
import com.syncsys.roundStrategies.RoundStrategy;

public class DoneMessage implements RoundMessage {
	private int senderID;
	
	public DoneMessage() {
		setSenderID(-1);
	}

	@Override
    public void processUsing(RoundStrategy strategy) {
		BellmanFordStrategy bfStrategy = (BellmanFordStrategy)strategy;
		
		bfStrategy.getDoneChildIDs().add(senderID);
    }
	
	public int getSenderID() {
	    return senderID;
    }

	public void setSenderID(int senderID) {
	    this.senderID = senderID;
    }

}
