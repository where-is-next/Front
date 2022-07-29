package com.example.front;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    static boolean auto_login_flag = false;
    Button auto_login;          // 자동 로그인 이미지 변경

    Button google_login;        // 구글 로그인 변수
    Button kakao_login;         // 카카오 로그인 변수

    ImageView sign_up;          // 회원가입
    Button login;               // 로그인
    Button search_id;           // 아이디 찾기
    Button search_pw;           // 비밀번호 찾기

    EditText id;
    EditText pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id = findViewById(R.id.input_id);
        id.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        pw = findViewById(R.id.input_pw);
        pw.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        // 자동 로그인 이미지 변경
        auto_login = findViewById(R.id.auto_login_check);
        auto_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!auto_login_flag) {
                    auto_login.setBackgroundResource(R.drawable.auto_login_yes);
                    auto_login_flag = true;
                }
                else {
                    auto_login.setBackgroundResource(R.drawable.auto_login_no);
                    auto_login_flag = false;
                }
            }
        });

        // 로그인
        login = findViewById(R.id.btn_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        // 아이디 찾기
        search_id = findViewById(R.id.search_id);
        search_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_id();
            }
        });

        // 비밀번호 찾기
        search_pw = findViewById(R.id.search_pw);
        search_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_pw();
            }
        });

        // 회원가입
        sign_up = findViewById(R.id.sign_in);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sign_up();
            }
        });


    }

    // 로그인 함수
    private void login() {
    }

    // 아이디 찾기
    private void search_id() {
    }

    // 비밀번호 찾기
    private void search_pw() {
    }

    // 회원가입 함수
    private void sign_up() {
        Intent intent = new Intent(getApplicationContext(), sign_up.class);
        startActivity(intent);
    }
}