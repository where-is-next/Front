package com.win.front.main;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.win.front.R;
import com.win.front.SignIn;
import com.win.front.domain.Location;
import com.win.front.retorfit.RetrofitAPI;
import com.win.front.retorfit.RetrofitClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kakao.sdk.user.UserApiClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment{

    private SharedPreferences sp;
    private List<Location> locationList;

    private RetrofitClient retrofitClient;      // retrofit2 객체 참조 변수
    private RetrofitAPI retrofitAPI;            // retrofit2 api 객체 참조 변수

    private ListView today_location_list;
    private HomeLocationListAdapter homeLocationListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_fragment, container, false);

        // 관광지 정보 불러오기
        settingList();

        // sharedPreference
        sp = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE); // 로그인한 유저 ID를 담고 있는 변수

        // 오늘의 여행지 리스트 뷰
        today_location_list = v.findViewById(R.id.today_location_list);

        return v;
    }

    // 관광지 리스트 추가 함수 , 수정 필요
    private void settingList() {
        //retrofit 생성
        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

        //저장된 데이터와 함께 api에서 정의한 getLoginResponse 함수를 실행한 후 응답을 받음
        retrofitAPI.getLocationResponse().enqueue(new Callback<List<Location>>() {
            @Override
            public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    locationList = response.body();

                    // 오늘의 여행지 리스트뷰 셋팅
                    setTodayLocationListView();
                }
            }

            @Override
            public void onFailure(Call<List<Location>> call, Throwable t) {
                custom_dialog_two_text("관광지 정보를 불러올 수 없습니다.", "다시 시도해주세요");
            }
        });
    }

    // 오늘의 여행지 리스트뷰 셋팅 함수
    private void setTodayLocationListView() {
        homeLocationListAdapter = new HomeLocationListAdapter();

        // 서비스중인 여행지 중에서 5개를 랜덤으로 선택
        Random rand = new Random();

        Set<Location> set = new HashSet<>();
        while (true) {
            set.add(locationList.get(rand.nextInt(locationList.size())));

            if (set.size() == 5) {
                break;
            }
        }

        Iterator<Location> iter = set.iterator();
        while (iter.hasNext()) {
            Location next = iter.next();

            homeLocationListAdapter.addItem(next.getName(), next.getLatitude(),
                    next.getLongitude(), next.getAddress(), next.getUrl());
        }

        // 아이템의 개수에 맞게 리스트뷰 높이 설정
        int hight = 0;
        for (int i = 0; i < homeLocationListAdapter.getCount(); i++) {
            View listItem = homeLocationListAdapter.getView(i, null, today_location_list);
            listItem.measure(0,0);
            hight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = today_location_list.getLayoutParams();
        params.height = hight + (today_location_list.getDividerHeight() * (homeLocationListAdapter.getCount() - 1));
        today_location_list.setLayoutParams(params);

        today_location_list.setAdapter(homeLocationListAdapter);
    }

    // 커스텀 다이얼로그 : two_text
    public void custom_dialog_two_text(String messageOne, String messageTwo) {
        LayoutInflater inflater= getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_alert_dialog_two_text, null);
        ((TextView)view.findViewById(R.id.first_text)).setText(messageOne);
        ((TextView)view.findViewById(R.id.second_text)).setText(messageTwo);

        AlertDialog alert = new AlertDialog.Builder(getActivity())
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

//
}
