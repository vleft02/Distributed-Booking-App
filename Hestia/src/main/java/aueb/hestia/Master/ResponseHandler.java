package aueb.hestia.Master;

import aueb.hestia.Domain.Room;
import aueb.hestia.Helper.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ResponseHandler extends Thread{

    private Pair<Integer,Object> mappedResults;
    private HashMap<Integer, ObjectOutputStream> connectionsMap;
    ObjectInputStream inputStream;

    ResponseHandler(ObjectInputStream inputStream, HashMap<Integer, ObjectOutputStream> connectionsMap){
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
        ObjectOutputStream out = connectionsMap.get(mappedResults.getKey());
        synchronized (connectionsMap) {
            connectionsMap.remove(mappedResults.getKey());
        }
        if (mappedResults.getValue() instanceof String)
        {
            String message = (String) mappedResults.getValue();
            try {
                out.writeObject(message);
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else
        {
            ArrayList<Room> rooms = (ArrayList<Room>) mappedResults.getValue();
            try {
                out.writeObject(rooms);
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
