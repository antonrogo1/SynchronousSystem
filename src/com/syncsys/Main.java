package com.syncsys;

import java.util.Map;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        ProcessController processController = new ProcessController();
        //processController.readInputFile("inputNew2.csv");
        processController.readInputFile("inputSimple.txt");

        System.out.println("Finished reading InputFile");

        processController.startProcesses();
        processController.runAsyncBFS();

        System.out.println("All Nodes Terminated\n");
        System.out.println("Final output: \n");

        processController.printFinalResult();
    }
}
