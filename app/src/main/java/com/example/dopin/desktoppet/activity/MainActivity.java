package com.example.dopin.desktoppet.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dopin.desktoppet.R;
import com.example.dopin.desktoppet.entity.Pet;
import com.example.dopin.desktoppet.event.eventConnect;
import com.example.dopin.desktoppet.event.eventDisconnect;
import com.example.dopin.desktoppet.fragment.ClockFragment;
import com.example.dopin.desktoppet.fragment.PreviewModelFragment;
import com.example.dopin.desktoppet.fragment.SettingFragment;
import com.example.dopin.desktoppet.fragment.addClockFragment;
import com.example.dopin.desktoppet.service.FloatWindowService;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class MainActivity extends AppCompatActivity
{
   public final static int previewFragment=1,clockListFragment=2,settingFragment=3,addClockFragment=4;
    public static int curFragment=1;
   Button settingButton;
    Button previewFragmentButton;
    Button clockButton;
    FrameLayout frameLayout;
    private FloatWindowService bindService;
    public static boolean isConnected=false;
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            FloatWindowService.MyBinder binder = (FloatWindowService.MyBinder)service;
            bindService = binder.getService();
        }
    };
    /**
     * 换宠物
     */
    public void changePet(String style){
        if(FloatWindowService.isCreated==false){
            Toast.makeText(this,"请先开启宠物",Toast.LENGTH_SHORT).show();
            return;
        }
        bindService.changePet(style);
    }
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void receiveMessage(eventConnect event) {
        Intent serviceIntent=new Intent(this,FloatWindowService.class);
        bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE);
    }
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void receiveMessage(eventDisconnect event) {
        unbindService(conn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_page);

        EventBus.getDefault().register(this);
        /**
         * 如果service已经创建，就绑定service
         */
        Intent serviceIntent = new Intent(this,FloatWindowService.class);
        if(FloatWindowService.isCreated==true){
            bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE);
            isConnected=true;
        }

        adaptScreen();
        initView();

        PreviewModelFragment previewModelFragment=new PreviewModelFragment();
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.mainpage_framelayout, previewModelFragment);
        transaction.commit();

        Rect outRect1 = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);

    }
   private void initView()
   {
       frameLayout=(FrameLayout)findViewById(R.id.mainpage_framelayout);

       settingButton=(Button)findViewById(R.id.mainpage_setting_button);
       settingButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               SettingFragment settingFragment = new SettingFragment();
               FragmentManager fragmentManager = getSupportFragmentManager();
               FragmentTransaction transaction = fragmentManager.beginTransaction();
               transaction.replace(R.id.mainpage_framelayout, settingFragment);
               transaction.commit();
           }
       });

       previewFragmentButton=(Button)findViewById(R.id.mainpage_preview_modal_button);
       previewFragmentButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               PreviewModelFragment previewModelFragment = new PreviewModelFragment();
               FragmentManager fragmentManager = getSupportFragmentManager();
               FragmentTransaction transaction = fragmentManager.beginTransaction();
               transaction.replace(R.id.mainpage_framelayout, previewModelFragment);
               transaction.commit();
           }
       });

        clockButton=(Button)findViewById(R.id.mainpage_clock_button);
       clockButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               ClockFragment clockFragment = new ClockFragment();
               FragmentManager fragmentManager = getSupportFragmentManager();
               FragmentTransaction transaction = fragmentManager.beginTransaction();
               transaction.replace(R.id.mainpage_framelayout, clockFragment);
               transaction.commit();
           }
       });

   }

    public void switchFragment(int target)
    {
        switch (target)
        {
            case previewFragment:
            {
                PreviewModelFragment previewModelFragment=new PreviewModelFragment();
                FragmentManager fragmentManager=getSupportFragmentManager();
                FragmentTransaction transaction=fragmentManager.beginTransaction();
                transaction.replace(R.id.mainpage_framelayout,previewModelFragment);
                transaction.commit();
            }break;
            case clockListFragment:
            {
                ClockFragment clockFragment=new ClockFragment();
                FragmentManager fragmentManager=getSupportFragmentManager();
                FragmentTransaction transaction=fragmentManager.beginTransaction();
                transaction.replace(R.id.mainpage_framelayout,clockFragment);
                transaction.commit();
            }break;
            case settingFragment:
            {
                SettingFragment settingFragment=new SettingFragment();
                FragmentManager fragmentManager=getSupportFragmentManager();
                FragmentTransaction transaction=fragmentManager.beginTransaction();
                transaction.replace(R.id.mainpage_framelayout,settingFragment);
                transaction.commit();
            }break;
            case addClockFragment:
            {
                com.example.dopin.desktoppet.fragment.addClockFragment previewModelFragment=new addClockFragment();
                FragmentManager fragmentManager=getSupportFragmentManager();
                FragmentTransaction transaction=fragmentManager.beginTransaction();
                transaction.replace(R.id.mainpage_framelayout,previewModelFragment);
                transaction.commit();
            }break;

        }
    }

    //负责为下方三个按钮的宽高适配，然后计算出显示部分的宽高，也就是碎片容器的宽高
    private void adaptScreen()
    {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //应用区域
        Rect outRect1 = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);

        int statusBarHeight = dm.heightPixels - outRect1.height();  //状态栏高度=屏幕高度-应用区域高度

        int outRectWidth=outRect1.width();
        int outRectHeight=outRect1.height();

        settingButton=(Button)findViewById(R.id.mainpage_setting_button);
        previewFragmentButton=(Button)findViewById(R.id.mainpage_preview_modal_button);
        clockButton=(Button)findViewById(R.id.mainpage_clock_button);
        frameLayout=(FrameLayout)findViewById(R.id.mainpage_framelayout) ;


        int density= (int)getResources().getDisplayMetrics().density;


        int statusBarHeight1 = -1;
//获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        int buttonHeight=60 * density;
        int toolbarHeight=60*density;//toolbar没有显示，所以不用减
        int frameLayoutHeight=outRectHeight-buttonHeight-statusBarHeight1;

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(isConnected){
            unbindService(conn);
        }
        isConnected=false;
        EventBus.getDefault().unregister(this);
    }
    @Override
    public void onBackPressed() {
        if(curFragment==previewFragment) {
            finish();
        }
        else if(curFragment==clockListFragment){
            switchFragment(previewFragment);
        }else if(curFragment==addClockFragment){
            switchFragment(clockListFragment);
        }else if(curFragment==settingFragment){
            switchFragment(previewFragment);
        }
    }
}
