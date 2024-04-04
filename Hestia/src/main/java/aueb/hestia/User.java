package aueb.hestia;// package com.aueb.hestia;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class User {
    public static void main(String[] args)
    {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in= null;

        try {
            while (true){
                requestSocket = new Socket("127.0.0.1", 4000);
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());

                out.writeObject("addRoom");
                out.flush();

                out.writeObject("Kostas");
                out.flush();

                out.writeObject("Priamos Hotel");
                out.flush();

                out.writeInt(1);
                out.flush();

                out.writeObject("Athens");
                out.flush();

                out.writeDouble(20.0);
                out.flush();

                out.writeObject("img");
                out.flush();
            }


//            System.out.println("Server>" + in.readInt());

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                in.close();	out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
    
}
