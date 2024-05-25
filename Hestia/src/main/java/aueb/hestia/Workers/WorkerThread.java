package aueb.hestia.Workers;

import aueb.hestia.Config.Config;
import aueb.hestia.Helper.DateRange;
import aueb.hestia.Helper.InvalidDateException;
import aueb.hestia.Helper.Pair;
import aueb.hestia.Domain.Room;
import aueb.hestia.Helper.RoomUnavailableException;
import aueb.hestia.Dao.RoomDao;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;

public class WorkerThread extends Thread {
    private Integer requestId;
    private int reducerPort;
    private String reducerIp;
    private RoomDao rooms;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private JSONObject requestJson;
    private String function;
    private Socket masterSocket;
    Socket requestSocket;

    WorkerThread(Socket masterSocket, RoomDao rooms) {
        this.rooms = rooms;
        this.masterSocket = masterSocket;
        try {
            this.outputStream = new ObjectOutputStream(masterSocket.getOutputStream());
            this.inputStream = new ObjectInputStream(masterSocket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Config config = new Config();
        this.reducerPort = config.getReducerPort();
        this.reducerIp = config.getReducerIP();
    }


    @Override
    public void run() {
        try {

            Pair<Integer, String> pair = (Pair<Integer, String>) inputStream.readObject();
            String requestJsonString = pair.getValue();
            this.requestJson = (JSONObject) new JSONParser().parse(requestJsonString);
            this.requestId = pair.getKey();
            this.function = (String) requestJson.get("function");


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
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void search(JSONObject json) throws IOException, ClassNotFoundException {
        String area = (String) json.get("area");
        Long longNoOfPersons = (Long) json.get("noOfPersons");
        int noOfPersons = longNoOfPersons.intValue();
        String dateRangeString = (String) json.get("dateRange");
        DateRange dateRange = null;
        try {
            dateRange = parseDateRange(dateRangeString);
        } catch (InvalidDateException e) {
            outputStream.writeUTF("Internal error due to false Dates");
            outputStream.flush();
            return;
        }

        Double dstars = (Double) json.get("stars");
        float stars = dstars.floatValue();
        ArrayList<Room> found;
        synchronized (rooms) {
            found = rooms.findByFilters(area, dateRange, noOfPersons, stars);
        }

        Pair<Integer, ArrayList<Room>> pair = new Pair<>();
        pair.put(requestId, found);

        requestSocket = new Socket(reducerIp, reducerPort);
        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());

        out.writeObject(json.toJSONString());
        out.flush();

        out.writeObject(pair);
        out.flush();

        out.close();
        in.close();
        if (requestSocket != null) {
            requestSocket.close();
        }

        //Send to reducer
    }

    public void book(JSONObject json) throws IOException, ClassNotFoundException {
        String roomName = (String) json.get("roomName");
        String dateRangeString = (String) json.get("dateRange");
        String username = (String) json.get("customerName");
        DateRange dateRange = null;
        try {
            dateRange = parseDateRange(dateRangeString);
        } catch (InvalidDateException e) {
            outputStream.writeUTF("Internal error due to false Dates");
            outputStream.flush();
            return;
        }
        synchronized (rooms) {
            Room roomToBook = rooms.findByRoomName(roomName);
            try {
                if (roomToBook != null) {
                    roomToBook.book(dateRange,username);
                    outputStream.writeUTF("Room: " + roomName + " booked Successfully for dates " + dateRange.toString());
                    outputStream.flush();
                } else {
                    outputStream.writeUTF("Room doesn't exist");
                    outputStream.flush();
                }


            } catch (RoomUnavailableException e) {
                outputStream.writeUTF("Booking of room " + roomName + " Failed for Dates Specified");
                outputStream.flush();

            }

        }


    }

    public void review(JSONObject json) throws IOException, ClassNotFoundException {
        String roomName = (String) json.get("roomName");
        Double dStars = (Double) json.get("stars");
        float stars = dStars.floatValue();
        synchronized (rooms) {
            Room roomToRate = rooms.findByRoomName(roomName);
            if (roomToRate != null) {
                roomToRate.review(stars);
                outputStream.writeUTF("Room reviewed Successfully");
                outputStream.flush();
            } else {
                outputStream.writeUTF("Room not Found");
                outputStream.flush();
            }
        }
    }

    public void addRoom(JSONObject json) throws IOException, ClassNotFoundException {
        String username = (String) json.get("username");
        String roomName = (String) json.get("roomName");
        Long longNoOfPersons = (Long) json.get("noOfPersons");
        int noOfPersons = longNoOfPersons.intValue();
        String area = (String) json.get("area");
        String dateRangeString = (String) json.get("dateRange");
        DateRange dateRange = null;
        try {
            dateRange = parseDateRange(dateRangeString);
        } catch (InvalidDateException e) {
            outputStream.writeUTF("Internal error due to false Dates");
            outputStream.flush();
            return;
        }

        double price = (double) json.get("price");
        double stars = (double) json.get("stars");

        String roomImage = (String) json.get("roomImage");

        String encodedImage = (String) json.get("roomImageData");

        Long longNoOfReviews = (Long) json.get("noOfReviews");
        int noOfReviews = longNoOfPersons.intValue();


        synchronized (rooms) {
            Room room = new Room(username, roomName, noOfPersons, area, (float)stars ,noOfReviews, price, roomImage);
            room.addAvailability(dateRange);
            room.setImageData(encodedImage);
            rooms.add(room);
        }
        outputStream.writeUTF("Room added Successfully");
        outputStream.flush();

    }

    public void addDate(JSONObject json) throws IOException, ClassNotFoundException {
        String roomName = (String) json.get("roomName");
        String dateRangeString = (String) json.get("dateRange");
        DateRange dateRange = null;
        try {
            dateRange = parseDateRange(dateRangeString);
        } catch (InvalidDateException e) {
            outputStream.writeUTF("Internal error due to false Dates");
            outputStream.flush();
            return;
        }

        synchronized (rooms) {
            Room roomToUpdate = rooms.findByRoomName(roomName);
            if (roomToUpdate != null) {
                roomToUpdate.addAvailability(dateRange);
                outputStream.writeUTF("Availability for room " + roomName + " added successfully");
                outputStream.flush();
            } else {
                outputStream.writeUTF("Room not found");
                outputStream.flush();
            }
        }
    }

    public void showBookings(JSONObject json) throws IOException, ClassNotFoundException {
        String username = (String) json.get("username");
        synchronized (rooms)
        {
            ArrayList<Room> ownedRooms = rooms.findByOwner(username);

            requestSocket = new Socket(reducerIp, reducerPort);
            ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());

            Pair<Integer, ArrayList<Room>> pair = new Pair<>();
            pair.put(requestId, ownedRooms);

            out.writeObject(json.toJSONString());
            out.flush();

            out.writeObject(pair);
            out.flush();

            out.close();
            in.close();
            if (requestSocket != null) {
                requestSocket.close();
            }
        }

    }

    public void showRooms(JSONObject json) throws IOException, ClassNotFoundException {
        String username = (String) json.get("username");
        ArrayList<Room> ownedRooms;
        synchronized (rooms) {
            ownedRooms = rooms.findByOwner(username);
        }
        Pair<Integer, ArrayList<Room>> pair = new Pair<>();
        pair.put(requestId, ownedRooms);

        requestSocket = new Socket(reducerIp, reducerPort);
        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());

        out.writeObject(json.toJSONString());
        out.flush();

        out.writeObject(pair);
        out.flush();


        out.close();
        in.close();
        if (requestSocket != null) {
            requestSocket.close();
        }

    }

    private DateRange parseDateRange(String text) throws InvalidDateException {
        int delimiterIndex = text.indexOf("-");

        if (delimiterIndex != -1) {
            String startDate = text.substring(0, delimiterIndex);
            String endDate = text.substring(delimiterIndex + 1);
            return new DateRange(startDate, endDate);
        }
        return null;
    }
}