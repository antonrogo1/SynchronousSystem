package com.syncsys;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import com.syncsys.enums.MessageType;


/**
 * Created by anton on 2/9/2017.
 */
public class ProcessNode implements Runnable
{
    private String id;                              //Id of process
    private int distance;                           //best distance to the root process know so far

    private Map<String, AsyncLink> links;   	   //Map of tuples: (id Of Neighbor process, link)
    private Map<String, ProcessNode> neighbors;    //Map of tuples: (id Of Neighbor process, neighbor)

    private ProcessNode parent;
    private List<String> childrenIds;         //List of Children - nodes that acknowldged this node as Parent
    private List<String> nonChildrenIds;      //List of NonChildren - nodes that acknowldged this node as Not-Parent

    private List<String> convCastChildrenIds;       //List of Children - that send ConvergeCastMessage


    private List<String> doneChildIDs;        //List of Children nodes that identified them as Complete (them and their children found Shortest Path)

    private List<String> responseIDs;

    private List<String> searchIDs;           //List of nodes to which this process send  TRASH

    private boolean isRoot;
    private boolean needNotifyNeighbors;              //flag indicating that Process have need infomation on distance and need to notify Neighbors
    private boolean isConvergeCastMessageAlreadySent; //flag indicating that Process already send ConvergeCast Message
    private boolean isTerminating;                    //flag indicating that process needs to terminate


    public ProcessNode(String id)
    {
        this.id = id;
        this.distance = Integer.MAX_VALUE;
        this.links = new ConcurrentHashMap<String, AsyncLink>();
        this.neighbors = new ConcurrentHashMap<String, ProcessNode>();
        this.childrenIds = new ArrayList<String >();
        this.nonChildrenIds = new ArrayList<String>();
        this.convCastChildrenIds = new ArrayList<String>();

        this.isRoot = false;
        this.needNotifyNeighbors = true;
        this.isConvergeCastMessageAlreadySent = false;
        this.isTerminating = false;
    }


    public void checkAndProcessIncomingMessages()
    {

        for (AsyncLink asyncLink : this.links.values())
        {
            List<Message> arrivedMessages = asyncLink.getArrivedMessagesFor(this);
            for(Message message : arrivedMessages)
            {
                if (message.getMessageType() == MessageType.EXPLORE) {
                    processExploreMessage(message, asyncLink.getWeight());
                }
                else if (message.getMessageType() == MessageType.PARENT_ACKNOWLEDGE)
                {
                    processParentAcknowldgeMessage(message);
                }
                else if (message.getMessageType() == MessageType.NOTPARENT_ACKNOWLEDGE)
                {
                    processNotParentAcknowldgeMessage(message);
                }
                else if (message.getMessageType() == MessageType.CONVERGECAST) {
                    processConvergeCastMessage(message, asyncLink.getWeight());
                }
                else if (message.getMessageType() == MessageType.TERMINATE) {
                    processTerminateMessage(message);
                }
            }
        }
    }

    private void processExploreMessage(Message message, int linkWeight)
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

            //We returning the distance that we got from original sender(parent), So parent know which latest version of distance this node got,
            //there could be a case that parent got better distance, so it need to know  to resend latest distance.
            messageToParent.setDistance(newDistanceFromNeighboor);

            this.links.get(parent.getId()).getOutQueueFor(this).add(messageToParent);  //getting async link to parent and adding ack message

