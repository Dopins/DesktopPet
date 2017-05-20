package com.example.dopin.desktoppet.util;

/**
 * Created by dopin on 2017/5/9.
 */
import android.app.Application;

/**
 * 全局获取context
 */
public class ContextUtil extends Application {
    private static ContextUtil instance;

    public static ContextUtil getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        instance = this;
    }
}