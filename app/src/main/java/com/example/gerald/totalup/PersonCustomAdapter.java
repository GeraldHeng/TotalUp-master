package com.example.gerald.totalup;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.nex3z.flowlayout.FlowLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by GERALD on 4/1/2018.
 */


public class PersonCustomAdapter extends ArrayAdapter {

    Context parent_context;
    int layout_id;
    ArrayList<Person> personList;

    public PersonCustomAdapter(Context context, int resource, ArrayList<Person> objects) {
        super(context, resource, objects);

        parent_context = context;
        layout_id = resource;
        personList = objects;
    }

    int phone_num = 0;


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(layout_id, parent, false);

        final EditText etName = (EditText) rowView.findViewById(R.id.etPersonName);
        ImageButton imgBDelete = (ImageButton) rowView.findViewById(R.id.imgBtnDelete);
        final ImageButton imgBtnCall = (ImageButton) rowView.findViewById(R.id.imgBtnCall);
        final ImageButton imgBtnMessage = (ImageButton) rowView.findViewById(R.id.imgBtnMessage);
        TextView tvTotal = (TextView) rowView.findViewById(R.id.tvPersonTotal);
        final EditText etPersonPhoneNumber = (EditText) rowView.findViewById(R.id.etPhoneNumber);
        Button btnConnectToContact = (Button) rowView.findViewById(R.id.btnConnectToContacts);
        Button btnPay = (Button) rowView.findViewById(R.id.btnPay);
        Button btnDeduct = rowView.findViewById(R.id.btnDeduct);
        final Person currentPerson = personList.get(position);

        final int RQS_PICKCONTACT = 99;

       /* if(phone_num != 0){
            etPersonPhoneNumber.setText(phone_num);
        }*/

        if (currentPerson.getAmt_paid() > 0) {
            btnDeduct.setEnabled(true);
        } else {
            btnDeduct.setEnabled(false);
        }

        btnDeduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deductPayment(currentPerson);
            }
        });

        addPerson(currentPerson, etName, etPersonPhoneNumber);
        checkEditTextIsEmpty(etPersonPhoneNumber, imgBtnMessage, imgBtnCall);
        imgBDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePerson(position);
            }
        });

        imgBtnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "call is pressed", Toast.LENGTH_SHORT).show();
                String number = etPersonPhoneNumber.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                }
                getContext().startActivity(callIntent);
            }
        });

        imgBtnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "message is pressed", Toast.LENGTH_SHORT).show();
                String number = etPersonPhoneNumber.getText().toString();
                SmsManager sms = SmsManager.getDefault();

                DatabaseHelper db;
                db = new DatabaseHelper(getContext());
                Bill bill = db.getBillDetails(currentPerson.getForeign_bill_id());
                String billName = bill.getBill_title();

                String personName = currentPerson.getPerson_name();
                String personAmtOwned = getTotalAmt(currentPerson);

                String template = "Hi " + personName
                        + ", you have yet to pay me " + personAmtOwned
                        + " for the meal at " + billName
                        + ".";

                sms.sendTextMessage(number, null, template, null, null);
            }
        });

       /* etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
               changeName(currentPerson, b, etName);
            }
        });*/

        btnConnectToContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* final Uri uriContact     = ContactsContract.Contacts.CONTENT_URI;
                Intent intentPickContact = new Intent(Intent.ACTION_PICK, uriContact);
                Activity origin = (Activity)parent_context;
                Log.e("MyAdapter", "Btn b id:" + currentPerson.getForeign_bill_id() + " p id:" + currentPerson.getPerson_id());
                intentPickContact.putExtra("bill_id", currentPerson.getForeign_bill_id());
                intentPickContact.putExtra("person_id", currentPerson.getPerson_id());
                origin.startActivityForResult(intentPickContact, RQS_PICKCONTACT);*/


                SharedPreferences pref = getContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("person_id", currentPerson.getPerson_id());
                editor.commit();

                Activity origin = (Activity) parent_context;
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                Log.d("onActivityResult", "putting person id " + currentPerson.getPerson_id());
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                origin.startActivityForResult(pickContactIntent, RQS_PICKCONTACT);

            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePayment(currentPerson);
            }
        });

        TextWatcher watcherForPhoneNumber = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String phoneNum = etPersonPhoneNumber.getText().toString();
                checkEditTextIsEmpty(etPersonPhoneNumber, imgBtnMessage, imgBtnCall);
                if (phoneNum.isEmpty()) {
                    currentPerson.setPhone_number(0);
                } else {
                    currentPerson.setPhone_number(Integer.parseInt(phoneNum));
                }
              //  Toast.makeText(getContext(), currentPerson.getPhone_number() + " id:" + currentPerson.getPerson_id(), Toast.LENGTH_SHORT).show();
                DatabaseHelper db;
                db = new DatabaseHelper(getContext());
                db.updatePerson(currentPerson);
                db.closeDB();

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        TextWatcher watcherForName = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String name = etName.getText().toString();
                currentPerson.setPerson_name(name);

                DatabaseHelper db;
                db = new DatabaseHelper(getContext());
                db.updatePerson(currentPerson);
                db.closeDB();


            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        etPersonPhoneNumber.addTextChangedListener(watcherForPhoneNumber);
        etName.addTextChangedListener(watcherForName);
        String totalAmt = "$" + getTotalAmt(currentPerson);
        tvTotal.setText(totalAmt);
        if(currentPerson.getPhone_number() != 0){
            etPersonPhoneNumber.setText(currentPerson.getPhone_number()+"");
        }else{
            etPersonPhoneNumber.setText("");
        }
     //   notifyDataSetChanged();
        return rowView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", "custom adapter result run");
        if (resultCode == RESULT_OK) {

            SharedPreferences pref = getContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            int n = pref.getInt("bill_id", 0);
            Log.d("onActivityResult", "bill id is " + n);

            // Get the URI that points to the selected contact
            Uri contactUri = data.getData();
            // We only need the NUMBER column, because there will be only one row in the result
            String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

            // Perform the   query on the contact to get the NUMBER column
            // We don't need a selection or sort order (there's only one result for the given URI)
            // CAUTION: The query() method should be called from a separate thread to avoid blocking
            // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
            // Consider using CursorLoader to perform the query.
            Cursor cursor = getContext().getContentResolver()
                    .query(contactUri, projection, null, null, null);
            cursor.moveToFirst();

            // Retrieve the phone number from the NUMBER column
            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String number = cursor.getString(column);
            Log.d("onActivityResult", "number is " + number);
            // Do something with the phone number...
        }
    }

    void removePerson(int position) {
        DatabaseHelper db;
        db = new DatabaseHelper(getContext());
        Person p = personList.get(position);

        ArrayList<Record> personHasRecords = db.getAllRecordsByPerson(p.getPerson_id());
        if (personHasRecords.size() > 0) {
            Toast.makeText(getContext(), "Make sure to uncheck all the item that this person is paying first!", Toast.LENGTH_SHORT).show();
        } else {
            db.deletePerson((long) p.getPerson_id());
            personList.remove(position);
            notifyDataSetChanged();

            double totalPaid = 0.0;
            for (Person person : personList) {
                totalPaid += person.getAmt_paid();
            }
            Log.e("makePayment", "total paid: " + totalPaid);
            new billActivity().tvTotalPaid.setText("$" + to2Decimal(totalPaid));

            ArrayList<Item> itemList = db.getAllItemInBill((int) p.getForeign_bill_id());
            double totalAmtForBill = 0.0;
            for (Item item : itemList) {
                totalAmtForBill += item.getItem_price() * item.getItem_quantity();
            }

            double totalLeft = totalAmtForBill - totalPaid;
            new billActivity().tvTotalLeft.setText("$" + to2Decimal(totalLeft));

            List<Person> persons = db.getAllPersons();
            for (Person person1 : persons) {
                Log.e("person name : ", person1.getPerson_name() + " " + person1.getForeign_bill_id() + " " + person1.getPerson_id());
            }
        }


        db.closeDB();

    }

    void addPerson(Person person, EditText etName, EditText etNum) {
        //  DatabaseHelper db;
        // db = new DatabaseHelper(getContext());
        // db.createPerson(person);
        etName.setText(person.getPerson_name());
        if (person.getPhone_number() == 0) {
            etNum.setText("");
        } else {
            etNum.setText(person.getPhone_number() + "");
        }
    }

    String getTotalAmt(Person person) {
        DatabaseHelper db;
        db = new DatabaseHelper(getContext());
        double totalAmt = 0.0;
        ArrayList<Record> records = db.getAllRecordsByPerson(person.getPerson_id());
        Log.e("have size", records.size() + " ");
        for (Record record : records) {
            Item item = db.getItemDetails(record.getItem_id());
            Log.e(person.getPerson_name() + " have ", record.getRecord_id() + " id" + item.getItem_name() + " $" + item.getItem_price());
            ArrayList<Record> recordByItem = db.getAllRecordsByBillAndItem(person.getForeign_bill_id(), item.getItem_id());
            totalAmt += (item.getItem_price() * item.getItem_quantity()) / recordByItem.size();
        }
        totalAmt -= person.getAmt_paid();
        db.closeDB();
        return to2Decimal(totalAmt);

    }

    String to2Decimal(double amt) {
        return String.format("%.2f", amt);
    }

    boolean checkEditTextIsEmpty(EditText et, ImageButton imgBtnMessage, ImageButton imgBtnCall) {
        String text = et.getText().toString();
        if (text.matches("")) {
            imgBtnCall.setEnabled(false);
            imgBtnMessage.setEnabled(false);
            imgBtnCall.setColorFilter(ContextCompat.getColor(getContext(), R.color.disabled));
            imgBtnMessage.setColorFilter(ContextCompat.getColor(getContext(), R.color.disabled));
            return true;
        }

        imgBtnCall.setEnabled(true);
        imgBtnMessage.setEnabled(true);
        //  imgBtnCall.setBackgroundColor(Color.GREEN);
        imgBtnCall.setColorFilter(ContextCompat.getColor(getContext(), R.color.green));
        imgBtnMessage.setColorFilter(ContextCompat.getColor(getContext(), R.color.green));
        // imgBtnMessage.setBackgroundColor(Color.GREEN);
        return false;
    }

    void makePayment(final Person person) {
        final double totalAmt = Double.parseDouble(getTotalAmt(person));
        if (totalAmt <= 0) {
            Toast.makeText(getContext(), "You do not need to pay anymore!", Toast.LENGTH_SHORT).show();
        } else {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final AlertDialog.Builder myBuilder = new AlertDialog.Builder(getContext());
            final View viewDialog = inflater.inflate(R.layout.person_payment_dialog, null);

            final EditText etPayment = (EditText) viewDialog.findViewById(R.id.etPayment);

            myBuilder.setTitle("Make Payment");
            myBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.e("PersonCustomAdapter", "makePayment " + etPayment.getText());
                    double amtPaid = Double.parseDouble(etPayment.getText().toString());

                    if (amtPaid > totalAmt) {
                        Toast.makeText(getContext(), "You cannot pay more than your total amount", Toast.LENGTH_SHORT).show();
                    } else {
                        person.add_amt_paid(amtPaid);
                        DatabaseHelper db;
                        db = new DatabaseHelper(getContext());
                        db.updatePerson(person);
                        Log.e("personCustomAdapter", "person paid:" + person.getAmt_paid());
                        ArrayList<Person> personList = db.getAllPersonsByBill(person.getForeign_bill_id());
                        double totalPaid = 0.0;
                        for (Person person : personList) {
                            totalPaid += person.getAmt_paid();
                        }
                        Log.e("makePayment", "total paid: " + totalPaid);
                        new billActivity().tvTotalPaid.setText("$" + to2Decimal(totalPaid));

                        ArrayList<Item> itemList = db.getAllItemInBill((int) person.getForeign_bill_id());
                        double totalAmtForBill = 0.0;
                        for (Item item : itemList) {
                            totalAmtForBill += item.getItem_price() * item.getItem_quantity();
                        }

                        double totalLeft = totalAmtForBill - totalPaid;
                        new billActivity().tvTotalLeft.setText("$" + to2Decimal(totalLeft));

                        Log.e("makePayment", "total: " + totalAmtForBill);

                        db.closeDB();
                        notifyDataSetChanged();
                    }
                }
            });

            myBuilder.setNegativeButton("Cancel", null);
            AlertDialog myDialog = myBuilder.create();
            myDialog.setView(viewDialog);
            myDialog.show();
        }
    }

    void deductPayment(final Person person) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final AlertDialog.Builder myBuilder = new AlertDialog.Builder(getContext());
        final View viewDialog = inflater.inflate(R.layout.person_revert_dialog, null);

        final EditText etDeduct = (EditText) viewDialog.findViewById(R.id.etDeduct);
        myBuilder.setTitle("Make Payment");
        myBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                double amtDeduct = Double.parseDouble(etDeduct.getText().toString());
                Toast.makeText(getContext(), amtDeduct + " ", Toast.LENGTH_SHORT).show();

                if (amtDeduct <= person.getAmt_paid()) {
                    person.deduct_amt_paid(amtDeduct);
                    DatabaseHelper db;
                    db = new DatabaseHelper(getContext());
                    db.updatePerson(person);
                    ArrayList<Person> personList = db.getAllPersonsByBill(person.getForeign_bill_id());
                    double totalPaid = 0.0;
                    for (Person person : personList) {
                        totalPaid += person.getAmt_paid();
                    }
                    new billActivity().tvTotalPaid.setText("$" + to2Decimal(totalPaid));

                    ArrayList<Item> itemList = db.getAllItemInBill((int) person.getForeign_bill_id());
                    double totalAmtForBill = 0.0;
                    for (Item item : itemList) {
                        totalAmtForBill += item.getItem_price() * item.getItem_quantity();
                    }

                    double totalLeft = totalAmtForBill - totalPaid;
                    new billActivity().tvTotalLeft.setText("$" + to2Decimal(totalLeft));

                    db.closeDB();
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "You cannot deduct more than you pay", Toast.LENGTH_SHORT).show();
                }


            }
        });
        myBuilder.setNegativeButton("Cancel", null);
        AlertDialog myDialog = myBuilder.create();
        myDialog.setView(viewDialog);
        myDialog.show();
    }


   /* void updateTotalAmt(){
        DatabaseHelper db;
        db = new DatabaseHelper(getContext());
        ArrayList<Item>itemList = db.getAllItemInBill((int)new billActivity().id);
        double totalAmtForBill = 0.0;
        for(Item item : itemList){
            totalAmtForBill += item.getItem_price() * item.getItem_quantity();
        }
        new billActivity().tvTotal.setText("$" + to2Decimal(totalAmtForBill));
    }*/

}

