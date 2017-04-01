package com.syncsys;

import com.syncsys.roundStrategies.BellmanFordStrategy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * Created by anton on 2/9/2017.
 */
public class ProcessController
{
    private Map<String, ProcessNode> processes;
    private boolean allNodesTerminated;
    private String rootID;

    public ProcessController()
    {
        processes = new LinkedHashMap<String, ProcessNode>(); //We want to quickly get to the process by using its id.
        allNodesTerminated = false;
    }

    public void runSingleRound()
    {

        for(ProcessNode processNode : processes.values())
        {
            try {
	            processNode.resetRoundToStart();
            } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
        }

        // Run each node, check to see if all nodes are terminated
        allNodesTerminated = true;
        for(ProcessNode processNode : processes.values())
        {
        	if (!processNode.isTerminating()) {
                Thread thread = new Thread(processNode);
                thread.start();
                
                allNodesTerminated = false;
        	}
        }

        while(this.isRoundComplete() ==false)
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Round completed\n");
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
                        if (stepCounter == 1)  //Our data structure don't need to know number of nodes - we skipping processing this line
                            stepCounter++;

                            //Processing number of nodes line
                        else if (stepCounter == 2) {
                            //Used to prerve order of ids - will be used when reading connectivity matrix
                            orderedProcesses = new ArrayList<ProcessNode>();

                            //Reading second line - getting ids of processes and storing them to the HashMap
                            for (String id : line.split(" ")) {
                                ProcessNode processNode = new ProcessNode(id);
                                processes.put(id, processNode);
                                orderedProcesses.add(processNode);
                            }
                            stepCounter++;
                        } else if (stepCounter == 3) {
                            //Reading thirdline line - getting id of root process
                            rootID = line;
                            processes.get(rootID).setRoot(true);
                            processes.get(rootID).setDist(0);
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
                                            orderedProcesses.get(i).getId(),
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
        for(ProcessNode processNode : processes.values())
        {
            if (!processNode.isRoundCompleted() && !processNode.isTerminating())
                return false;

        }
        return true;
    }

    public String getRootID() {
    	return rootID;
    }

    public Map<String, String> getNodeParentPairs() {
    	Map<String, String> nodeParentPairs = new LinkedHashMap<String, String>();
    	for(ProcessNode processNode : processes.values()) {
    		if (!processNode.getId().equals(rootID)) {
    			ProcessNode parent = processNode.getParent();
    			nodeParentPairs.put(processNode.getId(), parent.getId());
    		}
    	}
    	return nodeParentPairs;
    }

	public boolean isAllNodesTerminated() {
        allNodesTerminated = true;
        for(ProcessNode processNode : processes.values())
        {
        	if (!processNode.isTerminating()) {
                allNodesTerminated = false;
        	}
        }

        return allNodesTerminated;
    }

    public void printFinalResult()
    {
        for (ProcessNode processNode : this.processes.values())
        {
            System.out.println(
            		"Process ID: " + processNode.getId() + 
            		"; Distance: " + processNode.getDist() + 
            		"; Shortest Path: " + processNode.describeShortestPath(processNode));
        }
    }

	public void setAllNodesTerminated(boolean allNodesTerminated) {
	    this.allNodesTerminated = allNodesTerminated;
    }
}
