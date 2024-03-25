package aueb.hestia;// package com.aueb.hestia;

public class Main{
    public static void main(String[] args)
    {
        Room room1 = new Room("Priamos",2,"Athens",4.5f,4,"img/image");
        Room room2 = new Room("Xenos",2,"Athens",4.5f,4,"img/image");
        Room room3 = new Room("Kalypso",2,"Athens",4.5f,4,"img/image");


        room1.setPrice(10.00f);
        room2.setPrice(20.00f);
        room3.setPrice(30.00f);

        room1.addAvailability("2024-10-10","2024-10-20");
        room1.printAvailability();

        room1.addAvailability("2024-10-21", "2024-10-25");
        room1.printAvailability();

        room1.addAvailability("2024-10-05", "2024-10-09");
        room1.printAvailability();


        room1.book("2024-10-15", "2024-10-17");
        room1.printAvailability();

        room1.book("2024-10-18", "2024-10-25");
        room1.printAvailability();

        room1.book("2024-10-10", "2024-10-10");
        room1.printAvailability();

        room1.book("2024-11-11", "2024-11-29");

        room1.book("2024-11-11","2024-11-09");


        room1.addAvailability("2024-10-11");
        room1.printAvailability();


        room1.addAvailability("2024-10-11", "2024-10-16");
        room1.printAvailability();

        room1.addAvailability("2024-10-11", "2024-10-16");
        room1.printAvailability();
    }

}