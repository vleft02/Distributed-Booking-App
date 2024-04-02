package aueb.hestia;// package com.aueb.hestia;

import aueb.hestia.dao.RoomDao;
import aueb.hestia.dao.RoomMemoryDao;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Worker extends Thread{

    private static int counter = 0;

    ServerSocket providerSocket;
    Socket connection = null;
    private int id = 0;
    RoomDao rooms = new RoomMemoryDao();
    Worker()
    {
        id = counter++;
    }
    public static void main(String[] args) {
        Worker wk = new Worker();
        Worker wk2 = new Worker();
        Worker wk3 = new Worker();
        Worker wk4 = new Worker();
        Worker wk5 = new Worker();

        wk2.start();
        wk.start();
        wk3.start();
        wk4.start();
        wk5.start();

    }
    @Override
    public void run() {
        try {
            providerSocket = new ServerSocket(4001+id);
            while(true)
            {
                connection = providerSocket.accept();
                WorkerThread wt = new WorkerThread(connection, rooms);
                wt.start();

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
