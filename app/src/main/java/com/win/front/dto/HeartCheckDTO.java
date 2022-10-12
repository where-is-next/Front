package com.win.front.dto;

public class HeartCheckDTO {
    String post_number;
    String id;

    public String getPost_number() {
        return post_number;
    }

    public void setPost_number(String post_number) {
        this.post_number = post_number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "HeartCheckDTO{" +
                "post_number='" + post_number + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
