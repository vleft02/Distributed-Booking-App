package aueb.hestia.Reducer;

import aueb.hestia.Helper.Pair;
import aueb.hestia.Domain.Room;
import org.json.simple.JSONObject;

import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ReduceThread extends Thread{


    private Pair<Integer , Object> results;
    private int requestId;
    private boolean redirectImmediately = false ;

    private Socket requestSocket;

    private Socket workerSocket;
    HashMap<Integer, Pair<ArrayList<Room>,Integer>> receivedParts;
    private int numberOfThreads;

    public ReduceThread(Socket workerSocket, HashMap<Integer, Pair<ArrayList<Room>,Integer>> receivedParts, int numberOfThreads){
//        this.inputStream = inputStream;
        this.workerSocket=workerSocket;
        this.numberOfThreads = numberOfThreads;
        ObjectInputStream inputStream = null;
        try {

            inputStream = (ObjectInputStream) new ObjectInputStream(workerSocket.getInputStream());
            results = (Pair<Integer , Object>) inputStream.readObject();
            requestId = results.getKey();
            Object obj = results.getValue();
            if (obj instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }



    }

    public void run(){

            Object obj = results.getValue();
            ArrayList<Room> rooms = (ArrayList<Room>) obj;
            synchronized (receivedParts)
            {
                if (!receivedParts.containsKey(results.getKey()))
                {
                    Pair<ArrayList<Room>, Integer> receivedSegment = new Pair<ArrayList<Room>, Integer>();
                    receivedSegment.put(rooms,1);
                    receivedParts.put(results.getKey(), receivedSegment);
                }
                else
                {
                    Pair<ArrayList<Room>, Integer> roomsArrived = receivedParts.get(results.getKey());

                    roomsArrived.getKey().addAll(rooms);
                    roomsArrived.setValue(roomsArrived.getValue()+1);
                    //Make Sure that the condition turns to true at some points
                    if (roomsArrived.getValue() == numberOfThreads){
                        try {
                            requestSocket= new Socket("127.0.0.1", 3999);
                            ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());

                            Pair<Integer, ArrayList<Room>> response = new Pair<Integer, ArrayList<Room>>();
                            response.put(results.getKey(), roomsArrived.getKey());
                            out.writeObject(response);
                            out.flush();

                            out.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            }
    }
}
