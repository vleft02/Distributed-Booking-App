package aueb.hestia;// package com.aueb.hestia;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Master{

    private final HashMap<Integer, ObjectOutputStream> connectionsMap  = new HashMap<Integer,ObjectOutputStream>();
	int numberOfWorkers;
    ClientRequestListener clientRequestListener;
    ReducerRequestListener ReducerResponseListener;
	public static void main(String[] args) {
		new Master(5,0).openServer();
	}

	Master(int numberOfWorkers, int type )
	{
		this.numberOfWorkers = numberOfWorkers;
        this.clientRequestListener = new ClientRequestListener(connectionsMap, numberOfWorkers);
        this.ReducerResponseListener = new ReducerRequestListener(connectionsMap,numberOfWorkers);
	}

	void openServer() {
        /*try {
            providerSocket = new ServerSocket(4000);

            while (true) {
                connection = providerSocket.accept();
//                Thread t = new ActionsForClients(connection);

                ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
                connectionsMap.put(connectionsMap.size(), out);

				Thread requestHandler = new RequestHandler(in, numberOfWorkers,connectionsMap.size());
                requestHandler.start();

            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }*/
    }

}


class ClientRequestListener extends Thread{
    ServerSocket providerSocket;
    Socket connection = null;
    private final HashMap<Integer, ObjectOutputStream> connectionsMap  = new HashMap<Integer,ObjectOutputStream>();
    int numberOfWorkers;
    ClientRequestListener(HashMap<Integer, ObjectOutputStream> connectionsMap, int numberOfWorkers)
    {

    }

    @Override
    public void run() {


        try {
            providerSocket = new ServerSocket(4000);

            while (true) {
                connection = providerSocket.accept();
//                Thread t = new ActionsForClients(connection);

                ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
                connectionsMap.put(connectionsMap.size(), out);

                Thread requestHandler = new RequestHandler(in, numberOfWorkers,connectionsMap.size());
                requestHandler.start();

            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

}


class ReducerRequestListener extends Thread
{
    ServerSocket providerSocket;
    Socket connection = null;
    private int numberOfWorkers;
    private HashMap<Integer, ObjectOutputStream> connectionsMap;
    ReducerRequestListener(HashMap<Integer, ObjectOutputStream> connectionsMap, int numberOfWorkers)
    {
        this.connectionsMap = connectionsMap;
        this.numberOfWorkers = numberOfWorkers;
    }
    @Override
    public void run() {

        try {
            providerSocket = new ServerSocket(3999);

            while (true) {
                connection = providerSocket.accept();

                ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(connection.getInputStream());

                ResponseHandler responseHandler = new ResponseHandler(in, connectionsMap);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}