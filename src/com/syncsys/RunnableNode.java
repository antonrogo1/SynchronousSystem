package com.syncsys;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.syncsys.roundStrategies.RoundStrategy;

/**
 * Created by Cheng on 4/1/2017.
 */
public class RunnableNode implements Runnable
{                         
    protected String id;        
    protected volatile boolean roundCompleted;		  //indicates to the parent that Thread finished its round
    protected volatile boolean terminating;           //true if terminating
    protected Map<String, Integer> weights;   	 	  //Map of tuples: (id Of Neighbor process, weight)
    protected Map<String, ProcessNode> neighbors;     //Map of tuples: (id Of Neighbor process, neighbor)
    protected BlockingQueue<MessagePacket> messages;  //Messages sent to this node
    protected List<MessagePacket> messagesToProcess;  //Messages to process this round
    protected RoundStrategy roundStrategy;            //Strategy to execute during a round
    
    public RunnableNode(String id)
    {
        this.id = id;
        this.roundCompleted = false;
        this.weights = new ConcurrentHashMap<String, Integer>();
        this.neighbors = new ConcurrentHashMap<String, ProcessNode>();
        this.messages = new LinkedBlockingQueue<MessagePacket>();
        this.messagesToProcess = new LinkedList<MessagePacket>();
    }
    
    // Single Round (also see function below)
    @Override
    public void run()
    {
	    roundStrategy.execute();
        roundCompleted = true;
    }

    // Add a Neighbor to this node
    public void addNeighbor(String id, int weight, ProcessNode neighbor)
    {
        weights.put(id, weight);
        neighbors.put(id, neighbor);
    }

    // Before each round thread should complete this step.
    public void resetRoundToStart() throws InterruptedException
    {
        roundCompleted = false;
        
        // Allow messages to only be processed at the start of the next round
        messagesToProcess.clear();
        int numMessages = messages.size();
		for (int i = 0; i < numMessages; i++) {
			MessagePacket message = messages.take();
			messagesToProcess.add(message);
		}
    }

    // Add message to parse
    public void addMessage(MessagePacket message) {
        try {
            messages.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Recursive method that return tuple (shortest Path description and total distance)
    public String describeShortestPath(ProcessNode processNode)
    {
        String pathDescription =  processNode.getId() ;

        if(!processNode.root)
        {
            ProcessNode parentProcessNode = processNode.parent;
            String parentChain = processNode.describeShortestPath(parentProcessNode);
            pathDescription+= " =>" + parentChain;

            return pathDescription;
        }
        else
        {
            return processNode.getId();
        }
    }

	@Override
    public String toString() {
        return "Process{" +
                "id=" + getId() +
                ", neighbor weights=" + weights +
                '}';
    }
	
	//**************************************************************************************************//
	//                                                                                                  //
	//**************************************************************************************************//
	
	// Getters / Setters

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

	public BlockingQueue<MessagePacket> getMessages() {
	    return messages;
    }

	public void setMessages(BlockingQueue<MessagePacket> messages) {
	    this.messages = messages;
    }

    public List<MessagePacket> getMessagesToProcess() {
	    return messagesToProcess;
    }

	public void setMessagesToProcess(List<MessagePacket> messagesToProcess) {
	    this.messagesToProcess = messagesToProcess;
    }
}
