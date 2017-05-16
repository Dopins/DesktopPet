package com.example.dopin.desktoppet.entity;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.example.dopin.desktoppet.jsonBean.JsonPet;
import com.example.dopin.desktoppet.util.AssetIO;

/**
 * Created by dopin on 2017/5/3.
 */
public class Pet {
    protected JsonPet jsonPet;

    protected AnimationDrawable defaultAni;
    protected AnimationDrawable moveAni;
    protected AnimationDrawable hiddenLeftAni;
    protected AnimationDrawable hiddenRightAni;
    protected AnimationDrawable alarmAni;
    protected AnimationDrawable noticeAni;
    protected AnimationDrawable touchAni;

    protected String[] paths;
    protected String path;


    public Pet(String keyword){
        jsonPet =new JsonPet(keyword);

        paths=AssetIO.getStringList(keyword);
        path=keyword+"/";
        defaultAni=setAni("default",200);
        touchAni=setAni("touch", 200);
        moveAni=setAni("move", 200);
        alarmAni=setAni("alarm", 200);
        noticeAni=setAni("notice", 200);
        hiddenLeftAni=setAni("hidden_left", 200);
        hiddenRightAni=setAni("hidden_right", 200);

    }
    public Pet(JsonPet jsonPet){//专门用于生成蓝牙配对的宠物的构造函数
        this.jsonPet=jsonPet;

        paths=AssetIO.getStringList(jsonPet.getStyle());
        path=jsonPet.getStyle()+"/";
        defaultAni=setAni("default",200);//值初始化default动画
    }
    public AnimationDrawable setAni(String keyWord,int duration){
        AnimationDrawable anim=new AnimationDrawable();
        int count=getCountByStartStr(keyWord);
        StringBuilder filename=new StringBuilder();
        filename.append(path);
        filename.append(keyWord);
        for (int i = 1; i <= count; i++) {
            filename.append(i);
            filename.append(".png");
            Bitmap bmp= AssetIO.getBitmap(filename.toString());
            Drawable drawable =new BitmapDrawable(bmp);
            anim.addFrame(drawable,duration);

            filename.delete(path.length()+keyWord.length(),filename.length());
        }
        anim.setOneShot(false);//循环播放

        return anim;
    }
    private int getCountByStartStr(String str){
        int result=0;
        for(String path:paths){
            if(path.startsWith(str)){
                result++;
            }
        }
        return result;
    }
    public AnimationDrawable getDefaultAni(){
        return defaultAni;
    }
    public AnimationDrawable getTouchAni(){
        return touchAni;
    }
    public AnimationDrawable getHiddenLeftAni(){
        return hiddenLeftAni;
    }
    public AnimationDrawable getHiddenRightAni(){
        return hiddenRightAni;
    }
    public AnimationDrawable getMoveAni(){
        return moveAni;
    }
    public AnimationDrawable getNoticeAni(){
        return noticeAni;
    }
    public AnimationDrawable getAlarmAni(){
        return alarmAni;
    }

    public JsonPet getJsonPet(){
        return jsonPet;
    }
    public void setJsonPet(JsonPet jsonPet){
        this.jsonPet =jsonPet;
    }

    public void setName(String name){
        jsonPet.setName(name);
    }
    public void setAge(String age){
        jsonPet.setAge(age);
    }
    public void setSex(String sex){
        jsonPet.setSex(sex);
    }
    public void setSignature(String signature){
        jsonPet.setSignature(signature);
    }
    public String getName(){
        return jsonPet.getName();
    }
    public String getAge(){
        return jsonPet.getAge();
    }
    public String getSex(){
        return jsonPet.getSex();
    }
    public String getSignature(){
        return jsonPet.getSignature();
    }
}
