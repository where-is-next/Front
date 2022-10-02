package com.win.front.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.win.front.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainPage extends AppCompatActivity {

    private String userId;
    private SharedPreferences sp;
    private long backpressedTime = 0;

    Fragment[] fragments = {new MapFragment()};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        // 현재 로그인한 유저 아이디
        sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        userId = sp.getString("userId", "");
        System.out.println(" 현재 로그인한 유저 아이디 : " + userId);

        BottomNavigationView mBottomNavigationView = findViewById(R.id.bottom_navigation);

        // 첫 화면 띄우기
        getSupportFragmentManager().beginTransaction().add(R.id.frame_container,new HomeFragment()).commit();
        mBottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new HomeFragment()).commit();
                    break;

                case R.id.nav_map:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragments[0]).commit();
                    break;

                case R.id.nav_qr:
                    Intent intent = new Intent(this, ScannerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    item.setIntent(intent);
                    break;

                case R.id.nav_post:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new PostFragment()).commit();
                    break;

                case R.id.nav_myinfo:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new MyInfoFragment()).commit();
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