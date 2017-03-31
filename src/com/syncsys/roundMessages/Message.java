package com.syncsys.roundMessages;

import com.syncsys.roundMessages.enums.MessageType;

/**
 * Created by anton on 3/30/2017.
 */
public class Message
{
    private MessageType messageType;         //What type of message
    private int totalTransmisissionDuration; // how long it will take for message to arrive to destination
    private int transmissionStartTime;

    public Message(MessageType messageType, int transmissionStartTime)
    {
        this.messageType = messageType;
        this.transmissionStartTime = transmissionStartTime;
        this.totalTransmisissionDuration = generateTransmisissionDuration();
    }

    private int generateTransmisissionDuration(){
        final int MIN_BOUND = 1;
        final int MAX_BOUND = 19;
        return (int) ((Math.random()* (MAX_BOUND-MIN_BOUND)) + MIN_BOUND);
    }



    /**
      * GETTERS / SETTERS
      */

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public int getTotalTransmisissionDuration() {
        return totalTransmisissionDuration;
    }

    public void setTotalTransmisissionDuration(int totalTransmisissionDuration) {
        this.totalTransmisissionDuration = totalTransmisissionDuration;
    }

    public int getTransmissionStartTime() {
        return transmissionStartTime;
    }

    public void setTransmissionStartTime(int transmissionStartTime) {
        this.transmissionStartTime = transmissionStartTime;
    }
}
