package com.example.dopin.desktoppet.event;

import com.example.dopin.desktoppet.jsonBean.JsonPet;

/**
 * Created by dopin on 2017/5/13.
 */
public class eventInfo {
    private JsonPet jsonPet;
    public eventInfo(JsonPet jsonPet){
        this.jsonPet=jsonPet;
    }
    public JsonPet getJsonPet(){
        return jsonPet;
    }
}
