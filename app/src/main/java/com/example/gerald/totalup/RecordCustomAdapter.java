package com.example.gerald.totalup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nex3z.flowlayout.FlowLayout;

import java.util.ArrayList;

/**
 * Created by GERALD on 19/3/2018.
 */

public class RecordCustomAdapter extends ArrayAdapter{

    Context parent_context;
    int layout_id;
    ArrayList<Item> itemList;

    public RecordCustomAdapter(Context context, int resource, ArrayList<Item> objects){
        super(context,resource,objects);

        parent_context = context;
        layout_id = resource;
        itemList = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)parent_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(layout_id,parent,false);

        final TextView tvRecordItemName = (TextView)rowView.findViewById(R.id.tvRecordItemName);
        final TextView tvRecordItemPrice = (TextView)rowView.findViewById(R.id.tvRecordItemPrice);
        final TextView tvRecordItemQuantity = (TextView)rowView.findViewById(R.id.tvRecordItemQuantity);
        final TextView tvRecordStatement = (TextView)rowView.findViewById(R.id.tvStatement);
        final FlowLayout recordCheckboxs = (FlowLayout) rowView.findViewById(R.id.recordCheckboxes);
        final ImageButton imgBtnDelete = (ImageButton)rowView.findViewById(R.id.imgBtnDelete);

        final Item currentItem = itemList.get(position);
        //db
        final DatabaseHelper db;
        db = new DatabaseHelper(getContext());

        Log.e("current item", currentItem.getItem_name() + ", $" +currentItem.getItem_price());

        //get all person in bill and records
        ArrayList<Person>persons = db.getAllPersonsByBill(currentItem.getForeign_bill_id());
        ArrayList<Record>records = db.getAllRecordsByBillAndItem(currentItem.getForeign_bill_id(), currentItem.getItem_id());

        for(Record record : records){
            Log.e(currentItem.getItem_name() + " has :", " person id:" + record.getPerson_id() + " , in bill id:" + record.getBill_id() + " on record id:" + record.getRecord_id() + "length:" + records.size());
        }

        imgBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper db;
                db = new DatabaseHelper(getContext());
                db.deleteItem(currentItem.getItem_id());
                itemList.remove(position);
                notifyDataSetChanged();
                db.closeDB();

            }
        });

        //get checkboxes
        final ArrayList<Integer> checkList = new ArrayList<>();
        for(final Person person : persons){
            final CheckBox c = new CheckBox(getContext());
            //c.setId(x+1);
            c.setText(person.getPerson_name());
            c.setId(person.getPerson_id());

            //check if person is in record or not
            for(Record record : records){
                Log.d("item person compare", person.getPerson_id() + " " + record.getPerson_id());
                if(record.getPerson_id() == person.getPerson_id()){
                    Log.d("item person ticked", person.getPerson_id() + " ");
                    c.setChecked(true);
                    checkList.add(person.getPerson_id());
                }
            }

            recordCheckboxs.addView(c);
            c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Log.e("checkbox", c.getId() + " " + c.getText());

                    if(c.isChecked()){
                        Log.e("checkbox", c.getId() + " " + c.getText() + " checked");
                        checkList.add(c.getId());
                        Record newRecord = new Record(currentItem.getForeign_bill_id(), currentItem.getItem_id(), person.getPerson_id());
                        db.createRecord(newRecord);
                        tvRecordStatement.setText("Each will need to pay $ " + to2Decimal(calculateTotalPrice(currentItem.getItem_price(), currentItem.getItem_quantity(), checkList.size()))  + " for " + currentItem.getItem_name());

                    }

                    else{
                        if(checkList.size() <= 1){
                            c.setChecked(true);
                            Toast.makeText(getContext(),"cannot uncheck", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.e("checkbox", c.getId() + " " + c.getText() + " unchecked");
                            for (int i = 0; i < checkList.size(); i++) {
                                if (checkList.get(i) == c.getId()) {
                                    checkList.remove(i);
                                    db.deleteRecordByPersonAndItem(person.getPerson_id(), currentItem.getItem_id());
                                    Log.e("unchecking", "unchecked");
                                    tvRecordStatement.setText("Each will need to pay $ " + to2Decimal(calculateTotalPrice(currentItem.getItem_price(), currentItem.getItem_quantity(), checkList.size())) + " for " + currentItem.getItem_name());

                                }
                            }
                        }
                    }

                    for(int i = 0; i < checkList.size(); i++){
                        Log.e("checkbox list", checkList.get(i) + " ");
                    }
                }
            });
        }

        tvRecordItemName.setText(currentItem.getItem_name() + "");
        tvRecordItemPrice.setText("$" + currentItem.getItem_price());
        tvRecordItemQuantity.setText(currentItem.getItem_quantity()+"");
        //get the total price for item
        tvRecordStatement.setText("Each will need to pay $ " + to2Decimal(calculateTotalPrice(currentItem.getItem_price(), currentItem.getItem_quantity(), checkList.size()))  + " for " + currentItem.getItem_name());
        db.closeDB();

        return rowView;

    }

    double calculateTotalPrice(double itemPrice, double itemQuantiy, int size){
        return (itemPrice * itemQuantiy) / size;
    }

    String to2Decimal(double amt){
        return String.format("%.2f", amt);
    }

}
