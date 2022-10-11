package com.win.front.main.point_mall;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.win.front.R;
import com.win.front.domain.MyGoods;
import com.win.front.retorfit.RetrofitAPI;
import com.win.front.retorfit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointMallMyPageActivity extends AppCompatActivity {

    ImageView point_mall_my_page_back_view;
    ListView point_mall_my_page_list;

    private String userId;
    private SharedPreferences sp;

    private RetrofitClient retrofitClient;      // retrofit2 객체 참조 변수
    private RetrofitAPI retrofitAPI;            // retrofit2 api 객체 참조 변수

    PointMallMyPageItemAdapter pointMallMyPageItemAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_mall_my_page_activity);

        sp = this.getSharedPreferences("UserInfo", MODE_PRIVATE);
        userId = sp.getString("userId", "");

        point_mall_my_page_list = findViewById(R.id.point_mall_my_page_list);

        // 뒤로 돌아가기 버튼
        point_mall_my_page_back_view = findViewById(R.id.point_mall_my_page_back_view);
        point_mall_my_page_back_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 내 구매 아이템 리스트 셋팅
        setMyPageListView();
    }

    // 내 구매 아이템 리스트 셋팅
    private void setMyPageListView() {
        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

        retrofitAPI.isMyGoodsResponse().enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                // 상품이 있다면
                if (response.body()) {
                    retrofitAPI.getMyGoodsListResponse(userId).enqueue(new Callback<List<MyGoods>>() {
                        @Override
                        public void onResponse(Call<List<MyGoods>> call, Response<List<MyGoods>> response) {
                            pointMallMyPageItemAdapter = new PointMallMyPageItemAdapter();

                            List<MyGoods> responseList = response.body();
                            pointMallMyPageItemAdapter.addItem(responseList);
                            point_mall_my_page_list.setAdapter(pointMallMyPageItemAdapter);
                        }

                        @Override
                        public void onFailure(Call<List<MyGoods>> call, Throwable t) {
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });
    }
}
