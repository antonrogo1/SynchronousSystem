package com.syncsys.roundMessages;

public class BellmanFordMessage implements RoundMessage {
	private int senderID;
	private int distance;
	
	public BellmanFordMessage() {
		setSenderID(-1);
		setDistance(Integer.MAX_VALUE);
	}
	
	public BellmanFordMessage(int senderID, int distance) {
		this.senderID = senderID;
		this.distance = distance;
	}
	
	public int getSenderID() {
	    return senderID;
    }

	public void setSenderID(int senderID) {
	    this.senderID = senderID;
    }

	public int getDistance() {
	    return distance;
    }

	public void setDistance(int distance) {
	    this.distance = distance;
    }

}
