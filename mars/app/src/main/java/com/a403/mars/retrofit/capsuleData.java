package com.a403.mars.retrofit;

import com.google.gson.annotations.SerializedName;

import java.io.File;

public class capsuleData {

    @SerializedName("jwt")
    private String jwt;

    @SerializedName("title")
    private String title;

    @SerializedName("music_title")
    private String music_title;

    @SerializedName("memo")
    private String memo;

    @SerializedName("gps_x")
    private double gps_x;

    @SerializedName("gps_y")
    private double gps_y;

    @SerializedName("open_date")
    private String open_date;

    @SerializedName("address")
    private String address;

    @SerializedName("capsule_friends")
    private String capsule_friends;

    @SerializedName("files")
    private File[] files;
}
