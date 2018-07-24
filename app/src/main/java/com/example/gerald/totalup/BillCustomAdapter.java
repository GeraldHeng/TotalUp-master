package com.example.gerald.totalup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by GERALD on 16/3/2018.
 */

public class BillCustomAdapter extends ArrayAdapter {
    Context parent_context;
    int layout_id;
    ArrayList<Bill> billList;

    public BillCustomAdapter(Context context, int resource, ArrayList<Bill> objects){
        super(context,resource,objects);

        parent_context = context;
        layout_id = resource;
        billList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)parent_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(layout_id,parent,false);

        //get current bill
        final Bill currentBill = billList.get(position);

        //stuff
        final TextView tvBillName = (TextView)rowView.findViewById(R.id.tvBillName);
        final TextView tvTotalAmtForBill = (TextView)rowView.findViewById(R.id.tvTotalAmtForBill);
        final TextView tvAmtOfPerson = (TextView)rowView.findViewById(R.id.tvAmtOfPerson);

        DatabaseHelper db = new DatabaseHelper(getContext());
        ArrayList<Person> personInBill = db.getAllPersonsByBill(currentBill.getBill_id());

        //set stuff
        tvAmtOfPerson.setText(personInBill.size() + "");
        tvBillName.setText(currentBill.getBill_title().toString());
        tvTotalAmtForBill.setText(updateTotalAmt(currentBill));




        return rowView;
    }

    String updateTotalAmt(Bill currentBill){
        DatabaseHelper db = new DatabaseHelper(getContext());
        ArrayList<Item>itemList = db.getAllItemInBill((int)currentBill.getBill_id());
        double totalAmtForBill = 0.0;
        for(Item item : itemList){
            totalAmtForBill += item.getItem_price() * item.getItem_quantity();
        }

        ArrayList<Person>personList = db.getAllPersonsByBill((int)currentBill.getBill_id());
        double totalPaid = 0.0;
        for(Person person : personList){
            totalPaid += person.getAmt_paid();
        }
        double totalLeft = totalAmtForBill - totalPaid;

        return "$" + to2Decimal(totalLeft);

    }



    String to2Decimal(double amt){
        return String.format("%.2f", amt);
    }
}
