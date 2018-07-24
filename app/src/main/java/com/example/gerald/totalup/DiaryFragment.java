package com.example.gerald.totalup;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DiaryFragment extends Fragment {

    public DiaryFragment() {
    }

    FloatingActionButton fabAddBill;
    DatabaseHelper db;
    ListView lvBills;
    BillCustomAdapter BCA;
    ArrayList<Bill> billList;
    ArrayList<Record>recordList;
    ArrayList<Item>itemList;
    ArrayList<Person>personList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_diary, container, false);

        //all the stuff
        fabAddBill = rootView.findViewById(R.id.fabAddBill);
        lvBills = rootView.findViewById(R.id.lvBills);
        registerForContextMenu(lvBills);

        //setting of db, customAdapter, all bills to BCA
        db = new DatabaseHelper(getContext());
      //  db.reset();
        getAllBills();
        recordList = db.getAllRecords();
        itemList = db.getAllItems();
        personList = db.getAllPersons();

        if(!billList.isEmpty()){
            for(Bill bill : billList){
                Log.d("diary bill", bill.getBill_id() + ", " + bill.getBill_title());
            }
        }

        if(!recordList.isEmpty()){
            for(Record record : recordList){
                Log.d("diary record", record.getRecord_id() + ", b id:"  + record.getBill_id() + ", p id:" + record.getPerson_id() + ", i id:" + record.getItem_id());
            }
        }

        if(!itemList.isEmpty()){
            for(Item item : itemList){
                Log.d("diary item", item.getItem_id() + ", " + item.getItem_name() + ", " + item.getForeign_bill_id());
            }
        }

        if(!personList.isEmpty()){
            for(Person person : personList){
                Log.d("diary person", person.getPerson_id() + ", " + person.getPerson_name() + ", " + person.getForeign_bill_id() + ", paid:" + person.getAmt_paid() + ", number" + person.getPhone_number());
            }
        }

        //add a new bill
        fabAddBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), billActivity.class);
                startActivity(intent);
            }
        });

        lvBills.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d("Lv clicked", billList.get(position).getBill_title() + " id: " + billList.get(position).getBill_id() );
                Intent intent = new Intent(getActivity(), billActivity.class);
                intent.putExtra("id", billList.get(position).getBill_id());
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.lvBills) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("Delete " + billList.get(info.position).getBill_title() + "?");
            menu.add(Menu.NONE, 0,0, "delete");
            //menu.setHeaderTitle(Countries[info.position]);
            //String[] menuItems = getResources().getStringArray(R.array.menu);
            //for (int i = 0; i<menuItems.length; i++) {
              //  menu.add(Menu.NONE, i, i, menuItems[i]);
            //}
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        if(menuItemIndex == 0){
            Toast.makeText(getContext(), "delete", Toast.LENGTH_SHORT).show();
            DatabaseHelper db = new DatabaseHelper(getContext());
            db.deleteBill(billList.get(info.position).getBill_id());
            getAllBills();
        }

        return true;
    }

    @Override
    public void onResume() {
       getAllBills();
        Log.e("resumed.", "data change");
        super.onResume();
    }

    void getAllBills(){
        billList = db.getAllBills();
        BCA = new BillCustomAdapter(getContext(), R.layout.bill_item_row, billList);
        lvBills.setAdapter(BCA);
    }
}