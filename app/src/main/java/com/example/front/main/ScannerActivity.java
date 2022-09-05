package com.example.front.main;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.front.R;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.front.dto.AddStampDTO;
import com.example.front.retorfit.RetrofitAPI;
import com.example.front.retorfit.RetrofitClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScannerActivity extends AppCompatActivity {

    private IntentIntegrator qrScan;
    private RetrofitClient retrofitClient;      // retrofit2 객체 참조 변수
    private RetrofitAPI retrofitAPI;            // retrofit2 api 객체 참조 변수

    private SharedPreferences sp;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_activity);

        sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        userId = sp.getString("userId", "");

        qrScan = new IntentIntegrator(this);
        qrScan.setCaptureActivity(ScannerActivityCustom.class);

        qrScan.setPrompt("QR코드를 사각형 안에 넣어주세요.");

        qrScan.setOrientationLocked(true);  // 카메라 도중 회전 막기
        qrScan.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                finish();
            }
            else {
                String qrRequest = result.getContents();

                //retrofit 생성
                retrofitClient = RetrofitClient.getInstance();
                retrofitAPI = RetrofitClient.getRetrofitInterface();

                // 스캔한 값이 서비스중인 관광지인지 판별
                retrofitAPI.getConfrimLocationResponse(qrRequest).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body()) {

                                AddStampDTO addStampDTO = new AddStampDTO(userId, qrRequest);

                                // 서비스 중인 관광지이면 스탬프 추가
                                retrofitAPI.getAddStampResponse(addStampDTO).enqueue(new Callback<Boolean>() {
                                    @Override
                                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            if (response.body()) {
                                                buttonAlertDialog("[" + qrRequest + "]" + " 스탬프가 적립되었습니다.");
                                            }

                                            else {
                                                custom_dialog("이미 스탬프를 적립한 관광지입니다.");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Boolean> call, Throwable t) {
                                    }
                                });
                            }

                            else {
                                buttonAlertDialog("서비스중인 관광지가 아닙니다. 다시 시도해주세요");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        buttonAlertDialog("서버와 통신할 수 없습니다. 다시 시도해주세요");
                    }
                });
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // 버튼 다이얼로그
    public void buttonAlertDialog(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("알림");
        alert.setMessage(message);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.show();
    }

    // 커스텀 다이얼로그
    public void custom_dialog(String message) {
        LayoutInflater inflater= getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_alert_dialog, null);
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
                finish();
            }
        });

        alert.show();

        WindowManager.LayoutParams params = alert.getWindow().getAttributes();
        params.width = 900;
        alert.getWindow().setAttributes(params);
    }
}