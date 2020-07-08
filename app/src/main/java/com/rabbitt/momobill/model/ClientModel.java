package com.rabbitt.momobill.model;

public class ClientModel {

    String name, clientid;

    public ClientModel(String name, String client_id_) {

        this.name = name;
        this.clientid = client_id_;

    }

    public String getClient() {
        return name;
    }

    public String getClientid() {
        return clientid;
    }
}
