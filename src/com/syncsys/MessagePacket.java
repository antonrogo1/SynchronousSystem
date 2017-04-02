package com.syncsys;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.syncsys.roundMessages.RoundMessage;

public class MessagePacket {
	public static final int MIN_DELAY = 1;
	public static final int MAX_DELAY = 5; //18;
	
	private static volatile Map<String, Map<String, Integer>> nodeDelaysFromSenderToRecipient = 
			new ConcurrentHashMap<String, Map<String, Integer>>();
 	
	private List<RoundMessage> messages;
	private String senderId;
	private String recipientId;
	private int delay;
	
	public MessagePacket(String senderId, String recipientId) {
		this.messages = new ArrayList<RoundMessage>();
		this.senderId = senderId;
		this.recipientId = recipientId;
		
		calculateMessageDelay();
	}

	// Calculate delay in such a way that FIFO is preserved
	private void calculateMessageDelay() {
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
		
	}
	
	//**************************************************************************************************//
	//                                                                                                  //
	//**************************************************************************************************//
	
	// Getters / Setters
	
	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		Map<String, Integer> nodeDelaysFromSender = nodeDelaysFromSenderToRecipient.get(senderId);
		Integer nodeDelayFromSenderToRecipient = nodeDelaysFromSender.get(recipientId);
		
		if (delay == nodeDelayFromSenderToRecipient - 1) {
			nodeDelaysFromSender.put(recipientId, delay);
		}
		
		this.delay = delay;
	}

	public void addMessage(RoundMessage message) {
		messages.add(message);
	}
	
	public List<RoundMessage> getMessages() {
		return messages;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
}
