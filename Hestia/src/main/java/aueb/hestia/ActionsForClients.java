package aueb.hestia;// package com.aueb.hestia;

import java.io.*;
import java.net.*;
import org.json.simple.JSONArray;

import org.json.simple.JSONObject;

import org.json.simple.parser.*;

public class ActionsForClients extends Thread {
    ObjectInputStream in;
    ObjectOutputStream out;

    public ActionsForClients(Socket connection) {
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            JSONObject obj = (JSONObject) in.readObject();
            /*if (obj.get("function").equals("addRoom")){
                obj.put("roomName","grotta");
            }else if (obj.get("function").equals("showBookings")){
                obj.put("name","vagelis");
            }*/
            out.writeObject(obj);
//			out.writeInt(a + b);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
