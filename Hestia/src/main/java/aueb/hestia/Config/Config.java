package aueb.hestia.Config;

import java.io.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.io.IOException;

public class Config {
    private JSONObject j;
    public Config() {
        try {
            Object o = new JSONParser().parse(new FileReader("C:/Git/Distributed-Booking-App/Hestia/src/main/java/aueb/hestia/Config/config.json"));
            j = (JSONObject) o;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public int getMasterPort() {
        return (int) (long) j.get("MasterPort");
    }

    public int getWorkersPort() {
        return (int) (long) j.get("WorkersPort");
    }

    public int getClientRequestListenerPort() {
        return (int) (long) j.get("ClientRequestListenerPort");
    }

    public int getReducerRequestListener() {
        return (int) (long) j.get("ReducerRequestListener");
    }
}
