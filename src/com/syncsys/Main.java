package com.syncsys;

import java.util.Map;

public class Main {

    public static void main(String[] args)
    {
        ProcessController processController = new ProcessController();
        processController.readInputFile("inputNew.txt");

        System.out.println("Finished reading InputFile");

        while (!processController.isAllNodesTerminated()) {
            processController.runSingleRound();
        }

        System.out.println("All Nodes Terminated\n");
        System.out.println("Final output: \n");
        System.out.println("Root ID: " + processController.getRootID());

        processController.printFinalResult();
    }
}
