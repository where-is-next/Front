package com.win.front.retorfit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static RetrofitClient instance = null;
    private static RetrofitAPI retrofitAPI;

    private RetrofitClient() {
        //retrofit 설정
        //사용하고 있는 서버 BASE 주소
        String baseUrl = "http://192.168.0.31:8080/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitAPI = retrofit.create(RetrofitAPI.class);
    }

    public static RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public static RetrofitAPI getRetrofitInterface() {
        return retrofitAPI;
    }
}
