package aueb.hestia;// package com.aueb.hestia;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Master{
	ServerSocket providerSocket;
	Socket connection = null;

	int numberOfWorkers;

	public static void main(String[] args) {
		new Master(5,0).clientServer();
		new Server().openServer();
	}
	
	ServerSocket providerSocket;
    Socket connection = null;


	Master(int numberOfWorkers, int type )
	{
		this.numberOfWorkers = numberOfWorkers;
	}

	void openServer() {
        try {
            providerSocket = new ServerSocket(4321);

            while (true) {
                connection = providerSocket.accept();

                Thread t = new ActionsForClients(connection);
				//Thread t = new MasterThread(connection);
                t.start();

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


	void clientServer() {
		try {
			providerSocket = new ServerSocket(4000);
			while (true) {
				this.connection = providerSocket.accept();
				Thread t = new MasterThread(connection, numberOfWorkers);
				t.start();
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			try {
				if (providerSocket != null) {
					providerSocket.close();
				}
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}
}
