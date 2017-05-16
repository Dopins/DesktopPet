package com.example.dopin.desktoppet.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dopin.desktoppet.R;
import com.example.dopin.desktoppet.activity.MainActivity;
import com.example.dopin.desktoppet.entity.Pet;
import com.example.dopin.desktoppet.service.FloatWindowService;

import java.lang.reflect.Field;

/**
 * Created by dopin on 2017/5/2.
 */
public class FloatWindowView extends LinearLayout {
    /**
     * 记录小悬浮窗的宽度
     */
    public static int viewWidth;
    /**
     * 记录小悬浮窗的高度
     */
    public static int viewHeight;
    /**
     * 记录系统状态栏的高度
     */
    private static int statusBarHeight;
    /**
     * 用于更新小悬浮窗的位置
     */
    private WindowManager windowManager;
    /**
     * 小悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;
    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;
    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;
    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;
    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;
    /**
     * 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float xInView;
    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float yInView;

    private boolean isLeft=false;

    private boolean movable =true;

    private ImageView animationIV;

    private  LinearLayout buttonLayout;
    private  LinearLayout textLayout;

    private TextView noticeText;

    private boolean isMove=false;

    private Handler handler;

    private Runnable runnable;

    private Pet pet;

    public FloatWindowView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_window_small, this);
        View view = findViewById(R.id.small_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        animationIV = (ImageView) findViewById(R.id.animationIV);
        buttonLayout=(LinearLayout)findViewById(R.id.button_layout);
        textLayout=(LinearLayout)findViewById(R.id.text_layout);
        noticeText=(TextView)findViewById(R.id.text);
        Button btnOpenActivity =(Button)findViewById(R.id.btn_open_activity);
        Button btnBack =(Button)findViewById(R.id.btn_back);
        Button btnTouch =(Button)findViewById(R.id.btn_touch);

        btnOpenActivity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);

                hiddenAni();
                buttonLayout.setVisibility(GONE);
            }
        });

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hiddenAni();
                buttonLayout.setVisibility(GONE);
            }
        });

        btnTouch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                touchAni();
                buttonLayout.setVisibility(GONE);
            }
        });

        noticeText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                textLayout.setVisibility(GONE);
                hiddenAni();
            }
        });

        handler = new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {
                hiddenAni();
            }
        };

        this.pet = FloatWindowService.curPet;
        hiddenAni();
    }
    public void notice(String text){
        textLayout.setVisibility(VISIBLE);
        noticeText.setText(text);
        noticeText.setBackgroundResource(R.drawable.notice_text_shape);
        noticeAni();
    }
    public void alarm(String text){
        textLayout.setVisibility(VISIBLE);
        noticeText.setText("备忘: "+text);
        noticeText.setBackgroundResource(R.drawable.alarm_text_shape);
        alarmAni();
    }
    public void changePet(){
        pet=FloatWindowService.curPet;
        hiddenAni();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(movable ==false) return true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                // 手指移动的时候更新小悬浮窗的位置
                updateViewPosition();
                moveAni();
                isMove=true;
                break;
            case MotionEvent.ACTION_UP:
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
                    buttonLayout.setVisibility(VISIBLE);
                    defaultAni();
                }else{
                    int screenWidth = windowManager.getDefaultDisplay().getWidth();
                    if(xInScreen<screenWidth/2) {
                        xInScreen=0;
                        isLeft=true;
                    }
                    else {
                        xInScreen=screenWidth;
                        isLeft=false;
                    }
                    updateViewPosition();
                    isMove=false;
                    hiddenAni();
                }
                break;
            default:
                break;
        }
        return true;
    }
    private void moveAni(){
        if(isMove==true) return;
        AnimationDrawable ani=pet.getMoveAni();
        animationIV.setImageDrawable(ani);
        ani.start();
    }
    private void defaultAni(){
        AnimationDrawable ani=pet.getDefaultAni();
        animationIV.setImageDrawable(ani);
        ani.start();
        movable =false;
    }
    private void alarmAni(){
        handler.removeCallbacks(runnable);//取消
        buttonLayout.setVisibility(GONE);
        AnimationDrawable ani=pet.getAlarmAni();
        animationIV.setImageDrawable(ani);
        ani.start();
        movable =false;
    }
    private void noticeAni(){
        handler.removeCallbacks(runnable);
        buttonLayout.setVisibility(GONE);
        AnimationDrawable ani=pet.getNoticeAni();
        animationIV.setImageDrawable(ani);
        ani.start();
        movable =false;
    }
    private void hiddenAni(){
        handler.removeCallbacks(runnable);//取消
        buttonLayout.setVisibility(GONE);
        textLayout.setVisibility(GONE);

        movable =true;
        AnimationDrawable ani;
        if(isLeft){
            ani=pet.getHiddenLeftAni();
        }else{
            ani=pet.getHiddenRightAni();
        }
        animationIV.setImageDrawable(ani);
        ani.start();
    }
    private void touchAni(){
        AnimationDrawable ani=pet.getTouchAni();
        animationIV.setImageDrawable(ani);
        ani.start();
        movable =false;
        int duration = 0;
        for(int i=0;i<ani.getNumberOfFrames();i++){
            duration += ani.getDuration(i);
        }
        handler.postDelayed(runnable, duration);
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private void updateViewPosition() {
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
        windowManager.updateViewLayout(this, mParams);
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }
}