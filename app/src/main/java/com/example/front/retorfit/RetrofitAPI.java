package com.example.front.retorfit;

import com.example.front.domain.SocialUser;
import com.example.front.domain.LoginUser;
import com.example.front.dto.SignInDTO;
import com.example.front.dto.SignUpDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

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

    // 일반회원 가입 시 이메일 인증 요청 API : 이메일 전송
    @POST("email_service/mail_send")
    Call<String> getEmailAuthCodeSend(@Body String email);

    // 일반회원 가입 시 이메일 인증 요청 API : 인증번호 확인
    @POST("email_service/mail_confirm")
    Call<Boolean> getEmailAuthCodeConfirm(@Body String code);
}