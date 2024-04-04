package aueb.hestia;

import aueb.hestia.Domain.Room;
import aueb.hestia.Helper.DateRange;
import aueb.hestia.Helper.InvalidDateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {


    Room room1;
    Room room2;
    @BeforeEach
    void setUp() {
        room1 = new Room("Kostas","Priamos",2,"Athens",4.5f,4,20.0f,"img/image");
        room2 = new Room("Giorgos","Xenos",2,"Athens",4.5f,4,10.0f,"img/image");
    }

    @Test
    void setPrice() {
        room1.setPrice(10.0);
        assertEquals(10.0 , room1.getPrice());
    }
    @Test
    void setPriceInvalidAmount() {
        room1.setPrice(10.0);
        room1.setPrice(-10.0);
        assertEquals(10.0 , room1.getPrice());
    }

    @Test
    void addAvailability() throws InvalidDateException {
        room1.addAvailability(new DateRange("10/03/2024","20/03/2024"));
        assertEquals(new DateRange("10/03/2024","20/03/2024"), room1.getAvailability().get(0));

        room1.addAvailability(new DateRange("25/03/2024","30/03/2024"));
        assertEquals(new DateRange("25/03/2024","30/03/2024"), room1.getAvailability().get(1));
        assertEquals(2, room1.getAvailability().size());
    }

    @Test
    void addAvailabilityOverlapping() throws InvalidDateException {
        room1.addAvailability(new DateRange("10/03/2024","20/03/2024"));
        room1.addAvailability(new DateRange("05/03/2024","27/03/2024"));
        assertEquals(new DateRange("05/03/2024","27/03/2024"), room1.getAvailability().get(0));
        assertEquals(1, room1.getAvailability().size());

    }

    @Test
    void addAvailabilityAdjacent() throws InvalidDateException {
        room1.addAvailability(new DateRange("10/03/2024","20/03/2024"));
        room1.addAvailability(new DateRange("05/03/2024","09/03/2024"));
        assertEquals(new DateRange("05/03/2024","20/03/2024"), room1.getAvailability().get(0));
        assertEquals(1, room1.getAvailability().size());
    }


    @Test
    void book() throws InvalidDateException {
        room1.addAvailability(new DateRange("10/03/2024","20/03/2024"));
        room1.book(new DateRange("10/03/2024","15/03/2024"));

        assertEquals(new DateRange("16/03/2024", "20/03/2024"), room1.getAvailability().get(0));
        assertEquals(1, room1.getAvailability().size());
    }

    @Test
    void bookInTheMiddle() throws InvalidDateException {
        room1.addAvailability(new DateRange("10/03/2024","20/03/2024"));
        room1.book(new DateRange("13/03/2024","15/03/2024"));

        assertEquals(new DateRange("10/03/2024", "12/03/2024"), room1.getAvailability().get(0));
        assertEquals(new DateRange("16/03/2024", "20/03/2024"), room1.getAvailability().get(1));
        assertEquals(2, room1.getAvailability().size());
    }

    @Test
    void bookInvalid() throws InvalidDateException {
        room1.addAvailability(new DateRange("10/03/2024","20/03/2024"));
        room1.book(new DateRange("08/03/2024","15/03/2024"));

        assertEquals(new DateRange("10/03/2024", "20/03/2024"), room1.getAvailability().get(0));
        assertEquals(1, room1.getAvailability().size());
    }

    @Test
    void bookSingle() throws InvalidDateException {
        room1.addAvailability(new DateRange("10/03/2024","20/03/2024"));
        room1.book(new DateRange("11/03/2024","11/03/2024"));
        room1.printAvailability();

        assertEquals(new DateRange("10/03/2024", "10/03/2024"), room1.getAvailability().get(0));
        assertEquals(new DateRange("12/03/2024", "20/03/2024"), room1.getAvailability().get(1));
        assertEquals(2, room1.getAvailability().size());
    }

    @Test
    void review() {
        room1.review(2);
        assertEquals(4.0f,room1.getStars());
        assertEquals(5,room1.getNoOfReviews());
    }

    @Test
    void InvalidReview() {
        room1.review(-1);
        assertEquals(4.5f,room1.getStars());
        assertEquals(4,room1.getNoOfReviews());
    }
}