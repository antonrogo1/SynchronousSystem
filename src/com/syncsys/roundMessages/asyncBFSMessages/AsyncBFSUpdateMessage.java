package com.syncsys.roundMessages.asyncBFSMessages;

import com.syncsys.ProcessNode;
import com.syncsys.roundMessages.RoundMessage;
import com.syncsys.roundStrategies.AsyncBFSStrategy;
import com.syncsys.roundStrategies.RoundStrategy;

/**
 * Created by z on 3/26/17.
 */
public class AsyncBFSUpdateMessage implements RoundMessage {
    private String senderID;
    private ProcessNode sender;
    private int dist;

    @Override
    public void processUsing(RoundStrategy strategy) {
        AsyncBFSStrategy bfsStrategy = (AsyncBFSStrategy) strategy;
        ProcessNode recipient = bfsStrategy.getNode();
        if(dist < bfsStrategy.getDistance()){
            bfsStrategy.updateDistance(dist, sender);
        }
    }

    @Override
    public String getSenderID() {
        return senderID;
    }

    @Override
    public void setSenderID(String id) {
        senderID = id;
    }

    @Override
    public void setSender(ProcessNode node) {
        this.sender = node;
    }

    @Override
    public ProcessNode getSender() {
        return sender;
    }

    public int getDist() {
        return dist;
    }

    public void setDist(int dist) {
        this.dist = dist;
    }
}
