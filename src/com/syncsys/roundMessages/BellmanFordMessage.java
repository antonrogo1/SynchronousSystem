package com.syncsys.roundMessages;

import com.syncsys.ProcessNode;
import com.syncsys.roundStrategies.BellmanFordStrategy;
import com.syncsys.roundStrategies.RoundStrategy;

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

	@Override
    public void processUsing(RoundStrategy strategy) {
		BellmanFordStrategy bfStrategy = (BellmanFordStrategy)strategy;
		ProcessNode process = bfStrategy.getProcess();

		bfStrategy.getSearchIDs().add(senderID);
		
		int edgeWeight = process.getWeights().get(senderID);
		if (Integer.MAX_VALUE != distance && distance + edgeWeight < bfStrategy.getDist()) {
			bfStrategy.setDist(distance + edgeWeight);
			bfStrategy.setParent(process.getNeighbors().get(senderID));
		}
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
