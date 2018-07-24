package com.example.gerald.totalup;

/**
 * Created by GERALD on 11/3/2018.
 */

public class Item {
    private int item_id;
    private String item_name;
    private double item_price;
    private double item_quantity;
    private int foreign_bill_id;


    public Item(){

    }

    public Item(int item_id){
        this.item_id = item_id;
    }

    public Item(int item_id, String item_name){
        this.item_id = item_id;
        this.item_name = item_name;
        this.item_price = 0;
    }

    public Item(String item_name, double item_price, int item_quantity, int foreign_bill_id){
        this.item_name = item_name;
        this.item_price = item_price;
        this.item_quantity = item_quantity;
        this.foreign_bill_id = foreign_bill_id;
    }

    public Item(int item_id, String item_name, double price, int item_quantity){
        this.item_id = item_id;
        this.item_name = item_name;
        this.item_price = price;
        this.item_quantity = item_quantity;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public double getItem_price() {return item_price;}

    public void setItem_price(double price) {this.item_price = price;}

    public double getItem_quantity() {return item_quantity;}

    public void setItem_quantity(double item_quantity) {this.item_quantity = item_quantity;}

    public int getForeign_bill_id() {return foreign_bill_id;}

    public void setForeign_bill_id(int foreign_bill_id) {this.foreign_bill_id = foreign_bill_id;}
}
