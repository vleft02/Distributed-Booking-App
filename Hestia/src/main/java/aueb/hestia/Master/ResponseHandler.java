package aueb.hestia.Master;

import aueb.hestia.Domain.Room;
import aueb.hestia.Helper.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ResponseHandler extends Thread{

    private Pair<Integer,Object> mappedResults;
    private HashMap<Integer, Socket> connectionsMap;
    ObjectInputStream inputStream;

    ResponseHandler(ObjectInputStream inputStream, HashMap<Integer,Socket> connectionsMap){
        this.inputStream = inputStream;
        this.connectionsMap = connectionsMap;
        try {
            mappedResults = (Pair<Integer,Object>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public void run()
    {

        try {
            Socket clientSocket = connectionsMap.get(mappedResults.getKey());
            ObjectOutputStream out = null;
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            synchronized (connectionsMap) {
                connectionsMap.remove(mappedResults.getKey());
            }
            ArrayList<Room> rooms = (ArrayList<Room>) mappedResults.getValue();
            out.writeObject(rooms);
            out.flush();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
