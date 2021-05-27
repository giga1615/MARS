package com.a403.mars;

public class CapsuleStory {
    String title;
    String created_date;
    String open_date;
    String address;
    String capsule_friends;
    double gps_x;
    double gps_y;
    String jwt;

    public CapsuleStory() {
    }

    public CapsuleStory(String title, String created_date, String open_date, String address, String capsule_friends, double gps_x, double gps_y) {
        this.title = title;
        this.created_date = created_date;
        this.open_date = open_date;
        this.address = address;
        this.capsule_friends = capsule_friends;
        this.gps_x = gps_x;
        this.gps_y = gps_y;
    }

    public CapsuleStory(String title, String created_date, String open_date, String address, String capsule_friends, double gps_x, double gps_y, String jwt) {
        this.title = title;
        this.created_date = created_date;
        this.open_date = open_date;
        this.address = address;
        this.capsule_friends = capsule_friends;
        this.gps_x = gps_x;
        this.gps_y = gps_y;
        this.jwt = jwt;
    }

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

    public double getGps_x() {
        return gps_x;
    }

    public void setGps_x(double gps_x) {
        this.gps_x = gps_x;
    }

    public double getGps_y() {
        return gps_y;
    }

    public void setGps_y(double gps_y) {
        this.gps_y = gps_y;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
