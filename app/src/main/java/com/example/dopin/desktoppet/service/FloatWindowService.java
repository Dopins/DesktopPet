package com.example.dopin.desktoppet.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.example.dopin.desktoppet.broadcastReceiver.AlarmBroadcastReceiver;
import com.example.dopin.desktoppet.entity.Pet;
import com.example.dopin.desktoppet.presenter.MyWindowManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


public class FloatWindowService extends Service {
    public static boolean isCreated=false;

    public static Pet curPet;

    public class MyBinder extends Binder {

        public FloatWindowService getService(){
            return FloatWindowService.this;
        }
    }

    private MyBinder myBinder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        isCreated=true;
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }
    @Override
    public void onRebind(Intent intent) {

        super.onRebind(intent);
    }
    /**
     * 设置初始宠物
     */
    private void setDefaultPet(){
        if(FloatWindowService.curPet==null){

            Pet pet =getPet("cat");
            FloatWindowService.curPet=pet;
        }
    }
    private Pet getPet(String petStyle){
        SharedPreferences sp = getApplication().getSharedPreferences(petStyle, Context.MODE_PRIVATE);
        String name=sp.getString("name", "未设置");
        String sex=sp.getString("sex","未设置");
        String age=sp.getString("age","未设置");
        String signature=sp.getString("signature","未设置");
        Pet pet=new Pet(petStyle);
        pet.setSex(sex);
        pet.setAge(age);
        pet.setName(name);
        pet.setSignature(signature);
        return pet;
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        setDefaultPet();
        start();
        setAlarm();
        return super.onStartCommand(intent,flags,startId);
    }

    public void start() {
        MyWindowManager.createSmallWindow(getApplicationContext());
    }

    private void setAlarm(){
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("alarm", Context.MODE_PRIVATE);
        Map<String,?> map=sharedPreferences.getAll();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(String key:map.keySet()){
            String note=(String)map.get("key");
            try {
                Date date=simpleDateFormat.parse(key);
                new AlarmBroadcastReceiver(getApplicationContext()).setAlarm(note,date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyWindowManager.removeSmallWindow(getApplicationContext());
        MyWindowManager.destroy();
    }

    public void changePet(String style){
        curPet=getPet(style);
        MyWindowManager.windowChangePet();
    }


}
