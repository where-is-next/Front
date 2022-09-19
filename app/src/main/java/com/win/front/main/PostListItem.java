package com.win.front.main;

import java.util.Arrays;

public class PostListItem {
    String title;
    String nickname;
    String date;
    String contents;
    byte[] imageURI;
    String post_number;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public byte[] getImageURI() {
        return imageURI;
    }

    public void setImageURI(byte[] imageURI) {
        this.imageURI = imageURI;
    }

    public String getPost_number() {
        return post_number;
    }

    public void setPost_number(String post_number) {
        this.post_number = post_number;
    }

    @Override
    public String toString() {
        return "PostListItem{" +
                "title='" + title + '\'' +
                ", nickname='" + nickname + '\'' +
                ", date='" + date + '\'' +
                ", contents='" + contents + '\'' +
                ", imageURI=" + Arrays.toString(imageURI) +
                ", post_number='" + post_number + '\'' +
                '}';
    }
}
