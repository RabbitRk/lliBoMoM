package com.rabbitt.momobill.model;

public class OrderDetails {

    String name,product;

    public OrderDetails(String name, String product) {
        this.name = name;
        this.product = product;
    }

    public String getName() {
        return name;
    }

    public String getProduct() {
        return product;
    }
}
