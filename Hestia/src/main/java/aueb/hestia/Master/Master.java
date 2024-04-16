package aueb.hestia.Master;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import aueb.hestia.Config.Config;

public class Master{

    private final HashMap<Integer, ObjectOutputStream> connectionsMap  = new HashMap<>();
	int numberOfWorkers;

    ClientRequestListener clientRequestListener;
    ReducerRequestListener ReducerResponseListener;
    Config config;

	public static void main(String[] args) {
		new Master().openServer();
	}

	Master()
	{

        this.config = new Config();
		this.numberOfWorkers = config.getNumberOfWorkers();
        this.clientRequestListener = new ClientRequestListener(connectionsMap, numberOfWorkers);
        this.ReducerResponseListener = new ReducerRequestListener(connectionsMap, numberOfWorkers);
	}

	void openServer() {
        clientRequestListener.start();
        ReducerResponseListener.start();
    }

}


class ClientRequestListener extends Thread{
    private ServerSocket providerSocket;
    Socket connection = null;

    private final Object lock = new Object();
    private final HashMap<Integer, ObjectOutputStream> connectionsMap;
    int numberOfWorkers;
    Integer requestId = 0;
    Config config;

    ClientRequestListener(HashMap<Integer, ObjectOutputStream> connectionsMap, int numberOfWorkers)
    {
        this.connectionsMap = connectionsMap;
        this.numberOfWorkers = numberOfWorkers;
        this.config = new Config();
    }

    @Override
    public void run() {


        try {
            int requestListenerPort = config.getClientRequestListenerPort();
            providerSocket = new ServerSocket(requestListenerPort);
/*            providerSocket = new ServerSocket(4000);*/

            while (true) {
                connection = providerSocket.accept();

                Thread requestHandler;
                synchronized (lock)
                {
                    requestHandler = new RequestHandler(connection, numberOfWorkers,requestId, connectionsMap);
                    requestId++;
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
    private ServerSocket providerSocket;
    Socket connection = null;
    private int numberOfWorkers;
    private HashMap<Integer,ObjectOutputStream> connectionsMap;
    Config config ;
    ReducerRequestListener(HashMap<Integer, ObjectOutputStream> connectionsMap, int numberOfWorkers)
    {
        this.connectionsMap = connectionsMap;
        this.numberOfWorkers = numberOfWorkers;
        this.config = new Config();
    }
    @Override
    public void run() {

        try {
            int reducerResponsePort = config.getReducerRequestListener();
            providerSocket = new ServerSocket(reducerResponsePort);
//            providerSocket = new ServerSocket(3999);

            while (true) {
                connection = providerSocket.accept();

                ResponseHandler responseHandler = new ResponseHandler(connection, connectionsMap);
                responseHandler.start();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }finally {
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}