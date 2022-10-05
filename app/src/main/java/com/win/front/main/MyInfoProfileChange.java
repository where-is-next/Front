package com.win.front.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.win.front.R;
import com.win.front.SignIn;
import com.win.front.domain.User;
import com.win.front.retorfit.RetrofitAPI;
import com.win.front.retorfit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyInfoProfileChange extends AppCompatActivity {

    private String userId;
    private SharedPreferences sp;

    private RetrofitClient retrofitClient;      // retrofit2 객체 참조 변수
    private RetrofitAPI retrofitAPI;            // retrofit2 api 객체 참조 변수

    private User changeUser;

    TextView my_info_profile_change_nickname;
    TextView my_info_profile_change_pw;
    TextView my_info_profile_change_pw_vail;

    Button my_info_profile_change_confirm;
    Button my_info_profile_change_cancle_btn;
    Button my_info_profile_change_change_btn;

    boolean nickname_check_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_info_fragment_profile_change);

        sp = this.getSharedPreferences("UserInfo", MODE_PRIVATE);
        userId = sp.getString("userId", "");

        // 현재 로그인한 유저의 정보(User Domain)를 가져오는 함수
        SetLoginUser();

        // 변경할 닉네임
        my_info_profile_change_nickname = findViewById(R.id.my_info_profile_change_nickname);

        // 변경할 비밀번호
        my_info_profile_change_pw = findViewById(R.id.my_info_profile_change_pw);

        // 변경할 비밀번호 확인
        my_info_profile_change_pw_vail = findViewById(R.id.my_info_profile_change_pw_vail);

        // 닉네임 중복 확인 버튼
        my_info_profile_change_confirm = findViewById(R.id.my_info_profile_change_confirm);
        my_info_profile_change_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nickNameConfirm();
            }
        });

        // 돌아가기 버튼
        my_info_profile_change_cancle_btn = findViewById(R.id.my_info_profile_change_cancle_btn);
        my_info_profile_change_cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 변경하기 버튼
        my_info_profile_change_change_btn = findViewById(R.id.my_info_profile_change_change_btn);
        my_info_profile_change_change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeProfile();
            }
        });
    }

    // 닉네임 중복확인
    private void nickNameConfirm() {
        String confirmNickname = my_info_profile_change_nickname.getText().toString();

        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

        retrofitAPI.getProfileNickNameConfirmResponse(confirmNickname).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                // 중복된 것이 있다는 얘기
                if (response.body()) {
                    custom_dialog_one_text("중복된 닉네임 입니다.");
                    nickname_check_flag = false;
                }
                else {
                    custom_dialog_one_text("변경 가능한 닉네임 입니다.");
                    nickname_check_flag = true;
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });
    }

    // 유저 정보 변경
    private void changeProfile() {
        String changedNickname = my_info_profile_change_nickname.getText().toString();
        String changedPw = my_info_profile_change_pw.getText().toString();
        String changedPwVail = my_info_profile_change_pw_vail.getText().toString();

        if (changedNickname.equals("")) {
            custom_dialog_one_text("변경할 닉네임을 입력해주세요.");
        } else if (changedPw.equals("")) {
            custom_dialog_one_text("변경할 비밀번호를 입력해주세요.");
        } else if (changedPwVail.equals("")) {
            custom_dialog_one_text("변경할 비밀번호를 다시 한번 입력해주세요.");
        } else if (!changedPwVail.equals(changedPw)) {
            custom_dialog_one_text("변경할 비밀번호를 올바르게 입력해주세요.");
        } else {
            changeUser.nickname = changedNickname;
            changeUser.pw = changedPw;

            retrofitClient = RetrofitClient.getInstance();
            retrofitAPI = RetrofitClient.getRetrofitInterface();

            retrofitAPI.getUpdateUserProfileResponse(changeUser).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    custom_dialog_one_text_change_profile("변경되었습니다.");
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    custom_dialog_one_text("프로필 변경에 문제가 발생했습니다.");
                }
            });
        }
    }

    // 현재 로그인한 유저의 정보를 가져오는 함수
    private void SetLoginUser() {
        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

        retrofitAPI.getProfileUserResponse(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                changeUser = response.body();

                // 현재 유저의 닉네임 셋팅
                my_info_profile_change_nickname.setText(changeUser.nickname);

                // 로그인한 유저가 소셜 로그인 유저라면
                if (!changeUser.userEnum.equals("LoginUser")) {
                    my_info_profile_change_nickname.setEnabled(false);
                    my_info_profile_change_confirm.setEnabled(false);

                    my_info_profile_change_pw.setText("소셜 로그인은 변경할 수 없습니다.");
                    my_info_profile_change_pw.setEnabled(false);

                    my_info_profile_change_pw_vail.setText("소셜 로그인은 변경할 수 없습니다.");
                    my_info_profile_change_pw_vail.setEnabled(false);

                    my_info_profile_change_change_btn.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });
    }

    // 커스텀 다이얼로그 : one_text
    public void custom_dialog_one_text(String message) {
        LayoutInflater inflater= getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_alert_dialog_one_text, null);
        ((TextView)view.findViewById(R.id.first_text)).setText(message);

        AlertDialog alert = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create();

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);

        view.findViewById(R.id.alert_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });

        alert.show();

        WindowManager.LayoutParams params = alert.getWindow().getAttributes();
        params.width = 900;
        alert.getWindow().setAttributes(params);
    }

    // 커스텀 다이얼로그 : 변경 전용
    public void custom_dialog_one_text_change_profile(String message) {
        LayoutInflater inflater= getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_alert_dialog_one_text, null);
        ((TextView)view.findViewById(R.id.first_text)).setText(message);

        AlertDialog alert = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create();

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);

        view.findViewById(R.id.alert_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                finish();
            }
        });

        alert.show();

        WindowManager.LayoutParams params = alert.getWindow().getAttributes();
        params.width = 900;
        alert.getWindow().setAttributes(params);
    }
}