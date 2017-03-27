package com.syncsys;

import com.syncsys.factories.AsyncBFSFactory;
import com.syncsys.factories.FactoryHolder;

import java.util.Map;

public class Main {
    public static void main(String[] args)
    {
        FactoryHolder.setFactory(new AsyncBFSFactory());
        ProcessController processController = new ProcessController();
        processController.readInputFile("inputNew2.csv");

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
