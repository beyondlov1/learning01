package com.beyond.learning01;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity_Backup extends Activity {
    Calendar calendar=Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView0=(TextView)findViewById(R.id.textView0);
        textView0.setText(R.string.Day);
        TextView textView1=(TextView)findViewById(R.id.textView1);
        textView1.setText(R.string.hour);
        TextView textView2=(TextView)findViewById(R.id.textView2);
        textView2.setText(R.string.minute);

        GridView gridView0=(GridView)findViewById(R.id.gridView0);
        GridView gridView1=(GridView)findViewById(R.id.gridView1);
        GridView gridView2=(GridView)findViewById(R.id.gridView2);

        ArrayList<HashMap<String,String>> DayItem=new ArrayList<>();
        ArrayList<HashMap<String,String>> HourItem=new ArrayList<>();
        ArrayList<HashMap<String,String>> MinuteItem=new ArrayList<>();

        for(int i=0;i<5;i++){
            HashMap<String,String> day=new HashMap<>();
            day.put("ID",String.valueOf(i));
            day.put("day",String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)+i));
            DayItem.add(day);
        }
        for (int i=0;i<24;i++){
            HashMap<String,String> hour=new HashMap<>();
            hour.put("hour",String.valueOf(i+1));
            HourItem.add(hour);
        }
        for (int i=0;i<60;i++){
            HashMap<String,String> minute=new HashMap<>();
            minute.put("minute",String.valueOf(i+1));
            MinuteItem.add(minute);
        }

        SimpleAdapter simpleAdapterDay=new SimpleAdapter(this,DayItem,R.layout.day_item,new String[]{"day"},new int[]{R.id.textView0});
        SimpleAdapter simpleAdapterHour=new SimpleAdapter(this,HourItem,R.layout.hour_item,new String[]{"hour"},new int[]{R.id.textView1});
        SimpleAdapter simpleAdapterMinute=new SimpleAdapter(this,MinuteItem,R.layout.minute_item,new String[]{"minute"},new int[]{R.id.textView2});

        gridView0.setAdapter(simpleAdapterDay);
        gridView1.setAdapter(simpleAdapterHour);
        gridView2.setAdapter(simpleAdapterMinute);

        gridView0.setOnItemClickListener(new ItemClickListener(){

        });

        gridView0.setOnItemClickListener(new ItemClickListener());
        gridView1.setOnItemClickListener(new ItemClickListener());
        gridView2.setOnItemClickListener(new ItemClickListener());
        //simpleAdapterDay.notifyDataSetChanged();
    }
    class ItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //view.setBackgroundColor(Color.GREEN);
            HashMap<String,String> item=(HashMap<String, String>) parent.getItemAtPosition(position);
            String string=item.get("day");
            System.out.println(position);

        }
    }
}
