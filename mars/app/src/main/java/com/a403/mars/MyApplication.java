package com.a403.mars;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Kakao SDK 초기화
        KakaoSdk.init(this, "cb1846cc5c2961cdea45c0b86f93726e");
    }
}
