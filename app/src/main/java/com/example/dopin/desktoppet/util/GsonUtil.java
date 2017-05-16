package com.example.dopin.desktoppet.util;

import com.google.gson.Gson;

/**
 * Created by dopin on 2017/5/10.
 */
public class GsonUtil {
    private static Gson gson=new Gson();
    public static Object stringToObject( String json , Class classOfT){
        return  gson.fromJson( json , classOfT ) ;
    }
    public static <T> String objectToString(T object) {
        return gson.toJson(object);
    }

}
