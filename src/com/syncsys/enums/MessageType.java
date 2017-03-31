package com.syncsys.enums;

/**
 * Created by anton on 3/30/2017.
 */
public enum MessageType {
    EXPLORE,                 //Message to find Children
    PARENT_ACKNOWLEDGE,      //Response-Message to acknowldge parent
    NOTPARENT_ACKNOWLEDGE,   //Response-Message to EXPLORE message to not-parent node
    CONVERGECAST,            //ConvergeCast - (I am explored all the way down)
    TERMINATE                 //Terminate Message for children
}
