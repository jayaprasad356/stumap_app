package com.example.stumap.model;

public class User {
    String id,name,mobile,lotitude,longtidue;
    public User(){

    }

    public User(String id, String name, String mobile, String lotitude, String longtidue) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.lotitude = lotitude;
        this.longtidue = longtidue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLotitude() {
        return lotitude;
    }

    public void setLotitude(String lotitude) {
        this.lotitude = lotitude;
    }

    public String getLongtidue() {
        return longtidue;
    }

    public void setLongtidue(String longtidue) {
        this.longtidue = longtidue;
    }
}
