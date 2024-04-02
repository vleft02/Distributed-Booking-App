package aueb.hestia;

import aueb.hestia.DateRange;
import aueb.hestia.Room;
import aueb.hestia.dao.RoomDao;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class WorkerThread extends Thread{
    RoomDao rooms;
    ObjectInputStream in;
    ObjectOutputStream out;
    String function;
    Socket requestSocket;

    WorkerThread(Socket socket, RoomDao rooms)
    {
        this.rooms = rooms;
        try {
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.function = (String) in.readObject();
        System.out.println(function);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    

    @Override
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
//                    showBookings();
                    break;
                default:
                    System.out.print("Function Not Found");
            }
       } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
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

        synchronized (rooms)
        {
            ArrayList<Room> found  = rooms.findByFilters(area, dateRange, noOfPersons, stars);
        }

    }

    public void book(ObjectInputStream in) throws IOException, ClassNotFoundException {
        String roomName = (String) in.readObject();
        DateRange dateRange = (DateRange) in.readObject();

        synchronized (rooms)
        {
            Room roomToBook  = rooms.findByRoomName(roomName);
            if(roomToBook != null)
            {
                roomToBook.book(dateRange);
            }
        }
    }

    public void review(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        String roomName = (String) in.readObject();
        float stars = in.readFloat();

        synchronized (rooms)
        {
            Room roomToRate  = rooms.findByRoomName(roomName);
            if(roomToRate != null)
            {
                roomToRate.review(stars);
            }

        }
    }

    public void addRoom(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        String username = (String) in.readObject();
        String roomName = (String) in.readObject();
        int noOfPersons = in.readInt();
        String area = (String) in.readObject();
        double price = in.readDouble();
        String roomImage = (String) in.readObject();

        synchronized (rooms)
        {
            rooms.add(new Room(username,roomName, noOfPersons, area, 0,0,price,roomImage));
        }

        requestSocket= new Socket("127.0.0.1", 4009);
        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
        out.writeObject("Room Added Succesfully");
        out.flush();



    }

    public void addDate(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        String roomName= (String) in.readObject();
        DateRange daterange = (DateRange) in.readObject();

        synchronized (rooms)
        {
            Room roomToUpdate = rooms.findByRoomName(roomName);
            if (roomToUpdate != null)
            {
                roomToUpdate.addAvailability(daterange);
            }
        }
    }

    public void showBookings(String username)
    {

    }
    public void showRooms(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        String username = (String) in.readObject();

        synchronized (rooms)
        {
            ArrayList<Room> ownedRooms = rooms.findByOwner(username);
            if (ownedRooms != null)
            {

            }
        }
    }

}
