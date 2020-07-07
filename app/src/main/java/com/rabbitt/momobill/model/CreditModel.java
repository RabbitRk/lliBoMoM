package com.rabbitt.momobill.model;


// Model used in excel report


public class CreditModel {

    String name, date, bill, amnt, paid, cr;


    public CreditModel(String billno, String cr, String amnt, String date, String name, String paid) {
        this.paid = paid;
        this.name = name;
        this.bill = billno;
        this.cr = cr;
        this.date = date;
        this.amnt = amnt;

    }


    public String getName() {
        return name;
    }

    public String getAmnt() {
        return amnt;
    }

    public String getBill() {
        return bill;
    }

    public String getCr() {
        return cr;
    }

    public String getDate() {
        return date;
    }

    public String getPaid() {
        return paid;
    }

}
