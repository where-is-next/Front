package com.win.front.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class PostUpdateDTO implements Serializable {
    String post_number;
    String id;
    String nickname;
    String date;
    String title;
    String contents;
    ArrayList<byte[]> images;

    public PostUpdateDTO(String post_number, String id, String nickname, String date, String title, String contents, ArrayList<byte[]> images) {
        this.post_number = post_number;
        this.id = id;
        this.nickname = nickname;
        this.date = date;
        this.title = title;
        this.contents = contents;
        this.images = images;
    }
}
