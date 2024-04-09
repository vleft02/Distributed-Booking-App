package aueb.hestia.Domain;// package com.aueb.hestia;

import aueb.hestia.Helper.DateRange;
import aueb.hestia.Helper.RoomUnavailableException;

import java.io.Serializable;
import java.util.ArrayList;

public class Room implements Serializable {
    private final String roomName;
    private final int noOfPersons;
    private final String area;
    private float stars;
    private int noOfReviews;
    private String roomImage;
    private double price;
    private String ownerUsername;
    private ArrayList<DateRange> availability;




    public Room(String ownerUsername, String roomName, int noOfPersons, String area, float stars, int noOfReviews, double price,String roomImage)
    {
        this.ownerUsername = ownerUsername;
        this.roomName = roomName;
        this.noOfPersons = noOfPersons;
        this.area = area;
        this.stars = stars;
        this.noOfReviews = noOfReviews;
        this.roomImage = roomImage;
        this.price = 0.0;
        this.availability = new ArrayList<DateRange>();
    }

    //Setters and Getters
    public void setPrice(double price)
    {
        if (price>0.0)
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
    public double getPrice()
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



    public void book(DateRange bookingDateRange) throws RoomUnavailableException
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

    @Override
    public String toString()
    {

        String room = "Name: "+roomName+"\nNumber of persons: "+noOfPersons+"\nArea: "+area+"\nRating: "+stars+"\nReviews: "+noOfReviews+"\nPrice "+price+"$\nAvailable for: ";
        for (DateRange date : availability)
        {
            room = room+date.toString()+"\n";
        }
        return room;
    }
}