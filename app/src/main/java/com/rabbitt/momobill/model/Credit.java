package com.rabbitt.momobill.model;

public class Credit {
    String invoice_id, date_on, balance, client_id;

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getInvoice_id() {
        return invoice_id;
    }

    public void setInvoice_id(String invoice_id) {
        this.invoice_id = invoice_id;
    }

    public String getDate_on() {
        return date_on;
    }

    public void setDate_on(String date_on) {
        this.date_on = date_on;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
