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
    private ServerSocket providerSocket;
    Socket connection = null;

    private final Object lock = new Object();
    private final HashMap<Integer, ObjectOutputStream> connectionsMap;
    int numberOfWorkers;
    Integer requestId = 0;
    Config portConfig ;
    ClientRequestListener(HashMap<Integer, ObjectOutputStream> connectionsMap, int numberOfWorkers)
    {
        this.connectionsMap = connectionsMap;
        this.numberOfWorkers = numberOfWorkers;
//        this.portConfig = new Config();
    }

    @Override
    public void run() {


        try {
            /*int cPort = portConfig.getClientRequestListenerPort();
            providerSocket = new ServerSocket(cPort);*/
            providerSocket = new ServerSocket(4000);

            while (true) {
                connection = providerSocket.accept();
//                int requestId ;
//                synchronized (connectionsMap)
//                {
//                    connectionsMap.put(connectionsMap.size(), connection);
//                    requestId = connectionsMap.size()-1;
//                }
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
    Config portConfig ;
    ReducerRequestListener(HashMap<Integer, ObjectOutputStream> connectionsMap, int numberOfWorkers)
    {
        this.connectionsMap = connectionsMap;
        this.numberOfWorkers = numberOfWorkers;
//        this.portConfig = new Config();
    }
    @Override
    public void run() {

        try {
            /*int rPort = portConfig.getReducerRequestListener();
            providerSocket = new ServerSocket(rPort);*/
            providerSocket = new ServerSocket(3999);

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