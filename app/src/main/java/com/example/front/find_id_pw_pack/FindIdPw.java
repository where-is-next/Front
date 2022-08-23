package com.example.front.find_id_pw_pack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.front.R;
import com.example.front.SignIn;
import com.google.android.material.tabs.TabLayout;

public class FindIdPw extends FragmentActivity {

    // 전역변수
    TabLayout tabLayout;
    View back_view;

    Fragment find_id = FindIdFragment.newInstance();
    Fragment find_pw = FindPwFragment.newInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_id_pw);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame, find_id).commit();

        tabLayout = findViewById(R.id.find_id_pw_tab);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                Fragment selected = null;
                if(position == 0){
                    selected = FindIdFragment.newInstance();

                }else if (position == 1){
                    selected = FindPwFragment.newInstance();;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.frame, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        back_view = findViewById(R.id.back_view);
        back_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}