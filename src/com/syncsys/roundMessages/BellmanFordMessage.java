package com.syncsys.roundMessages;

import com.syncsys.ProcessNode;
import com.syncsys.roundStrategies.BellmanFordStrategy;
import com.syncsys.roundStrategies.RoundStrategy;

public class BellmanFordMessage implements RoundMessage {
	private String senderID;
	private int distance;
	
	public BellmanFordMessage() {
		setSenderID("-1");
		setDistance(Integer.MAX_VALUE);
	}
	
	public BellmanFordMessage(String senderID, int distance) {
		this.senderID = senderID;
		this.distance = distance;
	}

	@Override
    public void processUsing(RoundStrategy strategy) {
		BellmanFordStrategy bfStrategy = (BellmanFordStrategy)strategy;
		ProcessNode process = bfStrategy.getProcess();

		bfStrategy.getSearchIDs().add(senderID);

		if(process.getWeights().get(senderID) == null)
			System.out.println("Test");

		int edgeWeight = process.getWeights().get(senderID);
		if (Integer.MAX_VALUE != distance && distance + edgeWeight < bfStrategy.getDist()) {
			bfStrategy.setDist(distance + edgeWeight);
			bfStrategy.setParent(process.getNeighbors().get(senderID));
		}
    }

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
