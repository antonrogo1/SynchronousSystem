package com.syncsys.roundMessages;

import com.syncsys.ProcessNode;
import com.syncsys.roundStrategies.BellmanFordStrategy;
import com.syncsys.roundStrategies.RoundStrategy;

public class DoneMessage implements RoundMessage {
	private String senderID;
	
	public DoneMessage() {
		setSenderID("-1");
	}

	@Override
    public void processUsing(RoundStrategy strategy) {
		BellmanFordStrategy bfStrategy = (BellmanFordStrategy)strategy;
		
		bfStrategy.getDoneChildIDs().add(senderID);
    }

	public String getSenderID() {
		return senderID;
	}

	public void setSenderID(String senderID) {
		this.senderID = senderID;
	}
}
