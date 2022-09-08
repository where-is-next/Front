package com.win.front.find_id_pw_pack;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.win.front.R;
import com.win.front.retorfit.RetrofitAPI;
import com.win.front.retorfit.RetrofitClient;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindIdFragment extends Fragment {

    public static FindIdFragment newInstance() {
        return new FindIdFragment();
    }

    EditText phone;
    EditText phone_auth;

    Button auth_send_btn;
    Button auth_confirm_btn;

    Button btn_confirm;

    String search_id_code;

    RetrofitClient retrofitClient;      // retrofit2 객체 참조 변수
    RetrofitAPI retrofitAPI;            // retrofit2 API 참조 변수

    boolean phone_auth_send_flag = false;
    boolean phone_auth_complete = false;        // 휴대폰 인증이 완료 되었는지 확인하는 flg

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.find_id_fragment, container, false);


        // 휴대전화 번호 입력
        phone = rootView.findViewById(R.id.find_id_phone_input);
        phone.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        // 인증번호 전송 버튼
        auth_send_btn = rootView.findViewById(R.id.find_id_auth_send_btn);
        auth_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_vali_code();
            }
        });

        // 인증번호 입력
        phone_auth = rootView.findViewById(R.id.find_id_valinum_input);
        phone_auth.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        // 확인 버튼
        auth_confirm_btn = rootView.findViewById(R.id.find_id_auth_confirm_btn);
        auth_confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comfirm_vali_code();
            }
        });

        // 찾기 확인 버튼
        btn_confirm = rootView.findViewById(R.id.find_id_btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_id();
            }
        });
        return rootView;
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
            custom_dialog_two_text("인증번호가 다릅니다.","다시 입력해주세요.");
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
            sendSMS(phone_code, "Where-is-next(웨이네) 아이디 찾기 인증코드 입니다. \n" + search_id_code);
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

        for(int i=0;i<len;i++) {

            //0~9 까지 난수 생성
            String ran = Integer.toString(rand.nextInt(10));

            if(dupCd == 1) {
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

    // 아이디 찾기
    private void search_id() {
        if(!phone_auth_complete) {
            custom_dialog_one_text("휴대전화 인증을 진행해주세요.");
        }
        else {
            String phoneNum = phone.getText().toString();

            //retrofit 생성
            retrofitClient = RetrofitClient.getInstance();
            retrofitAPI = RetrofitClient.getRetrofitInterface();

            //저장된 데이터와 함께 api에서 정의한 getLoginResponse 함수를 실행한 후 응답을 받음
            retrofitAPI.getIDResponse(phoneNum).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String search_id = response.body().replaceAll("\"","");;

                    Bundle bundle = new Bundle();                   // 번들을 통해 프래그먼트에서 프래그먼트로 값 전달
                    bundle.putString("search_id", search_id);       //번들에 넘길 값 저장
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                    Fragment find_id_finish = FindIdFinishFragment.newInstance();
                    find_id_finish.setArguments(bundle);

                    transaction.replace(R.id.frame, find_id_finish);
                    transaction.commit();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    phone_auth_send_flag = false;
                    phone_auth_complete = false;

                    phone_auth.setInputType(InputType.TYPE_CLASS_TEXT);
                    phone.setInputType(InputType.TYPE_CLASS_TEXT);

                    custom_dialog_two_text("가입되어 있는 아이디가 없습니다.", "다시 시도해주세요.");
                }
            });
        }
    }

    // 커스텀 다이얼로그 : one_text
    public void custom_dialog_one_text(String message) {
        LayoutInflater inflater= getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_alert_dialog_one_text, null);
        ((TextView)view.findViewById(R.id.first_text)).setText(message);

        AlertDialog alert = new AlertDialog.Builder(requireActivity())
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

        AlertDialog alert = new AlertDialog.Builder(requireActivity())
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
}
