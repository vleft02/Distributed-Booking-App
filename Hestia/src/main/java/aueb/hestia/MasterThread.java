package aueb.hestia;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.json.simple.JSONObject;
import static java.lang.Math.abs;


public class MasterThread extends Thread{

    private String function;
    private Socket requestSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private int numberOfWorkers;
    MasterThread(Socket socket,int numberOfWorkers)
    {
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
            this.function = (String) in.readObject();
            this.numberOfWorkers = numberOfWorkers;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    public void run() {
        try {
            JSONObject obj = (JSONObject) in.readObject();
            function = (String) obj.get("function");
            //paizei na mhn mpainei se kanena case etsi, emena mono me equals doulepse
            switch (function) {
                case "addRoom":
                    addRoom(in, out);
                    break;
                case "addDate":
                    addDate(in, out);
                    break;
                case "search":
                    search(in,out);
                    break;
                case "book":
                    book(in,out);
                    break;
                case "review":
                    review(in,out);
                    break;
                case "showRooms":
                    showRooms(in,out);
                    break;
                case "showBookings":
                    showBookings(in,out);
                    break;
                default:
                    System.out.print("Function Not Found");
            }

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException | ClassNotFoundException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                in.close();	out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


    public void search(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException
    {
        String area = (String) in.readObject();
        DateRange dateRange = (DateRange) in.readObject();
        int noOfPersons = in.readInt();
        float stars = in.readFloat();


        for (int i=0; i<numberOfWorkers; i++)
        {
            requestSocket= new Socket("127.0.0.1", 4000+i+1);
            ObjectOutputStream reqOut = new ObjectOutputStream(requestSocket.getOutputStream());
            reqOut.writeObject("showBookings");
            reqOut.flush();
            reqOut.writeObject(area);
            reqOut.flush();
            reqOut.writeObject(dateRange);
            reqOut.flush();
            reqOut.writeInt(noOfPersons);
            reqOut.flush();
            reqOut.writeFloat(stars);
            reqOut.flush();
            if (requestSocket != null) {
                requestSocket.close();
            }
            reqOut.close();
        }
    }

    public void book(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException
    {
        String roomName = (String) in.readObject();
        DateRange dateRange = (DateRange) in.readObject();

        requestSocket= new Socket("127.0.0.1", 4000+hashCode(roomName));
        ObjectOutputStream reqOut = new ObjectOutputStream(requestSocket.getOutputStream());
        reqOut.writeObject("book");
        reqOut.flush();
        reqOut.writeObject(roomName);
        reqOut.flush();
        reqOut.writeObject(dateRange);
        reqOut.flush();

        if (requestSocket != null) {
            requestSocket.close();
        }
        reqOut.close();
    }



    public void review(ObjectInputStream in, ObjectOutputStream out) throws  IOException, ClassNotFoundException
    {
        String roomName = (String) in.readObject();
        float stars = in.readFloat();


        requestSocket= new Socket("127.0.0.1", 4000+hashCode(roomName));
        ObjectOutputStream reqOut = new ObjectOutputStream(requestSocket.getOutputStream());
        reqOut.writeObject("review");
        reqOut.flush();
        reqOut.writeObject(roomName);
        reqOut.flush();
        reqOut.writeFloat(stars);
        reqOut.flush();


        if (requestSocket != null) {
            requestSocket.close();
        }
        reqOut.close();
    }

    public void addRoom(ObjectInputStream in, ObjectOutputStream out) throws  IOException, ClassNotFoundException
    {
        String username = (String) in.readObject();
        String roomName = (String) in.readObject();
        int noOfPersons = in.readInt();
        String area = (String) in.readObject();
        double price = in.readDouble();
        System.out.print(price);
        String roomImage = (String) in.readObject();

        System.out.println(4000+hashCode(roomName));
        requestSocket = new Socket("127.0.0.1", 4000+hashCode(roomName));
        ObjectOutputStream reqOut = new ObjectOutputStream(requestSocket.getOutputStream());
        ObjectInputStream in2 = new ObjectInputStream(requestSocket.getInputStream());
        reqOut.writeObject("addRoom");
        reqOut.flush();
        reqOut.writeObject(username);
        reqOut.flush();
        reqOut.writeObject(roomName);
        reqOut.flush();
        reqOut.writeInt(noOfPersons);
        reqOut.flush();
        reqOut.writeObject(area);
        reqOut.flush();
        reqOut.writeDouble(price);
        reqOut.flush();
        reqOut.writeObject(roomImage);
        reqOut.flush();

        if (requestSocket != null) {
            requestSocket.close();
        }
        reqOut.close();
    }

    public void addDate(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException
    {
        String roomName= (String) in.readObject();
        DateRange daterange = (DateRange) in.readObject();

        requestSocket= new Socket("127.0.0.1", 4000+hashCode(roomName));
        ObjectOutputStream reqOut = new ObjectOutputStream(requestSocket.getOutputStream());
        reqOut.writeObject("addDate");
        reqOut.flush();
        reqOut.writeObject(roomName);
        reqOut.flush();
        reqOut.writeObject(daterange);
        reqOut.flush();

        if (requestSocket != null) {
            requestSocket.close();
        }
        reqOut.close();
    }

    public void showBookings(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException
    {
        String username = (String) in.readObject();

        for (int i=0; i<numberOfWorkers; i++)
        {
            requestSocket= new Socket("127.0.0.1", 4000+i+1);
            ObjectOutputStream reqOut = new ObjectOutputStream(requestSocket.getOutputStream());
            reqOut.writeObject("showBookings");
            reqOut.flush();
            reqOut.writeObject(username);
            reqOut.flush();
            if (requestSocket != null) {
                requestSocket.close();
            }
            reqOut.close();
        }

    }
    public void showRooms(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException
    {
        String username = (String) in.readObject();
        for (int i=0; i<numberOfWorkers; i++)
        {
            requestSocket= new Socket("127.0.0.1", 4000+i+1);
            ObjectOutputStream reqOut = new ObjectOutputStream(requestSocket.getOutputStream());
            reqOut.writeObject("showBookings");
            reqOut.flush();
            reqOut.writeObject(username);
            reqOut.flush();
            if (requestSocket != null) {
                requestSocket.close();
            }
            reqOut.close();
        }
    }



    private int hashCode(String roomName) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(roomName.getBytes(StandardCharsets.UTF_8));
            BigInteger bigInt = new BigInteger(1, encodedHash);
            return (int) abs(bigInt.intValue() % numberOfWorkers);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return 0;
        }
    }

}
