package com.syncsys;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.syncsys.roundMessages.RoundMessage;

public class MessagePacket {
	public static final int MIN_DELAY = 1;
	public static final int MAX_DELAY = 5; //19;
	
	private static volatile Map<String, Map<String, Integer>> nodeDelaysFromSenderToRecipient = new ConcurrentHashMap<String, Map<String, Integer>>();
	private Integer delay; 
	
	private List<RoundMessage> messages;
	
	public MessagePacket(String senderId, String recipientId) {
		delay = (int) ((Math.random() * (MAX_DELAY-MIN_DELAY)) + MIN_DELAY);
		
		// Handle node delays
		Map<String, Integer> nodeDelaysFromSender = nodeDelaysFromSenderToRecipient.get(senderId);
		if (null == nodeDelaysFromSender) {
			nodeDelaysFromSender = new ConcurrentHashMap<String, Integer>();
			nodeDelaysFromSenderToRecipient.put(senderId, nodeDelaysFromSender);
		}
		else {
			Integer nodeDelayFromSenderToRecipient = nodeDelaysFromSender.get(recipientId);
			
			if (null != nodeDelayFromSenderToRecipient) {
				delay += nodeDelayFromSenderToRecipient;
			}
		}
		nodeDelaysFromSender.put(recipientId, delay);
		
		messages = new ArrayList<RoundMessage>();
	}
	
	public void addMessage(RoundMessage message) {
		messages.add(message);
	}
	
	public List<RoundMessage> getMessages() {
		return messages;
	}
}
