package com.syncsys.roundStrategies;

import com.syncsys.MessagePacket;
import com.syncsys.ProcessNode;
import com.syncsys.roundMessages.BFSMessage;
import com.syncsys.roundMessages.ConvergeCastMessage;
import com.syncsys.roundMessages.DoneMessage;
import com.syncsys.roundMessages.RoundMessage;
import com.syncsys.roundMessages.TerminateMessage;

public class AsyncBFSStrategy implements RoundStrategy {
	
	/*** 
	 * AsyncBFS is defined in Distributed Algorithms by Nancy A. Lynch (p. 502).
	 * It is a modified SyncBFS where each node manages the distance. 
	 *
	 */

	private ProcessNode process;
	
	public AsyncBFSStrategy(ProcessNode process) {
		this.process = process;
	}
	
	@Override
    public void generateMessages() {
		if (!process.isNeedsToSendMessages()) { return; }
		
		for (ProcessNode neighbor : getProcess().getNeighbors().values()) {
			
			// Create a packet to hold all messages towards a neighbor
			MessagePacket packet = new MessagePacket(process.getId(), neighbor.getId());

			// Send BFS message
			if (process.isNeedsToSendInitialSearch() || process.isGotNewParent()) {
				BFSMessage search = new BFSMessage();
				search.setSenderID(process.getId());
				search.setDistance(process.getDist());
				packet.addMessage(search);
			}
			
			// Send ConvergeCast message
			if (process.getSearchIDs().contains(neighbor.getId())) {
				ConvergeCastMessage response = new ConvergeCastMessage();
				response.setSenderID(process.getId());
				response.setChild(isNeighborParent(neighbor));
				packet.addMessage(response);
				
				// Remove the ID to avoid sending the response again
				process.getSearchIDs().remove(neighbor.getId());
			}
			
			// Send Done message to parent
			if (isNeighborParent(neighbor) && process.isDone() && process.isNeedsToSendDoneToParent()) {
				DoneMessage done = new DoneMessage();
				done.setSenderID(process.getId());
				packet.addMessage(done);
				
				process.setNeedsToSendDoneToParent(false);
			}
			
//			// Send Terminate message to children
//			if (process.isTerminating() && process.getChildIDs().contains(neighbor.getId())) {
//				TerminateMessage terminate = new TerminateMessage();
//				terminate.setSenderID(process.getId());
//				packet.addMessage(terminate);
//			}
			
			neighbor.addMessage(packet);
		}
    }

	private boolean isNeighborParent(ProcessNode neighbor) {
		return null != process.getParent() && neighbor.getId() == process.getParent().getId();
	}

	@Override
    public void processMessages() {
		for (MessagePacket packet : process.getMessagesToProcess()) {
			for (RoundMessage message : packet.getMessages()) {
				
				// Only the message knows how it should be processed
				// Thus we give control to the message for processing
				
				message.handleUsing(process);
			}
		}
		
		if (!process.isDone() && null != process.getParent() && allChildrenDone()) {
			process.setDone(true);
			process.setNeedsToSendMessages(true);
		}
		
//		if (process.isRoot() && allChildrenDone()) {
//			process.setTerminating(true);
//		}
    }

	private boolean allChildrenDone() {
	    boolean allSearchesResponded = process.getResponseIDs().size() == process.getNeighbors().size();
	    boolean allChildrenDone = process.getChildIDs().size() == process.getDoneChildIDs().size();
	    return allSearchesResponded && allChildrenDone;
    }

	@Override
    public void execute() {
		processMessages();
		generateMessages();
		resetMessageFlags();
		
		System.out.println(
				"id: " + process.getId() + ", " +
				"dist: " + process.getDist() + ", " +
				"done: " + process.isDone() + ", " +
				"terminating: " + process.isTerminating() + ", " +
				"children/done: " + process.getChildIDs().size() + "/" + process.getDoneChildIDs().size() + ", " +
				"msg queue: " + process.getMessages().size() + ", " +
				"sender/delay: " + getDelays() + ", " +
				((null != process.getParent()) ? ("parent: " + process.getParent().getId()) : ""));
	}
	
	private void resetMessageFlags() {
		process.setGotNewParent(false);
		process.setNeedsToSendMessages(false);
		process.setNeedsToSendInitialSearch(false);
	}
	
	private String getDelays() {
		String delay = "{";
		
		for(MessagePacket message : process.getMessages()) {
			delay += message.getSenderId() + "/" + message.getDelay() + "; ";
		}
		
		return delay + "}";
	}

	//**************************************************************************************************//
	//                                                                                                  //
	//**************************************************************************************************//
	
	// Getters / Setters

	@Override
	public ProcessNode getProcess() {
	    return process;
    }

	@Override
	public void setProcess(ProcessNode process) {
	    this.process = process;
    }
}