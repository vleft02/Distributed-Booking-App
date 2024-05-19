package aueb.hestia.Helper;// package com.aueb.hestia;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public class DateRange implements Serializable {
    private LocalDate from;
    private LocalDate to;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DateRange(String from, String to) throws InvalidDateException
    {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fromDate = LocalDate.parse(from,dateFormatter);
        LocalDate toDate = LocalDate.parse(to,dateFormatter);
        if (fromDate.isAfter(toDate))
        {
            throw new InvalidDateException();
        }
        this.from = fromDate;
        this.to = toDate;
    }

    public DateRange(LocalDate fromDate, LocalDate toDate)
    {
        this.from = fromDate;
        this.to = toDate;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }
    public void setFrom(LocalDate from)
    {
        this.from = from;
    }

    public LocalDate getFrom()
    {
        return from;
    }

    public LocalDate getTo()
    {
        return to;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean contains(LocalDate date) {
        return !date.isBefore(from) && !date.isAfter(to);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean contains(DateRange range) {
        return !range.getFrom().isBefore(from) && !range.getTo().isAfter(to);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DateRange mergeOverlapping(DateRange interval)
    {
        LocalDate mergedStartDate = this.from.isBefore(interval.getFrom()) ? this.from : interval.getFrom();
        LocalDate mergedEndDate = this.to.isAfter(interval.getTo()) ? this.to : interval.getTo();
        return new DateRange(mergedStartDate, mergedEndDate);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void mergeAdjacent(DateRange interval)
    {
        LocalDate dayBeforeFromDate = interval.getFrom().minusDays(1);
        LocalDate dayAfterToDate =  interval.getTo().plusDays(1);

            if (dayBeforeFromDate.equals(this.to))
            {
                this.to = interval.getTo();
            }
            else if (dayAfterToDate.equals(this.from))
            {
                this.from = interval.getFrom();
            }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean overlaps(DateRange dateRange)
    {
        return !(this.to.isBefore(dateRange.from) || dateRange.to.isBefore(this.from));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isAdjacent(DateRange dateRange)
    {
        LocalDate dayBeforeFromDate = dateRange.getFrom().minusDays(1);
        LocalDate dayAfterToDate = dateRange.getTo().plusDays(1);

        if (dayBeforeFromDate.equals(this.to))
        {
            return true;
        }
        else if (dayAfterToDate.equals(this.from))
        {
            return true;
        }

        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public String toString()
    {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fromDateStr = from.format(dateFormatter);
        String toDateStr =  to.format(dateFormatter);
        return String.format("%s - %s", fromDateStr, toDateStr);
    }

    @Override
    public boolean equals(Object obj)
    {

        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DateRange dateRange)) {
            return false;
        }
        else
        {
            return (dateRange.getFrom().equals(from)) && (dateRange.getTo().equals(to));
        }
    }
}

