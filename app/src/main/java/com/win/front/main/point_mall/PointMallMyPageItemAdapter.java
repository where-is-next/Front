package com.win.front.main.point_mall;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.win.front.HangleToEnglish;
import com.win.front.R;
import com.win.front.domain.Goods;
import com.win.front.domain.MyGoods;

import java.util.ArrayList;
import java.util.List;

public class PointMallMyPageItemAdapter extends BaseAdapter {
    List<MyGoods> goodsList = new ArrayList<>();

    @Override
    public int getCount() {
        return goodsList.size();
    }

    @Override
    public MyGoods getItem(int i) {
        return goodsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View converView, ViewGroup viewGroup) {

        Context context = viewGroup.getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        converView = inflater.inflate(R.layout.point_mall_my_page_list_view_item, viewGroup, false);

        MyGoods item = getItem(i);

        ImageView point_mall_my_goods_img = converView.findViewById(R.id.point_mall_my_goods_img);

        TextView point_mall_my_goods_brand = converView.findViewById(R.id.point_mall_my_goods_brand);
        TextView point_mall_my_goods_name = converView.findViewById(R.id.point_mall_my_goods_name);
        TextView point_mall_my_goods_status = converView.findViewById(R.id.point_mall_my_goods_status);

        // 이미지 셋팅
        String link = "pointmall/" + item.getMy_goods_name() + ".png";

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
                    // Glide가 비동기라 데이터 Load전에 액티비티가 꺼지는 것을 방지
                    Activity activity = (Activity) context;
                    if (activity.isFinishing()) {
                        return;
                    }

                    Glide.with(context)
                            .load(uri)
                            .into(point_mall_my_goods_img);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

        // 텍스트 셋팅
        point_mall_my_goods_brand.setText(item.getMy_goods_brand());
        point_mall_my_goods_name.setText(item.getMy_goods_name());
        point_mall_my_goods_status.setText(item.getStatus());

        return converView;
    }

    public void addItem(List<MyGoods> inputGoods) {
        goodsList = inputGoods;
    }
}
