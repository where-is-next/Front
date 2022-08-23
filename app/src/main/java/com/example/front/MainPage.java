package com.example.front;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kakao.sdk.user.UserApiClient;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainPage extends AppCompatActivity {
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    GoogleSignInAccount acct;

    Button signOut;
    Button kakao_sign_out;

    private String user_id;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        user_id = sp.getString("user_id", "");
        System.out.println("유저 아이디 : " + user_id);

        signOut = findViewById(R.id.sign_out);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        acct = GoogleSignIn.getLastSignedInAccount(this);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sign_out();
            }
        });

        kakao_sign_out = findViewById(R.id.kakao_sign_out);
        kakao_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kakao_out();
            }
        });
    }

    private void sign_out() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                startActivity(new Intent(MainPage.this, SignIn.class));
            }
        });
    }

    private void kakao_out() {
        UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
            @Override
            public Unit invoke(Throwable throwable) {
                System.out.println("로그아웃");
                finish();
                startActivity(new Intent(MainPage.this, SignIn.class));

                return null;
            }
        });
    }
}