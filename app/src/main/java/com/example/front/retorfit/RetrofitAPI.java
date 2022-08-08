package com.example.front.retorfit;

import com.example.front.domain.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitAPI {
    //@통신 방식("통신 API명")
    @POST("usertest")
    Call<User> getLoginResponse(@Body User user); // <> : 받을 타입, @body : 보낼 타입
}