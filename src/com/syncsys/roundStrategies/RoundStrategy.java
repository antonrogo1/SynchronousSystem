package com.syncsys.roundStrategies;

import com.syncsys.ProcessNode;

public interface RoundStrategy {
	
	/*** 
	 * As defined in Distributed Algorithms by Nancy A. Lynch (p. 18). 
	 * The two phases of a round are as follows:
	 * 
	 * 1.	Apply the message-generation function to the current state to
	 * 		generate the messages to be sent to all outgoing neighbors.
	 * 		Put these messages in the appropriate channels.
	 * 
	 * 2.	Apply the state-transition function to the current state and 
	 * 		the incoming messages to obtain the new state. Remove all messages
	 * 		from the channels.
	 */

	void generateMessages();
	void processMessages();
	void execute();
}
