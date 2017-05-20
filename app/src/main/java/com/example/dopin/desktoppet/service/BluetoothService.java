package com.example.dopin.desktoppet.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.example.dopin.desktoppet.jsonBean.JsonPet;
import com.example.dopin.desktoppet.util.ClientSocket;
import com.example.dopin.desktoppet.util.GsonUtil;
import com.example.dopin.desktoppet.util.ServerSocket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 蓝牙连接服务
 */
public class BluetoothService extends Service {
    /**
     * 是否已经创建service
     */
    public static boolean isCreated;
    /**
     * 连接设备的名字
     */
    public static String connectName;
    /**
     * 连接设备的mac地址
     */
    public static String connectAddress;
    /**
     * 尝试连接的mac地址，用于之后设置connectAddress
     */
    public static String tryAddress;
    /**
     * 当前配对的宠物，没有设为null
     */
    private JsonPet curJsonPet;
    /**
     * 客户端socket，用于发送数据
     */
    private ClientSocket clientSocket;
    /**
     * 服务端socket，用于接受数据
     */
    private ServerSocket serverSocket;
    /**
     * 每个设备既是客户端，又是服务端
     */
    BluetoothAdapter mBluetoothAdapter;
    private MyBinder myBinder = new MyBinder();
    public class MyBinder extends Binder {

        public BluetoothService getService(){
            return BluetoothService.this;
        }
    }
    public BluetoothService() {
    }
    public void closeClientSocket(){
        clientSocket.close();
        clientSocket=null;
    }
    public void setCurJsonPet(JsonPet jsonPet){
        this.curJsonPet=jsonPet;
    }
    public JsonPet getCurJsonPet(){
        return curJsonPet;
    }

    /**
     * 由于Android的bug暂时找不到获取自身mac地址的方法，所以传输给对方自己设备的名称，对方通过名称获取自己的mac地址，
     * 然后传输对方的宠物信息过来。
     * @param address
     */
    public void sendInfo(String address){//对方设备的address,我方设备的名字
        String name=mBluetoothAdapter.getName();
        JsonPet jsonPet=FloatWindowService.curPet.getJsonPet();
        jsonPet.setPhoneName(name);
        jsonPet.setPhoneAddress(address);
        String info= GsonUtil.objectToString(jsonPet);
        if(clientSocket ==null) {//没有连接或者新的连接
            clientSocket = connectDevice(address);
        }else{
            closeClientSocket();
            clientSocket = connectDevice(address);
        }
        clientSocket.send(info);
    }
    public void setServerSocket(){
        serverSocket=new ServerSocket(mBluetoothAdapter);

    }
    public void startAccept(){
        serverSocket.startAccept();
    }
    public void sendCancel(){
        clientSocket.send("cancel");

        closeClientSocket();
    }
    public void sendRefuse(String address){
        if(clientSocket ==null) {
            clientSocket = connectDevice(address);
        }
        clientSocket.send("refuse");
    }

    /**
     * 通过mac地址建立socket连接
     * @param address
     * @return
     */
    private ClientSocket connectDevice(String address){
        ClientSocket socket=new ClientSocket(address,mBluetoothAdapter);
        return socket;
    }
    public void setBoundList(List<Map<String,String>> deviceData){
        Set<BluetoothDevice> set= mBluetoothAdapter.getBondedDevices();
        for(BluetoothDevice device:set){
            HashMap<String,String> map=new HashMap();
            map.put("name",device.getName());
            map.put("address",device.getAddress());
            deviceData.add(map);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub

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
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        start();
        return super.onStartCommand(intent,flags,startId);
    }

    public void start() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        isCreated=true;
    }
    @Override
    public void onDestroy() {
        isCreated=false;
        if(serverSocket!=null){
            serverSocket.closeClient();
            serverSocket.closeServer();
        }
        super.onDestroy();
    }
}
