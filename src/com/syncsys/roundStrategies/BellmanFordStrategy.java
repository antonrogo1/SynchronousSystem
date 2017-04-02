//package com.syncsys.roundStrategies;
//
//import com.syncsys.MessagePacket;
//import com.syncsys.ProcessNode;
//import com.syncsys.roundMessages.BellmanFordMessage;
//import com.syncsys.roundMessages.ConvergeCastMessage;
//import com.syncsys.roundMessages.DoneMessage;
//import com.syncsys.roundMessages.RoundMessage;
//import com.syncsys.roundMessages.TerminateMessage;
//
//
//@Deprecated
//public class BellmanFordStrategy implements RoundStrategy {
//	
//	/*** 
//	 * As defined in Distributed Algorithms by Nancy A. Lynch (p. 62). 
//	 * The Bellman Ford Algorithm for Shortest Path finding is as follows:
//	 * 
//	 * 		Each Process 'i' keeps track of 'dist', the shortest distance
//	 * 		from 'i0' it knows so far, together with 'parent' the incoming
//	 * 		neighbor that precedes 'i' in a path whose weight is 'dist'.
//	 * 
//	 * 		Initially, i0.dist = 0, i.dist = +INF for i != i0, and the
//	 * 		'parent' components are undefined.
//	 * 
//	 * 		At each round, each process sends its 'dist' to all its outgoing
//	 * 		neighbors. 
//	 * 
//	 * 		Then each process 'i' updates its 'dist' by a "relaxation step,"
//	 * 		in which it takes the minimum of its previous 'dist' value and
//	 * 		all the values j.dist + i.weight(j), where 'j' is an incoming 
//	 * 		neighbor. 
//	 * 
//	 * 		If 'dist' is changed, the 'parent' components is also updated
//	 * 		accordingly. After n-1 rounds, 'dist' contains the shortest
//	 * 		distance, and 'parent' the parent in the shortest paths tree.
//	 *
//	 */
//
//	private ProcessNode process;
//	
//	public BellmanFordStrategy(ProcessNode process) {
//		this.process = process;
//	}
//	
//	@Override
//    public void generateMessages() {
//		for (ProcessNode neighbor : getProcess().getNeighbors().values()) {
//			
//			MessagePacket packet = new MessagePacket(process.getId(), neighbor.getId());
//			
//			// Send BellmanFord message
//			BellmanFordMessage search = new BellmanFordMessage();
//			search.setSenderID(process.getId());
//			search.setDistance(process.getDist());
//			packet.addMessage(search);
//			
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
//			
//			neighbor.addMessage(packet);
//		}
//    }
//
//	@Override
//    public void processMessages() {
//		process.getChildIDs().clear();
//		process.getDoneChildIDs().clear();
//		process.getSearchIDs().clear();
//		process.getResponseIDs().clear();
//
//		for (MessagePacket packet : process.getMessagesToProcess()) {
//			for (RoundMessage message : packet.getMessages()) {
//				
//				// Only the message knows how it should be processed
//				// Thus we give control to the message for processing
//				
//				message.handleUsing(process);
//			}
//		}
//		
//		if (null != process.getParent() && allChildrenDone()) {
//			process.setDone(true);
//		}
//		
//		if (process.isRoot() && allChildrenDone()) {
//			process.setTerminating(true);
//		}
//    }
//
//	private boolean allChildrenDone() {
//	    boolean allSearchesResponded = process.getResponseIDs().size() == process.getNeighbors().size();
//	    boolean allChildrenDone = process.getChildIDs().size() == process.getDoneChildIDs().size();
//	    return allSearchesResponded && allChildrenDone;
//    }
//
//	@Override
//    public void execute() {
//		processMessages();
//		generateMessages();
//		
//		System.out.println(
//				"id: " + process.getId() + ", " +
//				"dist: " + process.getDist() + ", " +
//				"done: " + process.isDone() + ", " +
//				"terminating: " + process.isTerminating() + ", " +
//				"children/done: " + process.getChildIDs().size() + "/" + process.getDoneChildIDs().size() + ", " +
//				((null != process.getParent()) ? ("parent: " + process.getParent().getId()) : ""));
//	}
//
//	//**************************************************************************************************//
//	//                                                                                                  //
//	//**************************************************************************************************//
//	
//	// Getters / Setters
//
//	@Override
//	public ProcessNode getProcess() {
//	    return process;
//    }
//
//	@Override
//	public void setProcess(ProcessNode process) {
//	    this.process = process;
//    }
//}