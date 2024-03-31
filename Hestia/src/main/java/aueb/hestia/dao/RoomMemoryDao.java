package aueb.hestia.dao;

import aueb.hestia.DateRange;
import aueb.hestia.Room;

import java.util.ArrayList;

public class RoomMemoryDao implements RoomDao{

    ArrayList<Room> rooms;
    public RoomMemoryDao()
    {
        rooms = new ArrayList<Room>();
    }
    @Override
    public ArrayList<Room> findAll() {
        return rooms;
    }

    @Override
    public Room findByRoomName(String roomName) {
        for (Room room : rooms) {
            if (room.getRoomName().equals(roomName)) {
                return room;
            }
        }
        return null;
    }

    @Override
    public ArrayList<Room> findByOwner(String ownerUsername) {
        ArrayList<Room> found= new ArrayList<>();
        for (Room room : rooms) {
            if (room.getOwnerUsername().equals(ownerUsername)) {
                found.add(room);
            }
        }
        return found;
    }

    @Override
    public ArrayList<Room> findByFilters(String area, DateRange dateRange, int noOfPersons, float stars) {
        ArrayList<Room> found= new ArrayList<>();
        for (Room room : rooms) {
            if (room.getArea().equals(area) && room.isAvailable(dateRange) && room.getNoOfPersons() == noOfPersons && room.getStars()>=stars) {
                found.add(room);
            }
        }
        return found;
    }


    @Override
    public void add(Room room) {
        rooms.add(room);
    }

    @Override
    public void removeByRoomName() {

    }
}
