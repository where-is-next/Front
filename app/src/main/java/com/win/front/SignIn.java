package com.win.front;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.win.front.domain.SocialUser;
import com.win.front.dto.SignInDTO;
import com.win.front.enumpack.UserEnum;
import com.win.front.find_id_pw_pack.FindIdPw;
import com.win.front.main.MainPage;
import com.win.front.retorfit.RetrofitAPI;
import com.win.front.retorfit.RetrofitClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignIn extends AppCompatActivity {
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

    IdEditTextXBtn id;             // 로그인 화면에서의 id
    PwEditTextXBtn pw;                // 로그인 화면에서의 pw

    private RetrofitClient retrofitClient;      // retrofit2 객체 참조 변수
    private RetrofitAPI retrofitAPI;            // retrofit2 api 객체 참조 변수

    private SharedPreferences sp;
    private long backpressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        sp = getSharedPreferences("UserInfo", MODE_PRIVATE);

        String autoLogin = sp.getString("autoLogin", "");
        System.out.println("자동 로그인 판별 : " + autoLogin);
        if (autoLogin.equals("true")) {
            finish();
            Intent intent = new Intent(SignIn.this, MainPage.class);
            startActivity(intent);
        }

        // 아이디 입력
        id = (IdEditTextXBtn) findViewById(R.id.input_id);
        id.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });
        Drawable idIcon = DrawableCompat.wrap(ContextCompat.getDrawable(this, R.drawable.email_size));
        id.setCompoundDrawablesWithIntrinsicBounds(idIcon, null, null, null);

        // 패스워드 입력 및 키보드 내리기
        pw = (PwEditTextXBtn) findViewById(R.id.input_pw);
        pw.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(pw.getWindowToken(), 0);
                }
                return false;
            }
        });
        Drawable pwIcon = DrawableCompat.wrap(ContextCompat.getDrawable(this, R.drawable.lock_size));
        pw.setCompoundDrawablesWithIntrinsicBounds(pwIcon, null, null, null);

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
                Toast.makeText(getApplicationContext(), "구글 로그인을 취소하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // 구글 유저 로그인 성공 시 서버로 보내는 함수
    private void navigateToSecondActivity() {

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        String id = acct.getId();
        String pw = "go";
        String phone = "go_phone";
        String nickname = acct.getDisplayName();

        SocialUser socialUser = new SocialUser(id, pw, phone, nickname, UserEnum.GoogleUser);

        //retrofit 생성
        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

        //저장된 데이터와 함께 api에서 정의한 getLoginResponse 함수를 실행한 후 응답을 받음
        retrofitAPI.getSocialUserResponse(socialUser).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body()) {
                        sharedPreferenceMethod(id);

                        finish();
                        Intent intent = new Intent(SignIn.this, MainPage.class);
                        startActivity(intent);
                    }
                    else {
                        custom_dialog_two_text("구글 로그인에 실패하였습니다.","다시 시도해주세요.");
                        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                custom_dialog_one_text("구글 서버 전송에 실패하였습니다.");
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
                    String pw = "kakao";
                    String phone = "kakao_phone";
                    String nickname = user.getKakaoAccount().getProfile().getNickname();

                    SocialUser socialUser = new SocialUser(id, pw, phone, nickname, UserEnum.KaKaoUser);

                    //retrofit 생성
                    retrofitClient = RetrofitClient.getInstance();
                    retrofitAPI = RetrofitClient.getRetrofitInterface();

                    //저장된 데이터와 함께 api에서 정의한 getLoginResponse 함수를 실행한 후 응답을 받음
                    retrofitAPI.getSocialUserResponse(socialUser).enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            if (response.isSuccessful() && response.body() != null) {

                                if (response.body()) {
                                    sharedPreferenceMethod(id);

                                    finish();
                                    Intent intent = new Intent(SignIn.this, MainPage.class);
                                    startActivity(intent);
                                }
                                else {
                                    custom_dialog_two_text("카카오톡 로그인에 실패하였습니다.","다시 시도해주세요.");
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {
                            custom_dialog_one_text("카카오톡 서버 전송에 실패하였습니다.");
                        }
                    });
                }
                else {
                    custom_dialog_two_text("카카오톡 로그인에 실패하였습니다.", "다시 시도해주세요.");
                }
                return null;
            }
        });
    }
    private void KaKaoLogin() {
        // 해딩 기기에 카카오톡 설치가 되있는 경우
        if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(SignIn.this)) {
            UserApiClient.getInstance().loginWithKakaoTalk(SignIn.this, new Function2<OAuthToken, Throwable, Unit>() {
                @Override
                public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                    if (oAuthToken != null) {
                        getKaKaoLoginInfo();
                    }
                    if (throwable != null) {
                        custom_dialog_two_text("카카오톡 로그인에 실패하였습니다.","다시 시도해주세요.");
                    }
                    return null;
                }
            });
        }
        // 카카오톡 설치가 안되어 있는 경우 : 웹으로 로그인
        else {
            UserApiClient.getInstance().loginWithKakaoAccount(SignIn.this, new Function2<OAuthToken, Throwable, Unit>() {
                @Override
                public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                    if (oAuthToken != null) {
                        getKaKaoLoginInfo();
                    }
                    if (throwable != null) {
                        custom_dialog_two_text("카카오톡 로그인에 실패하였습니다.","다시 시도해주세요.");
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
                        sharedPreferenceMethod(id);

                        finish();
                        Intent intent = new Intent(SignIn.this, MainPage.class);
                        startActivity(intent);
                    }
                    else {
                        custom_dialog_two_text("아이디 혹은 비밀번호가 일치하지 않습니다.","다시 시도해주세요.");
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                custom_dialog_one_text("로그인에 실패하였습니다.");
            }
        });
    }

    // 유저 아이디 찾기
    private void search_id() {
        Intent intent = new Intent(getApplicationContext(), FindIdPw.class);
        startActivity(intent);
    }

    // 유저 비밀번호 찾기
    private void search_pw() {
        Intent intent = new Intent(getApplicationContext(), FindIdPw.class);
        startActivity(intent);
    }

    // 유저 회원가입 함수
    private void sign_up() {
        Intent intent = new Intent(getApplicationContext(), SignUp.class);
        startActivity(intent);
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

    // 커스텀 다이얼로그 : two_text
    public void custom_dialog_two_text(String messageOne, String messageTwo) {
        LayoutInflater inflater= getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_alert_dialog_two_text, null);
        ((TextView)view.findViewById(R.id.first_text)).setText(messageOne);
        ((TextView)view.findViewById(R.id.second_text)).setText(messageTwo);

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

    // SharedPreference : 로그인한 유저의 아이디를 담는 함수
    public void sharedPreferenceMethod(String id) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userId", id);

        if (auto_login_flag) {
            editor.putString("autoLogin", "true");
        } else {
            editor.putString("autoLogin", "false");
        }
        editor.commit();
    }

    // 뒤로가기 막기 함수
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backpressedTime + 2000) {
            backpressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() <= backpressedTime + 2000) {
            finish();
        }
    }
}