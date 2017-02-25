package com.syncsys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import com.syncsys.roundMessages.RoundMessage;
import com.syncsys.roundStrategies.BellmanFordStrategy;
import com.syncsys.roundStrategies.RoundStrategy;

/**
 * Created by anton on 2/9/2017.
 */
public class ProcessNode implements Runnable
{
    private int id;                                 //Id of process
    private boolean isRoundCompleted;				//indicates to the parent that Thread finished its round
    private Map<Integer, Integer> weights;   	 	//Map of tuples: (id Of Neighbor process, weight)
    private Map<Integer, ProcessNode> neighbors;    //Map of tuples: (id Of Neighbor process, neighbor)
    private BlockingQueue<RoundMessage> messages;   //Messages send to this node
    private RoundStrategy roundStrategy;            //Strategy to execute during a round
    
    public ProcessNode(int id, int idLeader)
    {
        this.id = id;
        isRoundCompleted = false;
        weights = new HashMap<Integer, Integer>();
        setNeighbors(new HashMap<Integer, ProcessNode>());
        
        messages = new LinkedBlockingQueue<RoundMessage>();
        
        //set round to execute the Bellman Ford Algorithm
        setRoundStrategy(new BellmanFordStrategy(this));
    }

    public void addNeighbor(int id, int weight, ProcessNode neighbor)
    {
        this.weights.put(id, weight);
        this.neighbors.put(id, neighbor);
    }

    public void closeMaps() {
    	//make maps unmodifiable to ensure read-only required for concurrency
        weights = Collections.unmodifiableMap(weights);
        neighbors = Collections.unmodifiableMap(neighbors);
    }

    //Single Round (also see function below)
    @Override
    public void run()
    {

        //System.out.println("Running Thread : " + this.toString());

        try {
	        roundStrategy.execute();
        } catch (InterruptedException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
        
        //System.out.println("Finished Thread : " + this.toString());
        this.isRoundCompleted = true;
        return;
    }


    //before each round thread should complete this step.
    public void resetRoundToStart()
    {
        this.isRoundCompleted = false;
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
        return isRoundCompleted;
    }

    public void setRoundCompleted(boolean roundCompleted) {
        isRoundCompleted = roundCompleted;
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
	
	public void addMessage(RoundMessage message) {
	    try {
	        messages.put(message);
        } catch (InterruptedException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Process{" +
                "id=" + id +
                ", neighbor weights=" + weights +
                '}';
    }


}
