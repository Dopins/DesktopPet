package com.example.dopin.desktoppet.jsonBean;

/**
 * Created by dopin on 2017/5/10.
 */
public class JsonPet {
     String name;
     String age;
     String sex;
     String signature;
     String style;

    String phoneName;
    String phoneAddress;

    public JsonPet(String keyword){
        this.setStyle(keyword);
        this.setSex("未设置");
        this.setName("未设置");
        this.setAge("未设置");
        this.setSignature("未设置");
        this.setPhoneName("");
        this.setPhoneAddress("");
    }

    public void setName(String name){
        this.name=name;
    }
    public void setAge(String age){
        this.age=age;
    }
    public void setSex(String sex){
        this.sex=sex;
    }
    public void setSignature(String signature){
        this.signature=signature;
    }
    public void setStyle(String style){
        this.style=style;
    }
    public void setPhoneName(String phoneName){
        this.phoneName=phoneName;
    }
    public void setPhoneAddress(String phoneAddress){
        this.phoneAddress=phoneAddress;
    }
    public String getName(){
        return name;
    }
    public String getAge(){
        return age;
    }
    public String getSex(){
        return sex;
    }
    public String getSignature(){
        return signature;
    }
    public String getStyle(){
        return style;
    }
    public String getPhoneName(){
        return phoneName;
    }
    public String getPhoneAddress(){
        return phoneAddress;
    }
}
