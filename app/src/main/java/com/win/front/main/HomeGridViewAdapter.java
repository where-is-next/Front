package com.win.front.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.win.front.domain.Location;
import com.win.front.retorfit.RetrofitAPI;
import com.win.front.retorfit.RetrofitClient;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeGridViewAdapter extends BaseAdapter{
    ArrayList<PostListItem> items = new ArrayList<>();

    private RetrofitClient retrofitClient;      // retrofit2 객체 참조 변수
    private RetrofitAPI retrofitAPI;            // retrofit2 api 객체 참조 변수

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public PostListItem getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View converView, ViewGroup viewGroup) {
        Context context = viewGroup.getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        converView = inflater.inflate(R.layout.home_fragment_grid_view_item, viewGroup, false);

        ImageView imageView = converView.findViewById(R.id.home_grid_view_image);
        TextView textView = converView.findViewById(R.id.home_grid_view_title);
        TextView tv_commnt_cnt = converView.findViewById(R.id.home_grid_view_comment_cnt);
        TextView tv_heart_cnt = converView.findViewById(R.id.home_grid_view_heart_cnt);

        PostListItem myItem = getItem(i);

        if (myItem.getImageURI() != null) {
            imageView.setImageBitmap(byteArrayToBitmap(myItem.getImageURI()));
            imageView.setColorFilter(Color.parseColor("#BDBDBD"), PorterDuff.Mode.MULTIPLY);
        }

        if (myItem.getTitle().length() > 14) {
            textView.setText(myItem.getTitle().substring(0, 14) + " ...");
        } else {
            textView.setText(myItem.getTitle());
        }

        // 댓글 수 셋팅
        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

        retrofitAPI.getPostCommentCntResponse(myItem.getPost_number()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                tv_commnt_cnt.setText(" "+ response.body().replaceAll("\"", ""));
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                System.out.println("에러 : " + t.getMessage());
            }
        });

        // 좋아요 수 셋팅
        retrofitAPI.getPostHeartCntResponse(myItem.getPost_number()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                tv_heart_cnt.setText(response.body().replaceAll("\"", ""));
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                System.out.println("에러 : " + t.getMessage());
            }
        });

        return converView;
    }

    public void addItem(String title, String nickname, String date, String contents, byte[] imageUri, String post_number) {

        PostListItem mItem = new PostListItem();

        mItem.setTitle(title);
        mItem.setNickname(nickname);
        mItem.setDate(date);
        mItem.setContents(contents);
        mItem.setImageURI(imageUri);
        mItem.setPost_number(post_number);

        items.add(mItem);
    }

    // Byte를 Bitmap으로 변환
    public Bitmap byteArrayToBitmap(byte[] byteArray ) {
        byte[] decodedImageBytes = Base64.decode(byteArray, Base64.DEFAULT);
        ByteArrayInputStream inStream = new ByteArrayInputStream(decodedImageBytes);

        Bitmap bitmap = BitmapFactory.decodeStream(inStream);

        return bitmap;
    }
}
