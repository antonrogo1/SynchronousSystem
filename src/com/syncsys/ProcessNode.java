package com.syncsys;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


import com.syncsys.enums.MessageType;


/**
 * Created by anton on 2/9/2017.
 */
public class ProcessNode implements Runnable
{
    private String id;                              //Id of process
    private int hopDistance;                        //best distance to the root process know so far

    private Map<String, AsyncLink> links;   	   //Map of tuples: (id Of Neighbor process, link)
    private Map<String, ProcessNode> neighbors;    //Map of tuples: (id Of Neighbor process, neighbor)

    private ProcessNode parent;
    private Set<String> childrenIds;         //List of Children - nodes that acknowldged this node as Parent
    private Set<String> nonChildrenIds;      //List of NonChildren - nodes that acknowldged this node as Not-Parent

    private Set<String> convCastChildrenIds;       //List of Children - that send ConvergeCastMessage


    private Set<String> doneChildIDs;        //List of Children nodes that identified them as Complete (them and their children found Shortest Path)

    private List<String> responseIDs;

    private List<String> searchIDs;           //List of nodes to which this process send  TRASH

    private boolean isRoot;
    private boolean needNotifyNeighbors;              //flag indicating that Process have need infomation on distance and need to notify Neighbors
    private boolean isConvergeCastMessageAlreadySent; //flag indicating that Process already send ConvergeCast Message
    private boolean isTerminating;                    //flag indicating that process needs to terminate

    private Map<String,  List<MessageType>> messagesThatShouldBePacketed = new ConcurrentHashMap<String, List<MessageType>>();

    public ProcessNode(String id)
    {
        this.id = id;
        this.hopDistance = Integer.MAX_VALUE;
        this.links = new ConcurrentHashMap<String, AsyncLink>();
        this.neighbors = new ConcurrentHashMap<String, ProcessNode>();
        this.childrenIds = new LinkedHashSet<String>();
        this.nonChildrenIds = new LinkedHashSet<String>();
        this.convCastChildrenIds = new LinkedHashSet<String>();

        this.isRoot = false;
        this.needNotifyNeighbors = true;
        this.isConvergeCastMessageAlreadySent = false;
        this.isTerminating = false;
    }

    private void addToPacketTest(String id, MessageType message) {
    	List<MessageType> messagesForId = messagesThatShouldBePacketed.get(id);
    	if (null != messagesForId) {
    		messagesForId.add(message);
    	}
    	else {
    		messagesForId = new ArrayList<MessageType>();
    		messagesForId.add(message);
    		messagesThatShouldBePacketed.put(id, messagesForId);
    	}
    }

    public void checkAndProcessIncomingMessages()
    {

        for (AsyncLink asyncLink : this.links.values())
        {
            List<Message> arrivedMessages = asyncLink.getArrivedMessagesFor(this);
            for(Message message : arrivedMessages)
            {
                if (message.getMessageType() == MessageType.EXPLORE) {
                    processExploreMessage(message);
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
                    processConvergeCastMessage(message);
                }
                else if (message.getMessageType() == MessageType.TERMINATE) {
                    processTerminateMessage(message);
                }
            }
        }
    }

    private void processExploreMessage(Message message)
    {

        int newHopDistanceFromNeighboor = message.getHopDistance(); //neighbors hopDistance (not counting hop distance to this node)

        //If node received EXPLORE message from child - it means child got a new parent (Child is no longer a child)
        if(childrenIds.contains(message.getSender().getId()))
            childrenIds.remove(message.getSender().getId());

        if(nonChildrenIds.contains(message.getSender().getId()))
            nonChildrenIds.remove(message.getSender().getId());

        if(newHopDistanceFromNeighboor != Integer.MAX_VALUE && newHopDistanceFromNeighboor + 1 < this.hopDistance ) //one for single extra hop
        {
            if(this.parent !=null)
            {
                if (this.parent.getId() != message.getSender().getId())
                {
                    //Sending message to old parent rejecting him as parent
                    Message messageToParent = new Message(MessageType.NOTPARENT_ACKNOWLEDGE, this);
                    this.links.get(parent.getId()).getOutQueueFor(this).add(messageToParent);
                    addToPacketTest(parent.getId(), MessageType.NOTPARENT_ACKNOWLEDGE);
                }
            }

            this.hopDistance = newHopDistanceFromNeighboor + 1;
            this.parent = message.getSender();

            System.out.println("Process " + this.id + " now have parent " + parent.getId() + " and distance " + this.hopDistance);

            //Sending message to parent acknowledging him as parent
            Message messageToParent = new Message(MessageType.PARENT_ACKNOWLEDGE, this);
            addToPacketTest(parent.getId(), MessageType.PARENT_ACKNOWLEDGE);

            //We returning the distance that we got from original sender(parent), So parent know which latest version of distance this node got,
            //there could be a case that parent got better distance, so it need to know  to resend latest distance.
            messageToParent.setHopDistance(newHopDistanceFromNeighboor);

            this.links.get(parent.getId()).getOutQueueFor(this).add(messageToParent);  //getting async link to parent and adding ack message

            //Non-Children may become children in the future
            nonChildrenIds = new LinkedHashSet<String>();
            this.needNotifyNeighbors = true;
        }
        else // returning ack-message to original sender so it knows its message was processed
        {
            Message messageToNonParent = new Message(MessageType.NOTPARENT_ACKNOWLEDGE, this);
            addToPacketTest(message.getSender().getId(), MessageType.NOTPARENT_ACKNOWLEDGE);


            //We returning the distance that we got from original sender(Non-parent), So non-parent know which latest version of distance this node got,
            //there could be a case that non-parent got better distance, so it need to know  to resend latest distance.
            messageToNonParent.setHopDistance(newHopDistanceFromNeighboor);

            this.links.get(message.getSender().getId()).getOutQueueFor(this).add(messageToNonParent);  //getting async link to non-parent and adding ack message (Not-Parent)
        }
    }

