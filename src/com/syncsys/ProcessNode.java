package com.syncsys;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.syncsys.roundMessages.RoundMessage;
import com.syncsys.roundStrategies.BellmanFordStrategy;
import com.syncsys.roundStrategies.RoundStrategy;

/**
 * Created by anton on 2/9/2017.
 */
public class ProcessNode implements Runnable
{
    private int id;                                 //Id of process
    private volatile boolean roundCompleted;		//indicates to the parent that Thread finished its round
    private volatile boolean terminating;           //true if terminating
    private Map<Integer, Integer> weights;   	 	//Map of tuples: (id Of Neighbor process, weight)
    private Map<Integer, ProcessNode> neighbors;    //Map of tuples: (id Of Neighbor process, neighbor)
    private BlockingQueue<RoundMessage> messages;   //Messages sent to this node
    private List<RoundMessage> messagesToProcess;	//Messages to process this round
    private RoundStrategy roundStrategy;            //Strategy to execute during a round
    
    public ProcessNode(int id)
    {
        this.id = id;
        this.roundCompleted = false;
        this.weights = new ConcurrentHashMap<Integer, Integer>();
        this.neighbors = new ConcurrentHashMap<Integer, ProcessNode>();
        this.messages = new LinkedBlockingQueue<RoundMessage>();
        this.messagesToProcess = new LinkedList<RoundMessage>();
        
        //set round to execute the Bellman Ford Algorithm
        this.roundStrategy = new BellmanFordStrategy(this);
    }

    public void addNeighbor(int id, int weight, ProcessNode neighbor)
    {
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
        String pathDescription =  Integer.toString(processNode.getID()) ;

        if(!((BellmanFordStrategy)processNode.getRoundStrategy()).isRoot())
        {
            ProcessNode parentProcessNode = ((BellmanFordStrategy)processNode.getRoundStrategy()).getParent();
            String parentChain = processNode.describeShortestPath(parentProcessNode);
            pathDescription+= " =>" + parentChain;

            return pathDescription;
        }
        else
        {
            return Integer.toString(processNode.getID());
        }
    }




    //Getters/Setters

    public int getID() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<Integer, Integer> getWeights() {
        return weights;
    }

    public void setWeights(HashMap<Integer, Integer> weights) {
        this.weights = weights;
    }

    public Map<Integer, ProcessNode> getNeighbors() {
	    return neighbors;
    }

	public void setNeighbors(Map<Integer, ProcessNode> neighbors) {
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

	public BlockingQueue<RoundMessage> getMessages() {
	    return messages;
    }

	public void setMessages(BlockingQueue<RoundMessage> messages) {
	    this.messages = messages;
    }

    public List<RoundMessage> getMessagesToProcess() {
	    return messagesToProcess;
    }

	public void setMessagesToProcess(List<RoundMessage> messagesToProcess) {
	    this.messagesToProcess = messagesToProcess;
    }

	@Override
    public String toString() {
        return "Process{" +
                "id=" + id +
                ", neighbor weights=" + weights +
                '}';
    }

}
