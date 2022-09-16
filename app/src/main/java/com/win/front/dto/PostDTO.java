package com.win.front.dto;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class PostDTO {
    String id;
    String nickname;
    String date;
    String title;
    String contents;
    ArrayList<String> images;

    public PostDTO(String id, String nickname, String date, String title, String contents, ArrayList<String> images) {
        this.id = id;
        this.nickname = nickname;
        this.date = date;
        this.title = title;
        this.contents = contents;
        this.images = images;
    }
}
