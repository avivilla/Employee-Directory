package com.example.EmployeeDirectory;

import java.util.ArrayList;
import java.util.HashMap;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


public class ListViewAdapter extends BaseAdapter{

    public ArrayList<HashMap<String, String>> list;
    private  Activity activity;
    public static final String FIRST_COLUMN="ID";
    public static final String SECOND_COLUMN="NAME";
    public static final String THIRD_COLUMN="AGE";
    public static final String FOURTH_COLUMN="GENDER";
    public String ID_STRING;
    public ListViewAdapter(Activity activity, ArrayList<HashMap<String, String>> list){
        super();

        this.list=list;
        this.activity =activity;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    private class ViewHolder{
        TextView txtFirst;
        TextView txtSecond;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder holder;

        LayoutInflater inflater=activity.getLayoutInflater();
        HashMap<String, String> map;
        if(convertView == null){
            convertView=inflater.inflate(R.layout.colmn_row, null);
            holder=new ViewHolder();
            holder.txtFirst=(TextView) convertView.findViewById(R.id.TextFirst);
            holder.txtSecond=(TextView) convertView.findViewById(R.id.TextSecond);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }

        map=list.get(position);
        ID_STRING =map.get(FIRST_COLUMN);
        holder.txtFirst.setText(map.get(FIRST_COLUMN));
        holder.txtSecond.setText(map.get(SECOND_COLUMN));

        ///////////////  COLORING ODD-EVEN ROW //////////////////////
         if(position%2==0)
         convertView.setBackgroundColor(Color.rgb(131,172,172));
        else
            convertView.setBackgroundColor(Color.rgb(114,150,150));


        //Handle buttons and add onClickListeners
        final Button callbtn= (Button)convertView.findViewById(R.id.btn);
        if(ID_STRING == "ID")
            callbtn.setVisibility(View.INVISIBLE);
            callbtn.setTag(holder.txtFirst.getText());
        callbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                ID_STRING = callbtn.getTag().toString();

                if(ID_STRING != "ID")
                {
                    Intent intent = new Intent(activity,ProfileActivity.class);
                    intent.putExtra("id", Integer.parseInt(ID_STRING));
                    activity.startActivity(intent);
                }


            }
        });
        return convertView;
    }

}