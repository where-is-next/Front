package com.win.front.main.point_mall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.win.front.R;
import com.win.front.domain.Goods;
import com.win.front.retorfit.RetrofitAPI;
import com.win.front.retorfit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointMallActivity extends AppCompatActivity {

    ImageView point_mall_back_view;  // 뒤로 돌아가기
    ImageView point_mall_my_page;   // 포인트몰 마이페이지 이동

    ListView point_mall_list_view_eat;   // 외식 리스트 뷰
    ListView point_mall_list_view_beauty;   // 뷰티 리스트 뷰
    ListView point_mall_list_view_cafe;   // 카페 리스트 뷰

    private String userId;
    private SharedPreferences sp;

    private RetrofitClient retrofitClient;      // retrofit2 객체 참조 변수
    private RetrofitAPI retrofitAPI;            // retrofit2 api 객체 참조 변수

    List<Goods> allGoods;

    PointMallActivityEatItemAdapter pointMallActivityEatItemAdapter;
    PointMallActivityBeautyItemAdapter pointMallActivityBeautyItemAdapter;
    PointMallActivityCafeItemAdapter pointMallActivityCafeItemAdapter;

    TextView point_mall_my_point;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_mall_activity);

        sp = this.getSharedPreferences("UserInfo", MODE_PRIVATE);
        userId = sp.getString("userId", "");

        // 뒤로 돌아가기 버튼
        point_mall_back_view = findViewById(R.id.point_mall_back_view);
        point_mall_back_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 아이템 리스트뷰
        point_mall_list_view_eat = findViewById(R.id.point_mall_list_view_eat);
        point_mall_list_view_beauty = findViewById(R.id.point_mall_list_view_beauty);
        point_mall_list_view_cafe = findViewById(R.id.point_mall_list_view_cafe);

        // 내 포인트 셋팅
        point_mall_my_point = findViewById(R.id.point_mall_my_point);
        setMyPoint();

        // 리스트뷰 아이템 셋팅
        setPointMallListVIew();

        // 포인트몰 마이페이지 이동
        point_mall_my_page = findViewById(R.id.point_mall_my_page);
        point_mall_my_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PointMallActivity.this, PointMallMyPageActivity.class);
                startActivity(intent);
            }
        });
    }

    // 내 포인트 셋팅
    private void setMyPoint() {
        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

        retrofitAPI.getMyPointResponse(userId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                point_mall_my_point.setText("내 포인트 : " + response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    // 리스트뷰 아이템 셋팅
    private void setPointMallListVIew() {
        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

        retrofitAPI.getAllGoodsResponse().enqueue(new Callback<List<Goods>>() {
            @Override
            public void onResponse(Call<List<Goods>> call, Response<List<Goods>> response) {
                allGoods = response.body();

                pointMallActivityEatItemAdapter = new PointMallActivityEatItemAdapter();
                pointMallActivityBeautyItemAdapter = new PointMallActivityBeautyItemAdapter();
                pointMallActivityCafeItemAdapter = new PointMallActivityCafeItemAdapter();

                ArrayList<Goods> Eat = new ArrayList<>();
                ArrayList<Goods> Beauty = new ArrayList<>();
                ArrayList<Goods> Cafe = new ArrayList<>();

                for (Goods allGood : allGoods) {
                    switch (allGood.getType()) {
                        case "외식":
                            Eat.add(allGood);
                            break;

                        case "뷰티/생활":
                            Beauty.add(allGood);
                            break;

                        case "카페":
                            Cafe.add(allGood);
                            break;
                    }
                }

                // 어댑터 셋팅
                pointMallActivityEatItemAdapter.addItem(Eat);
                pointMallActivityBeautyItemAdapter.addItem(Beauty);
                pointMallActivityCafeItemAdapter.addItem(Cafe);

                // 리스트뷰 셋팅
                // 외식
                int hight = 0;
                for (int i = 0; i < pointMallActivityEatItemAdapter.getCount(); i++) {
                    View listItem = pointMallActivityEatItemAdapter.getView(i, null, point_mall_list_view_eat);
                    listItem.measure(0,0);
                    hight += listItem.getMeasuredHeight();
                }

                ViewGroup.LayoutParams params = point_mall_list_view_eat.getLayoutParams();
                params.height = hight + (point_mall_list_view_eat.getDividerHeight() * (pointMallActivityEatItemAdapter.getCount() - 1));
                point_mall_list_view_eat.setLayoutParams(params);

                point_mall_list_view_eat.setAdapter(pointMallActivityEatItemAdapter);

                // 뷰티
                int hight2 = 0;
                for (int i = 0; i < pointMallActivityBeautyItemAdapter.getCount(); i++) {
                    View listItem2 = pointMallActivityBeautyItemAdapter.getView(i, null, point_mall_list_view_beauty);
                    listItem2.measure(0,0);
                    hight2 += listItem2.getMeasuredHeight();
                }

                ViewGroup.LayoutParams params2 = point_mall_list_view_beauty.getLayoutParams();
                params2.height = hight2 + (point_mall_list_view_beauty.getDividerHeight() * (pointMallActivityBeautyItemAdapter.getCount() - 1));
                point_mall_list_view_beauty.setLayoutParams(params2);

                point_mall_list_view_beauty.setAdapter(pointMallActivityBeautyItemAdapter);

                // 카페 리스트
                int hight3 = 0;
                for (int i = 0; i < pointMallActivityCafeItemAdapter.getCount(); i++) {
                    View listItem3 = pointMallActivityCafeItemAdapter.getView(i, null, point_mall_list_view_beauty);
                    listItem3.measure(0,0);
                    hight3 += listItem3.getMeasuredHeight();
                }

                ViewGroup.LayoutParams params3 = point_mall_list_view_cafe.getLayoutParams();
                params3.height = hight3 + (point_mall_list_view_cafe.getDividerHeight() * (pointMallActivityCafeItemAdapter.getCount() - 1));
                point_mall_list_view_cafe.setLayoutParams(params3);

                point_mall_list_view_cafe.setAdapter(pointMallActivityCafeItemAdapter);
            }

            @Override
            public void onFailure(Call<List<Goods>> call, Throwable t) {
            }
        });
    }

}
