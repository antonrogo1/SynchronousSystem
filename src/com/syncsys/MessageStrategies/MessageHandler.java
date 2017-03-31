package com.syncsys.MessageStrategies;

import com.syncsys.ProcessNode;
import com.syncsys.roundMessages.oldCrap.RoundMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by z on 3/26/17.
 */
public class MessageHandler {
//    Map<ProcessNode, Link> outgoingLinks = new HashMap<ProcessNode, Link>();
//    Map<ProcessNode, Link> incomingLinks = new HashMap<ProcessNode, Link>();
//
//    public void send(ProcessNode recipient, RoundMessage message){
//        Link link = outgoingLinks.get(recipient);
//        //deliberately not handling the case where link is null. That shouldn't happen, so we want to fail fast.
//        link.sendMessage(message);
//    }
//
//    public void broadcast(RoundMessage message){
//        for(Link link : outgoingLinks.values()){
//            link.sendMessage(message);
//        }
//    }
//    public List<RoundMessage> getMessages(){
//        List<RoundMessage> list = new ArrayList<RoundMessage>();
//        for(Link link:incomingLinks.values()){
//            while(link.hasAvailableMessage()){
//                list.add(link.receiveMessage());
//            }
//        }
//        return list;
//    }
//
//    public void addOutgoingLink(ProcessNode node, Link link){
//        outgoingLinks.put(node, link);
//    }
//    public void addIncomingLink(ProcessNode node, Link link){
//        incomingLinks.put(node, link);
//    }

}
