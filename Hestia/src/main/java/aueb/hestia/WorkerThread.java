package aueb.hestia;

import java.net.Socket;
import java.util.ArrayList;

public class WorkerThread extends Thread{
    ArrayList<Room> rooms;

    WorkerThread(Socket socket, ArrayList<Room> rooms)
    {
        this.rooms = rooms;
    }

    

    @Override
    public void run() {
        // try {
        //     switch
        //     {
        //         case(add):addDate()                
        //     }


        // }
        // catch(){}
    }
}
