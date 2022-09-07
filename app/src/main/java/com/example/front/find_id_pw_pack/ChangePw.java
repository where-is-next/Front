package com.example.front.find_id_pw_pack;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.front.R;
import com.example.front.dto.ChangePwDTO;
import com.example.front.retorfit.RetrofitAPI;
import com.example.front.retorfit.RetrofitClient;
import com.example.front.SignIn;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePw extends Fragment {
    public static ChangePw newInstance() {
        return new ChangePw();
    }

    String id;
    String phoneNum;

    EditText pw_input;
    EditText pw_input_vali;

    Button pw_btn_confirm;

    boolean pw_confirm = true;

    RetrofitClient retrofitClient;      // retrofit2 객체 참조 변수
    RetrofitAPI retrofitAPI;            // retrofit2 API 참조 변수

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.change_pw, container, false);

        if (getArguments() != null)
        {
            id = getArguments().getString("id");
            phoneNum = getArguments().getString("phoneNum");
        }

        // 비밀번호 입력
        pw_input = rootView.findViewById(R.id.change_pw_input);
        pw_input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        // 비밀번호 확인 입력
        pw_input_vali = rootView.findViewById(R.id.change_pw_input_vali);
        pw_input_vali.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        // 비밀번호 변경 버튼
        pw_btn_confirm = rootView.findViewById(R.id.change_pw_btn_confirm);
        pw_btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePwMethod();
            }
        });

        return rootView;
    }

    // 비밀번호 변경 함수
    private void changePwMethod() {
        if (!pw_input.getText().toString().equals(pw_input_vali.getText().toString())) {
            pw_confirm = false;
        }

        if (pw_confirm) {
            String pw = pw_input.getText().toString();

            ChangePwDTO changePwDTO = new ChangePwDTO(id, phoneNum, pw);

            //retrofit 생성
            retrofitClient = RetrofitClient.getInstance();
            retrofitAPI = RetrofitClient.getRetrofitInterface();

            retrofitAPI.changePwResponse(changePwDTO).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.body()) {              // 비밀번호 변경 성공하면 실행
                        custom_dialog_change_pw("비밀번호가 변경되었습니다.", "로그인을 진행해주세요.");
                    }
                    else {                              // 가입된 유저는 이미 판단했으므로 원래 비밀번호와 같으면 실행
                        custom_dialog_two_text("변경하려는 비밀번호가 기존의 비밀번호와 같습니다.","다른 비밀번호를 입력해주세요.");
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    custom_dialog_two_text("비밀번호 찾기에 실패하였습니다.","다시 시도해주세요.");
                }
            });
        }

        else {          // 입력한 비밀번호와 비밀번호 확인이 다를 때
            custom_dialog_two_text("비밀번호가 다릅니다. " ,"다시 확인해주세요.");
            pw_confirm = true;
        }
    }

    // 커스텀 다이얼로그 : one_text
    public void custom_dialog_one_text(String message) {
        LayoutInflater inflater= getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_alert_dialog_one_text, null);
        ((TextView)view.findViewById(R.id.first_text)).setText(message);

        AlertDialog alert = new AlertDialog.Builder(requireActivity())
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

    // 커스텀 다이얼로그 : two_text
    public void custom_dialog_two_text(String messageOne, String messageTwo) {
        LayoutInflater inflater= getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_alert_dialog_two_text, null);
        ((TextView)view.findViewById(R.id.first_text)).setText(messageOne);
        ((TextView)view.findViewById(R.id.second_text)).setText(messageTwo);

        AlertDialog alert = new AlertDialog.Builder(requireActivity())
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

    // 커스텀 다이얼로그 : 비밀번호 변경 전용
    public void custom_dialog_change_pw(String messageOne, String messageTwo) {
        LayoutInflater inflater= getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_alert_dialog_two_text, null);
        ((TextView)view.findViewById(R.id.first_text)).setText(messageOne);
        ((TextView)view.findViewById(R.id.second_text)).setText(messageTwo);

        AlertDialog alert = new AlertDialog.Builder(requireActivity())
                .setView(view)
                .setCancelable(false)
                .create();

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);

        view.findViewById(R.id.alert_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                ((FindIdPw)getActivity()).finish();
            }
        });

        alert.show();

        WindowManager.LayoutParams params = alert.getWindow().getAttributes();
        params.width = 900;
        alert.getWindow().setAttributes(params);
    }
}
