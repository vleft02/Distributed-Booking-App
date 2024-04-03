package aueb.hestia;

import java.io.*;
import java.net.*;
import java.util.*;

import org.json.simple.JSONArray;

import org.json.simple.JSONObject;

import org.json.simple.parser.*;

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
            requestSocket = new Socket("localhost", 4321);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.writeObject(lod);
            JSONObject lod2 = (JSONObject) in.readObject();
            System.out.println("Server>" + lod2);

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
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
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to our Hotel Booking App! Add now your lodging and start earning money.");
        while(adding) {
            System.out.println("Do you want to add a lodging?(y/n)");
            String answer = scanner.nextLine();
            if (answer.equals("y")){
                System.out.println("Give me please the path of your lodging's file.");
                String path = scanner.nextLine();
                System.out.println("Give me the available dates.(for example:01/04/24-25/04/24,15/05/24-29/05/24)");
                String dates = scanner.nextLine();
                Object o = new JSONParser().parse(new FileReader(path));
                JSONObject j = (JSONObject) o;

                String roomName = (String) j.get("roomName");
                Long noOfPersons = (Long ) j.get("noOfPersons");
                String area = (String) j.get("area");
                Long stars = (Long ) j.get("stars");
                Long noOfReviews = (Long ) j.get("noOfReviews");
                String roomImage = (String ) j.get("roomImage");

                JSONObject lodging = new JSONObject();
                lodging.put("roomName",roomName);
                lodging.put("noOfPersons",noOfPersons);
                lodging.put("area",area);
                lodging.put("stars",stars);
                lodging.put("noOfReviews",noOfReviews);
                lodging.put("roomImage",roomImage);
                lodging.put("dates",dates);
                lodging.put("function","addRoom");

                //System.out.println(lodging);
                new Console(lodging).start();
            }else if (answer.equals("n")) {
                System.out.println("Do you want to see your reservations?");
                answer = scanner.nextLine();
                if (answer.equals("y")){
                    System.out.println("Give me your name please.");
                    String name = scanner.nextLine();
                    JSONObject manager = new JSONObject();
                    manager.put("name",name);
                    manager.put("function","showBookings");
                    new Console(manager).start();
                }else if (answer.equals("n")) {
                    System.out.println("Thank you for visiting our app. We hope to see you again soon!");
                    adding = false;
                }else{
                    System.out.println("Please type only y or n.");
                }
            }else{
                System.out.println("Please type only y or n.");
            }
        }
        scanner.close();
    }
}