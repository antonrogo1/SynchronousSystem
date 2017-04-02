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
		
		// Child has new parent if it is broadcasting lower or equal distance
		if (process.getChildIDs().contains(senderID) && distance <= process.getDist()) {
			process.getChildIDs().remove(senderID);
		}
		
		// Distance is one more than sender
		if (distance + 1 < process.getDist()) {
			process.setDist(distance + 1);
			process.setParent(process.getNeighbors().get(senderID));
			process.setGotNewParent(true);
			process.setNeedsToSendDoneToParent(true);

			process.getChildIDs().clear();
			process.getDoneChildIDs().clear();
			process.getResponseIDs().clear();
		}

		// Even if the message isn't a parent,
		// we need to send a response message
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

	public int getDistance() {
	    return distance;
    }

	public void setDistance(int distance) {
	    this.distance = distance;
    }

}
