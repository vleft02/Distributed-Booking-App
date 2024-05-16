package aueb.hestia.Helper;

import aueb.hestia.Config.Config;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class CounterServer {
    public int workersCount=0;
    private final Config config = new Config();
    private final int counterPort;

    CounterServer()
    {
        this.counterPort = config.getCounterPort();

        runServer();
    }


    public static void main(String[] args)
    {
        CounterServer cs = new CounterServer();

    }

    void runServer()
    { 
        ServerSocket serverSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
           
            serverSocket = new ServerSocket(counterPort);
            while (true) {
                Socket socket = serverSocket.accept();
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                in.readInt();

                out.writeInt(workersCount);
                out.flush();
                workersCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                out.close();
                in.close();
                serverSocket.close();

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
