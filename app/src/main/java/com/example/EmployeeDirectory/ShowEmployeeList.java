package com.example.EmployeeDirectory;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowEmployeeList extends AppCompatActivity {
    DbHelper myDB;
    private ArrayList<HashMap<String, String>> list;
    public static final String FIRST_COLUMN="ID";
    public static final String SECOND_COLUMN="NAME";
    public static final String THIRD_COLUMN="AGE";
    public static final String FOURTH_COLUMN="GENDER";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_employee_list);
        myDB = new DbHelper(this);
        viewData();

    }
    void viewData()
    {
        Cursor res = myDB.getAllData();
        ListView listView=(ListView)findViewById(R.id.listView1);
        list=new ArrayList<HashMap<String,String>>();
        HashMap<String,String> hashmapheader=new HashMap<String, String>();
        hashmapheader.put(FIRST_COLUMN,FIRST_COLUMN);
        hashmapheader.put(SECOND_COLUMN,SECOND_COLUMN);
        list.add(hashmapheader);
        while (res.moveToNext()) {
            HashMap<String,String> hashmap=new HashMap<String, String>();
            hashmap.put(FIRST_COLUMN, Integer.toString(res.getInt(0)));
            hashmap.put (SECOND_COLUMN,res.getString(1));
            list.add(hashmap);
        }
        ListViewAdapter adapter=new ListViewAdapter(this, list);
        listView.setAdapter(adapter);

    }
    public void goToMain(View v)
    {
        Intent intent = new Intent(ShowEmployeeList.this,MainActivity.class);
        startActivity(intent);
    }

}
