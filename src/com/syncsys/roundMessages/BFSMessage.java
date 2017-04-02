package com.syncsys.roundMessages;

import com.syncsys.ProcessNode;

public class BFSMessage implements RoundMessage {
	private String senderID;
	private int distance;
	
	public BFSMessage() {
		setSenderID("-1");
		setDistance(Integer.MAX_VALUE);
	}
	
	public BFSMessage(String senderID, int distance) {
		this.senderID = senderID;
		this.distance = distance;
	}

	@Override
    public void handleUsing(ProcessNode process) {
		process.getSearchIDs().add(senderID);

		if (distance + 1 < process.getDist()) {
			process.setDist(distance + 1);
			process.setParent(process.getNeighbors().get(senderID));
			process.setNeedsToSendMessages(true);
		}
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

	public int getDistance() {
	    return distance;
    }

	public void setDistance(int distance) {
	    this.distance = distance;
    }

}
