package aueb.hestia;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class ResponseHandler extends Thread{

    private Pair<Integer,String> mappedResults;
    private HashMap<Integer, ObjectOutputStream> connectionsMap;
    ObjectInputStream inputStream;

    ResponseHandler(ObjectInputStream inputStream, HashMap<Integer, ObjectOutputStream> connectionsMap){
        this.inputStream = inputStream;
        this.connectionsMap = connectionsMap;

    }



    @Override
    public void run()
    {
//        Pair<Integer, >inputStream.readObject();
    }
}
