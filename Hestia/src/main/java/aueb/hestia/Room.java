package aueb.hestia;// package com.aueb.hestia;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Room {
    private final String roomName;
    private final int noOfPersons;
    private final String area;
    private float stars;
    private int noOfReviews;
    private String roomImage;
    private float price;
    private String ownerUsername;
    private ArrayList<DateRange> availability;




    public Room(String ownerUsername, String roomName, int noOfPersons, String area, float stars, int noOfReviews, float price,String roomImage)
    {
        this.ownerUsername = ownerUsername;
        this.roomName = roomName;
        this.noOfPersons = noOfPersons;
        this.area = area;
        this.stars = stars;
        this.noOfReviews = noOfReviews;
        this.roomImage = roomImage;
        this.price = 0.0f;
        this.availability = new ArrayList<DateRange>();
    }

    //Setters and Getters
    public void setPrice(float price)
    {
        if (price>0.0f)
        {
            this.price = price;
        }
    }

    public String getRoomName() {
        return roomName;
    }

    public int getNoOfPersons() {
        return noOfPersons;
    }

    public String getArea() {
        return area;
    }

    public float getStars() {
        return stars;
    }

    public int getNoOfReviews() {
        return noOfReviews;
    }

    public String getRoomImage() {
        return roomImage;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }
    public float getPrice()
    {
        return price;
    }

    public ArrayList<DateRange> getAvailability()
    {
        return availability;
    }


    //Methods
    public void addAvailability(DateRange dateRange)
    {

            for (DateRange interval : availability)
            {
                if (interval.contains(dateRange))
                {
                    return;
                }
                else
                {

                    if(interval.isAdjacent(dateRange))
                    {
                        interval.mergeAdjacent(dateRange);
                        return;
                    }
                    else if(interval.overlaps(dateRange))
                    {
                        DateRange mergedInterval = interval.mergeOverlapping(dateRange);
                        availability.remove(interval);
                        availability.add(mergedInterval);
                        return;
                    }
                }

            }
            availability.add(dateRange);
        }



    public void book(DateRange bookingDateRange)
    {
        try
        {
            for (DateRange interval : availability)
            {
                if (interval.contains(bookingDateRange))
                {
                    if (!interval.getFrom().equals(bookingDateRange.getFrom()))
                    {
                        availability.add(new DateRange(interval.getFrom(), bookingDateRange.getFrom().minusDays(1)));
                    }
                    if (!interval.getTo().equals(bookingDateRange.getTo()))
                    {
                        availability.add(new DateRange(bookingDateRange.getTo().plusDays(1), interval.getTo()));
                    }
                    availability.remove(interval);
                    return;
                }
            }
            throw new RoomUnavailableException();
        }
        catch(RoomUnavailableException e)
        {
            System.out.println("Room is unavailable for Dates specified");
        }
    }

    public void printAvailability()
    {
        for (DateRange interval: availability)
        {
            System.out.println("Current Availability: " + interval);
        }
        System.out.print("\n");
    }

    public void review(float starScore)
    {
        if (starScore>0 && starScore<=5)
        {
            noOfReviews++;
            stars = (stars * (noOfReviews - 1) + starScore) / noOfReviews;
        }
        else
        {
            System.out.println("stars should range from 1-5");
        }

    }

    public boolean isAvailable(DateRange dateRange) {
        for (DateRange interval : availability)
        {
            if (interval.contains(dateRange))
            {
                return true;
            }
        }
        return false;
    }
}