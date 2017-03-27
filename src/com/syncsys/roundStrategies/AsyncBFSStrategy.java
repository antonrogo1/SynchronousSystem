package com.syncsys.roundStrategies;

import com.syncsys.Links.AsyncLink;
import com.syncsys.ProcessNode;
import com.syncsys.roundMessages.RoundMessage;

import java.util.List;

/**
 * Created by z on 3/26/17.
 */
public class AsyncBFSStrategy implements RoundStrategy {
    private boolean isRoot = false;



    private int distance = Integer.MAX_VALUE;
    private ProcessNode node;

    public AsyncBFSStrategy(ProcessNode node){
        this.node = node;
    }

    public void updateDistance(int distance, ProcessNode parent){

    }
    @Override
    public void generateMessages() {

    }

    @Override
    public void processMessages() {
        List<RoundMessage> list = node.getMessageHandler().getMessages();
    }

    @Override
    public void execute() {
        generateMessages();
        processMessages();
    }

    @Override
    public void setRoot(boolean isRoot) {
        this.isRoot = true;
        if(isRoot){
            distance = 0;
        }
    }

    public int getDistance() {
        return distance;
    }

    public ProcessNode getNode() {
        return node;
    }
}
