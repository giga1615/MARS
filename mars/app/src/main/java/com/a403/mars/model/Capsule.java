package com.a403.mars.model;

import java.util.ArrayList;

public class Capsule {
    int no;
    String id;
    String title;
    String music_title;
    String write;
    String photo_url;
    String voice_url;
    String video_url;
    double gps_x;
    double gps_y;
    String created_date;
    String open_date;
    String address;
    String capsule_friends;
    String capsule_friends_by_name;

    public Capsule() { }

    public Capsule(int no, String id, String title, String music_title, String write, String photo_url, String voice_url, String video_url, double gps_x, double gps_y, String created_date, String open_date, String address, String capsule_friends, String capsule_friends_by_name) {
        this.no = no;
        this.id = id;
        this.title = title;
        this.music_title = music_title;
        this.write = write;
        this.photo_url = photo_url;
        this.voice_url = voice_url;
        this.video_url = video_url;
        this.gps_x = gps_x;
        this.gps_y = gps_y;
        this.created_date = created_date;
        this.open_date = open_date;
        this.address = address;
        this.capsule_friends = capsule_friends;
        this.capsule_friends_by_name = capsule_friends_by_name;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMusic_title() {
        return music_title;
    }

    public void setMusic_title(String music_title) {
        this.music_title = music_title;
    }

    public String getWrite() {
        return write;
    }

    public void setWrite(String write) {
        this.write = write;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getVoice_url() {
        return voice_url;
    }

    public void setVoice_url(String voice_url) {
        this.voice_url = voice_url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
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

    public String getCapsule_friends_by_name() {
        return capsule_friends_by_name;
    }

    public void setCapsule_friends_by_name(String capsule_friends_by_name) {
        this.capsule_friends_by_name = capsule_friends_by_name;
    }

    @Override
    public String toString() {
        return "Capsule{" +
                "no=" + no +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", music_title='" + music_title + '\'' +
                ", write='" + write + '\'' +
                ", photo_url='" + photo_url + '\'' +
                ", voice_url='" + voice_url + '\'' +
                ", video_url='" + video_url + '\'' +
                ", gps_x=" + gps_x +
                ", gps_y=" + gps_y +
                ", created_date='" + created_date + '\'' +
                ", open_date='" + open_date + '\'' +
                ", address='" + address + '\'' +
                ", capsule_friends='" + capsule_friends + '\'' +
                ", capsule_friends_by_name='" + capsule_friends_by_name + '\'' +
                '}';
    }
}
