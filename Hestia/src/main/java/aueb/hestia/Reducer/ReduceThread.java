package aueb.hestia.Reducer;

import aueb.hestia.Helper.Pair;
import aueb.hestia.Domain.Room;
import org.json.simple.JSONObject;

import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ReduceThread extends Thread{

    private ObjectInputStream inputStream;
    private String function;
    private Pair<Integer , Object> results;
    private int requestId;
    private boolean redirectImmediately = false ;

    private Socket requestSocket;
    private JSONObject responseJson;


    public ReduceThread(ObjectInputStream inputStream, HashMap<Integer, ArrayList<Room>> receivedParts){
        this.inputStream = inputStream;
        try {
            results = (Pair<Integer , Object>) inputStream.readObject();
            requestId = results.getKey();
            Object obj = results.getValue();
            if (obj instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) obj;
                redirectImmediately = true;
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }



    }

    public void run(){
        if (redirectImmediately)
        {
            try {
                requestSocket= new Socket("127.0.0.1", 3999);
                ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
                String message = (String) responseJson.get("message");
                out.writeObject(message);
                out.flush();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
