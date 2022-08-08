package com.example.front;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class sign_up extends AppCompatActivity {

    EditText input_nickname;
    EditText input_phone;
    EditText input_vali;
    EditText input_email;
    EditText input_pw;
    EditText input_pw_vali;

    Button nickname_vali;
    Button phone_vali;
    Button btn_sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        // 닉네임
        input_nickname = findViewById(R.id.input_nickname);
        input_nickname.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        // 중복확인
        nickname_vali = findViewById(R.id.nickname_vali);
        nickname_vali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double_check();
            }
        });

        // 휴대폰 번호
        input_phone = findViewById(R.id.input_phone);
        input_phone.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        // 인증번호 발송
        phone_vali = findViewById(R.id.phone_vali);
        phone_vali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_vali_code();
            }
        });

        // 인증번호
        input_vali = findViewById(R.id.valinum_input);
        input_vali.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        // 이메일
        input_email = findViewById(R.id.email_input);
        input_email.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        // 비밀번호
        input_pw = findViewById(R.id.pw_input);
        input_pw.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        // 비밀번호 확인
        input_pw_vali = findViewById(R.id.pw_input_vali);
        input_pw_vali.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        // 가입하기
        btn_sign_up = findViewById(R.id.btn_sign_up);
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sign_up_logic();
            }
        });
    }

    // 중복확인 로직
    private void double_check() {

    }

    // 인증번호 발송 로직
    private void send_vali_code() {

    }

    // 가입하기 로직
    private void sign_up_logic() {

    }
}
