package com.example.dopin.desktoppet.broadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import com.example.dopin.desktoppet.presenter.MyWindowManager;
import com.example.dopin.desktoppet.util.AlarmUtil;
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
        /**
         * 接受action为闹铃的广播，并且判断这个闹铃是否还在sp文件中，不在则表示此闹铃已被删除
         */
        if(action.equals("AlarmBroadcast")&& AlarmUtil.isExits(dateString))
        {
            /**
             * 接受广播，删除sp文件记录
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
