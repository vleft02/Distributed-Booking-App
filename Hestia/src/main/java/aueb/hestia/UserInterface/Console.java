package aueb.hestia.UserInterface;

import java.io.*;
import java.net.*;
import java.util.*;

import aueb.hestia.Domain.Room;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.text.ParseException;

public class Console extends Thread{

    private JSONObject requestJson;
    Console(JSONObject requestJson){
        this.requestJson = requestJson;
    }

    public Object request() {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            requestSocket = new Socket("127.0.0.1", 4000);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.writeUTF(requestJson.toJSONString());
            out.flush();

            Object obj =  in.readObject();
            return obj;

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();	out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        boolean adding=true;
        String datePattern = "\\d{2}/\\d{2}/\\d{4}-\\d{2}/\\d{2}/\\d{4}(,\\d{2}/\\d{2}/\\d{4}-\\d{2}/\\d{2}/\\d{4})*";
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to our Hotel Booking App! Add your lodging now and start earning money.");
        while(adding) {
            System.out.println("1.Add a lodging\n2.See Reservations Per Area\n3.See owned lodgings\n4.Add availability for Lodging\n5.Exit");
            System.out.println("Type the number if the operation you want to perform.");

//            System.out.println("Do you want to add a lodging?(y/n)");
            String answer = scanner.nextLine();
            if (answer.equals("1")){
                try{
                    System.out.println("Please give me the path of your lodging's JSON file.");
                    String path = scanner.nextLine();
                    System.out.println("Please give the available dates in the format 01/04/2024-25/04/2024,15/05/2024-29/05/2024)");
                    String dates = scanner.nextLine();
                    if (dates.matches(datePattern)) {
                        Object o = new JSONParser().parse(new FileReader(path));
                        JSONObject j = (JSONObject) o;

                        String roomName = (String) j.get("roomName");
                        Long noOfPersons = (Long ) j.get("noOfPersons");
                        String area = (String) j.get("area");
                        Long stars = (Long ) j.get("stars");
                        Long noOfReviews = (Long ) j.get("noOfReviews");
                        String roomImage = (String ) j.get("roomImage");

                        JSONObject room = new JSONObject();
                        room.put("roomName",roomName);
                        room.put("noOfPersons",noOfPersons);
                        room.put("area",area);
                        room.put("stars",stars);
                        room.put("noOfReviews",noOfReviews);
                        room.put("roomImage",roomImage);
                        room.put("dateRange", dates);
                        room.put("function","addRoom");

                        String response = (String) new Console(room).request();
                        System.out.println(response);
                    } else {
                        System.out.println("Invalid dates format. Please enter dates in the specified format.");
                    }
                }catch (FileNotFoundException e) {
                    System.err.println("File not found. Please provide a valid file path.");
                } catch (Exception e) {
                    System.err.println("An error occurred while parsing the JSON file.");
                    e.printStackTrace();
                }

            }else if (answer.equals("2")) {
//                System.out.println("Do you want to see your reservations?");
//                answer = scanner.nextLine();
//                if (answer.equals("y")){
                    System.out.println("Give me your name please.");
                    String name = scanner.nextLine();
                    if (name.matches("[a-zA-Z]+")) {
                        JSONObject manager = new JSONObject();
                        manager.put("name",name);
                        manager.put("function","showBookings");
                        new Console(manager).request();


                    } else {
                        System.out.println("Invalid input. Please enter a valid name containing only alphabetic characters.");
                    }
            }
            else if (answer.equals("3")) {
                //See Owned Lodgings
                System.out.println("Give me your name please.");
                String name = scanner.nextLine();
                if (name.matches("[a-zA-Z]+")) {
                    JSONObject manager = new JSONObject();
                    manager.put("name",name);
                    manager.put("function","showRooms");
                    ArrayList<Room> response = (ArrayList<Room>) new Console(manager).request();

                    for (Room room : response)
                    {
                        System.out.println(room);
                    }
                } else {
                    System.out.println("Invalid input. Please enter a valid name containing only alphabetic characters.");
                }
            }else if (answer.equals("4")) {
                //Add Availability
                System.out.println("Give me your lodging's name please.");
                String roomName = scanner.nextLine();
                System.out.println("Please give the available dates in the format 01/04/2024-25/04/2024,15/05/24-29/05/24");
                String dates = scanner.nextLine();
                // Check if the input matches the required format
                if (dates.matches(datePattern)) {
                    JSONObject addindDates = new JSONObject();
                    addindDates.put("roomName",roomName);
                    addindDates.put("function","addDate");
                    addindDates.put("dateRange",dates);
                    String response = (String) new Console(addindDates).request();
                    System.out.println(response);
                } else {
                    System.out.println("Invalid dates format. Please enter dates in the specified format.");
                }
            }else if (answer.equals("5")) {
                System.out.println("Thank you for visiting our app. We hope to see you again soon!");
                adding = false;
            }else{
                System.out.println("Invalid Operation");
            }
        }
        scanner.close();
    }
}