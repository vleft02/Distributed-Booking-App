package aueb.hestia.Workers;// package com.aueb.hestia;

import aueb.hestia.Config.Config;
import aueb.hestia.Dao.RoomDao;
import aueb.hestia.Dao.RoomMemoryDao;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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


    public static void main(String[] args) {
        Config config = new Config();
        int numberOfWorkers = config.getNumberOfWorkers();

        try {
            Worker wk = new Worker();
            wk.server();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }




    Worker() throws IOException {
        this.config = new Config();
        this.workerPort = config.getWorkersPort();
        this.id = getWorkersCount();
        System.out.println("Worker "+id);
    }


    public void server() {
        try {

            System.out.println("Listening on..."+ (workerPort + id));
            providerSocket = new ServerSocket(workerPort + id);
            while(true)
            {
                System.out.println("Connection Received");
                connection = providerSocket.accept();

                WorkerThread wt = new WorkerThread(connection, rooms);

                wt.start();
            }

        } catch (IOException /*| InterruptedException*/ e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(providerSocket!=null)
                {
                    providerSocket.close();
                }

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public int getWorkersCount() throws IOException {

        Socket socket = new Socket(config.getCounterIp(), config.getCounterPort());

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        out.writeInt(1);
        out.flush();

        int count = in.readInt();

        out.close();
        in.close();
        return count;
    }

}
