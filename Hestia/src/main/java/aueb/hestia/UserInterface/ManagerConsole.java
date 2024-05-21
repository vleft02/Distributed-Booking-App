package aueb.hestia.UserInterface;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.*;

import aueb.hestia.Config.Config;
import aueb.hestia.Domain.Room;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import javax.imageio.ImageIO;

public class ManagerConsole extends Thread{

    private JSONObject requestJson;
    private String masterIp;
    private int masterPort;
    ManagerConsole(JSONObject requestJson){
        this.requestJson = requestJson;
        Config config = new Config();
        this.masterIp = config.getMasterIp();
        this.masterPort = config.getClientRequestListenerPort();
    }

    public Object request() {
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
                if (in != null) {
                    in.close();
                }
                if (out != null)
                {
                    out.close();
                    requestSocket.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


    public static void main(String[] args)  {
        boolean adding=true;
        String datePattern = "\\d{2}/\\d{2}/\\d{4}-\\d{2}/\\d{2}/\\d{4}(,\\d{2}/\\d{2}/\\d{4}-\\d{2}/\\d{2}/\\d{4})*";
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to our Hotel Booking App! Add your lodging now and start earning money.");
        while(adding) {
            System.out.println("1.Add lodgings\n2.See Reservations Per Area\n3.See owned lodgings\n4.Add availability for Lodging\n5.Exit");
            System.out.println("Type the number of the operation you want to perform.");

//            System.out.println("Do you want to add a lodging?(y/n)");
            String answer = scanner.nextLine();
            if (answer.equals("1")){
                try{
                    System.out.println("Please give me your username");
                    String username = scanner.nextLine();
                    System.out.println("Please give me the path of your lodgings' JSON file.");
                    String path = scanner.nextLine();
                    System.out.println("Please give the available dates in the format 01/04/2024-25/04/2024");
                    String dates = scanner.nextLine();
                    if (dates.matches(datePattern)) {
                        BufferedReader reader = new BufferedReader(new FileReader(path));
                        String line;
                        StringBuilder jsonStringBuilder = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            jsonStringBuilder.append(line);
                        }
                        JSONParser parser = new JSONParser();
                        JSONArray jsonArray = (JSONArray) parser.parse(jsonStringBuilder.toString());

                        for (Object obj : jsonArray) {

                            JSONObject j = (JSONObject) obj;
                            String roomName = (String) j.get("roomName");
                            Long noOfPersons = (Long) j.get("noOfPersons");
                            String area = (String) j.get("area");
                            double stars = (double) j.get("stars");
                            double price = (double) j.get("price");
                            Long noOfReviews = (Long) j.get("noOfReviews");
                            String roomImage = (String) j.get("roomImage");

                            String osName = System.getProperty("os.name").toLowerCase();

                            File imageFile = null;
                            if (osName.contains("windows")) {

                                String filePath = new File("").getAbsolutePath();
                                imageFile = new File(filePath + "\\src\\main\\java\\aueb\\hestia\\Config\\images\\" + roomImage);

                            }else if (osName.contains("linux")) {
                                String filePath = new File("").getAbsolutePath();
                                imageFile = new File(filePath + "/src/main/java/aueb/hestia/Config/images/" + roomImage);
                            }
                            byte[] imageData = convertToByteArray(imageFile);
                            String encodedImage = Base64.getEncoder().encodeToString(imageData);

                            JSONObject room = new JSONObject();
                            room.put("username", username);
                            room.put("roomName",roomName);
                            room.put("noOfPersons",noOfPersons);
                            room.put("area",area);
                            room.put("stars",stars);
                            room.put("price", price);
                            room.put("noOfReviews",noOfReviews);
                            room.put("roomImage",roomImage);
                            room.put("roomImageData", encodedImage);
                            room.put("dateRange", dates);
                            room.put("function","addRoom");


                            String response = (String) new ManagerConsole(room).request();
                            System.out.println(response);
                        }
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
                System.out.println("Give me your name please.");
                String name = scanner.nextLine();
                System.out.println("Give The Date Range of interest");
                String dates= scanner.nextLine();
                if (dates.matches(datePattern)) {

                    if (name.matches("[a-zA-Z\\s]+")) {
                        JSONObject manager = new JSONObject();
                        manager.put("username", name);
                        manager.put("dateRange", dates);
                        manager.put("function", "showBookings");

                        String response = (String) new ManagerConsole(manager).request();
                        System.out.println(response);
                    } else {
                        System.out.println("Invalid input. Please enter a valid name containing only alphabetic characters.");
                    }
                } else
                {
                    System.out.println("Invalid dates format. Please enter dates in the specified format.");
                }
            }
            else if (answer.equals("3")) {
                //See Owned Lodgings
                System.out.println("Give me your name please.");
                String username = scanner.nextLine();
                if (username.matches("[a-zA-Z\\s]+")) {
                    JSONObject manager = new JSONObject();
                    manager.put("username",username);
                    manager.put("function","showRooms");
                    ArrayList<Room> response = (ArrayList<Room>) new ManagerConsole(manager).request();

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
                System.out.println("Please give the available dates in the format 01/04/2024-25/04/2024");
                String dates = scanner.nextLine();
                // Check if the input matches the required format
                if (dates.matches(datePattern)) {
                    JSONObject addindDates = new JSONObject();
                    addindDates.put("roomName",roomName);
                    addindDates.put("function","addDate");
                    addindDates.put("dateRange",dates);
                    String response = (String) new ManagerConsole(addindDates).request();
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
    public static byte[] convertToByteArray(File image)
    {
        ByteArrayOutputStream byteArrayOutputStream;
        try {
            BufferedImage bufferedImage = ImageIO.read(image);
            byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }
}


