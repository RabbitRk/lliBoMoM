package com.rabbitt.momobill.model;

public class OrderName {

    String name, id;

    public OrderName(String id, String name) {

        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
