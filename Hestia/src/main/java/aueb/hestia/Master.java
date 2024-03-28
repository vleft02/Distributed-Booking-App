package aueb.hestia;// package com.aueb.hestia;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Master {
	ServerSocket providerSocket;
	Socket connection = null;

	int numberOfWorkers = 5;

	public static void main(String[] args) {
		new Master().openServer();
	}

	void openServer() {
		try {
			providerSocket = new ServerSocket(4000);

			while (true) {
				this.connection = providerSocket.accept();
				System.out.println("Running");
				Thread t = new MasterThread(connection, numberOfWorkers);
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
}
