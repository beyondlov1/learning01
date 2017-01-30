package com.beyond.learning01;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity_Backup_2 extends Activity {
    Calendar calendar=Calendar.getInstance();
    private static String calenderURL = "content://com.android.calendar/calendars";
    private static String calenderEventURL = "content://com.android.calendar/events";
    private static String calenderReminderURL = "content://com.android.calendar/reminders";

    //
    private static int checkCalendarAccount(Context context) {
        Cursor userCursor = context.getContentResolver().query(Uri.parse(calenderURL), null, null, null, null);
        try {
            if (userCursor == null)//查询返回空值
                return -1;
            int count = userCursor.getCount();
            if (count > 0) {//存在现有账户，取第一个账户的id返回
                userCursor.moveToFirst();
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    private static String CALENDARS_NAME = "test";
    private static String CALENDARS_ACCOUNT_NAME = "test@gmail.com";
    private static String CALENDARS_ACCOUNT_TYPE = "com.android.exchange";
    private static String CALENDARS_DISPLAY_NAME = "测试账户";
    private static long addCalendarAccount(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);
        Uri calendarUri = Uri.parse(calenderURL);
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build();
        Uri result = context.getContentResolver().insert(calendarUri, value);
        long id = result == null ? -1 : ContentUris.parseId(result);
        return id;
    }

    private static int checkAndAddCalendarAccount(Context context){
        int oldId = checkCalendarAccount(context);
        if( oldId >= 0 ){
            return oldId;
        }else{
            long addId = addCalendarAccount(context);
            if (addId >= 0) {
                return checkCalendarAccount(context);
            } else {
                return -1;
            }
        }
    }
    public static void addCalendarEvent(Context context,String title, String description, long beginTime){
        // 获取日历账户的id
        int calId = checkAndAddCalendarAccount(context);
        if (calId < 0) {
            // 获取账户id失败直接返回，添加日历事件失败
            return;
        }
        ContentValues event = new ContentValues();
        event.put("title", title);
        event.put("description", description);
        // 插入账户的id
        event.put("calendar_id", calId);
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(beginTime);//设置开始时间
        long start = mCalendar.getTime().getTime();
        mCalendar.setTimeInMillis(start + 60*60*1000);//设置终止时间
        long end = mCalendar.getTime().getTime();
        event.put(CalendarContract.Events.DTSTART, start);
        event.put(CalendarContract.Events.DTEND, end);
        event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
        event.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");  //这个是时区，必须有，
        //添加事件
        Uri newEvent = context.getContentResolver().insert(Uri.parse(calenderEventURL), event);
        if (newEvent == null) {
            // 添加日历事件失败直接返回
            return;
        }
        //事件提醒的设定
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        // 提前10分钟有提醒
        values.put(CalendarContract.Reminders.MINUTES, 10);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri uri = context.getContentResolver().insert(Uri.parse(calenderReminderURL), values);
        if(uri == null) {
            // 添加闹钟提醒失败直接返回
            return;
        }
    }
    //


    public static int getCurrentMonthDay() {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }
    int ClickedDay,ClickedHour,ClickedMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView0=(TextView)findViewById(R.id.textView0);
        textView0.setText(R.string.Day);
        TextView textView1=(TextView)findViewById(R.id.textView1);
        textView1.setText(R.string.hour);
        TextView textView2=(TextView)findViewById(R.id.textView2);
        textView2.setText(R.string.minute);

        GridView gridView0=(GridView)findViewById(R.id.gridView0);
        GridView gridView1=(GridView)findViewById(R.id.gridView1);
        GridView gridView2=(GridView)findViewById(R.id.gridView2);

        final ArrayList<String> DayItem=new ArrayList<>();
        final ArrayList<String> HourItem=new ArrayList<>();
        final ArrayList<String> MinuteItem=new ArrayList<>();
        int maxDate=getCurrentMonthDay();
        System.out.println(calendar.get(Calendar.DAY_OF_MONTH));
        for(int i=0;i<5;i++){
            if (calendar.get(Calendar.DAY_OF_MONTH)+i<maxDate+1){
                DayItem.add(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)+i));
            }else {
                DayItem.add(String.valueOf(i-(maxDate-calendar.get(Calendar.DAY_OF_MONTH))));
            }
        }
        for (int i=0;i<24;i++){
            HourItem.add(String.valueOf(i+1));
        }
        for (int i=0;i<60;i++){
            MinuteItem.add(String.valueOf(i+1));
        }

        final LastClearableAdapter lastClearableAdapter0 = new LastClearableAdapter(this,DayItem);
        final LastClearableAdapter lastClearableAdapter1 = new LastClearableAdapter(this,HourItem);
        final LastClearableAdapter lastClearableAdapter2 = new LastClearableAdapter(this,MinuteItem);

        gridView0.setAdapter(lastClearableAdapter0);
        gridView1.setAdapter(lastClearableAdapter1);
        gridView2.setAdapter(lastClearableAdapter2);

        gridView0.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String string=DayItem.get(position);
                System.out.println(string);
                lastClearableAdapter0.clearSelection(position);
                lastClearableAdapter0.notifyDataSetChanged();
                ClickedDay=Integer.parseInt(string);
            }
        });

        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String string=HourItem.get(position);
                System.out.println(string);
                lastClearableAdapter1.clearSelection(position);
                lastClearableAdapter1.notifyDataSetChanged();
                ClickedHour = Integer.parseInt(string);
            }
        });
        gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String string=MinuteItem.get(position);
                System.out.println(string);
                lastClearableAdapter2.clearSelection(position);
                //实现点击一个上一个变回原来状态
                lastClearableAdapter2.notifyDataSetChanged();
                ClickedMinute=Integer.parseInt(string);
            }
        });
        Button button=(Button)findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCalendarAccount(MainActivity_Backup_2.this)==-1){
                    addCalendarAccount(MainActivity_Backup_2.this);
                }
                checkCalendarAccount(MainActivity_Backup_2.this);
                System.out.println(checkCalendarAccount(MainActivity_Backup_2.this));
                if (checkCalendarAccount(MainActivity_Backup_2.this)>0){
                    Calendar mCalendar=Calendar.getInstance();
                    mCalendar.set(2017,0,ClickedDay,ClickedHour,ClickedMinute);
                    long start = mCalendar.getTime().getTime();
                    addCalendarEvent(MainActivity_Backup_2.this,"title","this is a test",start);
                }
            }
        });

    }

    public class LastClearableAdapter extends BaseAdapter{

        private LayoutInflater inflater;
        private ArrayList<String> numberList=new ArrayList<String>();
        private int selectedPosition=-1;
        public LastClearableAdapter(Context context,ArrayList<String> numberList) {
            inflater = LayoutInflater.from(context);
            this.numberList = numberList;
        }
        @Override
        public int getCount() {
            return numberList.size();
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return numberList.get(position);
        }

        public void clearSelection(int position){
            selectedPosition=position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView==null){
                holder=new ViewHolder();
                convertView=inflater.inflate(R.layout.day_item,null);
                holder.textView=(TextView)convertView.findViewById(R.id.textView0);
                convertView.setTag(holder);
            }
            holder=(ViewHolder)convertView.getTag();
            if(numberList.get(position).length()>0){
                holder.textView.setText(numberList.get(position));
            }
            if (selectedPosition==position){
                holder.textView.setTextColor(Color.RED);
            }else {
                holder.textView.setTextColor(Color.BLACK);
            }
            return convertView;
        }

        public class ViewHolder{
            public TextView textView;
        }
    }
}
