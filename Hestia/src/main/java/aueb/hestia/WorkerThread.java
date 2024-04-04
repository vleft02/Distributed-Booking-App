package aueb.hestia;

import aueb.hestia.DateRange;
import aueb.hestia.Room;
import aueb.hestia.dao.RoomDao;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class WorkerThread extends Thread{
    private Integer requestId;
    RoomDao rooms;
    ObjectInputStream inputStream;
    JSONObject requestJson;
    String function;
    Socket requestSocket;

    WorkerThread(ObjectInputStream inputStream, RoomDao rooms)
    {
        this.rooms = rooms;
        try {
        this.inputStream = inputStream;
        Pair<Integer,String> pair = (Pair<Integer, String>) inputStream.readObject();
        this.requestJson = (JSONObject)  new JSONParser().parse(pair.getValue());
        this.requestId = pair.getKey();
        this.function = (String) requestJson.get("function");
        System.out.println(function);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    

    @Override
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
//                    showBookings();
                    break;
                default:
                    System.out.print("Function Not Found");
            }
       } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void search(JSONObject json) throws IOException, ClassNotFoundException
    {
        String area = (String) json.get("area");
        DateRange dateRange = null;
        DateRange dateRangeString = (DateRange) json.get("dateRange");;
        int noOfPersons = (int) json.get("noOfPersons");
        float stars =  (float) json.get("stars");
        ArrayList<Room> found;
        synchronized (rooms)
        {
            found  = rooms.findByFilters(area, dateRange, noOfPersons, stars);
        }

        Pair<Integer,ArrayList<Room>> pair = new Pair<>();
        pair.put(requestId, found);

        requestSocket= new Socket("127.0.0.1", 4009);
        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
        out.writeObject(pair);
        out.flush();
        if (requestSocket != null) {
            requestSocket.close();
        }
        out.close();
        //Send to reducer
    }

    public void book(JSONObject json) throws IOException, ClassNotFoundException {
        String roomName = (String) json.get("roomName");
        DateRange dateRange = (DateRange) json.get("dateRange");

        synchronized (rooms)
        {
            Room roomToBook  = rooms.findByRoomName(roomName);
            if(roomToBook != null)
            {
                roomToBook.book(dateRange);
            }
        }
    }

    public void review(JSONObject json) throws IOException, ClassNotFoundException
    {
        String roomName = (String) json.get("roomName");
        float stars = (float) json.get("stars");

        synchronized (rooms)
        {
            Room roomToRate  = rooms.findByRoomName(roomName);
            if(roomToRate != null)
            {
                roomToRate.review(stars);
            }

        }
    }

    public void addRoom(JSONObject json) throws IOException, ClassNotFoundException
    {
        String username = (String) json.get("username");
        String roomName = (String) json.get("roomName");
        int noOfPersons = (int) json.get("noOfPersons");
        String area = (String) json.get("area");
        double price = (double) json.get("price");
        String roomImage = (String) json.get("roomImage");

        synchronized (rooms)
        {
            rooms.add(new Room(username,roomName, noOfPersons, area, 0,0,price,roomImage));
        }

        requestSocket= new Socket("127.0.0.1", 4009);
        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
        out.writeObject("Room Added Succesfully");
        out.flush();



    }

    public void addDate(JSONObject json) throws IOException, ClassNotFoundException
    {
        String roomName= (String) json.get("roomName");
        DateRange daterange = (DateRange) json.get("dateRange");

        synchronized (rooms)
        {
            Room roomToUpdate = rooms.findByRoomName(roomName);
            if (roomToUpdate != null)
            {
                roomToUpdate.addAvailability(daterange);
            }
        }
    }

    public void showBookings(JSONObject json) throws IOException, ClassNotFoundException
    {
        String username = (String) json.get("username");

        //Immplement aggregation
    }
    public void showRooms(JSONObject json) throws IOException, ClassNotFoundException
    {
        String username = (String) json.get("username");
        ArrayList<Room> ownedRooms;
        synchronized (rooms)
        {
            ownedRooms = rooms.findByOwner(username);

        }
        Pair<Integer,ArrayList<Room>> pair = new Pair<>();
        pair.put(requestId, ownedRooms);

        requestSocket= new Socket("127.0.0.1", 4009);
        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
        out.writeObject(pair);
        out.flush();
        if (requestSocket != null) {
            requestSocket.close();
        }
        out.close();
    }

}
