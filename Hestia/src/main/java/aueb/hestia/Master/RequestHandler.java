package aueb.hestia.Master;

import aueb.hestia.Helper.DateRange;
import aueb.hestia.Helper.InvalidDateException;
import aueb.hestia.Helper.Pair;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import static java.lang.Math.abs;

import org.json.simple.parser.*;

public class RequestHandler extends Thread{

    private String function;
    private ObjectInputStream requestInputStream;
    private ObjectInputStream workerInputStream;
    private Socket requestSocket;
    JSONObject requestJson;
    private int requestId;
    private final Pair<Integer,JSONObject> mappedRequest  = new Pair<Integer,JSONObject>();
    private int numberOfWorkers;

    private HashMap<Integer, Socket> connectionsMap ;

    RequestHandler(ObjectInputStream in, int numberOfWorkers, int requestId, HashMap<Integer, Socket> connectionsMap)
    {
        try {
            this.requestInputStream = in;
            this.numberOfWorkers = numberOfWorkers;
            this.requestId = requestId;
            this.connectionsMap =connectionsMap;
            this.requestJson =  (JSONObject) requestInputStream.readObject();
            this.function = (String) requestJson.get("function");

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    public void run() {
        try {

            if ((function == "search") || (function == "showBookings") || (function == "showRooms")){
                reducefuncion(requestJson);

            }else{
                nonreducefunction(requestJson);
            }


            /*
            switch (function) {
                case "addRoom":
                    addRoom(requestJson);
                    break;
                case "addDate":
                    addDate(requestJson);
                    break;
                case "search":
                    search(requestJson);
                    break;
                case "book":
                    book(requestJson);
                    break;
                case "review":
                    review(requestJson);
                    break;
                case "showRooms":
                    showRooms(requestJson);
                    break;
                case "showBookings":
                    showBookings(requestJson);
                    break;
                default:
                    System.out.print("Function Not Found");
                    */

            } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException | ClassNotFoundException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                requestInputStream.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


//    public void search(JSONObject json) throws IOException, ClassNotFoundException {
//      /*  DateRange dateRange = null;
//        String dateRangeString = (String) json.get("dateRange");
//        if (dateRangeString.contains("-"))
//        {
//            String[] dateParts = dateRangeString.split(" - ");
//            try {
//                dateRange = new DateRange(dateParts[0],dateParts[1]);
//            } catch (InvalidDateException e) {
//                System.out.println("Invalid Date Given");
//            }
//        }
//        else
//        {
//            String dateStr = dateRangeString.replaceAll("\\s+", "");
//            try {
//                dateRange = new DateRange(dateStr,dateStr);
//            } catch (InvalidDateException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        */
//
//        mappedRequest.put(requestId, json);
//
//        sendToAllWorkers(mappedRequest);
//
//
//    }
//
//    public void book(JSONObject json) throws IOException, ClassNotFoundException
//    {
//        String roomName = (String) json.get("roomName");
//
//        mappedRequest.put(requestId, json);
//
//        sendToWorker(4001+hashCode(roomName) ,mappedRequest);
//
//        Pair<Integer, String> message = (Pair<Integer, String>) workerInputStream.readObject();
//
//        Socket clientSocket = connectionsMap.get(requestId);
//
//        ObjectOutputStream clientOutputStream = (ObjectOutputStream) new ObjectOutputStream(clientSocket.getOutputStream());
//
//        clientOutputStream.writeObject(message.getValue());
//        clientOutputStream.flush();
//
//        /*
//        reqOut.writeObject(roomName);
//        reqOut.flush();
//        reqOut.writeObject(dateRange);
//        reqOut.flush();
//
//        if (requestSocket != null) {
//            requestSocket.close();
//        }
//        reqOut.close();*/
//
//
//    }
//
//
//
//    public void review(JSONObject json) throws  IOException, ClassNotFoundException
//    {
///*      String roomName = (String) json.get("roomName");
//        float stars = (float) json.get("stars");
//
//
//        requestSocket= new Socket("127.0.0.1", 4001+hashCode(roomName));
//        ObjectOutputStream reqOut = new ObjectOutputStream(requestSocket.getOutputStream());
//        reqOut.writeObject("review");
//        reqOut.flush();
//        reqOut.writeObject(roomName);
//        reqOut.flush();
//        reqOut.writeFloat(stars);
//        reqOut.flush();
//
//
//        if (requestSocket != null) {
//            requestSocket.close();
//        }
//        reqOut.close();*/
//    }
//
//    public void addRoom(JSONObject json) throws  IOException, ClassNotFoundException
//    {
////        String username = (String) json.get("username");
////        String roomName = (String) json.get("roomName");
////        int noOfPersons = (int) json.get("noOfPersons");
////        String area = (String) json.get("roomName");
////        double price = (double) json.get("roomName");
////        System.out.print(price);
////        String roomImage = (String) json.get("roomImage");
////        //Add availability as well
////
////        JSONObject jsonToSend = new JSONObject();
////        jsonToSend.put("function", function);
////        jsonToSend.put("username", username);
////        jsonToSend.put("roomName", roomName);
////        jsonToSend.put("noOfPersons", noOfPersons);
////        jsonToSend.put("area", area);
////        jsonToSend.put("price", price);
////        jsonToSend.put("roomImage",roomImage);
////
////        mappedRequest.put(requestId, jsonToSend);
////
////        requestSocket = new Socket("127.0.0.1", 4001+hashCode(roomName));
////        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
////        out.writeObject(mappedRequest);
////        out.flush();
////
////        if (requestSocket != null) {
////            requestSocket.close();
////        }
////        out.close();
//
//    }
//
//    public void addDate(JSONObject json) throws IOException, ClassNotFoundException
//    {
///*        String roomName= (String) json.get("roomName");
//        DateRange daterange = (DateRange) in.readObject();
//
//        requestSocket= new Socket("127.0.0.1", 4001+hashCode(roomName));
//        ObjectOutputStream reqOut = new ObjectOutputStream(requestSocket.getOutputStream());
//        reqOut.writeObject("addDate");
//        reqOut.flush();
//        reqOut.writeObject(roomName);
//        reqOut.flush();
//        reqOut.writeObject(daterange);
//        reqOut.flush();
//
//        if (requestSocket != null) {
//            requestSocket.close();
//        }
//        reqOut.close();*/
//    }
//
//    public void showBookings(JSONObject json) throws IOException, ClassNotFoundException
//    {
//
//        mappedRequest.put(requestId, json);
//
//        sendToAllWorkers(mappedRequest);
//        /*
//        for (int i=0; i<numberOfWorkers; i++)
//        {
//            requestSocket= new Socket("127.0.0.1", 4000+i+1);
//            ObjectOutputStream reqOut = new ObjectOutputStream(requestSocket.getOutputStream());
//            reqOut.writeObject(mappedRequest);
//            reqOut.flush();
//            if (requestSocket != null) {
//                requestSocket.close();
//            }
//            reqOut.close();
//        }
//      /*   String username = (String) json.get("username");
//
//        for (int i=0; i<numberOfWorkers; i++)
//        {
//            requestSocket= new Socket("127.0.0.1", 4000+i+1);
//            ObjectOutputStream reqOut = new ObjectOutputStream(requestSocket.getOutputStream());
//            reqOut.writeObject("showBookings");
//            reqOut.flush();
//            reqOut.writeObject(username);
//            reqOut.flush();
//            if (requestSocket != null) {
//                requestSocket.close();
//            }
//            reqOut.close();
//        }*/
//
//    }
//    public void showRooms(JSONObject json) throws IOException, ClassNotFoundException
//    {
//
//        mappedRequest.put(requestId, json);
//
//        sendToAllWorkers(mappedRequest);
//        /*
//        for (int i=0; i<numberOfWorkers; i++)
//        {
//            requestSocket= new Socket("127.0.0.1", 4000+i+1);
//            ObjectOutputStream reqOut = new ObjectOutputStream(requestSocket.getOutputStream());
//            reqOut.writeObject("showBookings");
//            reqOut.flush();
//            reqOut.writeObject(username);
//            reqOut.flush();
//            if (requestSocket != null) {
//                requestSocket.close();
//            }
//            reqOut.close();
//        }*/
//    }

    public void sendToWorker(int port, Pair<Integer,JSONObject> request)
    {
        try {
            requestSocket = new Socket("127.0.0.1", port);
            ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());
            out.writeObject(request);
            out.flush();
            this.workerInputStream = in;
//            return in;
            if (requestSocket != null) {
                requestSocket.close();
            }
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public void sendToAllWorkers(Pair<Integer,JSONObject> request)
    {
        try {
            for (int i=0; i<numberOfWorkers; i++)
            {
                requestSocket= new Socket("127.0.0.1", 4000+i+1);
                ObjectOutputStream out = null;
                ObjectInputStream in = null;
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());
                out.writeObject(request);
                out.flush();
                if (requestSocket != null) {
                    requestSocket.close();
                }
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private int hashCode(String roomName) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(roomName.getBytes(StandardCharsets.UTF_8));
            BigInteger bigInt = new BigInteger(1, encodedHash);
            return (int) abs(bigInt.intValue() % numberOfWorkers);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void reducefuncion(JSONObject json) throws IOException, ClassNotFoundException{
        mappedRequest.put(requestId, json);

        sendToAllWorkers(mappedRequest);


    }
    public void nonreducefunction(JSONObject json) throws IOException, ClassNotFoundException{
        String roomName = (String) json.get("roomName");
        mappedRequest.put(requestId, json);

        sendToWorker(4001+hashCode(roomName) ,mappedRequest);

        Pair<Integer, String> message = (Pair<Integer, String>) workerInputStream.readObject();

        Socket clientSocket = connectionsMap.get(requestId);

        ObjectOutputStream clientOutputStream = (ObjectOutputStream) new ObjectOutputStream(clientSocket.getOutputStream());

        clientOutputStream.writeObject(message.getValue());
        clientOutputStream.flush();
    }


}