    private void processParentAcknowldgeMessage(Message message)
    {
        if(message.getHopDistance() == this.hopDistance) {
            System.out.println("Process " + this.id + " received ack-message from child-process " + message.getSender().getId() + " acknowledging it as parent");
            this.childrenIds.add(message.getSender().getId());

            this.needNotifyNeighbors = true;

            if(nonChildrenIds.contains(message.getSender().getId()))
                nonChildrenIds.remove(message.getSender().getId());
        }
        else 
            {
                if(childrenIds.contains(message.getSender().getId()))
                    childrenIds.remove(message.getSender().getId());
            System.out.println("Process " + this.id + " received ack-message NOT-PARENT from neighbor process " + message.getSender().getId() + " BUT HOP-DISTANCE DIDN'T MATCHES");
        }
    }

    private void processNotParentAcknowldgeMessage(Message message)
    {
        if(message.getHopDistance() == this.hopDistance) {
            System.out.println("Process " + this.id + " received ack-message NOT-PARENT from neighbor process " + message.getSender().getId() + " rejecting it as parent");
            this.nonChildrenIds.add(message.getSender().getId());


            if(childrenIds.contains(message.getSender().getId())) {
                childrenIds.remove(message.getSender().getId());
                this.needNotifyNeighbors = true;
            }
        }
        else
        {
            System.out.println("Process " + this.id + " received ack-message NOT-PARENT from neighbor process " + message.getSender().getId() + " BUT HOP-DISTANCE DIDN'T MATCHES");
        }
    }


    //minusDistance used to make sure that Child and Parent are in sync on what is the latest shortest distance is.
    private void processConvergeCastMessage(Message message)
    {

        System.out.println("Process " + this.id + " received CONVERGECAST message from child process " + message.getSender().getId() );

        //Child and parent are agree on latest shortest hop distance, parent acknowldge ConvergeCast message from its child.
        if( (message.getHopDistance() - 1) == this.hopDistance)
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

        for(String neighborId : this.links.keySet())

        {
            System.out.println("Process " + this.id + " sending TERMINATE message to the process " + neighborId);
            Message messageTerminateToCHild = new Message(MessageType.TERMINATE, this);

            addToPacketTest(neighborId, MessageType.TERMINATE);
            this.links.get(neighborId).getOutQueueFor(this).add(messageTerminateToCHild);
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
                    System.out.println("Process " + this.id + " sending EXPLORE message with distance " + this.hopDistance + " to the process " + neighborId);
                    Message messageToNeighbors = new Message(MessageType.EXPLORE, this);
                    addToPacketTest(neighborId, MessageType.EXPLORE);
                    messageToNeighbors.setHopDistance(this.hopDistance);
                    this.links.get(neighborId).getOutQueueFor(this).add(messageToNeighbors);
                }
                else
                    {
                    if (!neighborId.equals(parent.getId())) {
                        System.out.println("Process " + this.id + " sending EXPLORE message with distance " + this.hopDistance + " to the process " + neighborId);
                        Message messageToNeighbors = new Message(MessageType.EXPLORE, this);
                        addToPacketTest(neighborId, MessageType.EXPLORE);
                        messageToNeighbors.setHopDistance(this.hopDistance);
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
             this.childrenIds.size() == this.convCastChildrenIds.size() &&
                this.parent != null
                )
        {

            System.out.println("Process " + this.id + " sending CONVERGECAST message with distance " + this.hopDistance + " to the process " + this.parent.getId());
            Message messageToParentDone = new Message(MessageType.CONVERGECAST, this);
            addToPacketTest(parent.getId(), MessageType.CONVERGECAST);
            messageToParentDone.setHopDistance(this.hopDistance);
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
                addToPacketTest(childId, MessageType.TERMINATE);
                this.links.get(childId).getOutQueueFor(this).add(messageToParentDone);
            }
            this.isTerminating = true;
        }
        
        for (String id : messagesThatShouldBePacketed.keySet()) {
        	List<MessageType> messagesForId = messagesThatShouldBePacketed.get(id);
        	if (null != messagesForId && messagesForId.size() > 1) {
        		String shouldBePacketed = "=== Process " + this.id + " should packet messages to " + id + ": ";
        		
        		for (MessageType messageType : messagesForId) {
        			shouldBePacketed += messageType + ", ";
				}
        		
        		System.out.println(shouldBePacketed);
        	}
        }
        messagesThatShouldBePacketed = new ConcurrentHashMap<String, List<MessageType>>();
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
                Thread.sleep(50
                );
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

    public int getHopDistance() {
        return hopDistance;
    }

    public void setHopDistance(int hopDistance) {
        this.hopDistance = hopDistance;
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

    public Set<String> getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(Set<String> childrenIds) {
        this.childrenIds = childrenIds;
    }

    public void setNonChildrenIds(Set<String> nonChildrenIds) {
        this.nonChildrenIds = nonChildrenIds;
    }

    public void setConvCastChildrenIds(Set<String> convCastChildrenIds) {
        this.convCastChildrenIds = convCastChildrenIds;
    }

    public Set<String> getDoneChildIDs() {
        return doneChildIDs;
    }

    public void setDoneChildIDs(Set<String> doneChildIDs) {
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

    public Set<String> getNonChildrenIds() {
        return nonChildrenIds;
    }

    public Set<String> getConvCastChildrenIds() {
        return convCastChildrenIds;
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
