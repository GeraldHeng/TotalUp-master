package com.example.gerald.totalup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nex3z.flowlayout.FlowLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.zip.Inflater;

import static java.security.AccessController.getContext;

public class billActivity extends AppCompatActivity {

    //stuff
    ListView lvPerson;
    PersonCustomAdapter PCA;
    ArrayList<Person> personList;
    Toolbar toolbar;
    FloatingActionButton fabAddPpl;
    public static TextView tvTotal;
    public static TextView tvTotalPaid;
    public static TextView tvTotalLeft;
    EditText etBillName;
    ImageButton btnAddItem;
    Button btnSubmit;
    ImageButton btnRecords;
    DatabaseHelper db;
    //CheckBox c;

    //create bill
    Bill currentBill;

    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        setupToolbar();

        //set db
        db = new DatabaseHelper(getApplicationContext());
         //  db.createBill(new Bill("this is a new bill"));

        //stuff
        fabAddPpl = (FloatingActionButton)findViewById(R.id.fabAddPerson);
        tvTotal = (TextView)findViewById(R.id.tvTotal);
        tvTotalLeft = (TextView)findViewById(R.id.tvTotalLeft);
        tvTotalPaid = (TextView)findViewById(R.id.tvTotalPaid);
        btnAddItem = (ImageButton) findViewById(R.id.btnAdd);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnRecords = (ImageButton) findViewById(R.id.btnRecords);
        etBillName = (EditText)findViewById(R.id.etBillName);
        lvPerson = (ListView)findViewById(R.id.lvPerson);


        //if there's is intent data
        if(getIntent().getIntExtra("id", 0) != 0){
            Intent intent = getIntent();
            id = intent.getIntExtra("id", 0);
            Log.e("current id:", id + " ");

            currentBill = db.getBillDetails((int)id);
            etBillName.setText(currentBill.getBill_title());
            personList = db.getAllPersonsByBill((int)id);
            PCA = new PersonCustomAdapter(billActivity.this, R.layout.person_item_row, personList);
            lvPerson.setAdapter(PCA);
        }

        else{
            //set up custom adapter
            personList = new ArrayList<>();
            PCA = new PersonCustomAdapter(billActivity.this, R.layout.person_item_row, personList);
            lvPerson.setAdapter(PCA);
            //PCA.notifyDataSetChanged();

            //create new bill
            id = db.createBill(new Bill("new bill"));
            currentBill = db.getBillDetails((int)id);
            Log.e("new bill id is ", id + "");
        }

