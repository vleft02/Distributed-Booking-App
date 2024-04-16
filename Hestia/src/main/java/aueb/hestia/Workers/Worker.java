package aueb.hestia.Workers;// package com.aueb.hestia;

import aueb.hestia.Config.Config;
import aueb.hestia.Dao.RoomDao;
import aueb.hestia.Dao.RoomMemoryDao;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Worker extends Thread{

    private static int counter = 0;

    ServerSocket providerSocket;
    Socket connection = null;
    private int id = 0;
    private Config config;
    private int workerPort;
    RoomDao rooms = new RoomMemoryDao();
    Worker()
    {
        id = counter++;
    }
    public static void main(String[] args) {
        Worker wk = new Worker(0);
        Worker wk2 = new Worker(1);
        Worker wk3 = new Worker(2);
        Worker wk4 = new Worker(3);
        Worker wk5 = new Worker(4);

        wk2.start();
        wk.start();
        wk3.start();
        wk4.start();
        wk5.start();

    }


    Worker(int id)
    {
        this.id = id;
        this.config = new Config();
        this.workerPort = config.getWorkersPort();
    }
    @Override
    public void run() {
        try {
            providerSocket = new ServerSocket(workerPort + id);
            while(true)
            {
                connection = providerSocket.accept();

                WorkerThread wt = new WorkerThread(connection, rooms);

                wt.start();
            }

        } catch (IOException /*| InterruptedException*/ e) {
            throw new RuntimeException(e);
        } finally {
            try {
                providerSocket.close();

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


}
