package aueb.hestia.android.search;

import android.widget.EditText;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import aueb.hestia.Config.Config;
import aueb.hestia.Domain.Room;
import aueb.hestia.R;
import aueb.hestia.UserInterface.UserConsole;
import aueb.hestia.android.login.LoginView;

public class SearchPresenter {
    private SearchRoomsView view;

    public SearchRoomsView getView() {
        return view;
    }



    public void setView(SearchRoomsView view) {
        this.view = view;
    }


    public Object request(JSONObject requestJson) throws RuntimeException {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        Config cfg = new Config();
        String masterIp = cfg.getMasterIp();
        int masterPort = cfg.getClientRequestListenerPort();


        try {
            requestSocket = new Socket(masterIp, masterPort);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());



            out.writeUTF(requestJson.toJSONString());
            out.flush();

            Object obj =  in.readObject();
            return obj;

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();	out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public ArrayList<Room> search() {

        String area = view.getArea();
        int noOfPersons = view.getNoOfPersons();
        float stars = view.getStars();
        String dates = view.getDates();

        JSONObject search = new JSONObject();
        search.put("area",area);
        search.put("dateRange",dates);
        search.put("noOfPersons",noOfPersons);
        search.put("stars",stars);
        search.put("function","search");


        ArrayList<Room> response = (ArrayList<Room>) request(search);
        return response;
        //edo skeftomai na kaloume mia methodo poy tha tiponei ta apotelesmata
        //apo ti lista room sto room_list_item.xml
//                for (Room room : response)
//                            {
//                                System.out.println(room);
//                            }
    }
}
