package com.syncsys;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by anton on 2/9/2017.
 */
public class Process implements Runnable
{
    private int id;                                 //Id of process
    private HashMap<Integer, Integer> neighbors;    //  Map of tuples: (id Of Neighbor process, weight)
    private volatile boolean isRoundCompleted = false;  //volatile flag - indicates to the parent that Thread finished its round

    public Process(int id)
    {
        this.id=id;
        neighbors = new HashMap<Integer, Integer>();
    }

    public void addNeighbor(int id, int weight)
    {
        this.neighbors.put(id, weight);
    }


    //Single Round (also see function below)
    @Override
    public void run()
    {

        System.out.println("Running Thread : " + this.toString());

        //Simulating long run (testing round completion by ProcessController)
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("Finished Thread : " + this.toString());
        this.isRoundCompleted = true;
        return;
    }


    //before each round thread should complete this step.
    public void resetRoundToStart()
    {
        this.isRoundCompleted = false;
    }


    //Getters/Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HashMap<Integer, Integer> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(HashMap<Integer, Integer> neighbors) {
        this.neighbors = neighbors;
    }

    public boolean isRoundCompleted() {
        return isRoundCompleted;
    }

    public void setRoundCompleted(boolean roundCompleted) {
        isRoundCompleted = roundCompleted;
    }

    @Override
    public String toString() {
        return "Process{" +
                "id=" + id +
                ", neighbors=" + neighbors +
                '}';
    }


}
