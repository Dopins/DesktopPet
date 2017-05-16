package com.example.dopin.desktoppet.entity;

import java.util.Date;

/**
 * Created by mpi on 2017/5/12.
 */

public class Clock
{
   private String event;

    private Date time;

    public String getEvent() {
        return event;
    }

    public Date getTime() {
        return time;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Clock(String event, Date time)
    {
        this.event = event;
        this.time = time;
    }
}
