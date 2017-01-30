package com.beyond.learning01;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Administrator on 2017/1/25.
 */
public class Calendar_test extends Activity implements View.OnClickListener{
    private Button mReadUserButton;
    private Button mReadEventButton;
    private Button mWriteEventButton;
    Calendar calendar=Calendar.getInstance();

    private static String calenderURL = "content://com.android.calendar/calendars";
    private static String calenderEventURL = "content://com.android.calendar/events";
    private static String calenderReminderURL = "content://com.android.calendar/reminders";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_test);
        setupViews();
    }
    public void setupViews(){
        mReadUserButton = (Button)findViewById(R.id.button);
        mReadEventButton = (Button)findViewById(R.id.button2);
        mWriteEventButton = (Button)findViewById(R.id.button3);
        mReadUserButton.setOnClickListener(this);
        mReadEventButton.setOnClickListener(this);
        mWriteEventButton.setOnClickListener(this);
    }

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
    @Override
    public void onClick(View v) {
        if(v == mReadUserButton){

            if (checkCalendarAccount(this)==-1){
                addCalendarAccount(this);
            }
            checkCalendarAccount(this);
            System.out.println(checkCalendarAccount(this));
            Cursor userCursor = getContentResolver().query(Uri.parse(calenderURL), null, null, null, null);
            if(userCursor.getCount() > 0){
                userCursor.moveToFirst();
                String userName = userCursor.getString(userCursor.getColumnIndex("name"));
                Toast.makeText(Calendar_test.this, userName, Toast.LENGTH_LONG).show();
            }
            if (checkCalendarAccount(this)>0){
                Calendar mCalendar=Calendar.getInstance();
                mCalendar.set(2017,0,26,14,0);
                long start = mCalendar.getTime().getTime();
                addCalendarEvent(this,"title","this is a test",start);
            }

        }
    }
}
