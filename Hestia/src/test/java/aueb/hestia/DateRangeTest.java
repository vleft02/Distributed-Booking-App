package aueb.hestia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateRangeTest {

    DateRange range1;
    DateRange singleRange;
    DateRange overlappingRange1;
    DateRange overlappingRange2;
    DateRange adjacentRange1;
    DateRange adjacentRange2;

    @BeforeEach
    void setUp() throws InvalidDateException{
        range1 = new DateRange("2024-03-10","2024-03-20");
        singleRange = new DateRange("2024-03-11", "2024-03-11");
        overlappingRange1 = new DateRange("2024-03-09","2024-03-21");
        overlappingRange2 = new DateRange("2024-03-05","2024-03-15");

        adjacentRange1 =  new DateRange("2024-03-05","2024-03-09");
        adjacentRange2 =  new DateRange("2024-03-21","2024-03-27");
    }
    @Test
    void contains() throws InvalidDateException{
        DateRange range2 = new DateRange("2024-03-10","2024-03-15");
        assertTrue(range1.contains(range2));
    }
    @Test
    void notContains() throws InvalidDateException{
        DateRange range2 = new DateRange("2024-03-09","2024-03-15");
        assertFalse(range1.contains(range2));
    }


    @Test
    void overlapsBothSides() {
        assertTrue(range1.overlaps(overlappingRange1));
    }
    @Test
    void overlapsOneSide() {
        assertTrue(range1.overlaps(overlappingRange2));
    }
    @Test
    void noOverlap() {
        assertFalse(range1.overlaps(adjacentRange1));
    }

    @Test
    void isAdjacentLeft() {
        assertTrue(range1.isAdjacent(adjacentRange1));
    }

    @Test
    void isAdjacentRight() {
        assertTrue(range1.isAdjacent(adjacentRange2));
    }
    @Test
    void notAdjacent()
    {
        assertFalse(range1.isAdjacent(overlappingRange1));
    }

    @Test
    void notAdjacentContained()
    {
        assertFalse(range1.isAdjacent(singleRange));
    }

    @Test
    void mergeOverlapping() {
        DateRange merged = range1.mergeOverlapping(overlappingRange1);
        assertEquals(overlappingRange1,merged );
    }

    @Test
    void mergePartiallyOverlapping() throws InvalidDateException{
        DateRange merged = range1.mergeOverlapping(overlappingRange2);
        assertEquals(new DateRange("2024-03-05","2024-03-20"),merged );
    }

    @Test
    void mergeAdjacentLeft() throws InvalidDateException{
        range1.mergeAdjacent(adjacentRange1);
        assertEquals(new DateRange("2024-03-05","2024-03-20"),range1);
    }

    @Test
    void mergeAdjacentRight() throws InvalidDateException{
        range1.mergeAdjacent(adjacentRange2);
        assertEquals(new DateRange("2024-03-10","2024-03-27"),range1);
    }

}