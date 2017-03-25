package com.syncsys.roundMessages;

import com.syncsys.ProcessNode;
import com.syncsys.roundStrategies.BellmanFordStrategy;
import com.syncsys.roundStrategies.RoundStrategy;

public class TerminateMessage implements RoundMessage {
	private String senderID;

	public TerminateMessage() {
		setSenderID("-1");
	}

	@Override
    public void processUsing(RoundStrategy strategy) {
		BellmanFordStrategy bfStrategy = (BellmanFordStrategy)strategy;
		ProcessNode process = bfStrategy.getProcess();

		process.setTerminating(true);
    }

	public String getSenderID() {
		return senderID;
	}

	public void setSenderID(String senderID) {
		this.senderID = senderID;
	}
}
