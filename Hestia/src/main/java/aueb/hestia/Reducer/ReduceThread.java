package aueb.hestia.Reducer;

import aueb.hestia.Config.Config;
import aueb.hestia.Domain.Booking;
import aueb.hestia.Helper.DateRange;
import aueb.hestia.Helper.InvalidDateException;
import aueb.hestia.Helper.Pair;
import aueb.hestia.Domain.Room;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.Socket;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class ReduceThread extends Thread {


    private Pair<Integer, Object> results;
    private int requestId;
    private int masterPort;
    private String masterIp;
    ObjectOutputStream workerOutputStream;
    ObjectInputStream workerInputStream;
    private Socket requestSocket;

    private Socket workerSocket;
    HashMap<Integer, Pair<Integer, ArrayList<Room>>> receivedParts;
    private int numberOfThreads;
    private String function=null;


    public ReduceThread(Socket workerSocket, HashMap<Integer, Pair<Integer, ArrayList<Room>>> receivedParts, int numberOfThreads) {

        this.workerSocket = workerSocket;
        this.receivedParts = receivedParts;
        this.numberOfThreads = numberOfThreads;
        try {
            this.workerOutputStream = new ObjectOutputStream(workerSocket.getOutputStream());
            this.workerInputStream = new ObjectInputStream(workerSocket.getInputStream());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Config config = new Config();
        this.masterPort = config.getReducerRequestListener();
        this.masterIp = config.getReducerIP();
    }

    public void run() {
        try {
            reduce();
        } catch(IOException | ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        } finally {
            try {
                workerOutputStream.close();
                workerInputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void reduce() throws IOException, ClassNotFoundException {

        String receivedObject = (String) workerInputStream.readObject();
        JSONObject requestJson;
        try {
            String requestJsonString = (String) receivedObject;
            requestJson = (JSONObject) new JSONParser().parse(requestJsonString);
            results = (Pair<Integer, Object>) workerInputStream.readObject();

            function = (String) requestJson.get("function");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


        requestId = results.getKey();
        Object obj = results.getValue();
        ArrayList<Room> rooms = (ArrayList<Room>) obj;

        synchronized (receivedParts) {
            if (!receivedParts.containsKey(results.getKey())) {
                Pair<Integer, ArrayList<Room>> receivedSegment = new Pair<>();
                receivedSegment.put(1, rooms);
                receivedParts.put(requestId, receivedSegment);
            } else {
                Pair<Integer, ArrayList<Room>> roomsAlreadyArrived = receivedParts.get(requestId);

                roomsAlreadyArrived.getValue().addAll(rooms);
                synchronized (roomsAlreadyArrived)
                {
                    roomsAlreadyArrived.setKey(roomsAlreadyArrived.getKey() + 1);
                }
                //Make Sure that the condition turns to true at some points
                if (roomsAlreadyArrived.getKey() >= numberOfThreads) {
                    requestSocket = new Socket(masterIp, masterPort);
                    ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());
                    if (!function.equals("showBookings")) {
                        Pair<Integer, ArrayList<Room>> response = new Pair<Integer, ArrayList<Room>>();
                        response.put(requestId, roomsAlreadyArrived.getValue());
                        out.writeObject(response);
                        out.flush();
                    }
                    else
                    {
                        DateRange dateRange;
                        try {
                             dateRange = parseDateRange((String)requestJson.get("dateRange"));
                        } catch (InvalidDateException e) {
                            throw new RuntimeException(e);
                        }
                        String responseMessage = bookingsPerArea(roomsAlreadyArrived.getValue(), dateRange);
                        Pair<Integer, String> response = new Pair<>();
                        response.put(requestId, responseMessage);

                        out.writeObject(response);
                        out.flush();
                    }
                    out.close();
                    in.close();
                    if (requestSocket != null) {
                        requestSocket.close();
                    }
                }
            }
        }
    }

    public String bookingsPerArea(ArrayList<Room> rooms, DateRange dateRange)
    {
        LocalDate fromDate = dateRange.getFrom();
        LocalDate toDate = dateRange.getTo();

        HashMap<String, Integer> map = new HashMap<>();
        for (Room room : rooms)
        {
            String area = room.getArea();
            if ( !map.containsKey(area) )
            {
                int bookings = 0;
                for (Booking booking: room.getBookings())
                {
                    //If A booking starts within the dateRange i.e for given DateRange 01/04/2024-10/04/2024, Bookings for 05/04/2024 - 15/04/2024 are contained
                    if (fromDate.isBefore(booking.getDateRange().getFrom()) && toDate.isAfter(booking.getDateRange().getFrom()))
                    {
                        bookings++;
                    }
                }
                map.put(area,bookings);
            }
            else
            {
                int bookings = 0;
                for (Booking booking: room.getBookings())
                {
                    //If A booking starts within the dateRange i.e. for given DateRange 01/04/2024-10/04/2024, Bookings for 05/04/2024 - 15/04/2024 are contained
                    if (fromDate.isBefore(booking.getDateRange().getFrom()) && toDate.isAfter(booking.getDateRange().getFrom()))
                    {
                        bookings++;
                    }
                }
                map.put(area,map.get(area)+bookings);
            }
        }
        String output = "";
        for (String key : map.keySet())
        {
            output += key+": "+map.get(key)+"\n";
        }

        return output;
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

