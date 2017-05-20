package com.example.dopin.desktoppet.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.dopin.desktoppet.service.FloatWindowService;

/**
 * 监听开机广播，开机自启
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, FloatWindowService.class);
        context.startService(service);
    }

}