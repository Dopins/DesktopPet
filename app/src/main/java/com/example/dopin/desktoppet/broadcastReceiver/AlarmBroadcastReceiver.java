package com.example.dopin.desktoppet.broadcastReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.util.Log;

import com.example.dopin.desktoppet.presenter.MyWindowManager;

import java.util.Date;
/**
 * Created by ljquality on 17/5/8.
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver  {
    AlarmManager alarmManager;
    Context sysAlarmContext;
    int idCounter;
    public AlarmBroadcastReceiver()
    {}
    public AlarmBroadcastReceiver(Context context)
    {
        alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        sysAlarmContext=context;
        idCounter=0;
    }
    //set a alarm with a long type time
    public int setAlarm(String note,long time)
    {
        long curTime=System.currentTimeMillis();
        if(time<=curTime)
            return -1;//time has gone
        Intent messageIntent=new Intent();
        messageIntent.setAction("AlarmBroadcast");
        messageIntent.putExtra("note", note);
        PendingIntent sender=PendingIntent.getBroadcast(sysAlarmContext,idCounter++,messageIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, sender);
        return 0;
    }
    //set a alarm with a date type time
    public int setAlarm(String action,Date time )
    {
        return setAlarm(action,time.getTime());
    }

    @Override
    public void onReceive(Context context,Intent intent)
    {
        String action=intent.getAction();
        if(action.equals("AlarmBroadcast"))
        {
            deal(intent.getStringExtra("note"));
        }
    }
    //how to deal with the event when a alarm come
    protected void deal(String note){
        MyWindowManager.alarm(note);
    }
}
