package aueb.hestia.android.search;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import aueb.hestia.Config.Config;
import aueb.hestia.Domain.Booking;
import aueb.hestia.Domain.Room;
import aueb.hestia.Helper.DateRange;
import aueb.hestia.R;
import aueb.hestia.UserInterface.UserConsole;
import aueb.hestia.android.login.LoginView;

public class SearchPresenter {
    private SearchRoomsView view;

    public SearchRoomsView getView() {
        return view;
    }
    public Handler SearchResponseHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override

        public boolean handleMessage(@NonNull Message message) {

            String result = message.getData().getString("roomsJson");
            ArrayList<Room> rooms = parseJsonToRooms(result);

            view.showRooms(rooms);

            return false;
        }
    });


    public void setView(SearchRoomsView view) {
        this.view = view;
    }


    public void request(JSONObject requestJson) throws RuntimeException {

        Thread t1= new Thread(()->{
            Socket requestSocket = null;
            ObjectOutputStream out = null;
            ObjectInputStream in = null;

    //        Config cfg = new Config();
    //        String masterIp = cfg.getMasterIp();
    //        int masterPort = cfg.getClientRequestListenerPort();


            try {
                requestSocket = new Socket("192.168.56.1", 7000);
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());



                out.writeUTF(requestJson.toJSONString());
                out.flush();

                Object obj = in.readObject();

                ArrayList<Room> rooms = (ArrayList<Room>) obj;



                in.close();
                out.close();
                requestSocket.close();





                Message msg = new Message();
                Bundle bundle = new Bundle();
//                bundle.putParcelableArrayList("rooms", rooms);
                String roomsJson = convertRoomsToJson(rooms);
                bundle.putString("roomsJson",roomsJson);
                msg.setData(bundle);
                SearchResponseHandler.sendMessage(msg);

            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
    }

    public void search() {
        String datePattern = "\\d{2}/\\d{2}/\\d{4}-\\d{2}/\\d{2}/\\d{4}";

        String area = view.getArea();
        int noOfPersons = view.getNoOfPersons();
        float stars = view.getStars();
        String dates = view.getDates();

        if(!dates.matches(datePattern))
        {
            view.showMessage("Invalid Input", "Please provide a valid Date Range");
            return;
        }


        JSONObject search = new JSONObject();
        search.put("area",area);
        search.put("dateRange",dates);
        search.put("noOfPersons",noOfPersons);
        search.put("stars",stars);
        search.put("function","search");

        request(search);
        //edo skeftomai na kaloum
        // e mia methodo poy tha tiponei ta apotelesmata
        //apo ti lista room sto room_list_item.xml
//                for (Room room : response)
//                            {
//                                System.out.println(room);
//                            }
    }


    public static String convertRoomsToJson(ArrayList<Room> rooms) {
        JSONArray jsonRoomsArray = new JSONArray();
            if (rooms.size()>0) {
                for (Room room : rooms) {
                    JSONObject jsonRoom = new JSONObject();
                    jsonRoom.put("roomName", room.getRoomName());
                    jsonRoom.put("noOfPersons", room.getNoOfPersons());
                    jsonRoom.put("area", room.getArea());
                    jsonRoom.put("stars", room.getStars());
                    jsonRoom.put("noOfReviews", room.getNoOfReviews());
                    jsonRoom.put("roomImage", room.getRoomImage());
                    jsonRoom.put("price", room.getPrice());
                    jsonRoom.put("ownerUsername", room.getOwnerUsername());

                    // Convert availability to JSON array
                    JSONArray availabilityArray = new JSONArray();
                    for (DateRange dateRange : room.getAvailability()) {
                        JSONObject availabilityJson = new JSONObject();
                        availabilityJson.put("from", dateRange.getFrom().toString());
                        availabilityJson.put("to", dateRange.getTo().toString());
                        availabilityArray.add(availabilityJson);
                    }
                    jsonRoom.put("availability", availabilityArray);
                    jsonRoom.put("roomImageData", room.getImageData());


                    jsonRoomsArray.add(jsonRoom);
                }
                return jsonRoomsArray.toString();
            }
            else
            {
                return "";
            }
    }



        public static ArrayList<Room> parseJsonToRooms(String jsonString) {
            ArrayList<Room> rooms = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray jsonRoomsArray = null;

            if (jsonString != null && !jsonString.isEmpty()) {
                try {
                    jsonRoomsArray = (JSONArray) parser.parse(jsonString);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                for (Object obj : jsonRoomsArray) {
                    JSONObject jsonRoom = (JSONObject) obj;
                    String roomName = (String) jsonRoom.get("roomName");
                    int noOfPersons = ((Long) jsonRoom.get("noOfPersons")).intValue();
                    String area = (String) jsonRoom.get("area");
                    float stars = ((Number) jsonRoom.get("stars")).floatValue();
                    int noOfReviews = ((Long) jsonRoom.get("noOfReviews")).intValue();
                    String roomImage = (String) jsonRoom.get("roomImage");
                    double price = ((Number) jsonRoom.get("price")).doubleValue();
                    String ownerUsername = (String) jsonRoom.get("ownerUsername");
                    String encodedImageData = (String) jsonRoom.get("roomImageData");

                    // Parse availability array
                    JSONArray availabilityArray = (JSONArray) jsonRoom.get("availability");
                    List<DateRange> availability = new ArrayList<>();
                    for (Object availabilityObj : availabilityArray) {
                        JSONObject availabilityJson = (JSONObject) availabilityObj;
                        String from = (String) availabilityJson.get("from");
                        String to = (String) availabilityJson.get("to");
                        // Parse 'from' and 'to' strings into LocalDate objects
                        LocalDate fromDate = LocalDate.parse(from);
                        LocalDate toDate = LocalDate.parse(to);
                        availability.add(new DateRange(fromDate, toDate));
                    }


                    // Create Room object and add to list
                    Room room = new Room("", roomName, noOfPersons, area, (float) stars, noOfReviews, price, roomImage);

                    for (DateRange range : availability) {
                        room.addAvailability(range);
                    }
                    room.setImageData(encodedImageData);
                    rooms.add(room);
                }
            }

            return rooms;
        }
}

