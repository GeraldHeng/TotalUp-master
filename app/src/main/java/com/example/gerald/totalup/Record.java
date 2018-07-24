package com.example.gerald.totalup;

/**
 * Created by GERALD on 12/3/2018.
 */

public class Record {
    private int record_id;
    private int bill_id;
    private int person_id;
    private int item_id;

    public Record(){

    }

    public Record(int bill_id, int item_id, int person_id){
        this.bill_id = bill_id;
        this.item_id = item_id;
        this.person_id = person_id;
    }

    public Record(int item_id, int person_id){
        this.item_id = item_id;
        this.person_id = person_id;
    }


    public int getRecord_id() {
        return record_id;
    }

    public void setRecord_id(int record_id) {
        this.record_id = record_id;
    }

    public int getBill_id() {
        return bill_id;
    }

    public void setBill_id(int bill_id) {
        this.bill_id = bill_id;
    }

    public int getPerson_id() {
        return person_id;
    }

    public void setPerson_id(int person_id) {
        this.person_id = person_id;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

}
