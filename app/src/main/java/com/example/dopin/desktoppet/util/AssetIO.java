package com.example.dopin.desktoppet.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dopin on 2017/5/9.
 */
public class AssetIO {
    static AssetManager assetManager=ContextUtil.getInstance().getAssets();

    public static String[] getStringList(String str){
        String[] result=null;
        try {
            result = assetManager.list(str);
        }catch(IOException e){

        }
        return result;
    }
    public static Bitmap getBitmap(String filename){
        Bitmap bmp=null;
        try {
            InputStream in=assetManager.open(filename);
            bmp= BitmapFactory.decodeStream(in);
        } catch (Exception e) {

        }
        return bmp;
    }
}
