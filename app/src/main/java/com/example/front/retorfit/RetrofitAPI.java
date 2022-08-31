package com.example.front.retorfit;

import com.example.front.domain.Location;
import com.example.front.domain.SocialUser;
import com.example.front.dto.ChangePwDTO;
import com.example.front.dto.SignInDTO;
import com.example.front.dto.SignUpDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

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

    // 현재 서비스에 등록된 관광지 리스트 가져오는 API
    @GET("location_list")
    Call<List<Location>> getLocationResponse();
}