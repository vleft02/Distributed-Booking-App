package aueb.hestia;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class WorkerThread extends Thread{
    ArrayList<Room> rooms;
    ObjectInputStream in;
    ObjectOutputStream out;



    WorkerThread(Socket socket, ArrayList<Room> rooms)
    {
        this.rooms = rooms;
        try {
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    

    @Override
    public void run() {
        try {
            byte[] a = in.readAllBytes();
            int b = in.readInt();

//            out.writeInt(a + b);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
