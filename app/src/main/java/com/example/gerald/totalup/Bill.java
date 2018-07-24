package com.example.gerald.totalup;

/**
 * Created by GERALD on 11/3/2018.
 */

public class Bill {
    private int bill_id;
    private String bill_title;

    public Bill(){

    }

    public Bill(int bill_id){
        this.bill_id = bill_id;
    }

    public Bill(String title){
        this.bill_title = title;
    }

    public Bill(int bill_id, String bill_title){
        this.bill_id = bill_id;
        this.bill_title = bill_title;
    }

    public int getBill_id() {
        return bill_id;
    }

    public void setBill_id(int bill_id) {
        this.bill_id = bill_id;
    }

    public String getBill_title() {
        return bill_title;
    }

    public void setBill_title(String bill_title) {
        this.bill_title = bill_title;
    }
}
