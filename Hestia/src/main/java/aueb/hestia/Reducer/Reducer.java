package aueb.hestia.Reducer;
import aueb.hestia.Domain.Room;
import aueb.hestia.Helper.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Reducer {
    private ServerSocket providerSocket;
    private Socket connection = null;
    private int numberOfThreads;
    private HashMap<Integer, Pair<ArrayList<Room>, Integer>> receivedParts ;
    public static void main(String[] args) {
        new Reducer(5).openServer();
    }

    Reducer(int numberOfThreads)
    {
        this.numberOfThreads = numberOfThreads;
        this.receivedParts = new HashMap<>();
    }
    void openServer() {
        try {
            providerSocket = new ServerSocket(4009);

            while (true) {
                connection = providerSocket.accept();
                ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
                Thread rt = new ReduceThread(in, receivedParts,numberOfThreads);
                rt.start();
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