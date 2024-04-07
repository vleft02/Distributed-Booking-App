package aueb.hestia.Master;

import aueb.hestia.Helper.DateRange;
import aueb.hestia.Helper.InvalidDateException;
import aueb.hestia.Helper.Pair;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.lang.Math.abs;

import org.json.simple.parser.*;

public class RequestHandler extends Thread{

    private String function;
    private ObjectInputStream requestInputStream;
    private Socket requestSocket;
    JSONObject requestJson;
    private int requestId;
    private final Pair<Integer,String> mappedRequest  = new Pair<Integer,String>();
    private int numberOfWorkers;

    RequestHandler(ObjectInputStream in, int numberOfWorkers, int requestId)
    {
        try {
            this.requestInputStream = in;
            this.numberOfWorkers = numberOfWorkers;
            this.requestId = requestId;

            String requestJsonString =  (String) requestInputStream.readObject();
            this.requestJson = (JSONObject) new JSONParser().parse(requestJsonString);
            this.function = (String) requestJson.get("function");

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }


    public void run() {
        try {
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


    public void search(JSONObject json) throws IOException, ClassNotFoundException {
        String area = (String) json.get("area");
        DateRange dateRange = null;
        String dateRangeString = (String) json.get("dateRange");
        if (dateRangeString.contains("-"))
        {
            String[] dateParts = dateRangeString.split(" - ");
            try {
                dateRange = new DateRange(dateParts[0],dateParts[1]);
            } catch (InvalidDateException e) {
                System.out.println("Invalid Date Given");
            }
        }
        else
        {
            String dateStr = dateRangeString.replaceAll("\\s+", "");
            try {
                dateRange = new DateRange(dateStr,dateStr);
            } catch (InvalidDateException e) {
                throw new RuntimeException(e);
            }
        }

        int noOfPersons = (int) json.get("noOfPersons");
        float stars =  (float) json.get("stars");


        JSONObject jsonToSend = new JSONObject();
        jsonToSend.put("function", function);
        jsonToSend.put("area", area);
        jsonToSend.put("dateRange", dateRange);
        jsonToSend.put("noOfPersons",noOfPersons);
        jsonToSend.put("stars", stars);

        String jsonToSendString = jsonToSend.toJSONString();
        mappedRequest.put(requestId, jsonToSendString);

        for (int i=0; i<numberOfWorkers; i++)
        {
            requestSocket= new Socket("127.0.0.1", 4000+i+1);
            ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.writeObject(mappedRequest);
            out.flush();
            if (requestSocket != null) {
                requestSocket.close();
            }
            out.close();
        }

    }

    public void book(JSONObject json) throws IOException, ClassNotFoundException
    {
/*      String roomName = (String) json.get("roomName");
        DateRange dateRange = (DateRange) json.get("daterange");

        requestSocket= new Socket("127.0.0.1", 4001+hashCode(roomName));
        ObjectOutputStream reqOut = new ObjectOutputStream(requestSocket.getOutputStream());
        reqOut.writeObject("book");
        reqOut.flush();
        reqOut.writeObject(roomName);
        reqOut.flush();
        reqOut.writeObject(dateRange);
        reqOut.flush();

        if (requestSocket != null) {
            requestSocket.close();
        }
        reqOut.close();*/
    }



    public void review(JSONObject json) throws  IOException, ClassNotFoundException
    {
/*      String roomName = (String) json.get("roomName");
        float stars = (float) json.get("stars");


        requestSocket= new Socket("127.0.0.1", 4001+hashCode(roomName));
        ObjectOutputStream reqOut = new ObjectOutputStream(requestSocket.getOutputStream());
        reqOut.writeObject("review");
        reqOut.flush();
        reqOut.writeObject(roomName);
        reqOut.flush();
        reqOut.writeFloat(stars);
        reqOut.flush();


        if (requestSocket != null) {
            requestSocket.close();
        }
        reqOut.close();*/
    }

    public void addRoom(JSONObject json) throws  IOException, ClassNotFoundException
    {
        String username = (String) json.get("username");
        String roomName = (String) json.get("roomName");
        int noOfPersons = (int) json.get("noOfPersons");
        String area = (String) json.get("roomName");
        double price = (double) json.get("roomName");
        System.out.print(price);
        String roomImage = (String) json.get("roomImage");
        //Add availability as well

        JSONObject jsonToSend = new JSONObject();
        jsonToSend.put("function", function);
        jsonToSend.put("username", username);
        jsonToSend.put("roomName", roomName);
        jsonToSend.put("noOfPersons", noOfPersons);
        jsonToSend.put("area", area);
        jsonToSend.put("price", price);
        jsonToSend.put("roomImage",roomImage);

        String jsonToSendString = jsonToSend.toJSONString();
        mappedRequest.put(requestId, jsonToSendString);

        requestSocket = new Socket("127.0.0.1", 4001+hashCode(roomName));
        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
        out.writeObject(mappedRequest);
        out.flush();

        if (requestSocket != null) {
            requestSocket.close();
        }
        out.close();

    }

    public void addDate(JSONObject json) throws IOException, ClassNotFoundException
    {
/*        String roomName= (String) json.get("roomName");
        DateRange daterange = (DateRange) in.readObject();

        requestSocket= new Socket("127.0.0.1", 4001+hashCode(roomName));
        ObjectOutputStream reqOut = new ObjectOutputStream(requestSocket.getOutputStream());
        reqOut.writeObject("addDate");
        reqOut.flush();
        reqOut.writeObject(roomName);
        reqOut.flush();
        reqOut.writeObject(daterange);
        reqOut.flush();

        if (requestSocket != null) {
            requestSocket.close();
        }
        reqOut.close();*/
    }

    public void showBookings(JSONObject json) throws IOException, ClassNotFoundException
    {

        String username = (String) json.get("username");
        JSONObject jsonToSend = new JSONObject();
        jsonToSend.put("function", function);
        jsonToSend.put("username", username);


        String jsonToSendString = jsonToSend.toJSONString();
        mappedRequest.put(requestId, jsonToSendString);

        for (int i=0; i<numberOfWorkers; i++)
        {
            requestSocket= new Socket("127.0.0.1", 4000+i+1);
            ObjectOutputStream reqOut = new ObjectOutputStream(requestSocket.getOutputStream());
            reqOut.writeObject(mappedRequest);
            reqOut.flush();
            if (requestSocket != null) {
                requestSocket.close();
            }
            reqOut.close();
        }
      /*   String username = (String) json.get("username");

        for (int i=0; i<numberOfWorkers; i++)
        {
            requestSocket= new Socket("127.0.0.1", 4000+i+1);
            ObjectOutputStream reqOut = new ObjectOutputStream(requestSocket.getOutputStream());
            reqOut.writeObject("showBookings");
            reqOut.flush();
            reqOut.writeObject(username);
            reqOut.flush();
            if (requestSocket != null) {
                requestSocket.close();
            }
            reqOut.close();
        }*/

    }
    public void showRooms(JSONObject json) throws IOException, ClassNotFoundException
    {
        /* String username = (String) json.get("username");
        for (int i=0; i<numberOfWorkers; i++)
        {
            requestSocket= new Socket("127.0.0.1", 4000+i+1);
            ObjectOutputStream reqOut = new ObjectOutputStream(requestSocket.getOutputStream());
            reqOut.writeObject("showBookings");
            reqOut.flush();
            reqOut.writeObject(username);
            reqOut.flush();
            if (requestSocket != null) {
                requestSocket.close();
            }
            reqOut.close();
        }*/
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

}
