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
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class SubmitActivity extends AppCompatActivity {/*

    Toolbar toolbar;
    ListView lvSummary;
    SummaryCustomAdapter SCA;

    TextView tvTotal;
    TextView tvAvg;

    double total_amt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        setupToolbar();

        tvTotal = (TextView)findViewById(R.id.tvTotal);
        tvAvg = (TextView)findViewById(R.id.tvAvg);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        ArrayList<Person>personList = (ArrayList<Person>)bundle.getSerializable("personList");
        Log.e("SubmitActivity : ", "personList size: " + personList.size());

        lvSummary = (ListView)findViewById(R.id.lvPersonSummary);
        SCA = new SummaryCustomAdapter(this, R.layout.summary_item_row, personList);
        lvSummary.setAdapter(SCA);

        for(int i = 0; i < personList.size(); i++){
            total_amt += personList.get(i).getTotalAmt();
        }

        tvTotal.setText("Total: $" + to2Decimal(total_amt));
        tvAvg.setText("Average: $" + to2Decimal(total_amt/personList.size()));
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    String to2Decimal(double amt){
        return String.format("%.2f", amt);
    }*/
}
