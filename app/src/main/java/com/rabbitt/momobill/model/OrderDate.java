package com.rabbitt.momobill.model;

public class OrderDate {

    String date,fdate;

    public OrderDate(String fdate, String date) {
        this.date = date;
        this.fdate = fdate;
    }

    public String getDate() {
        return date;
    }

    public String getFdate()
    {
        return fdate;
    }


}
