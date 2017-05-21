package com.example.dopin.desktoppet.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.dopin.desktoppet.R;
import com.example.dopin.desktoppet.activity.MainActivity;
import com.example.dopin.desktoppet.adapter.ClockAdapter;
import com.example.dopin.desktoppet.entity.Clock;
import com.example.dopin.desktoppet.service.BluetoothService;
import com.example.dopin.desktoppet.util.AlarmUtil;
import java.util.List;
import java.util.Map;

/**
 * Created by mpi on 2017/5/6.
 */

public class ClockFragment extends Fragment {

    MainActivity mainActivity;
    RecyclerView recyclerView;
    List<Clock> clockList;
    ClockAdapter clockAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.layout_clock,container,false);
        MainActivity.curFragment=MainActivity.clockListFragment;
        recyclerView=(RecyclerView)view.findViewById(R.id.clock_item_list);
        mainActivity =(MainActivity)getActivity();
        LinearLayoutManager linearLayout=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayout);

        clockList=AlarmUtil.getClockList();
        clockAdapter=new ClockAdapter(clockList);

        clockAdapter.setOnItemClickListener(new ClockAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                showRemoveAlarmDialog(position);
            }

        });

        recyclerView.setAdapter(clockAdapter);

        FloatingActionButton addClock=(FloatingActionButton)view.findViewById(R.id.add_clock);

        addClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.switchFragment(mainActivity.addClockFragment);
            }
        });

        return view;
    }
    private void showRemoveAlarmDialog(final int position){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(getContext());
        normalDialog.setTitle("确定删除此闹钟?");
        normalDialog.setMessage("点击确定删除");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeAlarm(position);
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        normalDialog.show();
    }
    private void removeAlarm(int position){
        Clock clock=clockList.get(position);
        AlarmUtil.removeAlarm(clock.getTime());

        clockList.remove(position);
        clockAdapter.notifyDataSetChanged();

    }

}
