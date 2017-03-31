package com.syncsys;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import com.syncsys.Links.AsyncLink;

import com.syncsys.roundMessages.Message;
import com.syncsys.roundMessages.enums.MessageType;


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
    private List<String> childrenIds;         //List of Children - nodes that acknowldged this node as Parent
    private List<String> doneChildIDs;        //List of Children nodes that identified them as Complete (them and their children found Shortest Path)
    private List<String> searchIDs;           //List of nodes to which this process send
    private List<String> responseIDs;

    boolean needTotifyNeighbors;   //falg indicating that Process have need infomation on distance and need to notify Neighbors


    public ProcessNode(String id)
    {
        this.id = id;
        this.distance = Integer.MAX_VALUE;
        this.links = new ConcurrentHashMap<String, AsyncLink>();
        this.neighbors = new ConcurrentHashMap<String, ProcessNode>();
        this.childrenIds = new ArrayList<String >();


        this.needTotifyNeighbors = true;
    }


    public void checkAndProcessIncomingMessages()
    {

        for (AsyncLink asyncLink : this.links.values())
        {
            List<Message> arrivedMessages = asyncLink.getArrivedMessagesFor(this);
            for(Message message : arrivedMessages)
            {
                if (message.getMessageType() == MessageType.EXPLORE) {
                    processSearchMessage(message, asyncLink.getWeight());
                }
                else if (message.getMessageType() == MessageType.PARENT_ACKNOWLEDGE)
                {
                    processParentAcknowldgeMessage(message);
                }
                else if (message.getMessageType() == MessageType.CONVERGECAST) {

                }
                else if (message.getMessageType() == MessageType.DONE) {

                }
                else if (message.getMessageType() == MessageType.TERMINATE) {

                }
            }
        }
    }

    private void processSearchMessage(Message message, int linkWeight)
    {
        int newDistanceFromNeighboor = message.getDistance();

        //If node received EXPLORE message from child - it means child got a new parent (Child is no longer a child)
        if(childrenIds.contains(message.getSender().getId()))
            childrenIds.remove(message.getSender().getId());

        if(newDistanceFromNeighboor != Integer.MAX_VALUE && newDistanceFromNeighboor + linkWeight < this.distance )
        {
            this.distance = newDistanceFromNeighboor + linkWeight;
            this.parent = message.getSender();

            System.out.println("Process " + this.id + " now have parent " + parent.getId() + " and distance " + this.distance);

            //Sending message to parent acknowledging him as parent
            Message messageToParent = new Message(MessageType.PARENT_ACKNOWLEDGE, this);
            this.links.get(parent.getId()).getOutQueueFor(this).add(messageToParent);  //getting async link to parent and adding ack message

            this.needTotifyNeighbors = true;
        }
    }

    private void processParentAcknowldgeMessage(Message message)
    {
        System.out.println("Process " + this.id + " received message from child " + message.getSender().getId() + " acknowledgeing it as parent");
        this.childrenIds.add(message.getSender().getId());
    }



    public void sendMessages()
    {

        if(this.needTotifyNeighbors)
        {
            //Sending EXPLORE message to every neighbor Except parent
            for(String neighborId : this.links.keySet())
            {
                if(parent == null)
                {
                    System.out.println("Process " + this.id + " sending EXPLORE message with distance " + this.distance + " to the process " + neighborId);
                    Message messageToNeighbors = new Message(MessageType.EXPLORE, this);
                    messageToNeighbors.setDistance(this.distance);
                    this.links.get(neighborId).getOutQueueFor(this).add(messageToNeighbors);
                }
                else
                    {
                    if (!neighborId.equals(parent.getId())) {
                        System.out.println("Process " + this.id + " sending EXPLORE message with distance " + this.distance + " to the process " + neighborId);
                        Message messageToNeighbors = new Message(MessageType.EXPLORE, this);
                        messageToNeighbors.setDistance(this.distance);
                        this.links.get(neighborId).getOutQueueFor(this).add(messageToNeighbors);  //getting async link to parent and adding explore message
                    }
                }
            }
            this.needTotifyNeighbors = false;
        }
    }

    @Override
    public void run()
    {
        while(true) {
            checkAndProcessIncomingMessages();
            sendMessages();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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

    public Map<String, AsyncLink> getLinks() {
        return links;
    }

    public void setLinks(Map<String, AsyncLink> links) {
        this.links = links;
    }

    public Map<String, ProcessNode> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(Map<String, ProcessNode> neighbors) {
        this.neighbors = neighbors;
    }

    public ProcessNode getParent() {
        return parent;
    }

    public void setParent(ProcessNode parent) {
        this.parent = parent;
    }

    public List<String> getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(List<String> childrenIds) {
        this.childrenIds = childrenIds;
    }

    public List<String> getDoneChildIDs() {
        return doneChildIDs;
    }

    public void setDoneChildIDs(List<String> doneChildIDs) {
        this.doneChildIDs = doneChildIDs;
    }

    public List<String> getSearchIDs() {
        return searchIDs;
    }

    public void setSearchIDs(List<String> searchIDs) {
        this.searchIDs = searchIDs;
    }

    public List<String> getResponseIDs() {
        return responseIDs;
    }

    public void setResponseIDs(List<String> responseIDs) {
        this.responseIDs = responseIDs;
    }

    public boolean isNeedTotifyNeighbors() {
        return needTotifyNeighbors;
    }

    public void setNeedTotifyNeighbors(boolean needTotifyNeighbors) {
        this.needTotifyNeighbors = needTotifyNeighbors;
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
