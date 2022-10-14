package com.win.front.main.point_mall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.win.front.HangleToEnglish;
import com.win.front.R;
import com.win.front.domain.Goods;
import com.win.front.dto.BuyGoodsDTO;
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
    ImageView point_mall_refresh_btn; // 포인트 새로고침

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

    ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_mall_activity);

        sp = this.getSharedPreferences("UserInfo", MODE_PRIVATE);
        userId = sp.getString("userId", "");
        dialog = new ProgressDialog(this);

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

        // 아이템 구매하기
        // 외식
        point_mall_list_view_eat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Goods selected_goods = (Goods) adapterView.getAdapter().getItem(i);
                custom_dialog_buy_text(selected_goods.getName(), selected_goods.getPrice(), selected_goods.getBrand());
            }
        });

        // 뷰티/생활
        point_mall_list_view_beauty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Goods selected_goods = (Goods) adapterView.getAdapter().getItem(i);
                custom_dialog_buy_text(selected_goods.getName(), selected_goods.getPrice(), selected_goods.getBrand());
            }
        });

        // 카페
        point_mall_list_view_cafe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Goods selected_goods = (Goods) adapterView.getAdapter().getItem(i);
                custom_dialog_buy_text(selected_goods.getName(), selected_goods.getPrice(), selected_goods.getBrand());
            }
        });

        // 포인트 새로고침
        point_mall_refresh_btn = findViewById(R.id.point_mall_refresh_btn);
        point_mall_refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMyPoint();
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

    // buy_text 커스텀 다이얼로그
    public void custom_dialog_buy_text(String name, String price, String brand) {
        LayoutInflater inflater= getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_alert_dialog_buy_text, null);

        // 이미지 셋팅
        ImageView goods_img = (ImageView) view.findViewById(R.id.goods_img);
        String link = "pointmall/" + name + ".png";

        HangleToEnglish hangleToEnglish = new HangleToEnglish();
        String convert = hangleToEnglish.convert(link);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference pathReference = storageReference.child("pointmall");
        if (pathReference == null) {
            System.out.println("문제");
        } else {
            StorageReference submitProfile = storageReference.child(convert);
            submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
//                    Activity activity = (Activity) view.getContext();
//                    if (activity.isFinishing()) {
//                        return;
//                    }

                    Glide.with(view.getContext())
                            .load(uri)
                            .into(goods_img);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

        // 텍스트 셋팅
        ((TextView)view.findViewById(R.id.goods_brand)).setText("[" + brand + "]");
        ((TextView)view.findViewById(R.id.goods_text)).setText("[" + name + "]");

        AlertDialog alert = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create();

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 구매 버튼
        view.findViewById(R.id.point_mall_buy_complete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  // 다이얼로그 스타일 설정
                dialog.setCancelable(false);    // 화면 밖 터치해도 종료되지 않게
                dialog.setMessage("Loading ...");   // 메세지
                dialog.show();  // 다이얼로그 시작

                // 구매하는 함수
                buyGoods(name, price, brand);

                alert.dismiss();
            }
        });

        // 취소 버튼
        view.findViewById(R.id.point_mall_buy_cancle_btn).setOnClickListener(new View.OnClickListener() {
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

    // 구매하는 로직
    private void buyGoods(String name, String price, String brand) {
        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

        BuyGoodsDTO buyGoodsDTO = new BuyGoodsDTO();
        buyGoodsDTO.setId(userId);
        buyGoodsDTO.setGoods_name(name);
        buyGoodsDTO.setPrice(price);
        buyGoodsDTO.setBrand(brand);
        buyGoodsDTO.setStatus("구매 완료");

        retrofitAPI.getBuyGoodsResponse(buyGoodsDTO).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                dialog.dismiss();

                // 구매 성공
                if (response.body()) {
                    custom_dialog_buy_complete_text("구매 완료하였습니다.");
                }

                // 구매 실패 : 금액 부족
                else {
                    custom_dialog_one_text("보유한 포인트가 부족합니다.");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });
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

    // 커스텀 다이얼로그 : 구매 완료 시
    public void custom_dialog_buy_complete_text(String message) {
        LayoutInflater inflater= getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_alert_dialog_buy_complete_text, null);
        ((TextView)view.findViewById(R.id.first_text)).setText(message);

        AlertDialog alert = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create();

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 완료
        view.findViewById(R.id.dialog_complete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });

        // 쿠폰함 이동
        view.findViewById(R.id.point_mall_my_page_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();

                Intent intent = new Intent(PointMallActivity.this, PointMallMyPageActivity.class);
                startActivity(intent);
            }
        });

        alert.show();

        WindowManager.LayoutParams params = alert.getWindow().getAttributes();
        params.width = 900;
        alert.getWindow().setAttributes(params);
    }
}
