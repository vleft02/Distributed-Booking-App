package aueb.hestia;// package com.aueb.hestia;

import java.util.ArrayList;

public class Worker extends Thread{
    ArrayList<Room> rooms = new ArrayList<Room>();
    ArrayList<WorkerThread> threads = new ArrayList<WorkerThread>();
    
    
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
