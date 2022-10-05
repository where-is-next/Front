package com.win.front.retorfit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.win.front.domain.Comment;
import com.win.front.domain.Location;
import com.win.front.domain.SocialUser;
import com.win.front.domain.Stamp;
import com.win.front.domain.User;
import com.win.front.dto.AddStampDTO;
import com.win.front.dto.AllPostDTO;
import com.win.front.dto.ChangePwDTO;
import com.win.front.dto.CommentDTO;
import com.win.front.dto.CommentDeleteDTO;
import com.win.front.dto.PostDTO;
import com.win.front.dto.PostUpdateDTO;
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

    // 유저의 Enum 값(로그인 종류) 찾는 API
    @GET("user_enum/{id}")
    Call<String> getUserEnumResponse(@Path("id") String id);

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

    // 획득한 스탬프를 반환하는 API
    @GET("user_stamp_list/{id}")
    Call<List<Stamp>> getStampListResponse(@Path("id") String id);

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

    // 모든 댓글을 가져오는 API
    @GET("all_comment/{selected_number}")
    Call<List<Comment>> getAllCommentResponse(@Path("selected_number") String selected_number);

    // 댓글을 등록하는 API
    @POST("add_comment")
    Call<Long> getAddCommentResponse(@Body CommentDTO commentDTO);

    // 댓글을 삭제하는 API
    @POST("delete_comment")
    Call<Boolean> getDeleteCommentResponse(@Body CommentDeleteDTO commentDeleteDTO);

    // 포스트를 삭제하는 API
    @POST("delete_post")
    Call<Boolean> getDeletePostResponse(@Body String post_number);

    // 포스트를 수정하는 API ( update )
    @POST("amend_post")
    Call<Boolean> amendPostMethod(@Body PostUpdateDTO postUpdateDTO);

    // 프로필 수정 : 유저 객체를 가져오는 API
    @GET("profile_user/{id}")
    Call<User> getProfileUserResponse(@Path("id") String id);

    // 프로필 수정 : 닉네임 중복확인 API
    @GET("profile_nickname_confirm/{nickname}")
    Call<Boolean> getProfileNickNameConfirmResponse(@Path("nickname") String nickname);

    // 프로필 수정 : 유저 정보 업데이트
    @POST("profile_user_update")
    Call<Boolean> getUpdateUserProfileResponse(@Body User changeUser);
}