        // add person into lv
        fabAddPpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPerson();
                PCA.notifyDataSetChanged();
            }
        });

        //go to records
        btnRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "record presses", Toast.LENGTH_SHORT).show();
                Intent intentRecord;
                intentRecord = new Intent(getApplicationContext(), RecordActivity.class);
                intentRecord.putExtra("bill id", id);
                startActivity(intentRecord);
            }
        });

        //add item to selected person
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(personList.size() != 0){
                    addItem();
                }
                else{
                    Toast.makeText(getApplicationContext(), "You have not add anyone into the list!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //make a summary of the person and item bought
     /*   btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //submitSummary();
            }
        });*/

        TextWatcher watcherForBillName = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String billName = etBillName.getText().toString();
                currentBill.setBill_title(billName);

                DatabaseHelper db;
                db = new DatabaseHelper(getApplicationContext());
                db.updateBill(currentBill);
                db.closeDB();

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        etBillName.addTextChangedListener(watcherForBillName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_reset, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.save:
                Toast.makeText(getApplicationContext(), "save", Toast.LENGTH_SHORT).show();
                finish();
                return true;
          /*  case R.id.reset:
                Toast.makeText(getApplicationContext(), "reset", Toast.LENGTH_SHORT).show();*/
        }

        return super.onOptionsItemSelected(item);
    }

    void addPerson(){
        DatabaseHelper db;
        db = new DatabaseHelper(getApplicationContext());
        long newPersonId = db.createPerson(new Person((int)id));
        personList.add(new Person((int)id, (int)newPersonId));

        for(Person person : db.getAllPersons()){
            Log.e("new name", person.getPerson_name() + person.getPhone_number());
        }
        db.closeDB();

        //  personList = db.getAllPersons();
        //personList.add(new Person());
        //PCA.notifyDataSetChanged();
    }

    void clearAllPerson(){
        // personList.clear();
        //  PCA.notifyDataSetChanged();
        //updateTotalAmt();
    }

    String to2Decimal(double amt){
        return String.format("%.2f", amt);
    }

    void addItem(){
        LayoutInflater inflater = this.getLayoutInflater();
        final AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
        final View viewDialog = inflater.inflate(R.layout.item_dialog, null);

        final EditText etItemName = (EditText)viewDialog.findViewById(R.id.etItemName);
        final EditText etItemPrice = (EditText)viewDialog.findViewById(R.id.etItemPrice);
        final CheckBox cbAllPaying = (CheckBox)viewDialog.findViewById(R.id.cbAll);
        final FlowLayout checkboxes = (FlowLayout) viewDialog.findViewById(R.id.checkboxes);
        final EditText etItemQuantity = (EditText)viewDialog.findViewById(R.id.etQuantity);

        final ArrayList<Integer> checkList = new ArrayList<>();
        for(Person person : personList){
            final CheckBox c = new CheckBox(billActivity.this);
            //c.setId(x+1);
            c.setText(person.getPerson_name());
            c.setId(person.getPerson_id());
            checkboxes.addView(c);
            c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Log.e("checkbox", c.getId() + " " + c.getText());

                    if(c.isChecked()){
                        Log.e("checkbox", c.getId() + " " + c.getText() + " checked");
                        checkList.add(c.getId());
                    }

                    else{
                        Log.e("checkbox", c.getId() + " " + c.getText() + " unchecked");
                        for(int i = 0; i < checkList.size(); i++){
                            if(checkList.get(i) == c.getId()){
                                checkList.remove(i);
                            }
                        }
                    }

                    for(int i = 0; i < checkList.size(); i++){
                        Log.e("checkbox list", checkList.get(i) + " ");
                    }
                }
            });
        }

        myBuilder.setTitle("Add Item");
        myBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(!etItemName.getText().toString().isEmpty() && !etItemPrice.getText().toString().isEmpty() && !etItemQuantity.getText().toString().isEmpty()){
                    String itemName = etItemName.getText().toString();
                    double itemPrice = Double.parseDouble(etItemPrice.getText().toString());
                    int itemQuantity = Integer.parseInt(etItemQuantity.getText().toString());

                    long item_id = db.createItem(new Item(itemName, itemPrice, itemQuantity, currentBill.getBill_id()));
                    ArrayList<Item>itemList = db.getAllItems();
                    for(Item item : itemList){
                        Log.e("all items :", item.getItem_name() + " " + item.getItem_id() + " $" + item.getItem_price() + " " + item.getItem_quantity());
                    }

                    //db.createRecord(new Record());

                    for(int o = 0; o < checkList.size(); o++){
                        Log.e("checkbox list", checkList.get(o) + " ");
                        db.createRecord(new Record(currentBill.getBill_id(), (int)item_id, checkList.get(o)));
                    }

                    updateTotalAmt();
                    updateTotalPaid();
                    updateTotalLeft();
                    PCA.notifyDataSetChanged();
                    db.closeDB();

                }

                else{
                    Toast.makeText(getApplicationContext(), "empty", Toast.LENGTH_SHORT).show();
                }

               /* String itemName = etItemName.getText().toString();
                double amt = 0;

                if(!etItemPrice.getText().toString().isEmpty()){
                    amt += Double.parseDouble(etItemPrice.getText().toString());
                }

                int size = personList.size();
                if(!itemName.isEmpty() && amt != 0){

                    if(cbAllPaying.isChecked() == false){

                        if(!etPplPaying.getText().toString().isEmpty()){
                            int pplIndex = Integer.parseInt(etPplPaying.getText().toString());
                            if(pplIndex >= size){
                                Toast.makeText(getContext(), "No such person added. Item not added.", Toast.LENGTH_SHORT).show();
                            }

                            else{
                                personList.get(pplIndex).addItem(amt, itemName);
                                updateTotalAmt();
                            }
                        }

                        else{
                            Toast.makeText(getContext(),"You did not enter who gonna pay for the item.", Toast.LENGTH_SHORT).show();
                        }

                    }

                    else {
                        double amtToAdd = amt / size;
                        for (int x = 0; x < size; x++) {
                        personList.get(x).addItem(amtToAdd, itemName);
                        }
                        updateTotalAmt();
                         }
                }

                else{
                    Toast.makeText(getContext(), "Did not enter name or price." , Toast.LENGTH_SHORT).show();
                }
                PCA.notifyDataSetChanged();*/
            }
        });

        myBuilder.setNegativeButton("Cancel", null);
        AlertDialog myDialog = myBuilder.create();
        myDialog.setView(viewDialog);
        myDialog.show();
    }

    @Override
    protected void onResume() {
        PCA.notifyDataSetChanged();
        Log.e("billActivity", "Resumed");

        updateTotalAmt();
        updateTotalPaid();
        updateTotalLeft();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    /*    Log.e("billActivity", resultCode + " " + requestCode + " " + data.getIntExtra("person_id", 0));
        int billId = data.getIntExtra("bill_id", 0);
        int personId = data.getIntExtra("person_id", 0);
        Log.e("onActivityResult", "billActivity " + billId + " " + personId);
        PCA.onActivityResult(requestCode, resultCode, data);*/

        if (requestCode == 99) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();

                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column).trim();
                number.replace(" ", "");

                if(number.substring(0,1).equals("+")){
                    number = number.substring(1, number.length());
                }

                int numberInt = Integer.parseInt(number);
                Log.d("onActivityResult", "number is " + number);
                PCA.onActivityResult(requestCode, resultCode, data);
                // Do something with the phone number...


                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                int n = pref.getInt("person_id", 0);
                db.updatePersonNumberById(n, numberInt);
                PCA.notifyDataSetChanged();
                Log.d("onActivityResult", "person id is " + n);
            }
        }
    }

    void submitSummary(){
       /* if(!personList.isEmpty()){
            Intent intent = new Intent(getContext(), SubmitActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("personList", personList);
            intent.putExtra("bundle",bundle);
            startActivity(intent);
        }

        else{
            Toast.makeText(getContext(), "Nothing to summarize yet.", Toast.LENGTH_SHORT).show();
        } */
    }


    void setupToolbar(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

     void updateTotalAmt(){
        ArrayList<Item>itemList = db.getAllItemInBill((int)id);
        double totalAmtForBill = 0.0;
        for(Item item : itemList){
            totalAmtForBill += item.getItem_price() * item.getItem_quantity();
        }

        tvTotal.setText("$" + to2Decimal(totalAmtForBill));
        db.closeDB();

    }

    public void updateTotalPaid(){
        ArrayList<Person>personList = db.getAllPersonsByBill((int)id);
        double totalPaid = 0.0;
        for(Person person : personList){
            totalPaid += person.getAmt_paid();
        }
        tvTotalPaid.setText("$" + to2Decimal(totalPaid));
        db.closeDB();
    }

    public void updateTotalLeft(){
        ArrayList<Item>itemList = db.getAllItemInBill((int)id);
        double totalAmtForBill = 0.0;
        for(Item item : itemList){
            totalAmtForBill += item.getItem_price() * item.getItem_quantity();
        }

        ArrayList<Person>personList = db.getAllPersonsByBill((int)id);
        double totalPaid = 0.0;
        for(Person person : personList){
            totalPaid += person.getAmt_paid();
        }
        double totalLeft = totalAmtForBill - totalPaid;
        tvTotalLeft.setText("$" + to2Decimal(totalLeft));
    }



}
