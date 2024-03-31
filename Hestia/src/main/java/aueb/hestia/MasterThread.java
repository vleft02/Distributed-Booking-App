package aueb.hestia;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

            switch (function) {
                case "addRoom":
                    addRoom(in);
                    break;
                case "addDate":
                    addDate(in);
                    break;
                case "search":
                    search(in);
                    break;
                case "book":
                    book(in);
                    break;
                case "review":
                    review(in);
                    break;
                case "showRooms":
                    showRooms(in);
                    break;
                case "showBookings":
                    showBookings(in);
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


    public void search(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        String area = (String) in.readObject();
        DateRange dateRange = (DateRange) in.readObject();
        int noOfPersons = in.readInt();
        float stars = in.readFloat();


        for (int i=0; i<numberOfWorkers; i++)
        {
            requestSocket= new Socket("127.0.0.1", 4000+i+1);
            ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.writeObject("showBookings");
            out.flush();
            out.writeObject(area);
            out.flush();
            out.writeObject(dateRange);
            out.flush();
            out.writeInt(noOfPersons);
            out.flush();
            out.writeFloat(stars);
            out.flush();
            if (requestSocket != null) {
                requestSocket.close();
            }
            out.close();
        }
    }

    public void book(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        String roomName = (String) in.readObject();
        DateRange dateRange = (DateRange) in.readObject();

        requestSocket= new Socket("127.0.0.1", 4000+hashCode(roomName));
        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
        out.writeObject("book");
        out.flush();
        out.writeObject(roomName);
        out.flush();
        out.writeObject(dateRange);
        out.flush();

        if (requestSocket != null) {
            requestSocket.close();
        }
        out.close();
    }



    public void review(ObjectInputStream in) throws  IOException, ClassNotFoundException
    {
        String roomName = (String) in.readObject();
        float stars = in.readFloat();


        requestSocket= new Socket("127.0.0.1", 4000+hashCode(roomName));
        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
        out.writeObject("review");
        out.flush();
        out.writeObject(roomName);
        out.flush();
        out.writeFloat(stars);
        out.flush();


        if (requestSocket != null) {
            requestSocket.close();
        }
        out.close();
    }

    public void addRoom(ObjectInputStream in) throws  IOException, ClassNotFoundException
    {
        String username = (String) in.readObject();
        String roomName = (String) in.readObject();
        int noOfPersons = in.readInt();
        String area = (String) in.readObject();
        float price = in.readFloat();
        String roomImage = (String) in.readObject();

        System.out.println(4000+hashCode(roomName));
        requestSocket = new Socket("127.0.0.1", 4000+hashCode(roomName));
        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
        ObjectInputStream in2 = new ObjectInputStream(requestSocket.getInputStream());
        out.writeObject("addRoom");
        out.flush();
        out.writeObject(username);
        out.flush();
        out.writeObject(roomName);
        out.flush();
        out.writeInt(noOfPersons);
        out.flush();
        out.writeObject(area);
        out.flush();
        out.writeObject(price);
        out.flush();
        out.writeObject(roomImage);
        out.flush();

        if (requestSocket != null) {
            requestSocket.close();
        }
        out.close();
    }

    public void addDate(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        String roomName= (String) in.readObject();
        DateRange daterange = (DateRange) in.readObject();

        requestSocket= new Socket("127.0.0.1", 4000+hashCode(roomName));
        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
        out.writeObject("addDate");
        out.flush();
        out.writeObject(roomName);
        out.flush();
        out.writeObject(daterange);
        out.flush();

        if (requestSocket != null) {
            requestSocket.close();
        }
        out.close();
    }

    public void showBookings(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        String username = (String) in.readObject();

        for (int i=0; i<numberOfWorkers; i++)
        {
            requestSocket= new Socket("127.0.0.1", 4000+i+1);
            ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.writeObject("showBookings");
            out.flush();
            out.writeObject(username);
            out.flush();
            if (requestSocket != null) {
                requestSocket.close();
            }
            out.close();
        }

    }
    public void showRooms(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        String username = (String) in.readObject();
        for (int i=0; i<numberOfWorkers; i++)
        {
            requestSocket= new Socket("127.0.0.1", 4000+i+1);
            ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.writeObject("showBookings");
            out.flush();
            out.writeObject(username);
            out.flush();
            if (requestSocket != null) {
                requestSocket.close();
            }
            out.close();
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
