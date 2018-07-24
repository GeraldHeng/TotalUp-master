package com.example.gerald.totalup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by GERALD on 10/3/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "totalUp";

    // Table Names
    private static final String TABLE_PERSON = "person";
    private static final String TABLE_BILL = "bill";
    private static final String TABLE_ITEM = "item";
    private static final String TABLE_RECORD = "record";

    // Common column names
    private static final String KEY_ID = "id";

    // PERSON Table - column names
    private static final String KEY_PERSON_NAME = "person_name";
    private static final String KEY_FOREIGN_BILL_ID = "bill_id";
    private static final String KEY_AMOUNT_PAID = "amt_paid";
    private static final String KEY_PERSON_PHONE_NUMBER = "person_phone_number";

    // BILL Table - column names
    private static final String KEY_TITLE = "title";

    // ITEM Table - column names
    private static final String KEY_ITEM_NAME = "item_name";
    private static final String KEY_ITEM_PRICE = "item_price";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_FOREIGN_BILL_ID_ITEM = "bill_id";

    // RECORD Table - column names
    private static final String KEY_PERSON_ID = "person_id";
    private static final String KEY_BILL_ID = "bill_id";
    private static final String KEY_ITEM_ID = "item_id";

    // Person table create statement
    private static final String CREATE_TABLE_PERSON = "CREATE TABLE "
            + TABLE_PERSON + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_PERSON_NAME + " TEXT, "
            + KEY_FOREIGN_BILL_ID + " INTEGER,"
            + KEY_AMOUNT_PAID + " REAL, "
            + KEY_PERSON_PHONE_NUMBER  + " INTEGER,"
            + "FOREIGN KEY (" + KEY_FOREIGN_BILL_ID + ") REFERENCES " + TABLE_BILL + "(" + KEY_BILL_ID + "))";

    // Item table create statement
    private static final String CREATE_TABLE_ITEM = "CREATE TABLE "
            + TABLE_ITEM + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_ITEM_NAME + " TEXT, "
            + KEY_QUANTITY + " REAL, "
            + KEY_ITEM_PRICE + " REAL, "
            + KEY_FOREIGN_BILL_ID_ITEM + " INTEGER" + ")";

    // Bill table create statement
    private static final String CREATE_TABLE_BILL = "CREATE TABLE "
            + TABLE_BILL + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_TITLE + " TEXT " + ")";

    // Record table create statement
    private static final String CREATE_TABLE_RECORD = "CREATE TABLE "
            + TABLE_RECORD + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_PERSON_ID + " INTEGER,"
            + KEY_BILL_ID + " INTEGER, "
            + KEY_ITEM_ID + " INTEGER " + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BILL);
        db.execSQL(CREATE_TABLE_ITEM);
        db.execSQL(CREATE_TABLE_PERSON);
        db.execSQL(CREATE_TABLE_RECORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSON);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORD);

        // create new tables
        onCreate(db);
    }

    public void reset(){
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, 1,1);
    }

    //create bill
    public long createBill(Bill bill) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, bill.getBill_title());

        // insert row
        long bill_id = db.insert(TABLE_BILL, null, values);

        return bill_id;
    }

    //create person
    public long createPerson(Person person) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PERSON_NAME, person.getPerson_name());
        values.put(KEY_FOREIGN_BILL_ID, person.getForeign_bill_id());
        values.put(KEY_AMOUNT_PAID, person.getAmt_paid());
        values.put(KEY_PERSON_PHONE_NUMBER, person.getPhone_number());

        // insert row
        long person_id = db.insert(TABLE_PERSON, null, values);

        return person_id;
    }

    //create bill
    public long createItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ITEM_NAME, item.getItem_name());
        values.put(KEY_ITEM_PRICE, item.getItem_price());
        values.put(KEY_QUANTITY, item.getItem_quantity());
        values.put(KEY_FOREIGN_BILL_ID_ITEM, item.getForeign_bill_id());

        // insert row
        long item_id = db.insert(TABLE_ITEM, null, values);

        return item_id;
    }

    //binding a item, person and bill together, record
    public long createRecord(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BILL_ID, record.getBill_id());
        values.put(KEY_PERSON_ID, record.getPerson_id());
        values.put(KEY_ITEM_ID, record.getItem_id());

        long id = db.insert(TABLE_RECORD, null, values);
        return id;
    }

    //get all bills
    public ArrayList<Bill> getAllBills() {
        ArrayList<Bill> bills = new ArrayList<Bill>();
        String selectQuery = "SELECT  * FROM " + TABLE_BILL;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Bill b = new Bill();
                b.setBill_id(c.getInt((c.getColumnIndex(KEY_ID))));
                b.setBill_title(c.getString(c.getColumnIndex(KEY_TITLE)));

                // adding to tags list
                bills.add(b);
            } while (c.moveToNext());
        }
        return bills;
    }

    //get all persons
    public ArrayList<Person> getAllPersons() {
        ArrayList<Person> persons = new ArrayList<Person>();
        String selectQuery = "SELECT  * FROM " + TABLE_PERSON;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Person p = new Person();
                p.setPerson_id(c.getInt((c.getColumnIndex(KEY_ID))));
                p.setPerson_name(c.getString(c.getColumnIndex(KEY_PERSON_NAME)));
                p.setForeign_bill_id(c.getInt((c.getColumnIndex(KEY_FOREIGN_BILL_ID))));
                p.setPhone_number(c.getInt((c.getColumnIndex(KEY_PERSON_PHONE_NUMBER))));
                p.setAmt_paid(c.getDouble((c.getColumnIndex(KEY_AMOUNT_PAID))));

                // adding to tags list
                persons.add(p);
            } while (c.moveToNext());
        }
        return persons;
    }

    //get all items
    public ArrayList<Item> getAllItems() {
        ArrayList<Item> items = new ArrayList<Item>();
        String selectQuery = "SELECT  * FROM " + TABLE_ITEM;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Item i = new Item();
                i.setItem_id(c.getInt((c.getColumnIndex(KEY_ID))));
                i.setForeign_bill_id(c.getInt(c.getColumnIndex(KEY_FOREIGN_BILL_ID_ITEM)));
                i.setItem_name(c.getString(c.getColumnIndex(KEY_ITEM_NAME)));
                i.setItem_price(c.getDouble(c.getColumnIndex(KEY_ITEM_PRICE)));
                i.setItem_quantity(c.getDouble((c.getColumnIndex(KEY_QUANTITY))));

                // adding to tags list
                items.add(i);
            } while (c.moveToNext());
        }
        return items;
    }

    //get all records
    public ArrayList<Record> getAllRecords() {
        ArrayList<Record> records = new ArrayList<Record>();
        String selectQuery = "SELECT * FROM " + TABLE_RECORD;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Record r = new Record();
                r.setRecord_id(c.getInt((c.getColumnIndex(KEY_ID))));
                r.setBill_id(c.getInt((c.getColumnIndex(KEY_BILL_ID))));
                r.setPerson_id(c.getInt((c.getColumnIndex(KEY_PERSON_ID))));
                r.setItem_id(c.getInt((c.getColumnIndex(KEY_ITEM_ID))));

                // adding to tags list
                records.add(r);
            } while (c.moveToNext());
        }
        return records;
    }

    //get persons in a bill
    public ArrayList<Person> getAllPersonsByBill(int bill_id){
        ArrayList<Person> persons = new ArrayList<Person>();

        String selectQuery = "SELECT * FROM " + TABLE_PERSON
                + " WHERE " + KEY_FOREIGN_BILL_ID + " = " + bill_id;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Person person = new Person();
                person.setPerson_id(c.getInt((c.getColumnIndex(KEY_ID))));
                person.setPerson_name(c.getString(c.getColumnIndex(KEY_PERSON_NAME)));
                person.setForeign_bill_id(c.getInt((c.getColumnIndex(KEY_FOREIGN_BILL_ID))));
                person.setPhone_number(c.getInt((c.getColumnIndex(KEY_PERSON_PHONE_NUMBER))));
                person.setAmt_paid(c.getDouble((c.getColumnIndex(KEY_AMOUNT_PAID))));

                // adding to todo list
                persons.add(person);
            } while (c.moveToNext());
        }

        return persons;
    }

    //get records of a bill abd item
    public ArrayList<Record> getAllRecordsByBillAndItem(int bill_id, int item_id) {
        ArrayList<Record> records = new ArrayList<Record>();
        String selectQuery = "SELECT  * FROM " + TABLE_RECORD
                + " WHERE " + KEY_BILL_ID + " = " + bill_id
                + " AND " + KEY_ITEM_ID + " = " + item_id;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Record record = new Record();
                record.setBill_id(c.getInt((c.getColumnIndex(KEY_BILL_ID))));
                record.setRecord_id(c.getInt((c.getColumnIndex(KEY_ID))));
                record.setPerson_id(c.getInt((c.getColumnIndex(KEY_PERSON_ID))));
                record.setItem_id(c.getInt((c.getColumnIndex(KEY_ITEM_ID))));

                // adding to todo list
                records.add(record);
            } while (c.moveToNext());
        }

        return records;
    }

    //get records that have a person
    public ArrayList<Record> getAllRecordsByPerson(int person_id) {
        ArrayList<Record> records = new ArrayList<Record>();
        String selectQuery = "SELECT  * FROM " + TABLE_RECORD
                + " WHERE " + KEY_PERSON_ID + " = " + person_id;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Record record = new Record();
                record.setBill_id(c.getInt((c.getColumnIndex(KEY_BILL_ID))));
                record.setRecord_id(c.getInt((c.getColumnIndex(KEY_ID))));
                record.setPerson_id(c.getInt((c.getColumnIndex(KEY_PERSON_ID))));
                record.setItem_id(c.getInt((c.getColumnIndex(KEY_ITEM_ID))));

                // adding to todo list
                records.add(record);
            } while (c.moveToNext());
        }

        return records;
    }

    public ArrayList<Item> getAllItemInBill(int bill_id){
        ArrayList<Item> items = new ArrayList<Item>();
        String selectQuery = "SELECT * FROM " + TABLE_ITEM
                + " WHERE " + KEY_BILL_ID + " = " + bill_id;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Item i = new Item();
                i.setItem_id(c.getInt((c.getColumnIndex(KEY_ID))));
                i.setItem_name(c.getString(c.getColumnIndex(KEY_ITEM_NAME)));
                i.setItem_price(c.getDouble(c.getColumnIndex(KEY_ITEM_PRICE)));
                i.setItem_quantity(c.getDouble((c.getColumnIndex(KEY_QUANTITY))));
                i.setForeign_bill_id(c.getInt((c.getColumnIndex(KEY_FOREIGN_BILL_ID_ITEM))));

                // adding to tags list
                items.add(i);
            } while (c.moveToNext());
        }
        return  items;
    }

    //get bill details
    public Bill getBillDetails(int bill_id){
        Bill bill = new Bill();
        String selectQuery = "SELECT  * FROM " + TABLE_BILL
                + " WHERE id = " + bill_id;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                bill.setBill_title(c.getString(c.getColumnIndex(KEY_TITLE)));
                bill.setBill_id(c.getInt((c.getColumnIndex(KEY_ID))));

                // adding to todo list

            } while (c.moveToNext());
        }

        return bill;
    }

    //get person details
    public Person getPersonDetails(int person_id){
        Person person = new Person();
        String selectQuery = "SELECT  * FROM " + TABLE_PERSON
                + " WHERE id = " + person_id;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                person.setPerson_id(c.getInt((c.getColumnIndex(KEY_ID))));
                person.setPerson_name(c.getString(c.getColumnIndex(KEY_PERSON_NAME)));
                person.setForeign_bill_id(c.getInt((c.getColumnIndex(KEY_FOREIGN_BILL_ID))));
                person.setPhone_number(c.getInt((c.getColumnIndex(KEY_PERSON_PHONE_NUMBER))));
                person.setAmt_paid(c.getInt((c.getColumnIndex(KEY_AMOUNT_PAID))));

                // adding to todo list

            } while (c.moveToNext());
        }

        return person;
    }

    //get person details
    public Item getItemDetails(int item_id){
        Item item = new Item();
        String selectQuery = "SELECT * FROM " + TABLE_ITEM
                + " WHERE id = " + item_id;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                item.setItem_name(c.getString(c.getColumnIndex(KEY_ITEM_NAME)));
                item.setItem_quantity(c.getDouble((c.getColumnIndex(KEY_QUANTITY))));
                item.setItem_price(c.getDouble((c.getColumnIndex(KEY_ITEM_PRICE))));
                item.setItem_id(c.getInt((c.getColumnIndex(KEY_ID))));
                item.setForeign_bill_id(c.getInt((c.getColumnIndex(KEY_FOREIGN_BILL_ID_ITEM))));

                // adding to todo list

            } while (c.moveToNext());
        }

        return item;
    }

    //update bill
    public int updateBill(Bill bill) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, bill.getBill_title());

        // updating row
        return db.update(TABLE_BILL, values, KEY_ID + " = ?",
                new String[] { String.valueOf(bill.getBill_id())});
    }

    //update person
    public int updatePerson(Person person) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PERSON_NAME, person.getPerson_name());
        values.put(KEY_FOREIGN_BILL_ID, person.getForeign_bill_id());
        values.put(KEY_AMOUNT_PAID, person.getAmt_paid());
        values.put(KEY_PERSON_PHONE_NUMBER, person.getPhone_number());
        Log.e("updated person", person.getPerson_name() + " p id:" + person.getPerson_id() + " b id:" + person.getForeign_bill_id() + "num:" + person.getPhone_number());
        // updating row
        return db.update(TABLE_PERSON, values, KEY_ID + " = ? AND " + KEY_FOREIGN_BILL_ID + " = ?",
                new String[] { String.valueOf(person.getPerson_id()), String.valueOf(person.getForeign_bill_id())});
    }

    //delete person
    public void deletePerson(long person_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PERSON, KEY_ID + " = ?",
                new String[] { String.valueOf(person_id) });

        db.delete(TABLE_RECORD, KEY_PERSON_ID + " = ?",
                new String[] { String.valueOf(person_id) });
    }

    public void deleteItem(long item_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEM, KEY_ID + " = ?",
                new String[] { String.valueOf(item_id) });

        db.delete(TABLE_RECORD, KEY_ITEM_ID + " = ?",
                new String[] { String.valueOf(item_id) });
    }

    public void deleteBill(long bill_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_BILL, KEY_ID + " = ?",
                new String[] {String.valueOf(bill_id)});

        db.delete(TABLE_ITEM, KEY_FOREIGN_BILL_ID_ITEM + " = ?",
                new String[] { String.valueOf(bill_id) });

        db.delete(TABLE_RECORD, KEY_BILL_ID + " = ?",
                new String[] { String.valueOf(bill_id) });

        db.delete(TABLE_PERSON, KEY_FOREIGN_BILL_ID + " = ?",
                new String[] { String.valueOf(bill_id) });
    }


    //delete record
    public void deleteRecordByPersonAndItem(long person_id, long item_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECORD, KEY_PERSON_ID + " = ? AND " + KEY_ITEM_ID + " =?",
                new String[] { String.valueOf(person_id), String.valueOf(item_id) });
    }

    //update person number by id
    public void updatePersonNumberById(int person_id, int phone_num){
        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL = "UPDATE " +  TABLE_PERSON + " SET " + KEY_PERSON_PHONE_NUMBER + " = " + phone_num + " WHERE " + KEY_ID + " = " + person_id;
        Log.d("updatePersonNumberById", strSQL);
        db.execSQL(strSQL);
    }

    //close db
    public void closeDB(){
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null && db.isOpen())
            db.close();
    }


}
