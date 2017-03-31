package com.syncsys.roundMessages.oldCrap;

import com.syncsys.ProcessNode;
import com.syncsys.roundStrategies.RoundStrategy;

public interface RoundMessage { 
	void processUsing(RoundStrategy strategy);
	String getSenderID();
	void setSenderID(String id);
    void setSender(ProcessNode node);
    ProcessNode getSender();
}