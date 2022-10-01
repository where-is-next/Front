package com.win.front.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.win.front.HangleToEnglish;
import com.win.front.R;
import com.win.front.domain.Location;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class HomeLocationListAdapter extends BaseAdapter{
    ArrayList<Location> todayLocationList = new ArrayList<>();

    @Override
    public int getCount() {
        return todayLocationList.size();
    }

    @Override
    public Location getItem(int i) {
        return todayLocationList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View converView, ViewGroup viewGroup) {
        Context context = viewGroup.getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        converView = inflater.inflate(R.layout.home_fragment_today_location_list_item, viewGroup, false);

        ImageView iv_ordinal = (ImageView) converView.findViewById(R.id.today_location_list_ordinal);
        ImageView iv_icon = (ImageView) converView.findViewById(R.id.today_location_list_icon);

        TextView tv_name = (TextView) converView.findViewById(R.id.today_location_name);
        TextView tv_address = (TextView) converView.findViewById(R.id.today_location_address);

        Location myItem = getItem(i);

        if (i == 0) {
            iv_ordinal.setImageResource(R.drawable.main_item_s);
        } else if (i == 1) {
            iv_ordinal.setImageResource(R.drawable.main_item_t);
        } else if (i == 2) {
            iv_ordinal.setImageResource(R.drawable.main_item_a);
        } else if (i == 3) {
            iv_ordinal.setImageResource(R.drawable.main_item_m);
        } else if (i == 4) {
            iv_ordinal.setImageResource(R.drawable.main_item_p);
        }

        tv_name.setText(myItem.getName());
        tv_address.setText(myItem.getAddress());

        // 파이어베이스에서 아이콘을 가져와서 셋팅
        String link = "main/" + myItem.getName() + ".png";

        HangleToEnglish hangleToEnglish = new HangleToEnglish();
        String convert = hangleToEnglish.convert(link);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference pathReference = storageReference.child("main");
        if (pathReference == null) {
            System.out.println("문제");
        } else {
            StorageReference submitProfile = storageReference.child(convert);
            submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context)
                            .load(uri)
                            .into(iv_icon);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

        return converView;
    }

    public void addItem(String name, String address, String latitude, String longitude, String url) {
        Location mItem = new Location(name, address, latitude, longitude, url);
        todayLocationList.add(mItem);
    }
}
