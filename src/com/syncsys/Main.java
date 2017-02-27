package com.syncsys;

import java.util.Map;

public class Main {

    public static void main(String[] args)
    {
        ProcessController processController = new ProcessController();
        processController.readInputFile("professorInput.txt");

        System.out.println("Finished reading InputFile");

        while (!processController.isAllNodesTerminated()) {
            processController.runSingleRound();
        }

        System.out.println("All Nodes Terminated\n");
        System.out.println("Final output: \n");
        System.out.println("Root ID: " + processController.getRootID());
        
        Map<Integer, Integer> nodeParentPairs = processController.getNodeParentPairs();
        for (Integer nodeID : nodeParentPairs.keySet()) {
        	System.out.println(
        			"Node ID: " + nodeID + ", " + 
        			"Parent ID: " + nodeParentPairs.get(nodeID));
        }
    }
}
