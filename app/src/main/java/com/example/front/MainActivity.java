package com.example.front;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    static boolean auto_login_flag = false;
    Button auto_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

}