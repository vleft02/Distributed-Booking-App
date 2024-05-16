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
        Object receivedObject = reducerInputStream.readObject();


        if (receivedObject instanceof Pair<?, ?>) {
            Pair<?, ?> pair = (Pair<?, ?>) receivedObject;
            if (pair.getKey() instanceof Integer && pair.getValue() instanceof String) {
                Pair<Integer,String> results = (Pair<Integer,String>) receivedObject;
                ObjectOutputStream clientOutputStream = connectionsMap.get(results.getKey());


                synchronized (connectionsMap) {
                    connectionsMap.remove(results.getKey());
                }
                clientOutputStream.writeObject(results.getValue());
                clientOutputStream.flush();


                clientOutputStream.close();
            }else{
                mappedResults = (Pair<Integer,ArrayList<Room>>) receivedObject;
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





    }
}
