package aueb.hestia.Master;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Master{

    private final HashMap<Integer, Socket> connectionsMap  = new HashMap<>();
	int numberOfWorkers;
    ClientRequestListener clientRequestListener;
    ReducerRequestListener ReducerResponseListener;
	public static void main(String[] args) {
		new Master(5).openServer();
	}

	Master(int numberOfWorkers)
	{
		this.numberOfWorkers = numberOfWorkers;
        this.clientRequestListener = new ClientRequestListener(connectionsMap, numberOfWorkers);
        this.ReducerResponseListener = new ReducerRequestListener(connectionsMap,numberOfWorkers);
	}

	void openServer() {
        clientRequestListener.start();
        ReducerResponseListener.start();
    }

}


class ClientRequestListener extends Thread{
    ServerSocket providerSocket;
    Socket connection = null;
    private HashMap<Integer, Socket> connectionsMap;
    int numberOfWorkers;
    ClientRequestListener(HashMap<Integer, Socket> connectionsMap, int numberOfWorkers)
    {
        this.connectionsMap = connectionsMap;
        this.numberOfWorkers = numberOfWorkers;
    }

    @Override
    public void run() {


        try {
            providerSocket = new ServerSocket(4000);

            while (true) {
                connection = providerSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
                Thread requestHandler;
                synchronized (connectionsMap)
                {
                    connectionsMap.put(connectionsMap.size(), connection);
                    requestHandler = new RequestHandler(in, numberOfWorkers,connectionsMap.size()-1, connectionsMap);
                }
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
    private HashMap<Integer,Socket> connectionsMap;
    ReducerRequestListener(HashMap<Integer, Socket> connectionsMap, int numberOfWorkers)
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