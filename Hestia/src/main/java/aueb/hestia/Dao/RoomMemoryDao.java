package aueb.hestia.Dao;

import aueb.hestia.Helper.DateRange;
import aueb.hestia.Domain.Room;

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
    public ArrayList<Room> findByFilters(String area, DateRange dateRange, int noOfPersons, float stars, int price) {
        ArrayList<Room> found= new ArrayList<>();
        for (Room room : rooms) {
            if ((room.getArea().equals(area) || area.equals("")) && room.isAvailable(dateRange) && (room.getNoOfPersons() == noOfPersons || noOfPersons == -1 ) && (room.getStars()>=stars || stars==0.0f) && (room.getPrice()<=price || price==0)) {
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
