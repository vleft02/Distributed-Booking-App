package aueb.hestia.Domain;

import aueb.hestia.Helper.DateRange;

import java.io.Serializable;

public class Booking implements Serializable {

    private String username;
    private DateRange dateRange;

    Booking (DateRange dateRange,String username )
    {
        this.username = username;
        this.dateRange = dateRange;
    }

}
