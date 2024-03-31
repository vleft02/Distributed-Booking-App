package aueb.hestia.dao;

import aueb.hestia.DateRange;
import aueb.hestia.Room;

import java.util.ArrayList;

public interface RoomDao  {
    public ArrayList<Room> findAll();
    public Room findByRoomName(String roomName);
    public ArrayList<Room> findByOwner(String ownerUsername);
    public ArrayList<Room> findByFilters(String area, DateRange dateRange, int noOfPersons, float stars);


    public void add(Room room);

    public void removeByRoomName();

}
