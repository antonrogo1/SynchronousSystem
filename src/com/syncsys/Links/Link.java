package com.syncsys.Links;

import com.syncsys.roundMessages.RoundMessage;

/**
 * Created by z on 3/26/17.
 */
public abstract class Link {
    private static long round = -1;

    public abstract void sendMessage(RoundMessage msg);

    public abstract boolean hasAvailableMessage();

    public abstract RoundMessage receiveMessage();

    public static void incrementRound(){
        round++;
    }

    public static long getRound(){
        return round;
    }
}
