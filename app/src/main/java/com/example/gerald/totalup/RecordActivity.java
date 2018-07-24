package com.example.gerald.totalup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView lvRecords;
    RecordCustomAdapter RCA;
    ArrayList<Item> itemList;
    DatabaseHelper db;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        setupToolbar();

        lvRecords = (ListView)findViewById(R.id.lvRecords);

        //set db
        db = new DatabaseHelper(getApplicationContext());


        Intent intent = getIntent();
        id = intent.getLongExtra("bill id", 0);
        Log.e("In record Activity", id + " ");

        itemList = db.getAllItemInBill((int)id);
        RCA = new RecordCustomAdapter(RecordActivity.this, R.layout.record_item_row, itemList);
        lvRecords.setAdapter(RCA);
        /*Log.e("records in bill", itemList.size() + " ");

        for(Record record : recordList){
            Log.e("records in bill", record.getRecord_id() + " b id:" + record.getBill_id() + " p id:" + record.getPerson_id() + " i id:" + record.getItem_id());
        }*/
        db.closeDB();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_back, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    void setupToolbar(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
