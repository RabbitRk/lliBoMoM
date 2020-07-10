package com.rabbitt.momobill.model;

public class OrderDetails {

    String product, unit, amount;

    public OrderDetails(String product, String unit, String amount) {
        this.product = product;
        this.unit = unit;
        this.amount = amount;
    }

    public String getProduct() {
        return product;
    }

    public String getUnit() {
        return unit;
    }

    public String getAmount() {
        return amount;
    }
}
