package aueb.hestia.android.room;

import aueb.hestia.android.login.LoginView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import aueb.hestia.Config.Config;
import aueb.hestia.Domain.Booking;
import aueb.hestia.Domain.Room;
import aueb.hestia.Helper.DateRange;
import aueb.hestia.R;
import aueb.hestia.UserInterface.UserConsole;
import aueb.hestia.android.login.LoginView;

public class RoomDetailsPresenter {

    private RoomDetailsView view;

    public RoomDetailsView getView() {
        return view;
    }

    private String RoomName;
    public void setView(RoomDetailsView view) {
        this.view = view;
    }

    public void request(JSONObject requestJson) throws RuntimeException {

        Thread t1= new Thread(()->{
            Socket requestSocket = null;
            ObjectOutputStream out = null;
            ObjectInputStream in = null;

            try {
                requestSocket = new Socket("192.168.1.48", 7000);
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());

                out.writeUTF(requestJson.toJSONString());
                out.flush();
                System.out.println(requestJson);
                Object obj = in.readObject();

                String book = (String) obj;

                in.close();
                out.close();
                requestSocket.close();

                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("book",book);
                msg.setData(bundle);
                SearchResponseHandler.sendMessage(msg);

            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
    }

    public Handler SearchResponseHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override

        public boolean handleMessage(@NonNull Message message) {

            String result = message.getData().getString("book");
            if (result.contains("Successfully")) {
                view.showMessage("Successfully booked!");
            } else {
                view.showMessage("Booking Failed");
            }
            view.onBackPressed();

            return false;
        }
    });
    //book(){}
    public void book(String name,String roomName,String dates) {
        JSONObject booking = new JSONObject();
        booking.put("customerName",name);
        booking.put("roomName",roomName);
        booking.put("dateRange",dates);
        booking.put("function","book");

        request(booking);
    }
}
