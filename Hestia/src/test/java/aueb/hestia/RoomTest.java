package aueb.hestia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {


    Room room1;
    Room room2;
    @BeforeEach
    void setUp() {
        room1 = new Room("Priamos",2,"Athens",4.5f,4,"img/image");
        room2 = new Room("Xenos",2,"Athens",4.5f,4,"img/image");
    }

    @Test
    void setPrice() {
        room1.setPrice(10.0f);
        assertEquals(10.0f , room1.getPrice());
    }
    @Test
    void setPriceInvalidAmount() {
        room1.setPrice(10.0f);
        room1.setPrice(-10.0f);
        assertEquals(10.0f , room1.getPrice());
    }

    @Test
    void addAvailability() throws InvalidDateException {
        room1.addAvailability("2024-03-10","2024-03-20");
        assertEquals(new DateRange("2024-03-10","2024-03-20"), room1.getAvailability().get(0));

        room1.addAvailability("2024-03-25","2024-03-30");
        assertEquals(new DateRange("2024-03-25","2024-03-30"), room1.getAvailability().get(1));
        assertEquals(2, room1.getAvailability().size());
    }

    @Test
    void addAvailabilityOverlapping() throws InvalidDateException {
        room1.addAvailability("2024-03-10","2024-03-20");
        room1.addAvailability("2024-03-05","2024-03-27");
        assertEquals(new DateRange("2024-03-05","2024-03-27"), room1.getAvailability().get(0));
        assertEquals(1, room1.getAvailability().size());

    }

    @Test
    void addAvailabilityAdjacent() throws InvalidDateException {
        room1.addAvailability("2024-03-10","2024-03-20");
        room1.addAvailability("2024-03-05","2024-03-09");
        assertEquals(new DateRange("2024-03-05","2024-03-20"), room1.getAvailability().get(0));
        assertEquals(1, room1.getAvailability().size());
    }


    @Test
    void book() throws InvalidDateException {
        room1.addAvailability("2024-03-10","2024-03-20");
        room1.book("2024-03-10","2024-03-15");

        assertEquals(new DateRange("2024-03-16", "2024-03-20"), room1.getAvailability().get(0));
        assertEquals(1, room1.getAvailability().size());
    }

    @Test
    void bookInTheMiddle() throws InvalidDateException {
        room1.addAvailability("2024-03-10","2024-03-20");
        room1.book("2024-03-13","2024-03-15");

        assertEquals(new DateRange("2024-03-10", "2024-03-12"), room1.getAvailability().get(0));
        assertEquals(new DateRange("2024-03-16", "2024-03-20"), room1.getAvailability().get(1));
        assertEquals(2, room1.getAvailability().size());
    }

    @Test
    void bookInvalid() throws InvalidDateException {
        room1.addAvailability("2024-03-10","2024-03-20");
        room1.book("2024-03-08","2024-03-15");

        assertEquals(new DateRange("2024-03-10", "2024-03-20"), room1.getAvailability().get(0));
        assertEquals(1, room1.getAvailability().size());
    }

    @Test
    void bookSingle() throws InvalidDateException {
        room1.addAvailability("2024-03-10","2024-03-20");
        room1.book("2024-03-11","2024-03-11");
        room1.printAvailability();

        assertEquals(new DateRange("2024-03-10", "2024-03-10"), room1.getAvailability().get(0));
        assertEquals(new DateRange("2024-03-12", "2024-03-20"), room1.getAvailability().get(1));
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