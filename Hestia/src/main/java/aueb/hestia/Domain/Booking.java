package aueb.hestia.Domain;

import aueb.hestia.Helper.DateRange;

import java.io.Serializable;

public class Booking implements Serializable {

    private final String username;
    private final DateRange dateRange;

    Booking (DateRange dateRange,String username )
    {
        this.username = username;
        this.dateRange = dateRange;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public String getUsername() {
        return username;
    }
}
