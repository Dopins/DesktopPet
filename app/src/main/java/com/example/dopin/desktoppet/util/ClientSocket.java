package com.example.dopin.desktoppet.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dopin on 2017/5/10.
 */
public class ClientSocket {

    private final UUID MY_UUID = UUID.fromString("abcd1234-ab12-ab12-ab12-abcdef123456");//随便定义一个
    private BluetoothSocket clientSocket;
    private OutputStream os;//输出流
    private BluetoothDevice device;
    public ClientSocket(String address,BluetoothAdapter mBluetoothAdapter){

        //主动连接蓝牙服务端
            try {
                if (device == null) {
                    //获得远程设备
                    device = mBluetoothAdapter.getRemoteDevice(address);
                }
                if (clientSocket == null) {
                    //创建客户端蓝牙Socket
                    clientSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                    //开始连接蓝牙，如果没有配对则弹出对话框提示我们进行配对
                    clientSocket.connect();
                    //获得输出流（客户端指向服务端输出文本）
                    os = clientSocket.getOutputStream();
                }
            } catch (Exception e) {
            }

    }
    public void close(){
        try{
            os.close();
            clientSocket.close();
            os=null;
            clientSocket=null;
        }catch (Exception e){

        }


    }
    public void send(String str){
        try{
            if (os != null) {
                //往服务端写信息
                os.write(str.getBytes("utf-8"));
            }
        }catch (Exception e){

        }

    }
}