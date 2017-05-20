package com.example.dopin.desktoppet.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.Log;
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

    //0，1,2 分别代表窗口位置位于贴左边，不贴边，贴右边。
    private int position=2;

    private boolean movable =true;

    private ImageView animationIV;

    private  LinearLayout buttonLayout;
    private  LinearLayout textLayout;

    private TextView noticeText;

    private boolean isMove=false;

    private Handler handler;

    private Runnable runnable;

    private Pet pet;

    private int imageWidth;

    private int screenWidth;

    public FloatWindowView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_window_small, this);

        View view =findViewById(R.id.small_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;

        animationIV = (ImageView) findViewById(R.id.animationIV);
        buttonLayout=(LinearLayout)findViewById(R.id.button_layout);
        textLayout=(LinearLayout)findViewById(R.id.text_layout);
        noticeText=(TextView)findViewById(R.id.text);

        imageWidth=animationIV.getLayoutParams().width;

        Button btnOpenActivity =(Button)findViewById(R.id.btn_open_activity);
        Button btnBack =(Button)findViewById(R.id.btn_back);
        Button btnTouch =(Button)findViewById(R.id.btn_touch);

        screenWidth = windowManager.getDefaultDisplay().getWidth();

        btnOpenActivity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);

                buttonLayout.setVisibility(GONE);
            }
        });

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
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
                setDefault();
            }
        });

        handler = new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {
                setDefault();
            }
        };

        this.pet = FloatWindowService.curPet;
        setDefault();
    }
    public void notice(String text){
        /**
         * 取消touchAni设置的postDelay
         */
        handler.removeCallbacks(runnable);
        textLayout.setVisibility(VISIBLE);
        noticeText.setText(text);
        noticeText.setBackgroundResource(R.drawable.notice_text_shape);
        buttonLayout.setVisibility(GONE);
        noticeAni();
    }
    public void alarm(String text){
        handler.removeCallbacks(runnable);//取消
        textLayout.setVisibility(VISIBLE);
        noticeText.setText("备忘: "+text);
        noticeText.setBackgroundResource(R.drawable.alarm_text_shape);
        buttonLayout.setVisibility(GONE);
        alarmAni();
    }
    public void changePet(){
        pet=FloatWindowService.curPet;
        setDefault();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(movable ==false||buttonLayout.getVisibility()==View.VISIBLE) {
            return true;
        }
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

                /**
                 * 不能在action_move的时候更新xInView，因为小窗口的位置更新依靠的是，手指到屏幕左上角的位置减去手指到view
                 * 左上角的位置，即event.getRawX()-event.getX()，可以得到view应该的位置，用来设置位置参数。
                 * 如果在这里更新xInView，这时候event.getX()相对的是view原本的位置，而不是新的，因为还没更新。
                 * 这时候view的位置是不变的。
                 * 放在更新后面就可以
                 */
                // 手指移动的时候更新小悬浮窗的位置
                updateViewPosition();
                moveAni();
                isMove=true;
                break;
            case MotionEvent.ACTION_UP:
                /**
                 * 获取手指离开时，手指位置到小窗口左上角的距离
                 */
                xInView = event.getX();
                yInView = event.getY();
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {

                    buttonLayout.setVisibility(VISIBLE);
                }else{
                    /**
                     *手指到屏幕左上角距离-手指到小窗口左上角距离=0
                     * 就是小窗口贴左边的时候
                     */
                    if((xInScreen - xInView)==0) {
                        position=0;
                        /**
                         * 手指到屏幕左上角距离-手指到小窗口左上角距离+imageView大小=屏幕宽度
                         * 就是贴右边的时候
                         */
                    } else if((xInScreen-xInView+imageWidth)==screenWidth){
                        position=2;
                    }else{
                        position=1;
                    }
                    updateViewPosition();
                    setDefault();
                }
                isMove=false;
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
    private void setDefault(){
        handler.removeCallbacks(runnable);//取消
        buttonLayout.setVisibility(GONE);
        textLayout.setVisibility(GONE);
        defaultAni();
    }
    private void defaultAni(){

        AnimationDrawable ani;
        if(position==0){
            ani=pet.getHiddenLeftAni();
        }else if(position==2){
            ani = pet.getHiddenRightAni();
        }else{
            ani=pet.getDefaultAni();
        }

        animationIV.setImageDrawable(ani);
        ani.start();

        movable =true;
    }
    private void alarmAni() {
        AnimationDrawable ani=pet.getAlarmAni();
        animationIV.setImageDrawable(ani);
        ani.start();
        movable =false;
    }
    private void noticeAni() {
        AnimationDrawable ani=pet.getNoticeAni();
        animationIV.setImageDrawable(ani);
        ani.start();
        movable =false;
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