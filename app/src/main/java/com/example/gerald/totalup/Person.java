package com.example.gerald.totalup;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

import static java.lang.System.in;

/**
 * Created by GERALD on 4/1/2018.
 */

public class Person implements Serializable {
    private int person_id;
    private String person_name;
    private int foreign_bill_id;
    private double amt_paid;
    private int phone_number;

    public Person(){
        this.person_name = "name";
    }

   /* public Person(int person_id){
        this.person_id = person_id;
    }*/

   public Person(int foreign_bill_id){
       this.foreign_bill_id = foreign_bill_id;
       this.person_name = "name";
       this.phone_number = 0;
       this.amt_paid = 0;
   }

    public Person(int foreign_bill_id, int person_id){
        this.foreign_bill_id = foreign_bill_id;
        this.person_id = person_id;
        this.person_name = "name";
    }

    public Person(int person_id, String person_name){
        this.person_id = person_id;
        this.person_name = person_name;
    }


    public Person(String person_name){
        this.person_name = person_name;
    }

    public Person(String person_name, int foreign_bill_id){
        this.person_name = person_name;
        this.foreign_bill_id = foreign_bill_id;
        this.amt_paid = 0;
    }

    public int getPerson_id() {
        return person_id;
    }

    public void setPerson_id(int person_id) {
        this.person_id = person_id;
    }

    public String getPerson_name() {
        return person_name;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    public int getForeign_bill_id() {return foreign_bill_id;}

    public void setForeign_bill_id(int foreign_bill_id) {this.foreign_bill_id = foreign_bill_id;}

    public double getAmt_paid() {return amt_paid;}

    public void setAmt_paid(double amt_paid) {this.amt_paid = amt_paid;}

    public int getPhone_number() {return phone_number;}

    public void setPhone_number(int phone_number) {this.phone_number = phone_number;}

    public void add_amt_paid(double amt_paid){this.amt_paid += amt_paid;}

    public void deduct_amt_paid(double amt_paid){this.amt_paid -= amt_paid;}
}
