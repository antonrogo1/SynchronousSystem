package com.syncsys;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by anton on 2/9/2017.
 */
public class ProcessController
{
    private Map<String, ProcessNode> processes;
    private List<AsyncLink> links;              //links between processes
    private int timeLine;                       //to simulate time


    private boolean allNodesTerminated;
    private String rootID;

    public ProcessController()
    {
        timeLine = 0;
        processes = new LinkedHashMap<String, ProcessNode>(); //We want to quickly get to the process by using its id.
        links = new ArrayList<AsyncLink>();
        allNodesTerminated = false;
    }


    public void startProcesses() throws InterruptedException {

        for (ProcessNode processNode : processes.values()) {
            Thread thread = new Thread(processNode);
            thread.start();
        }

    }

    public void runAsyncBFS() throws InterruptedException {

        while(this.isAllNodesTerminated() == false )
        {
            for (AsyncLink asyncLink : this.links) {
                asyncLink.advanceTime();
            }
            Thread.sleep(1000);
        }
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

            //Used to preserve order of ids - will be used when reading connectivity matrix
            ArrayList<ProcessNode> orderedProcesses = null;

            while ((line = bufferedReader.readLine()) != null) {

                if (line.length() != 0) {
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
                            //Reading third line line - getting id of root process
                            rootID = line;
                            this.processes.get(rootID).setDistance(0);
                            this.processes.get(rootID).setRoot(true);
                            stepCounter++;
                        } else if (stepCounter == 4) {
			                /*
			                * Reading connectivity matrix depending on matrix size, and adding neighbors to the thread
			                * Each line is the current process we establishing links for, each column is the the other
			                * processes in relation to current process.
			                * */

                            String[] connectionWeights = line.split("\\s+");
                            ProcessNode processNodeOrigin = orderedProcesses.get(nodeCounter);

                            //we are reading only half of symmetric connectivity triangle
                            for (int i = nodeCounter; i < connectionWeights.length; i++) {
                                if (Integer.parseInt(connectionWeights[i]) != -1) //meaning there is a link
                                {
                                    ProcessNode processNodeTarget = orderedProcesses.get(i);

                                    AsyncLink asyncLink = new AsyncLink(processNodeOrigin, processNodeTarget, Integer.parseInt(connectionWeights[i]));
                                    links.add(asyncLink);
                                }
                            }
                            nodeCounter++;
                        }
                    }
                }
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //adding links to related processes
        for(AsyncLink asyncLink : this.links)
        {
            this.processes.get(asyncLink.getaProcess().getId()).getLinks().put(asyncLink.getbProcess().getId(), asyncLink);
            this.processes.get(asyncLink.getbProcess().getId()).getLinks().put(asyncLink.getaProcess().getId(), asyncLink);
        }

        System.out.println("Test");
    }




	public boolean isAllNodesTerminated()
    {
        allNodesTerminated = true;
        for(ProcessNode processNode : processes.values())
        {
        	if (processNode.isTerminating() == false)
        	{
                allNodesTerminated = false;
                break;
        	}
        }

        return allNodesTerminated;
    }

    public void printFinalResult()
    {
        for (ProcessNode processNode : this.processes.values())
        {
            System.out.println("Process ID: " + processNode.getId() + "; Distance: " + processNode.getDistance() + "; Shortest Path: " + this.describeShortestPath(processNode));
        }
    }


    //recursive method that return tuple (shortest Path description and total distance)
    public String describeShortestPath(ProcessNode processNode)
    {
        String pathDescription =  processNode.getId() ;

        if(!(processNode.isRoot()) )
        {
            ProcessNode parentProcessNode = processNode.getParent();
            String parentChain = this.describeShortestPath(parentProcessNode);
            pathDescription+= " =>" + parentChain;

            return pathDescription;
        }
        else
        {
            return processNode.getId();
        }
    }

}
