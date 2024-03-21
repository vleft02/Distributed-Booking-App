package com.aueb.hestia;
import java.util.Date;

public class DateRange {
    private String from;
    private String to;

    public DateRange(String from, String to)
    {
        this.from = from;
        this.to = to;
    }

    public void setTo(String to) {
        this.to = to;
    }
    public void setFrom(String from)
    {
        this.from = from;
    }

    public String getFrom()
    {
        return from;
    }

    public String getTo()
    {
        return to;
    }
}

