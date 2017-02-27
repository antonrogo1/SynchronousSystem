package com.syncsys;

public class Main {

    public static void main(String[] args)
    {
        ProcessController processController = new ProcessController();
        processController.readInputFile("inputSimple.txt");

        System.out.println("Finished reading InputFile");

        while (!processController.isAllNodesTerminated()) {
            processController.runSingleRound();
        }

    }
}
