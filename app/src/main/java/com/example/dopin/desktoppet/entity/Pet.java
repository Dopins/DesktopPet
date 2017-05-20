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
    /**
     * 默认动画，移动动画，左边隐藏、右边隐藏动画，闹铃动画，微信通知提醒动画，触摸动画
     */
    protected AnimationDrawable defaultAni;
    protected AnimationDrawable moveAni;
    protected AnimationDrawable hiddenLeftAni;
    protected AnimationDrawable hiddenRightAni;
    protected AnimationDrawable alarmAni;
    protected AnimationDrawable noticeAni;
    protected AnimationDrawable touchAni;
    /**
     * 帧动画实现动画效果
     * 一个宠物的所有动画图片放在一个文件夹，paths数组存放其中所有图片的名字
     */
    protected String[] paths;
    /**
     * 图片所在的目录
     */
    protected String path;

    /**
     * keyword是style，比如cat，totoro。文件夹以此命名
     * @param keyword
     */
    public Pet(String keyword){
        jsonPet =new JsonPet(keyword);

        paths=AssetIO.getStringList(keyword);
        path=keyword+"/";
        defaultAni=getAni("default",200);
        touchAni=getAni("touch", 200);
        moveAni=getAni("move", 200);
        alarmAni=getAni("alarm", 200);
        noticeAni=getAni("notice", 200);
        hiddenLeftAni=getAni("hidden_left", 200);
        hiddenRightAni=getAni("hidden_right", 200);

    }
    public Pet(JsonPet jsonPet){//专门用于生成蓝牙配对的宠物的构造函数
        this.jsonPet=jsonPet;

        paths=AssetIO.getStringList(jsonPet.getStyle());
        path=jsonPet.getStyle()+"/";
        defaultAni=getAni("default",200);//值初始化default动画
    }

    /**
     * 获取动画
     * @param keyWord 哪一个类型的动画
     * @param duration 每一帧持续时间
     * @return
     */
    public AnimationDrawable getAni(String keyWord,int duration){
        AnimationDrawable anim=new AnimationDrawable();
        /**
         * 以keyword开头的图片有count张
         * 文件命名方式，keyword+index。
         * 比如default1，default2...
         * 传入一个default关键词即可构造default动画。
         */
        int count=getCountByStartStr(keyWord);
        /**
         * 通过StringBuilder构造图片路径
         */
        StringBuilder filename=new StringBuilder();
        filename.append(path);
        filename.append(keyWord);
        for (int i = 1; i <= count; i++) {
            filename.append(i);
            filename.append(".png");
            /**
             * 通过图片绝对路径得到bitmap
             */
            Bitmap bmp= AssetIO.getBitmap(filename.toString());
            Drawable drawable =new BitmapDrawable(bmp);
            /**
             * 添加一帧
             */
            anim.addFrame(drawable,duration);

            filename.delete(path.length()+keyWord.length(),filename.length());
        }
        /**
         * 设置是否为循环播放。false为循环
         */
        anim.setOneShot(false);

        return anim;
    }

    /**
     * 获取paths中所有图片，文件名以str开头的数目
     * @param str
     * @return
     */
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
