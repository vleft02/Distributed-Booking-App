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




    public Room(String roomName, int noOfPersons, String area, float stars, int noOfReviews, String roomImage)
    {
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
    public void addAvailability(String from, String to)
    {
        try{   
            LocalDate fromDate = LocalDate.parse(from);
            LocalDate toDate = LocalDate.parse(to);

            for (DateRange interval : availability)
            {
                if (interval.contains(new DateRange(fromDate,toDate)))
                {
                    return;
                }
                else
                {

                    if(interval.isAdjacent(new DateRange(fromDate, toDate)))
                    {
                        interval.mergeAdjacent(new DateRange(fromDate, toDate));
                        return;
                    }
                    else if(interval.overlaps(new DateRange(fromDate, toDate)))
                    {
                        DateRange mergedInterval = interval.mergeOverlapping(new DateRange(fromDate, toDate));
                        availability.remove(interval);
                        availability.add(mergedInterval);
                        return;
                    }
                }

            }
            availability.add(new DateRange(from, to));
        }
        catch(InvalidDateException e)
        {
            System.out.println("The date given is Invalid");
        }
    }



    public void book(String from, String to)
    {
        try
        {
            DateRange bookingDateRange = new DateRange(from, to);
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
        catch(InvalidDateException e)
        {
            System.out.println("The Date given is Invalid");
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

    public void review(int starScore)
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


}
