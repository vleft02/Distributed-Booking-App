package aueb.hestia.Reducer;

import aueb.hestia.Config.Config;
import aueb.hestia.Helper.Pair;
import aueb.hestia.Domain.Room;
import org.json.simple.JSONObject;

import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ReduceThread extends Thread {


    private Pair<Integer, Object> results;
    private int requestId;
    private int masterPort;
    private String masterIp;
    ObjectOutputStream workerOutputStream;
    ObjectInputStream workerInputStream;
    private Socket requestSocket;

    private Socket workerSocket;
    HashMap<Integer, Pair<Integer, ArrayList<Room>>> receivedParts;
    private int numberOfThreads;

    public ReduceThread(Socket workerSocket, HashMap<Integer, Pair<Integer, ArrayList<Room>>> receivedParts, int numberOfThreads) {

        this.workerSocket = workerSocket;
        this.receivedParts = receivedParts;
        this.numberOfThreads = numberOfThreads;
        try {
            this.workerOutputStream = new ObjectOutputStream(workerSocket.getOutputStream());
            this.workerInputStream = new ObjectInputStream(workerSocket.getInputStream());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Config config = new Config();
        this.masterPort = config.getReducerRequestListener();
        this.masterIp = config.getReducerIP();
    }

    public void run() {
        try {
            reduce();
        } catch(IOException | ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        } finally {
            try {
                workerOutputStream.close();
                workerInputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void reduce() throws IOException, ClassNotFoundException {
        results = (Pair<Integer, Object>) workerInputStream.readObject();
        requestId = results.getKey();
        Object obj = results.getValue();
        ArrayList<Room> rooms = (ArrayList<Room>) obj;
        synchronized (receivedParts) {
            if (!receivedParts.containsKey(results.getKey())) {
                Pair<Integer, ArrayList<Room>> receivedSegment = new Pair<>();
                receivedSegment.put(1, rooms);
                receivedParts.put(requestId, receivedSegment);
            } else {
                Pair<Integer, ArrayList<Room>> roomsAlreadyArrived = receivedParts.get(requestId);

                roomsAlreadyArrived.getValue().addAll(rooms);
                synchronized (roomsAlreadyArrived)
                {
                    roomsAlreadyArrived.setKey(roomsAlreadyArrived.getKey() + 1);
                }
                //Make Sure that the condition turns to true at some points
                if (roomsAlreadyArrived.getKey() >= numberOfThreads) {
                    requestSocket = new Socket(masterIp, masterPort);
                    ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());

                    Pair<Integer, ArrayList<Room>> response = new Pair<Integer, ArrayList<Room>>();
                    response.put(requestId, roomsAlreadyArrived.getValue());
                    out.writeObject(response);
                    out.flush();

                    out.close();
                    in.close();
                    if (requestSocket != null) {
                        requestSocket.close();
                    }
                }
            }
        }
    }
}

