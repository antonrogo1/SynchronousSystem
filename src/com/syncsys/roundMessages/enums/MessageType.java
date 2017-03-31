package com.syncsys.roundMessages.enums;

/**
 * Created by anton on 3/30/2017.
 */
public enum MessageType {
    EXPLORE,              //Message to find Children
    PARENT_ACKNOWLEDGE,   //Message to acknowldge parent
    CONVERGECAST,         //ConvergeCast - (I am explored all the way down)
    DONE,
    TERMINATE             //Terminate Message for children
}
