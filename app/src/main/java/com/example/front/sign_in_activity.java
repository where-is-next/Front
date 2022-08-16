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

import com.example.front.domain.SocialUser;
import com.example.front.dto.SignInDTO;
import com.example.front.enumpack.UserEnum;
import com.example.front.find_id_pw_pack.find_id_pw;
import com.example.front.retorfit.RetrofitAPI;
import com.example.front.retorfit.RetrofitClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class sign_in_activity extends AppCompatActivity {
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

    EditText id;             // 로그인 화면에서의 id
    EditText pw;                // 로그인 화면에서의 pw

    private RetrofitClient retrofitClient;      // retrofit2 객체 참조 변수
    private RetrofitAPI retrofitAPI;            // retrofit2 api 객체 참조 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        // 아이디 입력
        id = findViewById(R.id.input_id);
        id.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        // 패스워드 입력
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

        // 카카오 로그인 로직
        kakao_login = findViewById(R.id.kakao_login);
        kakao_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KaKaoLogin();
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
    // 구글 유저 로그인 성공 시 서버로 보내는 함수
    private void navigateToSecondActivity() {

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        String id = acct.getId();
        String nickname = acct.getDisplayName();
        String email = acct.getEmail();
        String pw = "go";

        SocialUser socialUser = new SocialUser(id, pw, nickname, email, UserEnum.GoogleUser);

        //retrofit 생성
        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

        //저장된 데이터와 함께 api에서 정의한 getLoginResponse 함수를 실행한 후 응답을 받음
        retrofitAPI.getSocialUserResponse(socialUser).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body()) {
                        finish();
                        Intent intent = new Intent(sign_in_activity.this, main_page.class);

                        intent.putExtra("user", socialUser);
                        startActivity(intent);
                    }
                    else {
                        alertDialog("구글 로그인에 실패하였습니다." + "\n" + "다시 시도해주세요");
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                alertDialog("구글 서버 전송에 실패하였습니다.");
            }
        });
    }

    // 카카오 로그인 함수
    private void getKaKaoLoginInfo() {
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if (user != null) {
                    String id = Long.toString(user.getId());
                    String nickname = user.getKakaoAccount().getProfile().getNickname();
                    String email = user.getKakaoAccount().getEmail();
                    String pw = "go";

                    SocialUser socialUser = new SocialUser(id, pw, nickname, email, UserEnum.KaKaoUser);

                    //retrofit 생성
                    retrofitClient = RetrofitClient.getInstance();
                    retrofitAPI = RetrofitClient.getRetrofitInterface();

                    //저장된 데이터와 함께 api에서 정의한 getLoginResponse 함수를 실행한 후 응답을 받음
                    retrofitAPI.getSocialUserResponse(socialUser).enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            if (response.isSuccessful() && response.body() != null) {

                                if (response.body()) {
                                    finish();
                                    Intent intent = new Intent(sign_in_activity.this, main_page.class);
                                    intent.putExtra("user", socialUser);
                                    startActivity(intent);
                                }
                                else {
                                    alertDialog("카카오톡 로그인에 실패하였습니다." + "\n" + "다시 시도해주세요.");
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {
                            alertDialog("카카오톡 서버 전송에 실패하였습니다.");
                        }
                    });
                }
                else {
                    alertDialog("카카오톡 로그인에 실패하였습니다." + "\n" + "다시 시도해주세요.");
                }
                return null;
            }
        });
    }
    private void KaKaoLogin() {
        // 해딩 기기에 카카오톡 설치가 되있는 경우
        if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(sign_in_activity.this)) {
            UserApiClient.getInstance().loginWithKakaoTalk(sign_in_activity.this, new Function2<OAuthToken, Throwable, Unit>() {
                @Override
                public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                    if (oAuthToken != null) {
                        getKaKaoLoginInfo();
                    }
                    if (throwable != null) {
                        alertDialog("카카오톡 로그인에 실패하였습니다." + "\n" + "다시 시도해주세요.");
                    }
                    return null;
                }
            });
        }
        // 카카오톡 설치가 안되어 있는 경우 : 웹으로 로그인
        else {
            UserApiClient.getInstance().loginWithKakaoAccount(sign_in_activity.this, new Function2<OAuthToken, Throwable, Unit>() {
                @Override
                public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                    if (oAuthToken != null) {
                        getKaKaoLoginInfo();
                    }
                    if (throwable != null) {
                        alertDialog("카카오톡 로그인에 실패하였습니다." + "\n" + "다시 시도해주세요.");
                    }
                    return null;
                }
            });
        }
    }

    // 유저 로그인 함수
    // 일반 유저 로그인 시 retrofit2를 통해 서버로 보내는 함수
    public void login(String id, String pw) {
        SignInDTO signInDTO = new SignInDTO(id, pw, UserEnum.LoginUser);

        //retrofit 생성
        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

        //저장된 데이터와 함께 api에서 정의한 getLoginResponse 함수를 실행한 후 응답을 받음
        retrofitAPI.getLoginResponse(signInDTO).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body()) {
                        finish();
                        Intent intent = new Intent(sign_in_activity.this, main_page.class);
                        intent.putExtra("user", signInDTO);
                        startActivity(intent);
                    }
                    else {
                        alertDialog("아이디 혹은 비밀번호가 일치하지 않습니다." + "\n" + "다시 시도해주세요.");
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                alertDialog("로그인에 실패하였습니다.");
            }
        });
    }

    // 유저 아이디 찾기
    private void search_id() {
        Intent intent = new Intent(getApplicationContext(), find_id_pw.class);
        startActivity(intent);
    }

    // 유저 비밀번호 찾기
    private void search_pw() {
        Intent intent = new Intent(getApplicationContext(), find_id_pw.class);
        startActivity(intent);
    }

    // 유저 회원가입 함수
    private void sign_up() {
        Intent intent = new Intent(getApplicationContext(), sign_up_activity.class);
        startActivity(intent);
    }

    // 다이얼로그 띄우는 함수
    public void alertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(sign_in_activity.this);
        builder.setTitle("알림")
                .setMessage(message)
                .setPositiveButton("확인", null)
                .create()
                .show();
    }
}