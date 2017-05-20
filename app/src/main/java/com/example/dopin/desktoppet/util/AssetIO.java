package com.example.dopin.desktoppet.util;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dopin on 2017/5/9.
 */
public class AssetIO {
    static AssetManager assetManager=ContextUtil.getInstance().getAssets();

    /**
     * 获取str目录下所有文件名
     * @param str
     * @return
     */
    public static String[] getStringList(String str){
        String[] result=null;
        try {
            result = assetManager.list(str);
        }catch(IOException e){

        }
        return result;
    }

    /**
     * 从assert文件夹中通过绝对路径的文件名（文件是图片文件，包括后缀名）得到bitmap
     * @param filename
     * @return
     */
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
