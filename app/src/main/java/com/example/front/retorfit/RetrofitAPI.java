package com.example.front.retorfit;

import com.example.front.domain.SocialUser;
import com.example.front.domain.LoginUser;
import com.example.front.dto.ChangePwDTO;
import com.example.front.dto.SignInDTO;
import com.example.front.dto.SignUpDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitAPI {
    //@통신 방식("통신 API명")

    // 일반 회원 로그인 API
    @POST("user_login")
    Call<Boolean> getLoginResponse(@Body SignInDTO signInDTO); // Post : <> = 받을 타입, @body = 보낼 타입

    // 소셜 회원 회원가입 및 로그인 API
    @POST("user_login")
    Call<Boolean> getSocialUserResponse(@Body SocialUser socialUser);

    // 일반 회원 회원가입 API
    @POST("user_sign_up")
    Call<Boolean> getSignUpResponse(@Body SignUpDTO signUpDTO);

    // 아이디 찾기 API
    @GET("user_search_id/{phoneNum}")
    Call<String> getIDResponse(@Path("phoneNum") String phoneNum);

    // 비밀번호 찾기 API
    @GET("user_search_pw/{id}/{phoneNum}")
    Call<Boolean> getPwResponse(@Path("id") String id,
                                @Path("phoneNum") String phoneNum);

    // 비밀번호 변경 API
    @POST("user_change_pw")
    Call<Boolean> changePwResponse(@Body ChangePwDTO changePwDTO);
}