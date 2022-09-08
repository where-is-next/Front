package com.win.front.application;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KaKaoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        KakaoSdk.init(this,"56b9c3f9c686b60e73604a4665ddff25");
    }
}
