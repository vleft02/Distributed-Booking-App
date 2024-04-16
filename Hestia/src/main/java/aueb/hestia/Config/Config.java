package aueb.hestia.Config;

import java.io.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.io.IOException;

public class Config {
    private JSONObject j;
    public Config() {
        try {
            String filePath = new File("").getAbsolutePath();
            Object o = new JSONParser().parse(new FileReader(filePath+"\\Hestia\\src\\main\\java\\aueb\\hestia\\Config\\"+"config.json"));
            j = (JSONObject) o;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
    public int getNumberOfWorkers() {
        Long num =  (Long) j.get("NumberOfWorkers");
        return num.intValue();
    }
    public int getReducerPort() {
        return (int) (long) j.get("ReducerPort");
    }

    public int getWorkersPort() {
        return (int) (long) j.get("WorkersPort");
    }
    public String getWorkersIP()
    {
        return (String) j.get("WorkersIP");
    }

    public String getReducerIP()
    {
        return (String) j.get("ReducerIP");
    }

    public int getClientRequestListenerPort() {
        return (int) (long) j.get("ClientRequestListenerPort");
    }

    public int getReducerRequestListener() {
        return (int) (long) j.get("ReducerRequestListener");
    }

    public String getMasterIp(){
        return (String) j.get("MasterIP");
    }
}
