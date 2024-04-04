package aueb.hestia;// package com.aueb.hestia;

import aueb.hestia.dao.RoomDao;
import aueb.hestia.dao.RoomMemoryDao;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

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
                ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(connection.getInputStream());

                WorkerThread wt = new WorkerThread(in, rooms);

                wt.start();
            }

        } catch (IOException /*| InterruptedException*/ e) {
            throw new RuntimeException(e);
        }
    }


}
