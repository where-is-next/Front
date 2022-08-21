package com.example.front.domain;

import com.example.front.enumpack.UserEnum;

import java.io.Serializable;

public class SocialUser implements Serializable {

    private String id;  // 소셜 로그인의 id값 ( getId() ), LoginUser의 id
    private String pw;
    private String phone;
    private String nickname; // nickname
    private UserEnum userEnum;

    public SocialUser(String id, String pw, String phone, String nickname, UserEnum userEnum) {
        this.id = id;
        this.pw = pw;
        this.phone = phone;
        this.nickname = nickname;
        this.userEnum = userEnum;
    }

    @Override
    public String toString() {
        return "SocialUser{" +
                "id='" + id + '\'' +
                ", pw='" + pw + '\'' +
                ", phone='" + phone + '\'' +
                ", nickname='" + nickname + '\'' +
                ", userEnum=" + userEnum +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getPw() {
        return pw;
    }

    public String getPhone() {
        return phone;
    }

    public String getNickname() {
        return nickname;
    }

    public UserEnum getUserEnum() {
        return userEnum;
    }
}
