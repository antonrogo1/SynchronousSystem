package com.syncsys.roundMessages;

import com.syncsys.ProcessNode;
import com.syncsys.roundStrategies.RoundStrategy;

public interface RoundMessage { 
	void processUsing(RoundStrategy strategy);
}