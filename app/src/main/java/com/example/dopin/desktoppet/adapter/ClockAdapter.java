package com.example.dopin.desktoppet.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.dopin.desktoppet.R;
import com.example.dopin.desktoppet.entity.Clock;

import java.util.Date;
import java.util.List;

/**
 * Created by mpi on 2017/5/12.
 */

public class ClockAdapter extends RecyclerView.Adapter<ClockAdapter.ViewHolder>
{
    private List<Clock> clockList;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }


    public ClockAdapter(List<Clock>clockList)
    {
        this.clockList=clockList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.clock_item_layout, parent, false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Clock clock=clockList.get(position);
        holder.eventText.setText(clock.getEvent());
        Date date=clock.getTime();
        String dateString="";

        dateString+=date.getHours();
        if(dateString.length()==1)
            dateString="0"+dateString;
        dateString+=":";
        if(date.getMinutes()>=10)
        dateString+=date.getMinutes();
      else dateString+="0"+date.getMinutes();

        holder.timeText.setText(dateString);
    }

    @Override
    public int getItemCount() {
        return clockList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener
    {
        TextView timeText;
        TextView eventText;
        public ViewHolder(View itemView)
        {
            super(itemView);
            timeText=(TextView)itemView.findViewById(R.id.clock_time);
            eventText=(TextView)itemView.findViewById(R.id.clock_event);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onItemClickListener != null){
                onItemClickListener.onItemClick(v,getPosition());
            }
        }
    }
}
