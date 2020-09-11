package com.rabbitt.momobill.model;

public class GstModel {

    String tax, taxVal, sgst, cgst, tot;

    public GstModel(String tax, String taxVal, String sgst, String cgst, String tot) {
        this.tax = tax;
        this.taxVal = taxVal;
        this.sgst = sgst;
        this.cgst = cgst;
        this.tot = tot;
    }

    public String getTax() {
        return tax;
    }

    public String getTaxVal() {
        return taxVal;
    }

    public String getSgst() {
        return sgst;
    }

    public String getCgst() {
        return cgst;
    }

    public String getTot() {
        return tot;
    }
}

