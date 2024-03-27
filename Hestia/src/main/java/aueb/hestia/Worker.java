package aueb.hestia;// package com.aueb.hestia;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Worker extends Thread{

    private static int counter = -1;

    private int id = 0;
    ArrayList<Room> rooms = new ArrayList<Room>();
    ArrayList<WorkerThread> threads = new ArrayList<WorkerThread>();


    Worker()
    {
        id = counter++;
    }

    public void openServer()
    {
        ServerSocket ssocket = null;
        try {
            ssocket = new ServerSocket(4321+id);
            while(true)
            {

                Socket client = ssocket.accept();
                WorkerThread wt = new WorkerThread(client, rooms);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    
    @Override
    public void run() {
        // try {}
        // catch(){}
    }

    public void search(String area, DateRange dateRange, int noOfPersons, float stars)
    {
        
    }

    public void book(String roomName, DateRange dateRange)
    {

    }

    public void review(String roomName, float stars)
    {

    }

    public void addRoom(String username, String roomName, int noOfPersons, String area, String roomImage)
    {

    }

    public void addDate(String roomName, DateRange daterange)
    {

    }

    public void showBookings(String username)
    {

    }


}
