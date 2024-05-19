package aueb.hestia.UserInterface;// package com.aueb.hestia;

import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.util.*;

import aueb.hestia.Config.Config;
import aueb.hestia.Domain.Room;

import org.json.simple.JSONObject;

public class UserConsole extends Thread{

    JSONObject requestJson ;

    private String masterIp;
    private int masterPort;

    public UserConsole(JSONObject requestJson){
        this.requestJson = requestJson;
        Config config = new Config();
        this.masterIp = config.getMasterIp();
        this.masterPort = config.getClientRequestListenerPort();
    }

    ObjectInputStream responseInputStream;
    public Object request() throws RuntimeException {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            requestSocket = new Socket(masterIp, masterPort);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());



            out.writeUTF(requestJson.toJSONString());
            out.flush();

            Object obj =  in.readObject();
            return obj;

        } catch (IOException | ClassNotFoundException e) {
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


//    public static void main(String[] args) throws IOException, ParseException {
//        boolean searching=true;
//        String datePattern = "\\d{2}/\\d{2}/\\d{4}-\\d{2}/\\d{2}/\\d{4}";
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Welcome to our Hotel Booking App! Book now your next holidays!");
//        while(searching) {
//            System.out.println("1.Search\n2.Book a room\n3.Make a review\n4.Exit");
//            System.out.println("Type the number if the operation you want to perform.");
//
//            String answer = scanner.nextLine();
//            if (answer.equals("1")){
//                //looking for a room
//                System.out.println("Please give me the area you are looking for.");
//                String area = scanner.nextLine();
//                System.out.println("Please give the dates you are looking for in the format 01/04/2024-25/04/2024");
//                String dates = scanner.nextLine();
//                if (dates.matches(datePattern)) {
//                    System.out.println("Please give the number of persons.");
//                    if (scanner.hasNextInt()) {
//                        int noOfPersons = scanner.nextInt();
//                        System.out.println("Please give the number of stars.");
//                        if (scanner.hasNextInt()) {
//                            int stars = scanner.nextInt();
//                            JSONObject search = new JSONObject();
//                            search.put("area",area);
//                            search.put("dateRange",dates);
//                            search.put("noOfPersons",noOfPersons);
//                            search.put("stars",stars);
//                            search.put("function","search");
//                            ArrayList<Room> response = (ArrayList<Room>)new UserConsole(search).request();
//
//                            for (Room room : response)
//                            {
//                                System.out.println(room);
//                            }
//                            scanner.nextLine();
//
//                        } else {
//                            System.out.println("Invalid input. Please enter an integer for the number of stars.");
//                        }
//                    } else {
//                        System.out.println("Invalid input. Please enter an integer for the number of persons.");
//                    }
//                } else {
//                    System.out.println("Invalid dates format. Please enter dates in the specified format.");
//                }
//            }else if (answer.equals("2")) {
//                //booking a room
//                System.out.println("Give me your name please.");
//                String name = scanner.nextLine();
//                if (name.matches("[a-zA-Z\\s]+")) {
//                    System.out.println("Give me the room's name please.");
//                    String roomName = scanner.nextLine();
//                    System.out.println("Please give the dates you want to reserve in the format 01/04/2024-25/04/2024");
//                    String dates = scanner.nextLine();
//                    // Check if the input matches the required format
//                    if (dates.matches(datePattern)) {
//                        JSONObject booking = new JSONObject();
//                        booking.put("customerName",name);
//                        booking.put("roomName",roomName);
//                        booking.put("dateRange",dates);
//                        booking.put("function","book");
//
//                        String response = (String) new UserConsole(booking).request();
//
//                        System.out.println("\n"+response+"\n\n");
//
//
//                    } else {
//                        System.out.println("Invalid dates format. Please enter dates in the specified format.");
//                    }
//                } else {
//                    System.out.println("Invalid input. Please enter a valid name containing only alphabetic characters.");
//                }
//            }
//            else if (answer.equals("3")) {
//                //make a review
//                System.out.println("Give me your name please.");
//                String name = scanner.nextLine();
//                if (name.matches("[a-zA-Z\\s]+")) {
//                    System.out.println("Give me the room's name please.");
//                    String roomName = scanner.nextLine();
//                    System.out.println("Please give the number of stars.");
//                    if (scanner.hasNextInt()) {
//                        int stars = scanner.nextInt();
//                        JSONObject review = new JSONObject();
//                        review.put("customerName",name);
//                        review.put("roomName",roomName);
//                        review.put("stars",stars);
//                        review.put("function","review");
//
//                        String response  = (String) new UserConsole(review).request();
//
//                        System.out.println("\n"+response+"\n\n");
//                    } else {
//                        System.out.println("Invalid input. Please enter an integer for the number of stars.");
//                    }
//                } else {
//                    System.out.println("Invalid input. Please enter a valid name containing only alphabetic characters.");
//                }
//            }else if (answer.equals("4")) {
//                System.out.println("Thank you for visiting our app. We hope to see you again soon!");
//                searching = false;
//            }else{
//                System.out.println("Invalid Operation");
//            }
//        }
//        scanner.close();
//    }
}
