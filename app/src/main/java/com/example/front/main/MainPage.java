package com.example.front.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.front.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainPage extends AppCompatActivity {

    private String user_id;
    private SharedPreferences sp;
    private long backpressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        // 현재 로그인한 유저 아이디
        sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        user_id = sp.getString("user_id", "");

        BottomNavigationView mBottomNavigationView = findViewById(R.id.bottom_navigation);

        // 첫 화면 띄우기
        getSupportFragmentManager().beginTransaction().add(R.id.frame_container,new HomeFragment()).commit();

        mBottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,new HomeFragment()).commit();
                    break;
                case R.id.nav_map:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,new MapFragment()).commit();
                    break;
                case R.id.nav_qr:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,new QRFragment()).commit();
                    break;
                case R.id.nav_post:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,new PostFragment()).commit();
                    break;
                case R.id.nav_myinfo:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,new MyInfoFragment()).commit();
                    break;
            }
            return true;
        });
    }

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