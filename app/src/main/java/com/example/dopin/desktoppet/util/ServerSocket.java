package com.example.dopin.desktoppet.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import com.example.dopin.desktoppet.event.eventCancel;
import com.example.dopin.desktoppet.event.eventInfo;
import com.example.dopin.desktoppet.event.eventRefuse;
import com.example.dopin.desktoppet.jsonBean.JsonPet;

import java.io.InputStream;
import java.util.UUID;

import de.greenrobot.event.EventBus;

/**
 * Created by dopin on 2017/5/10.
 */
public class ServerSocket {

    private BluetoothAdapter mBluetoothAdapter;
    private AcceptThread acceptThread;
    private final UUID MY_UUID = UUID
            .fromString("abcd1234-ab12-ab12-ab12-abcdef123456");//和客户端相同的UUID
    private final String NAME = "Bluetooth_Socket";
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket socket;
    private InputStream is;//输入流

    private class AcceptThread extends Thread {
        public AcceptThread() {
            try {
                serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (Exception e) {
            }
        }
        public void run() {

            while (true) {
                try {
                    socket = serverSocket.accept();//阻塞函数
                    is = socket.getInputStream();
                    while (is != null) {
                        byte[] buffer = new byte[1024];
                        int count = is.read(buffer);
                        String info = new String(buffer, 0, count, "utf-8");
                        if(info.equals("cancel")){
                            EventBus.getDefault().post(new eventCancel());
                        }else if(info.equals("refuse")){
                            EventBus.getDefault().post(new eventRefuse());
                        }else{
                            JsonPet jsonPet=(JsonPet)GsonUtil.stringToObject(info,JsonPet.class);
                            EventBus.getDefault().post(new eventInfo(jsonPet));
                        }

                    }
                } catch (Exception e) {
                }
            }

        }
    }
    public void closeServer(){
        try{
            serverSocket.close();
            serverSocket=null;
        }catch (Exception e){

        }
    }
    public void closeClient(){
        try{
            if(is!=null)is.close();
            if(socket!=null&&socket.isConnected())socket.close();
            is=null;
            socket=null;
        }catch (Exception e){

        }
    }
    public ServerSocket(BluetoothAdapter mBluetoothAdapter){
        this.mBluetoothAdapter=mBluetoothAdapter;
    }
    public void startAccept(){
        acceptThread=new AcceptThread();
        acceptThread.start();
    }
}
