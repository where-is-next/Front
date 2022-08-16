package com.example.front;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.front.dto.SignUpDTO;
import com.example.front.retorfit.RetrofitAPI;
import com.example.front.retorfit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class sign_up_activity extends AppCompatActivity {

    EditText id;            // 아이디
    EditText email;         // 이메일
    EditText email_auth;    // 이메일 인증 코드 입력
    EditText nickname;      // 닉네임
    EditText pw;            // 비밀번호
    EditText pw_confirm;    // 비밀번호 확인

    Button auth_send;       // 인증코드 전송 버튼
    Button auth_confirm;    // 확인 버튼

    Button btn_sign_up;     // 가입 버튼

    private RetrofitClient retrofitClient;      // retrofit2 객체 참조 변수
    private RetrofitAPI retrofitAPI;            // retrofit2 API 참조 변수

    boolean email_auth_send_flag = false;
    boolean email_auth_complete = false;        // 이메일 인증이 완료 되었는지 확인하는 flg

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

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

        // 이메일
        email = findViewById(R.id.sign_up_email_input);
        email.setOnKeyListener(new View.OnKeyListener() {
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
        auth_send = findViewById(R.id.auth_send_btn);
        auth_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_vali_code();
            }
        });

        // 인증번호 입력
        email_auth = findViewById(R.id.sign_up_valinum_input);
        email_auth.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        // 확인 버튼
        auth_confirm = findViewById(R.id.auth_confirm_btn);
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
    }

    // 이메일 코드 확인
    private void comfirm_vali_code() {
        String code = email_auth.getText().toString();

        if (code.equals("")) {
            alertDialog("코드를 입력해주세요.");
        }
        else if (!email_auth_send_flag) {
            alertDialog("인증번호 전송을 먼저 진행해주세요.");
        }
        else if (email_auth_complete) {
            alertDialog("이미 이메일 인증에 성공하셨습니다.");
        }
        else {
            //retrofit 생성
            retrofitClient = RetrofitClient.getInstance();
            retrofitAPI = RetrofitClient.getRetrofitInterface();

            //저장된 데이터와 함께 api에서 정의한 getLoginResponse 함수를 실행한 후 응답을 받음
            retrofitAPI.getEmailAuthCodeConfirm(code).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.body()) {
                        alertDialog("이메일 인증에 성공하였습니다.");

                        email_auth.setInputType(InputType.TYPE_NULL);
                        email.setInputType(InputType.TYPE_NULL);
                        email_auth_complete = true;
                    } else {
                        alertDialog("이메일 인증에 실패하였습니다." + "\n" + "입력한 코드를 확인해주세요.");
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    alertDialog("이메일 인증에 실패하였습니다." + "\n" + "입력한 코드를 확인해주세요.");
                }
            });
        }
    }

    // 인증번호 발송 로직
    private void send_vali_code() {
        String email_code = email.getText().toString();

        if (email_code.equals("")) {
            alertDialog("이메일을 입력해주세요.");
        }

        else if (email_auth_complete) {
            alertDialog("이미 이메일 인증에 성공하셨습니다.");
        }
        else {
            ProgressDialog progressDialog = new ProgressDialog(sign_up_activity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("이메일을 확인하는 중입니다.");
            progressDialog.setCancelable(false);
            progressDialog.show();

            //retrofit 생성
            retrofitClient = RetrofitClient.getInstance();
            retrofitAPI = RetrofitClient.getRetrofitInterface();

            retrofitAPI.getEmailAuthCodeSend(email_code).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String res = response.body().replaceAll("\"", "");

                    if (res.equals("ok")) {
                        alertDialog("입력한 이메일로 코드를 전송하였습니다.");
                        email_auth_send_flag = true;
                    }
                    else if(res.equals("mailError")){
                        alertDialog("인증번호 발송에 실패하였습니다." + "\n" + "올바른 이메일을 입력해주세요.");
                    }
                    progressDialog.cancel();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    alertDialog("인증번호 발송에 실패하였습니다.");
                    progressDialog.cancel();
                }
            });
        }
    }

    // 가입하기 로직
    private void sign_up_logic() {
        String sign_up_input_id = id.getText().toString();
        String sign_up_input_email = email.getText().toString();
        String sign_up_input_nickname = nickname.getText().toString();
        String sign_up_input_pw = pw.getText().toString();
        String sign_up_input_pw_confirm = pw_confirm.getText().toString();

        System.out.println("입력한 비밀번호 : " + sign_up_input_pw);
        System.out.println("확인 = " + sign_up_input_pw_confirm);

        if (sign_up_input_id.equals("")) {
            alertDialog("아이디를 입력해주세요.");
        } else if (!email_auth_complete) {
            alertDialog("이메일 인증을 진행해주세요.");
        } else if (sign_up_input_nickname.equals("")) {
            alertDialog("닉네임을 입력해주세요.");
        } else if (sign_up_input_pw.equals("") || sign_up_input_pw_confirm.equals("")) {
            alertDialog("비밀번호를 올바르게 입력해주세요.");
        } else if (!sign_up_input_pw.equals(sign_up_input_pw_confirm)) {
            alertDialog("비밀번호가 다릅니다. 다시 확인해주세요.");
        } else {
            SignUpDTO signUpDTO = new SignUpDTO(sign_up_input_id, sign_up_input_email, sign_up_input_nickname, sign_up_input_pw);
            //retrofit 생성
            retrofitClient = RetrofitClient.getInstance();
            retrofitAPI = RetrofitClient.getRetrofitInterface();

            //저장된 데이터와 함께 api에서 정의한 getLoginResponse 함수를 실행한 후 응답을 받음
            retrofitAPI.getSignUpResponse(signUpDTO).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (!response.body()){
                            alertDialog("중복된 아이디 혹은 중복된 닉네임입니다." + "\n" + "다시 입력해주세요");
                        }

                        else {
                            email_auth_send_flag = false;
                            email_auth_complete = false;

                            AlertDialog.Builder alert = new AlertDialog.Builder(sign_up_activity.this);
                            alert.setTitle("알림");
                            alert.setMessage("회원가입이 완료되었습니다." + "\n" + "로그인을 진행해주세요.");
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(sign_up_activity.this, sign_in_activity.class);
                                    startActivity(intent);
                                }
                            });
                            alert.show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    alertDialog("회원 가입에 실패하였습니다.");
                }
            });
        }
    }

    // 다이얼로그 띄우는 함수
    public void alertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(sign_up_activity.this);
        builder.setTitle("알림")
                .setMessage(message)
                .setPositiveButton("확인", null)
                .create()
                .show();
    }
}