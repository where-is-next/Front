package com.win.front.main;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kakao.sdk.user.UserApiClient;
import com.win.front.R;
import com.win.front.SignIn;
import com.win.front.domain.Location;
import com.win.front.domain.Stamp;
import com.win.front.dto.AllPostDTO;
import com.win.front.dto.PostDTO;
import com.win.front.retorfit.RetrofitAPI;
import com.win.front.retorfit.RetrofitClient;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyInfoFragment extends Fragment {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    ImageView my_info_user_type_image;
    TextView my_info_nickname;
    TextView my_info_stamp_status;

    Button my_info_logout_btn;
    Button my_info_profile_btn;
    Button my_info_shop_btn;

    private String userId;
    private SharedPreferences sp;

    private RetrofitClient retrofitClient;      // retrofit2 객체 참조 변수
    private RetrofitAPI retrofitAPI;            // retrofit2 api 객체 참조 변수

    private GridView my_info_gridView = null;
    private MyInfoStampGridViewAdapter myInfoStampGridViewAdapter;

    String user_type;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.my_info_fragment, container, false);

        sp = this.getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        userId = sp.getString("userId", "");

        // 그리드 뷰
        my_info_gridView = v.findViewById(R.id.my_info_grid_view);

        // 로그인한 유저의 타입 이미지 셋팅
        my_info_user_type_image = v.findViewById(R.id.my_info_user_type_image);

        // 로그인한 유저의 닉네임
        my_info_nickname = v.findViewById(R.id.my_info_nickname);

        // 스탬프 현황
        my_info_stamp_status = v.findViewById(R.id.my_info_stamp_status);

        // 프로필 관리 버튼
        my_info_profile_btn = v.findViewById(R.id.my_info_profile_btn);
        my_info_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeProfile();
            }
        });

        // 포인트몰 버튼
        my_info_shop_btn = v.findViewById(R.id.my_info_shop_btn);
        my_info_shop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // 로그아웃 버튼
        my_info_logout_btn = v.findViewById(R.id.my_info_logout_btn);
        my_info_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });

        // 구글
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(getActivity(), gso);

        // 닉네임과 스탬프 셋팅
        SetMyInfoContents();

        return v;
    }

    // 프로필 관리 액티비티 실행
    private void changeProfile() {
        Intent intent = new Intent(getActivity(), MyInfoProfileChange.class);
        startActivity(intent);
    }

    // 닉네임 셋팅
    private void SetMyInfoContents() {
        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

        retrofitAPI.getNickNameResponse(userId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String nickname = response.body().replaceAll("\"", "");
                my_info_nickname.setText(nickname);

                // 유저 타입 셋팅
                SetUserType();

                // 스탬프 셋팅
                SetStamp();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    // 유저 타입 셋팅
    private void SetUserType() {
        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

        retrofitAPI.getUserEnumResponse(userId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                user_type = response.body().replaceAll("\"", "");

                if (user_type.equals("GoogleUser")) {
                    my_info_user_type_image.setImageResource(R.drawable.my_info_google_user_icon);
                } else if (user_type.equals("KaKaoUser")) {
                    my_info_user_type_image.setImageResource(R.drawable.my_info_kakao_user_icon);
                } else {
                    my_info_user_type_image.setImageResource(R.drawable.my_info_login_user_icon);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    // 스탬프 셋팅
    private void SetStamp() {
        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

        // 현재 로그인한 유저의 스탬프를 가져옴
        retrofitAPI.getStampListResponse(userId).enqueue(new Callback<List<Stamp>>() {
            @Override
            public void onResponse(Call<List<Stamp>> call, Response<List<Stamp>> response) {
                List<Stamp> user_stamp_list = response.body();

                // 스탬프의 총 개수
                retrofitAPI.getLocationResponse().enqueue(new Callback<List<Location>>() {
                    @Override
                    public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                        List<Location> locationList = response.body();
                        my_info_stamp_status.setText(user_stamp_list.size() + "/" + locationList.size());

                        // 그리드뷰에 스탬프 셋팅
                        SetGridViewStamp(user_stamp_list, locationList);
                    }

                    @Override
                    public void onFailure(Call<List<Location>> call, Throwable t) {
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Stamp>> call, Throwable t) {
            }
        });
    }

    // 그리드뷰에 스탬프 셋팅
    private void SetGridViewStamp(List<Stamp> user_stamp_list, List<Location> locationList) {
        myInfoStampGridViewAdapter = new MyInfoStampGridViewAdapter();
        myInfoStampGridViewAdapter.addItem(user_stamp_list, locationList);

        my_info_gridView.setAdapter(myInfoStampGridViewAdapter);
    }

    // 로그아웃을 관리하는 함수
    private void LogOut() {
        if (user_type.equals("GoogleUser")) {
            sign_out();
        } else if (user_type.equals("KaKaoUser")) {
            kakao_out();
        } else {
            sign_out();
        }

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userId", "");
        editor.putString("autoLogin", "");
        editor.commit();
    }

    // 구글 로그아웃 함수, 로그인 유저 로그아웃 함수
    private void sign_out() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                custom_dialog_one_text("로그아웃 되었습니다.");
            }
        });
    }

    // 카카오 로그아웃 함수
    private void kakao_out() {
        UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
            @Override
            public Unit invoke(Throwable throwable) {
                custom_dialog_one_text("로그아웃 되었습니다.");
                return null;
            }
        });
    }

    // 커스텀 다이얼로그 : one_text
    public void custom_dialog_one_text(String message) {
        LayoutInflater inflater= getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_alert_dialog_one_text, null);
        ((TextView)view.findViewById(R.id.first_text)).setText(message);

        AlertDialog alert = new AlertDialog.Builder(getContext())
                .setView(view)
                .setCancelable(false)
                .create();

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);

        view.findViewById(R.id.alert_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                getActivity().finish();
                Intent intent = new Intent(getActivity(), SignIn.class);
                startActivity(intent);
            }
        });

        alert.show();

        WindowManager.LayoutParams params = alert.getWindow().getAttributes();
        params.width = 900;
        alert.getWindow().setAttributes(params);
    }
}
