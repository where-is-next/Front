package com.win.front.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class AllPostDTO {
    Long number;
    String id;
    String nickname;
    String date;
    String title;
    String contents;
    ArrayList<byte[]> allImages;

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public ArrayList<byte[]> getAllImages() {
        return allImages;
    }

    public void setAllImages(ArrayList<byte[]> allImages) {
        this.allImages = allImages;
    }
}
