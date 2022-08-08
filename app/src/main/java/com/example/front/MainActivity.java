package com.example.front;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.front.domain.User;
import com.example.front.retorfit.RetrofitAPI;
import com.example.front.retorfit.RetrofitClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    static boolean auto_login_flag = false;
    Button auto_login;          // 자동 로그인 이미지 변경

    Button google_login;        // 구글 로그인 변수
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    Button kakao_login;         // 카카오 로그인 변수

    ImageView sign_up;          // 회원가입
    Button login;               // 로그인
    Button search_id;           // 아이디 찾기
    Button search_pw;           // 비밀번호 찾기

    EditText id;                // 로그인 화면에서의 email
    EditText pw;                // 로그인 화면에서의 pw

    private RetrofitClient retrofitClient;
    private RetrofitAPI retrofitAPI;

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
        id = (EditText)findViewById(R.id.input_id);
        pw = (EditText)findViewById(R.id.input_pw);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id_toString = id.getText().toString();
                String pw_toString = pw.getText().toString();

                login(id_toString, pw_toString);
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

        // 구글 로그인 로직
        google_login = findViewById(R.id.google_login);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        google_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                google_signIn();
            }
        });
    }

    // 구글 로그인 함수
    private void google_signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                task.getResult(ApiException.class);
                navigateToSecondActivity();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // 구글 로그인 성공 시 호출
    private void navigateToSecondActivity() {

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        String name = acct.getDisplayName();
        String email = acct.getEmail();

        //LoginResponse(name, email);
    }

    // 서버로 보냄
    public void LoginResponse(String id, String pw) {

        // 사용자가 입력한 id와 pw를 저장
        //GoogleUser googleUser = new GoogleUser(name, email);
        User user = new User(id, pw);
        System.out.println("user.getId() = " + user.getId());
        System.out.println("user.getPw() = " + user.getPw());

        //retrofit 생성
        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

        //저장된 데이터와 함께 api에서 정의한 getLoginResponse 함수를 실행한 후 응답을 받음
        retrofitAPI.getLoginResponse(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    System.out.println("retrofit 성공");

                    User reUser = response.body();
                    System.out.println(reUser.getClass());

                    /*finish();
                    Intent intent = new Intent(MainActivity.this, main_page.class);
                    startActivity(intent);*/
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("알림")
                        .setMessage("예기치 못한 오류가 발생하였습니다.")
                        .setPositiveButton("확인", null)
                        .create()
                        .show();
            }
        });
    }

    // 로그인 함수
    private void login(String id, String pw) {
        LoginResponse(id, pw);
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

