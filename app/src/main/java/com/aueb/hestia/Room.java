package com.aueb.hestia;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Room {
    private String roomName;
    private int noOfPersons;
    private String area;
    private float stars;
    private int noOfReviews;
    private String roomImage;
    private float price;
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

    public void setPrice(float price)
    {
        this.price = price;
    }

    public void addAvailability(String from, String to)
    {
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dayBeforeFromDate = fromDate.minusDays(1).format(formatter);
        String dayAfterToDate = toDate.plusDays(1).format(formatter);

        for (DateRange interval : availability)
        {
            if (dayBeforeFromDate.equals(interval.getTo()))
            {
                interval.setTo(toDate.format(formatter));
            }
            else if (dayAfterToDate.equals(interval.getFrom()))
            {
                interval.setFrom(fromDate.format(formatter));
            }
        }
        availability.add(new DateRange(from, to));
    }
    public void addAvailability(String singleDate)
    {

        availability.add(new DateRange(singleDate, singleDate));
    }



}
