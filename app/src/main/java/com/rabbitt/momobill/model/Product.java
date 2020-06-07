package com.rabbitt.momobill.model;

public class Product {

    String img_url;
    String product_name;
    String quantity;
    String sale_rate;
    String date_added;
    String cgst_sgst;
    String cess;
    String in_ex;
    String unit;
    String hsn;
    String per_case;
    String product_id;

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSale_rate() {
        return sale_rate;
    }

    public void setSale_rate(String sale_rate) {
        this.sale_rate = sale_rate;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getCgst_sgst() {
        return cgst_sgst;
    }

    public void setCgst_sgst(String cgst_sgst) {
        this.cgst_sgst = cgst_sgst;
    }

    public String getCess() {
        return cess;
    }

    public void setCess(String cess) {
        this.cess = cess;
    }

    public String getIn_ex() {
        return in_ex;
    }

    public void setIn_ex(String in_ex) {
        this.in_ex = in_ex;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getHsn() {
        return hsn;
    }

    public void setHsn(String hsn) {
        this.hsn = hsn;
    }

    public String getPer_case() {
        return per_case;
    }

    public void setPer_case(String per_case) {
        this.per_case = per_case;
    }
}
