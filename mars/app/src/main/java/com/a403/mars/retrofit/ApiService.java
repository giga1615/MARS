package com.a403.mars.retrofit;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Akshay Raj on 05/02/18.
 * akshay@snowcorp.org
 * www.snowcorp.org
 */

public interface ApiService {
    String BASE_URL = "http://k4a403.p.ssafy.io:8000/";

    @Multipart
    @POST("api/capsule/create")
    Call<ResponseBody> uploadMultiple(
            @Part("jwt") RequestBody jwt,
            @Part("title") RequestBody title,
            @Part("music_title") RequestBody music_title,
            @Part("memo") RequestBody memo,
            @Part("gps_x") RequestBody gps_x,
            @Part("gps_y") RequestBody gps_y,
            @Part("open_date") RequestBody open_date,
            @Part("address") RequestBody address,
            @Part("capsule_friends") RequestBody capsule_friends,
            @Part MultipartBody.Part[] files);
}
