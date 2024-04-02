package aueb.hestia;// package com.aueb.hestia;

public class Main{
    public static void main(String[] args) throws InvalidDateException {
        Room room1 = new Room("Kostas","Priamos",2,"Athens",4.5f,4,5.0f,"img/image");
        Room room2 = new Room("Giorgos","Xenos",2,"Athens",4.5f,4,10.0f,"img/image");


        room1.setPrice(10.0);
        room2.setPrice(20.0);


        room1.addAvailability(new DateRange("2024-10-10","2024-10-20"));
        room1.printAvailability();

        room1.addAvailability(new DateRange("2024-10-21", "2024-10-25"));
        room1.printAvailability();

        room1.addAvailability(new DateRange("2024-10-05", "2024-10-09"));
        room1.printAvailability();


        room1.book(new DateRange("2024-10-15", "2024-10-17"));
        room1.printAvailability();

        room1.book(new DateRange("2024-10-18", "2024-10-25"));
        room1.printAvailability();

        room1.book(new DateRange("2024-10-10", "2024-10-10"));
        room1.printAvailability();

        room1.book(new DateRange("2024-11-11", "2024-11-29"));

        room1.book(new DateRange("2024-11-11","2024-11-09"));


//        room1.addAvailability("2024-10-11");
//        room1.printAvailability();


        room1.addAvailability(new DateRange("2024-10-11", "2024-10-16"));
        room1.printAvailability();

        room1.addAvailability(new DateRange("2024-10-11", "2024-10-16"));
        room1.printAvailability();
    }

}