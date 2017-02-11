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
    private Map<Integer,Process> processes;

    public ProcessController()
    {
        processes = new LinkedHashMap<Integer,Process>(); //We want to quickly get to the process by using its id.
    }

    public void runSingleRound()
    {

        for(Process process : processes.values())
        {
            process.resetRoundToStart();
        }

        for(Process process : processes.values())
        {
            Thread thread = new Thread(process);
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
            ArrayList<Process> orderedProcesses = new ArrayList<Process>();

            //Reading second line - getting ids of processes and storing them to the HashMap
            line = bufferedReader.readLine();
            for(String stringId : line.split(","))
            {

                Process process = new Process(Integer.parseInt(stringId));
                this.processes.put(Integer.parseInt(stringId), process);
                orderedProcesses.add(process);
            }


            /*
            * Reading connectivity matrix depending on matrix size, and adding neighbors to the thread
            * Each line is the current process we establishing links for, each column is the the other processes in relation to current process.
            * */
            for(Process process : this.processes.values())
            {
                line = bufferedReader.readLine();

                String[] connectionWeights = line.split(",");
                for(int i = 0; i< connectionWeights.length; i++)
                {
                    if( Integer.parseInt(connectionWeights[i]) != -1)
                        process.addNeighbor(orderedProcesses.get(i).getId(), Integer.parseInt(connectionWeights[i]));
                }

            }


            bufferedReader.close();
        }
        catch (Exception e)
        {e.printStackTrace();}
    }


    //fucntion to check if round complete
    private boolean isRoundComplete()
    {
        boolean isRoundComplete = true;

        for(Process process : processes.values())
        {
            if (process.isRoundCompleted() ==false)
                return false;

        }
        return isRoundComplete;
    }
}
