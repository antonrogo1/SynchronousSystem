package com.syncsys;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by anton on 2/9/2017.
 */
public class ProcessController
{
    private Map<Integer, ProcessNode> processes;

    public ProcessController()
    {
        processes = new LinkedHashMap<Integer, ProcessNode>(); //We want to quickly get to the process by using its id.
    }

    public void runSingleRound()
    {

        for(ProcessNode processNode : processes.values())
        {
            processNode.resetRoundToStart();
        }

        for(ProcessNode processNode : processes.values())
        {
            Thread thread = new Thread(processNode);
            thread.start();
        }

        while(this.isRoundComplete() ==false)
        {
            try {
                System.out.println("Checking if round complete");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Round completed");
        return;
    }


    public void readInputFile(String inputFileName)
    {
        int lineCounter=1;
        int matrixSize;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFileName));
            StringBuilder stringBuilder = new StringBuilder();

            //Reading first line - determining number of processes and size of matrix
            String line = bufferedReader.readLine();
            matrixSize = Integer.parseInt(line);


            //Used to prerve order of ids - will be used when reading connectivity matrix
            ArrayList<ProcessNode> orderedProcesses = new ArrayList<ProcessNode>();

            //Reading second line - getting ids of processes and storing them to the HashMap
            line = bufferedReader.readLine();
            for(String stringId : line.split(","))
            {

                ProcessNode processNode = new ProcessNode(Integer.parseInt(stringId));
                this.processes.put(Integer.parseInt(stringId), processNode);
                orderedProcesses.add(processNode);
            }


            /*
            * Reading connectivity matrix depending on matrix size, and adding neighbors to the thread
            * Each line is the current process we establishing links for, each column is the the other 
            * processes in relation to current process.
            * */
            for(ProcessNode processNode : this.processes.values())
            {
                line = bufferedReader.readLine();

                String[] connectionWeights = line.split(",");
                for(int i = 0; i< connectionWeights.length; i++)
                {
                    if( Integer.parseInt(connectionWeights[i]) != -1) {
                        processNode.addNeighbor(
                        		orderedProcesses.get(i).getID(), 
                        		Integer.parseInt(connectionWeights[i]),
                        		orderedProcesses.get(i));
                    }
                }

            }


            bufferedReader.close();
        }
        catch (Exception e)
        {e.printStackTrace();}
    }


    //Function to check if round complete
    private boolean isRoundComplete()
    {
        boolean isRoundComplete = true;

        for(ProcessNode processNode : processes.values())
        {
            if (processNode.isRoundCompleted() ==false)
                return false;

        }
        return isRoundComplete;
    }
}
