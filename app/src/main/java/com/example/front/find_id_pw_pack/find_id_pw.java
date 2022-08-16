package com.example.front.find_id_pw_pack;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.front.R;
import com.example.front.sign_in_activity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class find_id_pw extends FragmentActivity {
    // 전역변수
    TabLayout tabLayout;
    ViewPager2 viewPager;
    TabPageAdapter adapter;

    View back_view;

    String[] str = new String[] { "아이디 찾기", "비밀번호 찾기"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_id_pw);

        tabLayout = findViewById(R.id.find_id_pw_tab);
        viewPager = findViewById(R.id.pager);

        adapter = new TabPageAdapter(this);
        viewPager.setAdapter(adapter);


        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                TextView textView = new TextView(find_id_pw.this);
                textView.setText(str[position]);
                textView.setGravity(Gravity.CENTER);
                tab.setCustomView(textView);
            }
        }).attach();

        back_view = findViewById(R.id.back_view);
        back_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), sign_in_activity.class);
                startActivity(intent);
            }
        });
    }


}
