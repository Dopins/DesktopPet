package com.example.dopin.desktoppet.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.*;
import android.widget.Toast;

import com.example.dopin.desktoppet.presenter.MyWindowManager;

import java.util.List;
/**
 * Created by ljquality on 17/5/9.
 */
public  class WechatMessageService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {
        int eventType=event.getEventType();

        if(eventType==AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED)
        {
            List<CharSequence> textList=event.getText();
            String sumText="";
            for(CharSequence text:textList)
            {
                sumText+=text;
            }
            deal(sumText);
        }
    }
    @Override
    public void onInterrupt()
    {
        System.out.println("onIn");
    }

    @Override
    protected void onServiceConnected()
    {
        Log.w("tag","start");
        AccessibilityServiceInfo asInfo=new AccessibilityServiceInfo();
        asInfo.eventTypes=AccessibilityEvent.TYPES_ALL_MASK;
        asInfo.feedbackType=AccessibilityServiceInfo.FEEDBACK_GENERIC;
        asInfo.notificationTimeout=100;
        asInfo.packageNames=new String[]{"com.tencent.mm"};
        setServiceInfo(asInfo);
        Log.w("tag", "end");
        super.onServiceConnected();
    }
    public void deal(String text)
    {
        MyWindowManager.notice(text);
    }
}
