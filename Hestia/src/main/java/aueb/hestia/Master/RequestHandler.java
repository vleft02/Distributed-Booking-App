package aueb.hestia.Master;

import aueb.hestia.Config.Config;
import aueb.hestia.Helper.DateRange;
import aueb.hestia.Helper.InvalidDateException;
import aueb.hestia.Helper.Pair;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import static java.lang.Math.abs;

import org.json.simple.parser.*;

public class RequestHandler extends Thread{

    private int workerOffsetPort;
    private String workerIp;
    private String function;
    private ObjectInputStream requestInputStream;
    private ObjectOutputStream requestOutputStream;
    private ObjectInputStream workerInputStream;
    private Socket requestSocket;
    JSONObject requestJson;
    private int requestId;
    private final Pair<Integer,String> mappedRequest  = new Pair<Integer,String>();
    private int numberOfWorkers;

    private HashMap<Integer, ObjectOutputStream> connectionsMap ;

    private boolean reducerFucntion = false;

    private Socket clientSocket;
    RequestHandler(Socket clientSocket, int numberOfWorkers, int requestId, HashMap<Integer, ObjectOutputStream> connectionsMap)
    {
        try {
            this.clientSocket = clientSocket;
            this.requestOutputStream = (ObjectOutputStream) new ObjectOutputStream(clientSocket.getOutputStream());
            this.requestInputStream = (ObjectInputStream) new ObjectInputStream(clientSocket.getInputStream());
            this.numberOfWorkers = numberOfWorkers;
            this.requestId = requestId;
            this.connectionsMap =connectionsMap;

        } catch (IOException e) {
            e.printStackTrace();
        }
        Config config = new Config();
        this.workerIp = config.getWorkersIP();
        this.workerOffsetPort = config.getWorkersPort();
    }

@Override
public void run() {
        try {

            String requestJsonString = requestInputStream.readUTF();
            this.requestJson = (JSONObject) new JSONParser().parse(requestJsonString);
            this.function = (String) requestJson.get("function");

            if ((function.equals("search")) || (function.equals("showBookings")) || (function.equals("showRooms"))){
                reducerFucntion = true;
                reduceFunction(requestJson);
            }else{
                nonReduceFunction(requestJson);
            }


            } catch (IOException | ClassNotFoundException | ParseException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(!reducerFucntion){
                    requestOutputStream.close();
                    requestInputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }



    public String sendToWorker(int port, Pair<Integer,String> request)
    {
        try {
            requestSocket = new Socket(workerIp, port);
            ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());

            out.writeObject(request);
            out.flush();

            String response = in.readUTF();
            out.close();
            in.close();
            if (requestSocket != null) {
                requestSocket.close();
            }

            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                requestSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }


    public void sendToAllWorkers(Pair<Integer,String> request)
    {
        ObjectOutputStream out =null;
        ObjectInputStream in = null;
        try {
            for (int i=0; i<numberOfWorkers; i++)
            {

                requestSocket= new Socket(workerIp, workerOffsetPort+i);
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());
                out.writeObject(request);
                out.flush();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                out.close();
                in.close();
                if (requestSocket != null) {
                    requestSocket.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private int hashCode(String roomName) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(roomName.getBytes(StandardCharsets.UTF_8));
            BigInteger bigInt = new BigInteger(1, encodedHash);
            return (int) abs(bigInt.intValue() % numberOfWorkers);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void reduceFunction(JSONObject json) throws IOException, ClassNotFoundException{
        connectionsMap.put(requestId, requestOutputStream);
        mappedRequest.put(requestId, json.toJSONString());

        sendToAllWorkers(mappedRequest);


    }
    public void nonReduceFunction(JSONObject json) throws IOException, ClassNotFoundException{
        String roomName = (String) json.get("roomName");
        mappedRequest.put(requestId, json.toJSONString());

         String message = sendToWorker(workerOffsetPort+hashCode(roomName) ,mappedRequest);


//        Socket clientSocket = connectionsMap.get(requestId);
//
//        ObjectOutputStream clientOutputStream = (ObjectOutputStream) new ObjectOutputStream(clientSocket.getOutputStream());

        synchronized (connectionsMap)
        {
            connectionsMap.remove(requestId);
        }
        requestOutputStream.writeObject(message);
        requestOutputStream.flush();
    }


}