            //Non-Children may become children in the future
            nonChildrenIds = new ArrayList<String>();
            this.needNotifyNeighbors = true;
        }
        else // returning ack-message to original sender so it knows its message was processed
        {
            Message messageToNonParent = new Message(MessageType.NOTPARENT_ACKNOWLEDGE, this);

            //We returning the distance that we got from original sender(Non-parent), So non-parent know which latest version of distance this node got,
            //there could be a case that non-parent got better distance, so it need to know  to resend latest distance.
            messageToNonParent.setDistance(newDistanceFromNeighboor);

            this.links.get(message.getSender().getId()).getOutQueueFor(this).add(messageToNonParent);  //getting async link to non-parent and adding ack message (Not-Parent)
        }
    }

    private void processParentAcknowldgeMessage(Message message)
    {
        if(message.getDistance() == this.distance) {
            System.out.println("Process " + this.id + " received ack-message from child-process " + message.getSender().getId() + " acknowledging it as parent");
            this.childrenIds.add(message.getSender().getId());
        }
        else
            this.needNotifyNeighbors = true;
    }

    private void processNotParentAcknowldgeMessage(Message message)
    {
        if(message.getDistance() == this.distance) {
            System.out.println("Process " + this.id + " received ack-message from neighbor process " + message.getSender().getId() + " rejecting it as parent");
            this.nonChildrenIds.add(message.getSender().getId());


            if(childrenIds.contains(message.getSender().getId()))
                childrenIds.remove(message.getSender().getId());
        }
        else
            this.needNotifyNeighbors = true;
    }


    //minusDistance used to make sure that Child and Parent are in sync on what is the latest shortest distance is.
    private void processConvergeCastMessage(Message message, int minusDistance)
    {

        System.out.println("Process " + this.id + " received CONVERGECAST message from child process " + message.getSender().getId() );

        //Child and parent are agree on latest shortest distance, parent acknowldge ConvergeCast message from its child.
        if( (message.getDistance() - minusDistance) == this.distance)
        {
            this.convCastChildrenIds.add(message.getSender().getId());
        }
        else //child is missing latest distance information - discarding received ConvergeCast Message
        {
            System.out.println("Process " + this.id + " discards CONVERGECAST message from child process " + message.getSender().getId() + " because distances don't match");
            childrenIds.remove(message.getSender().getId());
            this.needNotifyNeighbors = true;
        }
    }

    private void processTerminateMessage(Message message)
    {
        System.out.println("Process " + this.id + " received TERMINATE message from parent process " + message.getSender().getId() );


        //Sending Terminate Message to its children
        for(String childId : this.childrenIds)
        {
            System.out.println("Process " + this.id + " sending TERMINATE message to the process " + childId);
            Message messageToParentDone = new Message(MessageType.TERMINATE, this);
            this.links.get(childId).getOutQueueFor(this).add(messageToParentDone);
        }
        this.isTerminating = true;

    }




    public void sendMessages()
    {

        //IF RCEIVED NEW DISTANCE INFO
        if(this.needNotifyNeighbors)
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
            this.needNotifyNeighbors = false;
        }//if(this.needTotifyNeighbors)


        //CONVERGECAST
        //IF all children and non-Children have been identified
        if ( this.isConvergeCastMessageAlreadySent == false &&
                //AND All children and non children have been identified
            ((this.childrenIds.size() + this.nonChildrenIds.size() + 1) == this.links.size()) && // 1 is for parent
                //AND if all chidren(if any) has sent their ConvergeCastMessages
             this.childrenIds.size() == this.convCastChildrenIds.size()
                )
        {

            if( this.parent == null)
            {
                System.out.println("Catched null parent for process " + this.id);
            }

            System.out.println("Process " + this.id + " sending CONVERGECAST message with distance " + this.distance + " to the process " + this.parent.getId());
            Message messageToParentDone = new Message(MessageType.CONVERGECAST, this);
            messageToParentDone.setDistance(this.distance);
            this.links.get(this.parent.getId()).getOutQueueFor(this).add(messageToParentDone);
            this.isConvergeCastMessageAlreadySent=true;
        }

        //IF this is root and it received all ConvergeCast Messages from Children - Send TERMINATE message
        if(this.isRoot && this.childrenIds.size() == this.convCastChildrenIds.size() && (this.childrenIds.size() + this.nonChildrenIds.size() == this.links.size() ))
        {
            for(String childId : this.childrenIds)
            {
                System.out.println("Process " + this.id + " sending TERMINATE message to the process " + childId);
                Message messageToParentDone = new Message(MessageType.TERMINATE, this);
                this.links.get(childId).getOutQueueFor(this).add(messageToParentDone);
            }
            this.isTerminating = true;
        }


    }

    @Override
    public void run()
    {

        while(this.isTerminating == false)
        {
            checkAndProcessIncomingMessages();
            sendMessages();
            try
            {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Process " + this.id + " TERMINATED");
    }




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

    public boolean isNeedNotifyNeighbors() {
        return needNotifyNeighbors;
    }

    public void setNeedNotifyNeighbors(boolean needNotifyNeighbors) {
        this.needNotifyNeighbors = needNotifyNeighbors;
    }

    public List<String> getNonChildrenIds() {
        return nonChildrenIds;
    }

    public void setNonChildrenIds(List<String> nonChildrenIds) {
        this.nonChildrenIds = nonChildrenIds;
    }

    public List<String> getConvCastChildrenIds() {
        return convCastChildrenIds;
    }

    public void setConvCastChildrenIds(List<String> convCastChildrenIds) {
        this.convCastChildrenIds = convCastChildrenIds;
    }

    public boolean isConvergeCastMessageAlreadySent() {
        return isConvergeCastMessageAlreadySent;
    }

    public void setConvergeCastMessageAlreadySent(boolean convergeCastMessageAlreadySent) {
        isConvergeCastMessageAlreadySent = convergeCastMessageAlreadySent;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public boolean isTerminating() {
        return isTerminating;
    }

    public void setTerminating(boolean terminating) {
        isTerminating = terminating;
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
