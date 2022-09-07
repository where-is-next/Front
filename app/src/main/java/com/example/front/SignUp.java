package com.example.front;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.front.dto.SignUpDTO;
import com.example.front.retorfit.RetrofitAPI;
import com.example.front.retorfit.RetrofitClient;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {

    EditText id;            // 아이디
    EditText phone;         // 휴대폰 번호
    EditText phone_auth;    // 휴대폰 인증 코드 입력
    EditText nickname;      // 닉네임
    EditText pw;            // 비밀번호
    EditText pw_confirm;    // 비밀번호 확인

    Button auth_send;       // 인증코드 전송 버튼
    Button auth_confirm;    // 확인 버튼

    Button btn_sign_up;     // 가입 버튼

    String search_id_code;  // 인증코드 저장 변수

    private ImageView backImage;      // 회원 가입에서 메인으로 돌아가는 이미지

    static final int SMS_SEND_PERMISSON = 1;

    private RetrofitClient retrofitClient;      // retrofit2 객체 참조 변수
    private RetrofitAPI retrofitAPI;            // retrofit2 API 참조 변수

    boolean phone_auth_send_flag = false;
    boolean phone_auth_complete = false;        // 휴대폰 인증이 완료 되었는지 확인하는 flg

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        int permissonCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (permissonCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                Toast.makeText(getBaseContext(), "sms 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.SEND_SMS}, SMS_SEND_PERMISSON);
        }

        // 아이디
        id = findViewById(R.id.sign_up_id_input);
        id.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        // 휴대폰 번호
        phone = findViewById(R.id.sign_up_phone_input);
        phone.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        // 닉네임
        nickname = findViewById(R.id.sign_up_nickname_input);
        nickname.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        // 인증번호 전송 버튼
        auth_send = findViewById(R.id.sign_up_auth_send_btn);
        auth_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_vali_code();
            }
        });

        // 인증번호 입력
        phone_auth = findViewById(R.id.sign_up_valinum_input);
        phone_auth.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        // 확인 버튼
        auth_confirm = findViewById(R.id.sign_up_auth_confirm_btn);
        auth_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comfirm_vali_code();
            }
        });


        // 비밀번호
        pw = findViewById(R.id.sign_up_pw_input);
        pw.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        // 비밀번호 확인
        pw_confirm = findViewById(R.id.sign_up_pw_input_vali);
        pw_confirm.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        // 가입하기
        btn_sign_up = findViewById(R.id.btn_sign_up);
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sign_up_logic();
            }
        });

        // 뒤로 돌아가기
        backImage = (ImageView) findViewById(R.id.sign_up_back);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // 휴대전화 코드 확인
    private void comfirm_vali_code() {
        String code = phone_auth.getText().toString();

        if (code.equals("")) {
            custom_dialog_one_text("인증번호를 입력해주세요.");
        }

        else if (phone_auth_complete) {
            custom_dialog_one_text("이미 휴대폰 인증에 성공하셨습니다.");
        }

        else if (code.equals(search_id_code)) {
            phone_auth_send_flag = true;
            phone_auth_complete = true;

            phone_auth.setInputType(InputType.TYPE_NULL);
            phone.setInputType(InputType.TYPE_NULL);

            custom_dialog_one_text("휴대폰 인증이 완료되었습니다.");
        }

        else if (!code.equals(search_id_code)) {
            custom_dialog_two_text("인증번호가 다릅니다.", "다시 입력해주세요.");
        }
    }

    // 인증번호 발송 로직
    private void send_vali_code() {
        String phone_code = phone.getText().toString();

        if (phone_code.equals("")) {
            custom_dialog_one_text("휴대폰 번호를 입력해주세요.");
        }

        else if (phone_auth_complete) {
            custom_dialog_one_text("이미 휴대폰 인증에 성공하셨습니다.");
        }

        else {
            search_id_code = numberGen(6, 2);
            sendSMS(phone_code, "Where-is-next(웨이네) 회원가입 인증코드 입니다. \n" + search_id_code);
        }
    }
    public void sendSMS(String phone, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, message, null, null);

        custom_dialog_one_text("인증 코드가 전송되었습니다.");
    }

    public static String numberGen(int len, int dupCd ) {

        Random rand = new Random();
        String numStr = ""; //난수가 저장될 변수

        for(int i = 0; i < len; i++) {

            //0~9 까지 난수 생성
            String ran = Integer.toString(rand.nextInt(10));

            if(dupCd==1) {
                //중복 허용시 numStr에 append
                numStr += ran;
            }
            else if(dupCd == 2) {
                //중복을 허용하지 않을시 중복된 값이 있는지 검사한다
                if(!numStr.contains(ran)) {
                    //중복된 값이 없으면 numStr에 append
                    numStr += ran;
                }else {
                    //생성된 난수가 중복되면 루틴을 다시 실행한다
                    i -= 1;
                }
            }
        }
        return numStr;
    }

    // 가입하기 로직
    private void sign_up_logic() {
        String sign_up_input_id = id.getText().toString();
        String sign_up_input_phone = phone.getText().toString();
        String sign_up_input_nickname = nickname.getText().toString();
        String sign_up_input_pw = pw.getText().toString();
        String sign_up_input_pw_confirm = pw_confirm.getText().toString();

        System.out.println("입력한 비밀번호 : " + sign_up_input_pw);
        System.out.println("확인 = " + sign_up_input_pw_confirm);

        if (sign_up_input_id.equals("")) {
            custom_dialog_one_text("아이디를 입력해주세요.");
        } else if (!phone_auth_complete) {
            custom_dialog_one_text("휴대폰 인증을 진행해주세요.");
        } else if (sign_up_input_nickname.equals("")) {
            custom_dialog_one_text("닉네임을 입력해주세요.");
        } else if (sign_up_input_pw.equals("") || sign_up_input_pw_confirm.equals("")) {
            custom_dialog_one_text("비밀번호를 올바르게 입력해주세요.");
        } else if (!sign_up_input_pw.equals(sign_up_input_pw_confirm)) {
            custom_dialog_two_text("비밀번호가 다릅니다.", "다시 확인해주세요.");
        } else {
            SignUpDTO signUpDTO = new SignUpDTO(sign_up_input_id, sign_up_input_phone, sign_up_input_nickname, sign_up_input_pw);
            //retrofit 생성
            retrofitClient = RetrofitClient.getInstance();
            retrofitAPI = RetrofitClient.getRetrofitInterface();

            //저장된 데이터와 함께 api에서 정의한 getLoginResponse 함수를 실행한 후 응답을 받음
            retrofitAPI.getSignUpResponse(signUpDTO).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (!response.body()){
                            custom_dialog_two_text("중복된 아이디 혹은 중복된 닉네임입니다.", "다시 입력해주세요.");

                            phone_auth_send_flag = false;
                            phone_auth_complete = false;

                            phone_auth.setInputType(InputType.TYPE_CLASS_TEXT);
                            phone.setInputType(InputType.TYPE_CLASS_TEXT);
                        }

                        else {
                            custom_dialog_sign_up("회원가입이 완료되었습니다.","로그인을 진행해주세요.");
                        }
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    phone_auth_send_flag = false;
                    phone_auth_complete = false;

                    phone_auth.setInputType(InputType.TYPE_CLASS_TEXT);
                    phone.setInputType(InputType.TYPE_CLASS_TEXT);

                    custom_dialog_one_text("회원 가입에 실패하였습니다.");
                }
            });
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

    // 커스텀 다이얼로그 : 회원 가입 버튼 전용
    public void custom_dialog_sign_up(String messageOne, String messageTwo) {
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