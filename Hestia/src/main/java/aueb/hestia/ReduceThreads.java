package aueb.hestia;

import java.net.Socket;
import java.io.*;
import java.net.*;

public class ReduceThreads extends Thread{

    ObjectInputStream in;
    ObjectOutputStream out;

    public ReduceThreads(Socket connection){
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
            String msg = (String)in.readObject();
            System.out.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(){

    }
}
