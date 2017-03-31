package com.syncsys;

import com.syncsys.enums.MessageType;

/**
 * Created by anton on 3/30/2017.
 */
public class Message
{
    private MessageType messageType;         //What type of message
    private int distance;                    //for Search Message Types
    private ProcessNode sender;                 //SenderId
    private int totalTransmisissionDuration; // how long it will take for message to arrive to destination
    private int transmisissionDurationSoFar; // for how long this message have been traveled
    private int transmissionStartTime;

    public Message(MessageType messageType, ProcessNode sender)
    {
        this.messageType = messageType;
        this.sender = sender;
        this.transmisissionDurationSoFar = 0;
        this.totalTransmisissionDuration = generateTransmisissionDuration();
    }

    private int generateTransmisissionDuration(){
        final int MIN_BOUND = 1;
        final int MAX_BOUND = 3; //switch back to 19
        return (int) ((Math.random()* (MAX_BOUND-MIN_BOUND)) + MIN_BOUND);
    }


    //Tells if message is arrived
    public boolean isMessageArrived()
    {
        boolean result = false;

        if(transmisissionDurationSoFar >= totalTransmisissionDuration)
            result = true;

        return result;
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

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public ProcessNode getSender() {
        return sender;
    }

    public void setSender(ProcessNode sender) {
        this.sender = sender;
    }

    public int getTransmisissionDurationSoFar() {
        return transmisissionDurationSoFar;
    }

    public void setTransmisissionDurationSoFar(int transmisissionDurationSoFar) {
        this.transmisissionDurationSoFar = transmisissionDurationSoFar;
    }
}
