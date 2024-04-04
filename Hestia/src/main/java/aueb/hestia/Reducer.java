package aueb.hestia;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Reducer {
    ServerSocket providerSocket;
    Socket connection = null;


    public static void main(String[] args) {
        new Reducer().openServer();
    }
    void openServer() {
        try {
            providerSocket = new ServerSocket(4009);

            while (true) {
                connection = providerSocket.accept();
                System.out.println("Running");
                Thread t = new ReduceThreads(connection);
                t.start();

            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}