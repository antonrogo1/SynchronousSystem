package com.syncsys.roundMessages;

import com.syncsys.ProcessNode;

public class TerminateMessage implements RoundMessage {
	private String senderID;

	public TerminateMessage() {
		setSenderID("-1");
	}

	@Override
    public void handleUsing(ProcessNode process) {
		process.setTerminating(true);
		process.setNeedsToSendMessages(true);
    }
	
	//**************************************************************************************************//
	//                                                                                                  //
	//**************************************************************************************************//
	
	// Getters / Setters
	
	public String getSenderID() {
		return senderID;
	}

	public void setSenderID(String senderID) {
		this.senderID = senderID;
	}
}
