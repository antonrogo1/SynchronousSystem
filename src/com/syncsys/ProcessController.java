package com.syncsys;

import com.syncsys.roundStrategies.BellmanFordStrategy;

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

        //indicating input step (step 1 gettings number of processes, step 2 get ids of processes, etc)
        int stepCounter=1;

        //used when reading connection matrix
        int nodeCounter=0;

        try {

            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFileName));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            //Used to prerve order of ids - will be used when reading connectivity matrix
            ArrayList<ProcessNode> orderedProcesses = null;

            while ((line = bufferedReader.readLine()) != null) {

                if( line.length() != 0 )
                {
                    if (!(line.charAt(0) == '#')) {
                        if (stepCounter == 1)
                            stepCounter++;

                            //Processing number of nodes line
                        else if (stepCounter == 2) {
                            //Used to prerve order of ids - will be used when reading connectivity matrix
                            orderedProcesses = new ArrayList<ProcessNode>();

                            //Reading second line - getting ids of processes and storing them to the HashMap
                            for (String stringId : line.split(" ")) {
                                ProcessNode processNode = new ProcessNode(Integer.parseInt(stringId));
                                this.processes.put(Integer.parseInt(stringId), processNode);
                                orderedProcesses.add(processNode);
                            }
                            stepCounter++;
                        } else if (stepCounter == 3) {
                            //Reading thirdline line - getting id of root process
                            int rootId = Integer.parseInt(line);
                            ((BellmanFordStrategy) this.processes.get(rootId).getRoundStrategy()).setDist(0);
                            stepCounter++;
                        } else if (stepCounter == 4) {
                            /*
                  * Reading connectivity matrix depending on matrix size, and adding neighbors to the thread
                 * Each line is the current process we establishing links for, each column is the the other
                 * processes in relation to current process.
                 * */
                            ProcessNode processNode = orderedProcesses.get(nodeCounter);
                            String[] connectionWeights = line.split("\\s+");
                            for (int i = 0; i < connectionWeights.length; i++) {
                                if (Integer.parseInt(connectionWeights[i]) != -1) {
                                    processNode.addNeighbor(
                                            orderedProcesses.get(i).getID(),
                                            Integer.parseInt(connectionWeights[i]),
                                            orderedProcesses.get(i));
                                }
                            }

                            nodeCounter++;

                        }
                    }
                }

            }
            bufferedReader.close();
        }
        catch (Exception e)
        {e.printStackTrace();}


        System.out.println();
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
