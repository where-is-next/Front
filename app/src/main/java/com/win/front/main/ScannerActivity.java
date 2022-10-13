package com.win.front.main;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.win.front.HangleToEnglish;
import com.win.front.R;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.win.front.dto.AddStampDTO;
import com.win.front.retorfit.RetrofitAPI;
import com.win.front.retorfit.RetrofitClient;
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

    ProgressDialog dialog;  // 프로그레스 다이얼로그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_activity);

        dialog = new ProgressDialog(this);

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
                                                custom_dialog_icon("[" + qrRequest + "]" + " 스탬프가 적립되었습니다.", qrRequest);
                                            }

                                            else {
                                                custom_dialog_one_text("이미 스탬프를 적립한 관광지입니다.");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Boolean> call, Throwable t) {
                                    }
                                });
                            }

                            else {
                                custom_dialog_two_text("서비스중인 관광지가 아닙니다.", "다시 시도해주세요.");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        custom_dialog_two_text("서버와 통신할 수 없습니다.","다시 시도해주세요.");
                    }
                });
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
                finish();
            }
        });

        alert.show();

        WindowManager.LayoutParams params = alert.getWindow().getAttributes();
        params.width = 900;
        alert.getWindow().setAttributes(params);
    }

    // 커스텀 다이얼로그 : icon
    public void custom_dialog_icon(String message, String location) {

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  // 다이얼로그 스타일 설정
        dialog.setCancelable(false);    // 화면 밖 터치해도 종료되지 않게
        dialog.setMessage("Loading ...");   // 메세지
        dialog.show();  // 다이얼로그 시작

        LayoutInflater inflater= getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_alert_dialog_icon_text, null);
        ((TextView)view.findViewById(R.id.first_text)).setText(message);
        ((TextView)view.findViewById(R.id.second_text)).setText("포인트 50원 적립!");

        String link = "scan/" + location + ".png";

        HangleToEnglish hangleToEnglish = new HangleToEnglish();
        String convert = hangleToEnglish.convert(link);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference pathReference = storageReference.child("scan");
        if (pathReference == null) {
            System.out.println("문제");
        } else {
            StorageReference submitProfile = storageReference.child(convert);
            submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(ScannerActivity.this)
                            .load(uri)
                            .into(((ImageView) view.findViewById(R.id.icon)));

                    dialog.dismiss();

                    AlertDialog alert = new AlertDialog.Builder(ScannerActivity.this)
                            .setView(view)
                            .setCancelable(false)
                            .create();

                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alert.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    view.findViewById(R.id.alert_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                            finish();
                        }
                    });

                    alert.show();

                    WindowManager.LayoutParams params = alert.getWindow().getAttributes();
                    params.width = 900;
                    alert.getWindow().setAttributes(params);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
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
                finish();
            }
        });

        alert.show();

        WindowManager.LayoutParams params = alert.getWindow().getAttributes();
        params.width = 900;
        alert.getWindow().setAttributes(params);
    }
}