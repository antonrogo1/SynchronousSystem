package com.syncsys;

import com.syncsys.ProcessNode;
import com.syncsys.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.Thread.sleep;

/**
 * An AsyncLink represents the link between two nodes. Specifically, it represents one direction
 * of a bidirectional link. It will delay a message for a random amount of time. It will still
 * preserve FIFO order.
 */
public class AsyncLink implements Runnable
{
    private int weight;

    private ProcessNode aProcess;
    private ProcessNode bProcess;

    private Queue<Message> queueAtoB;   // One Way from A to B
    private Queue<Message> queueBtoA;   // Back


    public AsyncLink(ProcessNode a, ProcessNode b, int weight)
    {
        this.aProcess=a;
        this.bProcess=b;
        this.weight = weight;
        this.queueAtoB = new ConcurrentLinkedQueue<Message>();
        this.queueBtoA = new ConcurrentLinkedQueue<Message>();
    }


    public void advanceTime()
    {
        for(Message m : queueAtoB)
        {
            m.setTransmisissionDurationSoFar( m.getTransmisissionDurationSoFar()+1);
        }
        for(Message m : queueBtoA)
        {
            m.setTransmisissionDurationSoFar( m.getTransmisissionDurationSoFar()+1);
        }
    }


    //Returns link's queue incoming to Process p
    public Queue<Message> getInQueueFor(ProcessNode p)
    {
        Queue<Message> result;
        if(p.equals(aProcess))
            result = getQueueBtoA();
        else
            result = getQueueAtoB();

        return result;
    }

    //Returns link's queue outgoing from Process p
    public Queue<Message> getOutQueueFor(ProcessNode p)
    {
        Queue<Message> result;
        if(p.equals(aProcess))
            result = getQueueAtoB();
        else
            result = getQueueBtoA();

        return result;
    }

    public List<Message> getArrivedMessagesFor(ProcessNode p)
    {
        List<Message> result = new ArrayList<Message>();
        boolean haveMoreArrivedMessages = true;
        while(haveMoreArrivedMessages)
        {
            Message message = this.getInQueueFor(p).peek();
            if(message!= null)
            {
                if(message.isMessageArrived())
                {
                    message = this.getInQueueFor(p).remove();
                    result.add(message);
                }
                else
                    break;   //Not checking other messages in a queue if prior one still not arrived
            }
            else
                break; //no meessages in the queue

        }
        return result;
    }





    @Override
    public void run()
    {

        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * GETTERS / SETTERS
     */

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public ProcessNode getaProcess() {
        return aProcess;
    }

    public void setaProcess(ProcessNode aProcess) {
        this.aProcess = aProcess;
    }

    public ProcessNode getbProcess() {
        return bProcess;
    }

    public void setbProcess(ProcessNode bProcess) {
        this.bProcess = bProcess;
    }

    public Queue<Message> getQueueAtoB() {
        return queueAtoB;
    }

    public void setQueueAtoB(Queue<Message> queueAtoB) {
        this.queueAtoB = queueAtoB;
    }

    public Queue<Message> getQueueBtoA() {
        return queueBtoA;
    }

    public void setQueueBtoA(Queue<Message> queueBtoA) {
        this.queueBtoA = queueBtoA;
    }







    //OLD crap

    //public void sendMessage(RoundMessage msg){
//        queue.add(new MessageContainer(AsyncLink.getRound(), msg));
//    }

//    public boolean hasAvailableMessage(){
//        if(queue.isEmpty()){
//            return false;
//        }
//        MessageContainer top = queue.peek();
//        return top.shouldPop();
//    }
//
//    public RoundMessage receiveMessage(){
//        if(!hasAvailableMessage()){
//            return null;
//        }
//        return queue.remove().getMessage();
//    }



//    class MessageContainer{
//        long entryRound;
//        int delay;
//        RoundMessage message;
//
//        MessageContainer(long entryRound, RoundMessage message){
//            this.entryRound = entryRound;
//            this.message = message;
//            this.delay = randomDelay();
//        }
//
//        public boolean shouldPop(){
//            long round = Link.getRound();
//            return (round >= (entryRound+delay));
//
//        }
//
//        public RoundMessage getMessage(){
//            return this.message;
//        }
//
//        private int randomDelay(){
//            final int MIN_BOUND = 1;
//            final int MAX_BOUND = 19;
//            return (int) ((Math.random()* (MAX_BOUND-MIN_BOUND)) + MIN_BOUND);
//        }
//    }
}
