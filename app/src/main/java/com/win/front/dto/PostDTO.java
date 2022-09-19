package com.win.front.dto;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Multipart;

public class PostDTO implements Serializable {
    String id;
    String nickname;
    String date;
    String title;
    String contents;
    ArrayList<byte[]> images;

    public PostDTO(String id, String nickname, String date, String title, String contents, ArrayList<byte[]> images) {
        this.id = id;
        this.nickname = nickname;
        this.date = date;
        this.title = title;
        this.contents = contents;
        this.images = images;
    }
}
