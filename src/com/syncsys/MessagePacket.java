package com.syncsys;

import java.util.ArrayList;
import java.util.List;
import com.syncsys.roundMessages.RoundMessage;

public class MessagePacket {
	private List<RoundMessage> messages;
	
	public MessagePacket() {
		messages = new ArrayList<RoundMessage>();
	}
	
	public void addMessage(RoundMessage message) {
		messages.add(message);
	}
	
	public List<RoundMessage> getMessages() {
		return messages;
	}
}
