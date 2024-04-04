package aueb.hestia;

import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ReduceThread extends Thread{

    ObjectInputStream inputStream;
    String function;

    public ReduceThread(ObjectInputStream inputStream, HashMap<Integer, ArrayList<Room>> receivedParts){
        this.inputStream = inputStream;


    }

    public void run(){

    }
}
