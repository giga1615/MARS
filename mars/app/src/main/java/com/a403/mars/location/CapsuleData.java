package com.a403.mars.location;

import java.io.Serializable;

public class CapsuleData implements Serializable {
    private String title;
    private String created_date;
    private String open_date;
    private String address;
    private String capsule_friends;



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getOpen_date() {
        return open_date;
    }

    public void setOpen_date(String open_date) {
        this.open_date = open_date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCapsule_friends() {
        return capsule_friends;
    }

    public void setCapsule_friends(String capsule_friends) {
        this.capsule_friends = capsule_friends;
    }
}
