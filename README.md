# Hestia (Distributed Room Booking System)
<p align="center">
  <img src="https://github.com/user-attachments/assets/cec9054d-e21c-4ae6-a63e-c3910fd9f9ae" alt="app_logo" width="300">
</p>
A distributed system for managing and booking room rentals, similar to platforms like Airbnb and Booking. This project was developed as part of a university course on Distributed Systems, following a client-server architecture with a Java-based backend and an Android-based frontend.

## Features

### Manager (Console Application)
- Add properties with available rental dates
- View bookings for owned properties
- Communicate with the backend via TCP sockets

### Renter (Android App)
- Search for properties using filters (location, dates, number of guests, price, rating)
- Book rooms and leave ratings (1-5 stars)
- Real-time interaction with the server using asynchronous requests

### Backend
- Implements a distributed backend using Java with MapReduce for data processing
- Master-Worker architecture: 
  - Master node manages requests and assigns them to worker nodes
  - Worker nodes handle data storage and request processing
- TCP communication between the Master and Worker nodes
- Multithreading for concurrent request handling
- In-memory data storage, no database used

## Key Technologies
- **Java** for backend implementation (Master, Worker nodes)
- **Android** for the frontend mobile application
- **TCP Sockets** for communication between the client and server
- **MapReduce** for parallel processing and filtering of room data

## Setup Instructions
1. Clone the repository
2. Set up and run the backend (Master and Workers) on separate machines using the provided Java files.
3. Install the Android app on a mobile device.
4. Start using the system by adding properties as a manager or searching/bookings as a renter.
