package com.syncsys.roundMessages;

import com.syncsys.ProcessNode;
import com.syncsys.roundStrategies.RoundStrategy;

public class DoneMessage implements RoundMessage {
	private String senderID;
	
	public DoneMessage() {
		setSenderID("-1");
	}

	//**************************************************************************************************//
	//                                                                                                  //
	//**************************************************************************************************//
	
	// Getters / Setters
	
	@Override
    public void handleUsing(ProcessNode process) {
		process.getDoneChildIDs().add(senderID);
    }

	public String getSenderID() {
		return senderID;
	}

	public void setSenderID(String senderID) {
		this.senderID = senderID;
	}
}
