package aueb.hestia.Workers;

import aueb.hestia.Helper.DateRange;
import aueb.hestia.Helper.InvalidDateException;
import aueb.hestia.Helper.Pair;
import aueb.hestia.Domain.Room;
import aueb.hestia.Helper.RoomUnavailableException;
import aueb.hestia.dao.RoomDao;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class WorkerThread extends Thread {
    private Integer requestId;
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
//                    showBookings();
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

        String dateRangeString = (String) json.get("dateRange");
        DateRange dateRange = null;
        try {
            dateRange = parseDateRange(dateRangeString);
        } catch (InvalidDateException e) {
            outputStream.writeUTF("Internal error due to false Dates");
            outputStream.flush();
            return;
        }

        int noOfPersons = (int) json.get("noOfPersons");
        float stars = (float) json.get("stars");
        ArrayList<Room> found;
        synchronized (rooms) {
            found = rooms.findByFilters(area, dateRange, noOfPersons, stars);
        }

        Pair<Integer, ArrayList<Room>> pair = new Pair<>();
        pair.put(requestId, found);

        requestSocket = new Socket("127.0.0.1", 4009);
        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());
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
                    roomToBook.book(dateRange);
                    outputStream.writeUTF("Room" + roomName + " booked Successfully for dates " + dateRange.toString());
                    outputStream.flush();
                } else {
                    outputStream.writeUTF("Room doesn't exist");
                    outputStream.flush();
                }


            } catch (RoomUnavailableException e) {
                outputStream.writeUTF("Booking of room " + roomName + "Failed");
                outputStream.flush();

            }

        }


    }

    public void review(JSONObject json) throws IOException, ClassNotFoundException {
        String roomName = (String) json.get("roomName");
        Long longStars = (Long) json.get("stars");
        int stars = longStars.intValue();
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
        int noOfPersons = (int) json.get("noOfPersons");
        String area = (String) json.get("area");
        double price = (double) json.get("price");
        String roomImage = (String) json.get("roomImage");

        synchronized (rooms) {
            rooms.add(new Room(username, roomName, noOfPersons, area, 0, 0, price, roomImage));
        }
        outputStream.writeUTF("Rooms added Successfully");
        outputStream.flush();


        outputStream.close();

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

        //Immplement aggregation
    }

    public void showRooms(JSONObject json) throws IOException, ClassNotFoundException {
        String username = (String) json.get("username");
        ArrayList<Room> ownedRooms;
        synchronized (rooms) {
            ownedRooms = rooms.findByOwner(username);

        }
        Pair<Integer, ArrayList<Room>> pair = new Pair<>();
        pair.put(requestId, ownedRooms);

        requestSocket = new Socket("127.0.0.1", 4009);
        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());

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