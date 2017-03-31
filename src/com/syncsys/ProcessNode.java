package com.syncsys;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.syncsys.Links.AsyncLink;
import com.syncsys.MessageStrategies.MessageHandler;
import com.syncsys.factories.FactoryHolder;
import com.syncsys.roundMessages.Message;
import com.syncsys.roundMessages.oldCrap.RoundMessage;

/**
 * Created by anton on 2/9/2017.
 */
public class ProcessNode implements Runnable
{
    private String id;                              //Id of process
    private int distance;                           //best distance to the root process know so far
    private volatile boolean terminating;           //true if terminating

    private Map<String, AsyncLink> links;   	   //Map of tuples: (id Of Neighbor process, link)
    private Map<String, ProcessNode> neighbors;    //Map of tuples: (id Of Neighbor process, neighbor)

    private ProcessNode parent;
    private List<String> childIDs;       //List of Children
    private List<String> doneChildIDs;   //List of Children nodes that identified them as Complete (them and their children found Shortest Path)
    private List<String> searchIDs;      //List of nodes to which this process send
    private List<String> responseIDs;

    boolean needTotifyNeighbors;


    public ProcessNode(String id)
    {
        this.id = id;
        this.distance = Integer.MAX_VALUE;
        this.links = new ConcurrentHashMap<String, AsyncLink>();
        this.neighbors = new ConcurrentHashMap<String, ProcessNode>();
    }

    public void processing()
    {
        for (AsyncLink asyncLink : this.links.values())
        {


            asyncLink.getOutQueueFor(this);
        }
    }

    public checkAndProcessIncomingMessages()
    {

        for (AsyncLink asyncLink : this.links.values())
        {
            Message message = asyncLink.getInQueueFor(this).peek();
        }

    }




    @Override
    public void run()
    {
        //execution
    }




    public void addNeighbor(AsyncLink asyncLink, ProcessNode neighbor)
    {

    }

    //Single Round (also see function below)
//    @Override
//    public void run()
//    {
//	    roundStrategy.execute();
//        roundCompleted = true;
//    }
//
//    //before each round thread should complete this step.
//    public void resetRoundToStart() throws InterruptedException
//    {
//        roundCompleted = false;
//
//        // Allow messages to only be processed at the start of the next round
//        messagesToProcess.clear();
//        int numMessages = messages.size();
//		for (int i = 0; i < numMessages; i++) {
//			RoundMessage message = messages.take();
//			messagesToProcess.add(message);
//		}
//    }

//    @Deprecated
//    public void addMessage(RoundMessage message) {
//        try {
//            messages.put(message);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }


//    //recursive method that return tuple (shortest Path description and total distance)
//    public String describeShortestPath(ProcessNode processNode)
//    {
//        String pathDescription =  processNode.getId() ;
//
//        if(!((BellmanFordStrategy)processNode.getRoundStrategy()).isRoot())
//        {
//            ProcessNode parentProcessNode = ((BellmanFordStrategy)processNode.getRoundStrategy()).getParent();
//            String parentChain = processNode.describeShortestPath(parentProcessNode);
//            pathDescription+= " =>" + parentChain;
//
//            return pathDescription;
//        }
//        else
//        {
//            return processNode.getId();
//        }
//    }




    /**
     * GETTERS / SETTERS
     */


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public boolean isTerminating() {
        return terminating;
    }

    public void setTerminating(boolean terminating) {
        this.terminating = terminating;
    }


    public Map<String, ProcessNode> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(Map<String, ProcessNode> neighbors) {
        this.neighbors = neighbors;
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

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public Map<String, AsyncLink> getLinks() {
        return links;
    }

    public void setLinks(Map<String, AsyncLink> links) {
        this.links = links;
    }



    /**
     * Equals and Hash
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessNode that = (ProcessNode) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
