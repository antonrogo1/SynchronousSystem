package com.syncsys;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.syncsys.Links.AsyncLink;
import com.syncsys.Links.Link;
import com.syncsys.MessageStrategies.MessageHandler;
import com.syncsys.factories.FactoryHolder;
import com.syncsys.roundMessages.RoundMessage;
import com.syncsys.roundStrategies.BellmanFordStrategy;
import com.syncsys.roundStrategies.RoundStrategy;

/**
 * Created by anton on 2/9/2017.
 */
public class ProcessNode implements Runnable
{
    private String id;                                 //Id of process
    private volatile boolean roundCompleted;		//indicates to the parent that Thread finished its round
    private volatile boolean terminating;           //true if terminating
    private Map<String, Integer> weights;   	 	//Map of tuples: (id Of Neighbor process, weight)
    private Map<String, ProcessNode> neighbors;    //Map of tuples: (id Of Neighbor process, neighbor)
    private BlockingQueue<RoundMessage> messages;   //Messages sent to this node
    private List<RoundMessage> messagesToProcess;	//Messages to process this round
    private RoundStrategy roundStrategy;            //Strategy to execute during a round



    private MessageHandler messageHandler;
    
    public ProcessNode(String id)
    {
        this.id = id;
        this.roundCompleted = false;
        this.weights = new ConcurrentHashMap<String, Integer>();
        this.neighbors = new ConcurrentHashMap<String, ProcessNode>();
        this.messages = new LinkedBlockingQueue<RoundMessage>();
        this.messagesToProcess = new LinkedList<RoundMessage>();
        this.messageHandler = new MessageHandler();
        this.roundStrategy = FactoryHolder.getFactory().newRoundStrategy(this);
    }

    public void addNeighbor(String id, int weight, ProcessNode neighbor)
    {
        Link link = FactoryHolder.getFactory().newLink();
        messageHandler.addIncomingLink(neighbor, link);
        neighbor.getMessageHandler().addOutgoingLink(this, link);
        weights.put(id, weight);
        neighbors.put(id, neighbor);
    }

    //Single Round (also see function below)
    @Override
    public void run()
    {
	    roundStrategy.execute();
        roundCompleted = true;
    }

    //before each round thread should complete this step.
    public void resetRoundToStart() throws InterruptedException
    {
        roundCompleted = false;
        
        // Allow messages to only be processed at the start of the next round
        messagesToProcess.clear();
        int numMessages = messages.size();
		for (int i = 0; i < numMessages; i++) {
			RoundMessage message = messages.take();
			messagesToProcess.add(message);
		}
    }

    @Deprecated
    public void addMessage(RoundMessage message) {
        try {
            messages.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    //recursive method that return tuple (shortest Path description and total distance)
    public String describeShortestPath(ProcessNode processNode)
    {
        String pathDescription =  processNode.getId() ;

        if(!((BellmanFordStrategy)processNode.getRoundStrategy()).isRoot())
        {
            ProcessNode parentProcessNode = ((BellmanFordStrategy)processNode.getRoundStrategy()).getParent();
            String parentChain = processNode.describeShortestPath(parentProcessNode);
            pathDescription+= " =>" + parentChain;

            return pathDescription;
        }
        else
        {
            return processNode.getId();
        }
    }




    //Getters/Setters


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Integer> getWeights() {
        return weights;
    }

    public void setWeights(Map<String, Integer> weights) {
        this.weights = weights;
    }

    public Map<String, ProcessNode> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(Map<String, ProcessNode> neighbors) {
        this.neighbors = neighbors;
    }

    public boolean isRoundCompleted() {
        return roundCompleted;
    }

    public void setRoundCompleted(boolean roundCompleted) {
        this.roundCompleted = roundCompleted;
    }

	public boolean isTerminating() {
	    return terminating;
    }

	public void setTerminating(boolean terminating) {
	    this.terminating = terminating;
    }

	public RoundStrategy getRoundStrategy() {
	    return roundStrategy;
    }

	public void setRoundStrategy(RoundStrategy roundStrategy) {
	    this.roundStrategy = roundStrategy;
    }

    @Deprecated
	public BlockingQueue<RoundMessage> getMessages() {
	    return messages;
    }

    @Deprecated
	public void setMessages(BlockingQueue<RoundMessage> messages) {
	    this.messages = messages;
    }

    @Deprecated
    public List<RoundMessage> getMessagesToProcess() {
	    return messagesToProcess;
    }

    @Deprecated
	public void setMessagesToProcess(List<RoundMessage> messagesToProcess) {
	    this.messagesToProcess = messagesToProcess;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    @Override
    public String toString() {
        return "Process{" +
                "id=" + id +
                ", neighbor weights=" + weights +
                '}';
    }

}
