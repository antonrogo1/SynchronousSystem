package com.syncsys.Links;

import com.syncsys.roundMessages.RoundMessage;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * An AsyncLink represents the link between two nodes. Specifically, it represents one direction
 * of a bidirectional link. It will delay a message for a random amount of time. It will still
 * preserve FIFO order.
 */
public class AsyncLink extends Link{
    private Queue<MessageContainer> queue = new ConcurrentLinkedQueue<MessageContainer>();


    public void sendMessage(RoundMessage msg){
        queue.add(new MessageContainer(AsyncLink.getRound(), msg));
    }

    public boolean hasAvailableMessage(){
        if(queue.isEmpty()){
            return false;
        }
        MessageContainer top = queue.peek();
        return top.shouldPop();
    }

    public RoundMessage receiveMessage(){
        if(!hasAvailableMessage()){
            return null;
        }
        return queue.remove().getMessage();
    }



    class MessageContainer{
        long entryRound;
        int delay;
        RoundMessage message;

        MessageContainer(long entryRound, RoundMessage message){
            this.entryRound = entryRound;
            this.message = message;
            this.delay = randomDelay();
        }

        public boolean shouldPop(){
            long round = Link.getRound();
            return (round >= (entryRound+delay));

        }

        public RoundMessage getMessage(){
            return this.message;
        }

        private int randomDelay(){
            final int MIN_BOUND = 1;
            final int MAX_BOUND = 19;
            return (int) ((Math.random()* (MAX_BOUND-MIN_BOUND)) + MIN_BOUND);
        }
    }
}
