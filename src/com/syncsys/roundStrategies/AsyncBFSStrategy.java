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
	 * It is a modfied SyncBFS where each node manages the distance. 
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
			
			MessagePacket packet = new MessagePacket(process.getId(), neighbor.getId());

			// Send BFS message
			if (process.isNeedsToSendInitialSearch() || null != process.getParent()) {
				BFSMessage search = new BFSMessage();
				search.setSenderID(process.getId());
				search.setDistance(process.getDist());
				packet.addMessage(search);
			}
			
//			// Send ConvergeCast message
//			if (process.getSearchIDs().contains(neighbor.getId())) {
//				ConvergeCastMessage response = new ConvergeCastMessage();
//				response.setSenderID(process.getId());
//				response.setChild(null != process.getParent() && neighbor.getId() == process.getParent().getId());
//				packet.addMessage(response);
//			}
//			
//			// Send Done message to parent
//			if (null != process.getParent() && neighbor.getId() == process.getParent().getId() && process.isDone()) {
//				DoneMessage done = new DoneMessage();
//				done.setSenderID(process.getId());
//				packet.addMessage(done);
//			}
//			
//			// Send Terminate message to children
//			if (process.isTerminating() && process.getChildIDs().contains(neighbor.getId())) {
//				TerminateMessage terminate = new TerminateMessage();
//				terminate.setSenderID(process.getId());
//				packet.addMessage(terminate);
//			}
			
			neighbor.addMessage(packet);
		}
    }

	@Override
    public void processMessages() {
		process.getChildIDs().clear();
		process.getDoneChildIDs().clear();
		process.getSearchIDs().clear();
		process.getResponseIDs().clear();

		for (MessagePacket packet : process.getMessagesToProcess()) {
			for (RoundMessage message : packet.getMessages()) {
				
				// Only the message knows how it should be processed
				// Thus we give control to the message for processing
				
				message.handleUsing(process);
			}
		}
		
		if (null != process.getParent() && allChildrenDone()) {
			process.setDone(true);
		}
		
		if (process.isRoot() && allChildrenDone()) {
			process.setTerminating(true);
		}
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
		
		process.setNeedsToSendInitialSearch(false);
		process.setNeedsToSendMessages(false);
		
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