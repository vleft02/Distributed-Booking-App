package aueb.hestia;

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.text.ParseException;

public class Console extends Thread{

    JSONObject lod;
    Console(JSONObject lod){
        this.lod = lod;
    }

    public void run() {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            requestSocket = new Socket("127.0.0.1", 4000);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());
            System.out.println(lod.toJSONString());
            out.writeObject(lod.toJSONString());
            out.flush();


            String responseJson = (String) in.readObject();
            System.out.println("Server>" + responseJson);

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } /*catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }*/ catch (ClassNotFoundException e) {
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
        String datePattern = "\\d{2}/\\d{2}/\\d{2}-\\d{2}/\\d{2}/\\d{2}(,\\d{2}/\\d{2}/\\d{2}-\\d{2}/\\d{2}/\\d{2})*";
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
                    System.out.println("Please give the available dates in the format 01/04/24-25/04/24,15/05/24-29/05/24)");
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
                        room.put("dates",dates);
                        room.put("function","addRoom");

                        //System.out.println(room);
                        new Console(room).start();
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
                        new Console(manager).start();
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
                    new Console(manager).start();
                } else {
                    System.out.println("Invalid input. Please enter a valid name containing only alphabetic characters.");
                }
            }else if (answer.equals("4")) {
                //Add Availability
                System.out.println("Give me your lodging's name please.");
                String roomName = scanner.nextLine();
                System.out.println("Please give the available dates in the format 01/04/24-25/04/24,15/05/24-29/05/24)");
                String dates = scanner.nextLine();
                // Check if the input matches the required format
                if (dates.matches(datePattern)) {
                    JSONObject addindDates = new JSONObject();
                    addindDates.put("roomName",roomName);
                    addindDates.put("function","addDate");
                    addindDates.put("dates",dates);
                    new Console(addindDates).start();
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