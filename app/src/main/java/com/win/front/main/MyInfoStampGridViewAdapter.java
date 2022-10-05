package com.win.front.main;

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
import com.win.front.domain.Location;
import com.win.front.domain.Stamp;

import java.net.StandardProtocolFamily;
import java.util.ArrayList;
import java.util.List;

public class MyInfoStampGridViewAdapter extends BaseAdapter {
    List<Location> items = new ArrayList<>();
    List<Stamp> myStamp = new ArrayList<>();

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Location getItem(int i) {
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
        converView = inflater.inflate(R.layout.my_info_fragment_grid_view_item, viewGroup, false);

        ImageView icon = converView.findViewById(R.id.my_info_grid_view_item_icon);
        TextView icon_text = converView.findViewById(R.id.my_info_grid_view_item_text);

        Location myItem = getItem(i);
        String link = null;

        if (myStamp.size() == 0) {
            link = "myinfo/" + myItem.getName() + "_no" + ".png";  // 무색 아이콘
        }
        else {
            for (Stamp stamp : myStamp) {
                if (stamp.getStampLocation().equals(myItem.getName())) {
                    link = "myinfo/" + myItem.getName() + ".png";   // 유색아이콘
                    break;
                }
                else {
                    link = "myinfo/" + myItem.getName() + "_no" + ".png";  // 무색 아이콘
                }
            }
        }

        // 파이어베이스에서 아이콘을 가져와서 셋팅
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
                    // Glide가 비동기라 데이터 Load전에 액티비티가 꺼지는 것을 방지
                    Activity activity = (Activity) context;
                    if (activity.isFinishing()) {
                        return;
                    }

                    Glide.with(context)
                            .load(uri)
                            .into(icon);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

        icon_text.setText(myItem.getName());

        return converView;
    }

    public void addItem(List<Stamp> user_stamp_list, List<Location> locationList) {
        items = locationList;
        myStamp = user_stamp_list;
    }
}
