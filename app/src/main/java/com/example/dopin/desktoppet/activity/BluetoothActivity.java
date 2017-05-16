package com.example.dopin.desktoppet.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dopin.desktoppet.R;
import com.example.dopin.desktoppet.entity.Pet;
import com.example.dopin.desktoppet.event.eventCancel;
import com.example.dopin.desktoppet.event.eventInfo;
import com.example.dopin.desktoppet.event.eventRefuse;
import com.example.dopin.desktoppet.jsonBean.JsonPet;
import com.example.dopin.desktoppet.service.BluetoothService;
import com.example.dopin.desktoppet.util.GsonUtil;
import com.example.dopin.desktoppet.util.ServerSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class BluetoothActivity extends Activity {

    private BluetoothService bluetoothService;
    BroadcastReceiver bluetoothReceiver;
    List<Map<String,String>> deviceData=new ArrayList<>();
    ListView deviceList;
    SimpleAdapter adapter;
    ImageView imageView;
    TextView textAge;
    TextView textName;
    TextView textSex;
    TextView textSignature;
    LinearLayout layout;
    TextView state;
    Button btnCloseCon;
    ProgressDialog waitingDialog;
    boolean isRequester;
    Handler handler;
    Runnable runnable;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            BluetoothService.MyBinder binder = (BluetoothService.MyBinder)service;
            bluetoothService = binder.getService();

            bluetoothService.setServerSocket();
            bluetoothService.startAccept();
            bluetoothService.setBoundList(deviceData);
            adapter.notifyDataSetChanged();

            if(bluetoothService.getCurJsonPet()!=null){
                setLayoutValue(bluetoothService.getCurJsonPet());
            }
        }
    };
    private String getAddressFromPhoneName(String name){
        for(Map<String,String> map:deviceData){
            if(map.get("name").equals(name)) return map.get("address");
        }
        return null;
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void receiveMessage(eventCancel event) {
        showToast(BluetoothService.connectName + "关闭了与你的连接");
        BluetoothService.connectAddress=null;
        BluetoothService.connectName=null;
        bluetoothService.setCurJsonPet(null);
        bluetoothService.closeClientSocket();

        setLayoutEmpty();
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void receiveMessage(eventRefuse event) {
        showToast("对方拒绝了你的请求");
        isRequester=false;
        if(waitingDialog.isShowing()) waitingDialog.dismiss();
        bluetoothService.closeClientSocket();
        handler.removeCallbacks(runnable);

    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void receiveMessage(eventInfo event) {
        JsonPet jsonPet=event.getJsonPet();
        /**
         * * 请求发送者收到回复，完成配对
         * */
        if(isRequester) {
            isRequester=false;
            setLayoutValue(jsonPet);
            waitingDialog.dismiss();

            BluetoothService.connectAddress=BluetoothService.tryAddress;
            BluetoothService.connectName=jsonPet.getPhoneName();
            bluetoothService.setCurJsonPet(jsonPet);

        }else{
            /**
             * 请求接受者收到信息
             */
            Map<String,String> map=new HashMap();
            String address=getAddressFromPhoneName(jsonPet.getPhoneName());//获取对方的mac地址
            map.put("name", jsonPet.getPhoneName());//放入对方的名字（用与显示对话框）
            map.put("address", address);//放入对方mac地址

            showAcceptDialog(map, jsonPet);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bluetooth);

        EventBus.getDefault().register(this);
        handler=new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {
                if (waitingDialog.isShowing()) {
                    bluetoothService.closeClientSocket();

                    waitingDialog.dismiss();
                    showToast("连接超时");

                }
            }
        };

        deviceList = (ListView) findViewById(R.id.list_device);
        imageView=(ImageView)findViewById(R.id.pet_image);
        layout=(LinearLayout)findViewById(R.id.pet_layout);
        textAge=(TextView)findViewById(R.id.age);
        textName=(TextView)findViewById(R.id.name);
        textSex=(TextView)findViewById(R.id.sex);
        textSignature=(TextView)findViewById(R.id.signature);
        state=(TextView)findViewById(R.id.state);

        btnCloseCon=(Button)findViewById(R.id.btn_close_con);
        btnCloseCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothService.connectAddress == null) showToast("当前没有配对");
                else {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", BluetoothService.connectName);
                    map.put("address", BluetoothService.connectAddress);
                    showCloseConDialog(map);
                }
            }
        });
        Intent serviceIntent = new Intent(BluetoothActivity.this,BluetoothService.class);
        if(bluetoothService.isCreated == false) {
            startService(serviceIntent);
        }
        bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE);

        adapter=new SimpleAdapter(this,deviceData, R.layout.device_item,
                new String[]{"name", "address"},
                new int[]{R.id.name, R.id.address});

        deviceList.setAdapter(adapter);
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, String> map = deviceData.get(i);
                if (BluetoothService.connectAddress == null) showRequestDialog(map);
                else if (map.get("address").equals(BluetoothService.connectAddress)) {
                    showToast("已和此设备配对");
                } else {
                    showToast("请先取消原有配对");
                }
            }

        });
        setWaitingDialog();
        setReceiver();
    }

    private void showRequestDialog(final Map<String,String> map){

        String name=map.get("name");
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this);
        normalDialog.setTitle("与"+name+"匹配宠物");
        normalDialog.setMessage("点击确定匹配宠物");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        waitingDialog.show();
                        dialog.dismiss();
                        /**
                         * 传入对方的地址，我方的名字（用于对方获取我方的地址）
                         */
                        bluetoothService.sendInfo(map.get("address"));

                        BluetoothService.tryAddress=map.get("address");

                        isRequester=true;

                        handler.postDelayed(runnable, 5000);
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        normalDialog.show();
    }
    private void showToast(String text){
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }
    private void showAcceptDialog(final Map<String,String> map,final JsonPet jsonPet){
        /**
         * map中是对方的地址和名字
         */
        final String name=map.get("name");

        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this);
        normalDialog.setTitle(name + "的匹配请求");
        normalDialog.setMessage("点击确定匹配宠物");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        /**
                         * 传入对方的地址
                         */
                        bluetoothService.sendInfo(map.get("address"));

                        BluetoothService.connectAddress=map.get("address");
                        BluetoothService.connectName=name;
                        bluetoothService.setCurJsonPet(jsonPet);

                        setLayoutValue(jsonPet);
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        bluetoothService.sendRefuse(map.get("address"));

                    }
                });

        normalDialog.show();
    }

    private void showCloseConDialog(final Map<String,String> map){
        final String name=map.get("name");

        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this);
        normalDialog.setTitle("当前连接：" + name);
        normalDialog.setMessage("点击确定关闭连接");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        /**
                         * 传入cancel取消连接
                         */
                        bluetoothService.sendCancel();

                        BluetoothService.connectAddress=null;
                        BluetoothService.connectName=null;
                        bluetoothService.setCurJsonPet(null);

                        setLayoutEmpty();
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        normalDialog.show();
    }
    private void setLayoutValue(JsonPet jsonPet){
        layout.setVisibility(View.VISIBLE);
        textAge.setText("年龄：" + jsonPet.getAge());
        textName.setText("姓名：" + jsonPet.getName());
        textSex.setText("性别："+jsonPet.getSex());
        textSignature.setText("个性签名：" + jsonPet.getSignature());
        state.setText("已配对：" + jsonPet.getPhoneName());

        Pet pet=new Pet(jsonPet);

        AnimationDrawable ani=pet.getDefaultAni();
        imageView.setImageDrawable(ani);
        ani.start();

    }

    private void setLayoutEmpty() {
        layout.setVisibility(View.GONE);
        state.setText("无配对宠物");

    }
    private void setWaitingDialog() {
    /* 等待Dialog具有屏蔽其他控件的交互能力
     * @setCancelable 为使屏幕不可点击，设置为不可取消(false)
     * 下载等事件完成后，主动调用函数关闭该Dialog
     */
        waitingDialog=
                new ProgressDialog(this);
        waitingDialog.setTitle("正在连接");
        waitingDialog.setMessage("请等待...");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
    }

    private void setReceiver(){
        IntentFilter filter = new IntentFilter();
        //设备连接状态改变
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //蓝牙设备状态改变
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        bluetoothReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {

                    deviceData.clear();
                    bluetoothService.setBoundList(deviceData);
                    adapter.notifyDataSetChanged();

                } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {

                }
            }
        };
        registerReceiver(bluetoothReceiver, filter);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbindService(conn);
        unregisterReceiver(bluetoothReceiver);
    }
}
