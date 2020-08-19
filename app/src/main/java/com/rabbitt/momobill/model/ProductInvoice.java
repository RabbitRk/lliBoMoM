package com.rabbitt.momobill.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductInvoice implements Parcelable {

    String img_url;
    String product_name;
    String sale_rate;
    String unit;
    String product_id;
    public String cgst;
    String cess;
    String in;
    String single;
    String hsn;

    public ProductInvoice(Parcel inp) {
        img_url = inp.readString();
        product_name = inp.readString();
        sale_rate = inp.readString();
        unit = inp.readString();
        product_id = inp.readString();
        cgst = inp.readString();
        cess = inp.readString();
        in = inp.readString();
        single = inp.readString();
        hsn = inp.readString();
    }

    public static final Creator<ProductInvoice> CREATOR = new Creator<ProductInvoice>() {
        @Override
        public ProductInvoice createFromParcel(Parcel in) {
            return new ProductInvoice(in);
        }

        @Override
        public ProductInvoice[] newArray(int size) {
            return new ProductInvoice[size];
        }
    };

    public ProductInvoice() {

    }

    public String getHsn() {
        return hsn;
    }

    public void setHsn(String hsn) {
        this.hsn = hsn;
    }

    public String getSingle() {
        return single;
    }

    public void setSingle(String single) {
        this.single = single;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getCgst() {
        return cgst;
    }

    public void setCgst(String cgst) {
        this.cgst = cgst;
    }

    public String getCess() {
        return cess;
    }

    public void setCess(String cess) {
        this.cess = cess;
    }

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

    public String getSale_rate() {
        return sale_rate;
    }

    public void setSale_rate(String sale_rate) {
        this.sale_rate = sale_rate;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(img_url);
        dest.writeString(product_name);
        dest.writeString(sale_rate);
        dest.writeString(unit);
        dest.writeString(product_id);
        dest.writeString(cgst);
        dest.writeString(cess);
        dest.writeString(in);
        dest.writeString(single);
        dest.writeString(hsn);
    }
}
