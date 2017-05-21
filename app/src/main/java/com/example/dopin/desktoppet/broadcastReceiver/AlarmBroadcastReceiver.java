package com.example.dopin.desktoppet.broadcastReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.dopin.desktoppet.presenter.MyWindowManager;
import com.example.dopin.desktoppet.util.AlarmUtil;
import com.example.dopin.desktoppet.util.ContextUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Created by ljquality on 17/5/8.
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver  {

    @Override
    public void onReceive(Context context,Intent intent)
    {
        String action=intent.getAction();
        String dateString=intent.getStringExtra("date");
        String note=intent.getStringExtra("note");
        if(action.equals("AlarmBroadcast")&& AlarmUtil.isExits(dateString))
        {
            /**
             * 接受广播，删除文件记录
             */
            AlarmUtil.removeAlarm(dateString);
            /**
             * 响应闹铃
             */
            deal(note);
        }
    }
    private void deal(String note){
        MyWindowManager.alarm(note);
    }
}
