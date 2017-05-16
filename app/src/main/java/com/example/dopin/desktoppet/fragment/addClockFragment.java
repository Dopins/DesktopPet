package com.example.dopin.desktoppet.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.example.dopin.desktoppet.R;
import com.example.dopin.desktoppet.activity.MainActivity;
import com.example.dopin.desktoppet.broadcastReceiver.AlarmBroadcastReceiver;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mpi on 2017/5/12.
 */

public class addClockFragment extends Fragment
{
    MainActivity mainPage;
    View view;
    int clockHour;
    int clockMinute;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view=inflater.inflate(R.layout.add_clock_fragment_layout,container,false);
        MainActivity.curFragment=MainActivity.addClockFragment;
        TimePicker timePicker=(TimePicker)view.findViewById(R.id.add_clock_timepicker);
        timePicker.setIs24HourView(true);
         clockHour=timePicker.getCurrentHour();//默认是当前时间
        clockMinute=timePicker.getCurrentMinute();
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                clockHour=hourOfDay;
                clockMinute=minute;
            }
        });
        mainPage=(MainActivity)getActivity();//切换fragment
        Toolbar addClockToolbar=(Toolbar)view.findViewById(R.id.add_clock_toolbar);
        final EditText action=(EditText) view.findViewById(R.id.clock_event_input);
        addClockToolbar.setTitle("增加闹钟");
        addClockToolbar.inflateMenu(R.menu.add_clock_fragment_menu);
        addClockToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.save_clock) {
                    // 获取日历对象。
                    Calendar cal = Calendar.getInstance();
                    // 获取年月日时分秒的信息
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH) ;
                    Date date=new Date();
                    //date的year是从1900年以来的年数
                    date.setYear(year-1900);
                    date.setMonth(month);
                    date.setDate(day);
                    date.setHours(clockHour);
                    date.setMinutes(clockMinute);
                    date.setSeconds(0);
                    //String dateString = "" + year + "-" + month + "-" + day + " " + clockHour + ":" + clockMinute + ":" + "00";
                    String actionText = action.getText().toString();

                    setAlarm(date, actionText);

                    mainPage.switchFragment(mainPage.clockListFragment);
                }
                return true;
            }
        });
        return view;
    }
    private void setAlarm(Date date,String note){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        new AlarmBroadcastReceiver(getContext()).setAlarm(note, date);

        String dateString= simpleDateFormat.format(date);
        SharedPreferences sp = getContext().getSharedPreferences("alarm", Context.MODE_PRIVATE);
        sp.edit().putString(dateString, note).commit();

        Log.w("tag", dateString);
    }
}
