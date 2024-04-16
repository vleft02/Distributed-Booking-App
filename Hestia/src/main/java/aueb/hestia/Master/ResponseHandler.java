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

    private Pair<Integer,ArrayList<Room>> mappedResults;
    private HashMap<Integer, ObjectOutputStream> connectionsMap;
    private ObjectInputStream reducerInputStream;
    private ObjectOutputStream reducerOutputStream;
    private Socket reducerSocket ;

    ResponseHandler(Socket reducerSocket, HashMap<Integer,ObjectOutputStream> connectionsMap){

        this.connectionsMap = connectionsMap;
        try {
            this.reducerSocket = reducerSocket;
            this.reducerOutputStream = new ObjectOutputStream(reducerSocket.getOutputStream());
            this.reducerInputStream =  new ObjectInputStream(reducerSocket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public void run()
    {
        try {
            forwardToClient();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                reducerOutputStream.close();
                reducerInputStream.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }


    public void forwardToClient() throws IOException, ClassNotFoundException {
        mappedResults = (Pair<Integer,ArrayList<Room>>) reducerInputStream.readObject();
        ObjectOutputStream clientOutputStream = connectionsMap.get(mappedResults.getKey());


        synchronized (connectionsMap) {
            connectionsMap.remove(mappedResults.getKey());
        }
        ArrayList<Room> rooms = mappedResults.getValue();
        clientOutputStream.writeObject(rooms);
        clientOutputStream.flush();

        clientOutputStream.close();
    }
}
