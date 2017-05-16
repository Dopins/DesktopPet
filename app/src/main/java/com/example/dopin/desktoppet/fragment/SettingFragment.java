package com.example.dopin.desktoppet.fragment;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dopin.desktoppet.R;
import com.example.dopin.desktoppet.activity.BluetoothActivity;
import com.example.dopin.desktoppet.activity.MainActivity;
import com.example.dopin.desktoppet.entity.Pet;
import com.example.dopin.desktoppet.event.eventConnect;
import com.example.dopin.desktoppet.event.eventDisconnect;
import com.example.dopin.desktoppet.service.FloatWindowService;

import de.greenrobot.event.EventBus;

/**
 * Created by mpi on 2017/5/6.
 */

public class SettingFragment extends Fragment
{
    //名字,显隐
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.layout_setting,container,false);
        MainActivity.curFragment=MainActivity.settingFragment;
        Switch isShowPet=(Switch)view.findViewById(R.id.is_show_pet_switch);
        if(FloatWindowService.isCreated) {
            isShowPet.setChecked(true);
        }
        isShowPet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                /**
                 * checked是change过的
                 */
                if (isChecked == false) {
                    stopPet();
                } else {
                    startPet();
                }
            }
        });
        Button btnBluetooth=(Button)view.findViewById(R.id.btn_bluetooth);

        final TextView petName=(TextView)view.findViewById(R.id.setting_name);
        final TextView petSex=(TextView)view.findViewById(R.id.setting_sex);
        final TextView petAge=(TextView)view.findViewById(R.id.setting_age);
        final TextView petSignature=(TextView)view.findViewById(R.id.setting_signature);

        petName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog("名字","name",petName);
            }
        });
        petSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog("性别","sex",petSex);
            }
        });
        petAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog("年龄", "age", petAge);
            }
        });
        petSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog("个性签名","signature",petSignature);
            }
        });
        if(FloatWindowService.curPet!=null){
            Pet pet=FloatWindowService.curPet;
            petName.setText("姓名: "+pet.getName());
            petAge.setText("年龄: "+pet.getAge());
            petSex.setText("性别: "+pet.getSex());
            petSignature.setText("个性签名: "+pet.getSignature());
        }

        btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FloatWindowService.isCreated==false){
                    showToast("需要先开启宠物");
                }else if(!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    //弹出对话框提示用户后打开
                    Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(enabler);
                }else{
                    Intent intent = new Intent(getActivity(), BluetoothActivity.class);
                    startActivity(intent);
                }
            }
        });

        return view;
    }
    private void showToast(String text){
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }
    private void showInputDialog(final String title,final String key,final TextView textView){
        final EditText editText = new EditText(getActivity());
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(getActivity());
        inputDialog.setTitle(title).setView(editText)
        .setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = editText.getText().toString();
                        setPetInfo(key, value);
                        textView.setText(title + ": " + value);
                    }
                })
                .show();
    }

    /**
     * 使用SharedPreferences存储,有时间可以存到后台。
     * @param key
     * @param value
     */
    private void setPetInfo(String key,String value){
        String style=FloatWindowService.curPet.getJsonPet().getStyle();
        SharedPreferences sp = getContext().getSharedPreferences(style, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();

        if(key.equals("name")){
            FloatWindowService.curPet.setName(value);
        }else if(key.equals("age")){
            FloatWindowService.curPet.setAge(value);
        }else if(key.equals("sex")){
            FloatWindowService.curPet.setSex(value);
        }else{
            FloatWindowService.curPet.setSignature(value);
        }

    }

    public void startPet(){
        if(MainActivity.isConnected==true)return;
        MainActivity.isConnected=true;
        if(FloatWindowService.isCreated==true){
            EventBus.getDefault().post(new eventConnect());
        }else{
            Intent serviceIntent=new Intent(getActivity(),FloatWindowService.class);
            getActivity().startService(serviceIntent);
            EventBus.getDefault().post(new eventConnect());
        }
    }
    public void stopPet(){
        if(MainActivity.isConnected==false) return;
        Intent serviceIntent=new Intent(getActivity(),FloatWindowService.class);
        EventBus.getDefault().post(new eventDisconnect());
        getActivity().stopService(serviceIntent);
        MainActivity.isConnected=false;
        FloatWindowService.isCreated=false;
    }
}
