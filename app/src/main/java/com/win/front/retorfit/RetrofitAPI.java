package com.win.front.retorfit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.win.front.domain.Location;
import com.win.front.domain.SocialUser;
import com.win.front.dto.AddStampDTO;
import com.win.front.dto.AllPostDTO;
import com.win.front.dto.ChangePwDTO;
import com.win.front.dto.PostDTO;
import com.win.front.dto.SignInDTO;
import com.win.front.dto.SignUpDTO;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
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

    // QR 코드 스캔 시 서버에 등록되어 있는 여행지 QR 코드 인지 확인하는 API
    @POST("confirm_location")
    Call<Boolean> getConfrimLocationResponse(@Body String qrRequest);

    // QR 코드 스캔 성공 시 스탬프를 추가하는 API
    @POST("add_stamp")
    Call<Boolean> getAddStampResponse(@Body AddStampDTO addStampDTO);

    // 닉네임을 반환하는 API
    @GET("response_nickname/{id}")
    Call<String> getNickNameResponse(@Path("id") String id);

    // 포스트를 등록하는 API ( Create )
    @POST("add_post")
    Call<Boolean> addPostMethod(@Body PostDTO postDTO);

    // 존재하는 포스트가 있는지 확인하는 API
    @GET("post_check")
    Call<Boolean> isPostResponse();

    // 모든 포스트를 가져오는 API
    @GET("all_post")
    Call<JsonArray> getAllPostResponse();

    // 현재 로그인한 사용자의 포스트를 가져오는 API
    @GET("my_post/{id}")
    Call<JsonArray> getMyPostResponse(@Path("id") String id);
}