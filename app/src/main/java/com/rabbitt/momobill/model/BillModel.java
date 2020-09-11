package com.rabbitt.momobill.model;

public class BillModel {

    String product,mrp,rate,qty,amt;

    public BillModel(String product,String mrp,String qty,String amt)
    {
        this.product = product;
        this.mrp = mrp;
//        this.rate = rate;
        this.qty = qty;
        this.amt = amt;
    }

    public String getProduct() {
        return product;
    }

    public String getMrp() {
        return mrp;
    }

//    public String getRate() {
//        return rate;
//    }

    public String getQty() {
        return qty;
    }

    public String getAmt() {
        return amt;
    }
}

