package com.example.dopin.desktoppet.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.dopin.desktoppet.entity.Clock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by dopin on 2017/5/21.
 */
public class AlarmUtil {
    static Context context=ContextUtil.getInstance();
    static AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    static int idCounter=0;
    static SharedPreferences sharedPreferences = ContextUtil.getInstance().getSharedPreferences("alarm", Context.MODE_PRIVATE);
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //set a alarm with a long type time
    public static int setAlarm(String note,long time,Date date)
    {
        long curTime=System.currentTimeMillis();
        if(time<=curTime)
            return -1;//time has gone
        Intent messageIntent=new Intent();

        String dateString= simpleDateFormat.format(date);
        messageIntent.setAction("AlarmBroadcast");
        messageIntent.putExtra("note", note);
        messageIntent.putExtra("date",dateString);
        PendingIntent sender=PendingIntent.getBroadcast(context,idCounter++,messageIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, sender);
        return 0;
    }
    //set a alarm with a date type time
    public static int setAlarm(String action,Date time )
    {
        return setAlarm(action, time.getTime(), time);
    }
    public static boolean isExits(String dateString){
        return sharedPreferences.contains(dateString);
    }
    public static int removeAlarm(String dateString){
        if(sharedPreferences.contains(dateString)){
            sharedPreferences.edit().remove(dateString).commit();
            return 1;//success
        }else{
            return 0;//fail
        }
    }
    public static int removeAlarm(Date date){
        String dateString= simpleDateFormat.format(date);
        return removeAlarm(dateString);
    }
    public static ArrayList<Clock> getClockList(){
        ArrayList<Clock> list=new ArrayList<>();
        Map<String,?> map=sharedPreferences.getAll();
        for(String key:map.keySet()){
            String note=(String)map.get(key);
            try {
                Date date=simpleDateFormat.parse(key);
                list.add(new Clock(note,date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

}
